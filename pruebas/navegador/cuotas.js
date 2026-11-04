// Recorrido de las cuotas (Modulo 4). Base recien creada.
const { chromium } = require('playwright-core')
const path = require('path')
const { WEB, API, NAVEGADOR, CAPTURAS, ejecutarSql } = require('./base')

const resultados = []
const erroresConsola = []

function caso(nombre, ok, detalle = '') {
  resultados.push(ok)
  console.log(`${ok ? 'OK   ' : 'FALLA'} ${nombre}${detalle ? `  -> ${detalle}` : ''}`)
}
const captura = (p, n) => p.screenshot({ path: path.join(CAPTURAS, `${n}.png`), fullPage: true })
const tarjeta = (p, t) => p.locator(`section:has(> h2:text-is("${t}"))`)

function mes(desplazamiento) {
  const f = new Date()
  f.setDate(1)
  f.setMonth(f.getMonth() + desplazamiento)
  return `${f.getFullYear()}-${String(f.getMonth() + 1).padStart(2, '0')}`
}
const NOMBRES = ['enero', 'febrero', 'marzo', 'abril', 'mayo', 'junio', 'julio', 'agosto', 'septiembre', 'octubre', 'noviembre', 'diciembre']
const texto = (m) => `${NOMBRES[Number(m.split('-')[1]) - 1]} de ${m.split('-')[0]}`

async function configurar(p, m, importe, vencimiento, interes) {
  await p.getByLabel('Mes', { exact: true }).fill(m)
  await p.getByLabel('Importe de la cuota').fill(importe)
  await p.getByLabel('Fecha de vencimiento').fill(vencimiento)
  await p.getByLabel('Interés por mora (%)').fill(interes)
  await p.getByRole('button', { name: 'Guardar período' }).click()
  await p.getByText(`Se guardó el período de ${texto(m)}.`).waitFor()
}

;(async () => {
  const conf = await fetch(`${API}/configuracion-inicial`, {
    method: 'POST', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ dni: 20111222, nombre: 'Marta', apellido: 'Admin', email: 'marta@rojasfc.test', contrasena: 'AdminSegura1' }),
  })
  if (!conf.ok) throw new Error(`configuracion inicial: ${conf.status}`)
  const { token } = await conf.json()
  const encabezados = { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` }
  const alta = (dni, nombre, apellido, responsable) => fetch(`${API}/alumnos`, {
    method: 'POST', headers: encabezados,
    body: JSON.stringify({ dni, nombre, apellido, fechaNacimiento: '2015-03-10', direccion: 'San Martín 123',
      responsables: [{ dni: responsable, nombre: 'Laura', apellido, vinculo: 'MADRE', telefono: '3511234567', contrasenaProvisoria: 'provisoria123' }] }),
  })
  for (const r of [await alta(45111222, 'Mateo', 'Gómez', 30111222), await alta(45111223, 'Sofía', 'López', 30111223), await alta(45111224, 'Tomás', 'Ruiz', 30111224)]) {
    if (!r.ok) throw new Error(`alta: ${r.status} ${await r.text()}`)
  }
  await fetch(`${API}/alumnos/45111224/baja`, { method: 'POST', headers: encabezados })
  // Las altas son de hoy: se adelantan para que el mes vencido del ejemplo les corresponda.
  ejecutarSql("UPDATE permanencia SET fecha_alta = '2026-01-01'")

  const navegador = await chromium.launch({ executablePath: NAVEGADOR, headless: true })
  const p = await (await navegador.newContext({ viewport: { width: 1280, height: 900 } })).newPage()
  p.on('console', (m) => m.type() === 'error' && erroresConsola.push(m.text()))
  p.on('pageerror', (e) => erroresConsola.push(String(e)))
  let mensajeDialogo = ''
  p.on('dialog', (d) => { mensajeDialogo = d.message(); d.accept() })

  await p.goto(`${WEB}/cuotas`)
  await p.getByLabel('DNI').fill('20111222')
  await p.getByLabel('Contraseña', { exact: true }).fill('AdminSegura1')
  await p.getByRole('button', { name: 'Ingresar' }).click()
  await p.waitForURL(`${WEB}/cuotas`)
  await p.getByRole('heading', { name: 'Cuotas', exact: true }).waitFor()
  await p.getByText('Todavía no hay períodos configurados.').waitFor()
  await p.getByText('No hay alumnos con deuda vencida.').waitFor()
  caso('Pantalla Cuotas vacía, con avisos de sin períodos y sin morosos', true)
  caso('Por defecto propone el mes actual', (await p.getByLabel('Mes', { exact: true }).inputValue()) === mes(0))
  await captura(p, '30-cuotas-vacia')

  // Vencido: mes pasado.
  const pasado = mes(-1)
  await configurar(p, pasado, '1000', `${pasado}-13`, '10')
  caso('Configurar un período (RF-17, RF-19, RF-20)', await p.getByRole('cell', { name: texto(pasado), exact: true }).isVisible())
  const filaPasado = tarjeta(p, 'Períodos').getByRole('row', { name: new RegExp(texto(pasado)) })
  caso('Muestra importe, vencimiento e interés', (await filaPasado.textContent()).includes('1.000,00') && (await filaPasado.textContent()).includes(`13/${pasado.slice(5)}/${pasado.slice(0, 4)}`) && (await filaPasado.textContent()).includes('10 %'), await filaPasado.textContent())

  // Modificar antes de generar.
  await p.getByRole('button', { name: `Modificar el período de ${texto(pasado)}` }).click()
  caso('Modificar carga los datos del período', (await p.getByLabel('Importe de la cuota').inputValue()) === '1000')
  await p.getByLabel('Importe de la cuota').fill('1500')
  await p.getByRole('button', { name: 'Guardar período' }).click()
  await filaPasado.getByText('1.500,00').waitFor()
  caso('Modificar el período antes de generar (RF-17)', true)

  // Vencimiento anterior al periodo: lo frena el formulario o el backend.
  const r400 = await fetch(`${API}/periodos/${pasado}`, { method: 'PUT', headers: encabezados, body: JSON.stringify({ importe: 1000, fechaVencimiento: '2000-01-01', porcentajeInteres: 10 }) })
  caso('El backend rechaza un vencimiento anterior al período', r400.status === 400, String(r400.status))

  // Futuro: no se puede generar.
  const futuro = mes(2)
  await configurar(p, futuro, '2000', `${futuro}-13`, '5')
  caso('Un mes futuro se configura pero no se puede generar', await p.getByRole('button', { name: `Generar las cuotas de ${texto(futuro)}` }).isDisabled())

  // Generar
  await p.getByRole('button', { name: `Generar las cuotas de ${texto(pasado)}` }).click()
  await p.getByText(`Se generaron 2 cuotas de ${texto(pasado)}.`).waitFor()
  caso('Generar pide confirmación (RNF-13)', mensajeDialogo.includes(`¿Generar las cuotas de ${texto(pasado)}`), mensajeDialogo)
  caso('Genera solo para los alumnos activos: 2 de 3 (RF-18, RN-14)', true)
  await filaPasado.getByRole('cell', { name: '2', exact: true }).waitFor()
  caso('Ya generado, no ofrece modificar ni generar', (await filaPasado.getByRole('button').count()) === 0)
  await captura(p, '31-cuotas-generadas')

  // Morosos
  const morosos = tarjeta(p, 'Alumnos morosos')
  await morosos.getByRole('link', { name: 'Gómez, Mateo' }).waitFor()
  const filaMoroso = await morosos.getByRole('row', { name: /Gómez, Mateo/ }).textContent()
  caso('Morosos con el total con interés: 1500 + 10 % (RF-23)', filaMoroso.includes('1.650,00'), filaMoroso)
  caso('La alumna Sofía también figura; el dado de baja sin cuotas no', (await morosos.getByRole('link', { name: 'López, Sofía' }).isVisible()) && (await morosos.getByRole('link', { name: 'Ruiz, Tomás' }).count()) === 0)

  // RN-22 en pantalla
  await p.getByLabel('Mes', { exact: true }).fill(pasado)
  await p.getByText('Este período ya tiene cuotas generadas').waitFor()
  caso('Un período generado no se puede guardar (RN-22)', await p.getByRole('button', { name: 'Guardar período' }).isDisabled())
  await captura(p, '32-periodo-bloqueado')
  const r409 = await fetch(`${API}/periodos/${pasado}`, { method: 'PUT', headers: encabezados, body: JSON.stringify({ importe: 9, fechaVencimiento: `${pasado}-13`, porcentajeInteres: 10 }) })
  caso('El backend también lo rechaza con 409', r409.status === 409, String(r409.status))

  // Cuotas del alumno
  await morosos.getByRole('link', { name: 'Gómez, Mateo' }).click()
  await p.waitForURL(`${WEB}/alumnos/45111222`)
  const cuotasAlumno = tarjeta(p, 'Cuotas')
  await cuotasAlumno.getByText(texto(pasado)).waitFor()
  const fila = await cuotasAlumno.getByRole('row', { name: new RegExp(texto(pasado)) }).textContent()
  caso('Cuotas del alumno: período, importe, vencimiento, a pagar y estado (RF-21, RF-22)', fila.includes('1.500,00') && fila.includes('1.650,00') && fila.includes('Vencida'), fila)
  caso('El alumno figura como Moroso y sigue Activo (RN-23, RN-24)', (await p.getByText('Moroso', { exact: true }).isVisible()) && (await p.getByText('Activo', { exact: true }).isVisible()))
  await captura(p, '33-cuotas-alumno')

  // Alumno sin cuotas
  await p.goto(`${WEB}/alumnos/45111224`)
  await tarjeta(p, 'Cuotas').getByText('Todavía no hay cuotas.').waitFor()
  caso('Un alumno sin cuotas lo indica', true)

  // Listado de alumnos: filtro de morosidad, ya con datos reales.
  await p.goto(`${WEB}/alumnos`)
  await p.getByLabel('Morosidad').selectOption('true')
  await p.getByRole('button', { name: 'Buscar', exact: true }).click()
  await p.waitForLoadState('networkidle')
  const listado = await tarjeta(p, 'Alumnos').getByRole('link').allTextContents()
  caso('El filtro de morosos del listado muestra a los morosos', JSON.stringify(listado) === JSON.stringify(['Gómez, Mateo', 'López, Sofía']), JSON.stringify(listado))

  // Celular
  const pm = await (await navegador.newContext({ viewport: { width: 390, height: 844 } })).newPage()
  await pm.goto(`${WEB}/cuotas`)
  await pm.getByLabel('DNI').fill('20111222')
  await pm.getByLabel('Contraseña', { exact: true }).fill('AdminSegura1')
  await pm.getByRole('button', { name: 'Ingresar' }).click()
  await pm.waitForURL(`${WEB}/cuotas`)
  await pm.getByRole('link', { name: 'Gómez, Mateo' }).waitFor()
  const a1 = await pm.evaluate(() => document.documentElement.scrollWidth)
  await captura(pm, '34-cuotas-celular')
  await pm.goto(`${WEB}/alumnos/45111222`)
  await tarjeta(pm, 'Cuotas').getByText(texto(pasado)).waitFor()
  const a2 = await pm.evaluate(() => document.documentElement.scrollWidth)
  caso('Celular sin desborde horizontal', a1 <= 390 && a2 <= 390, `cuotas=${a1} alumno=${a2}`)

  // Responsable sin acceso
  const pr = await (await navegador.newContext()).newPage()
  const login = await fetch(`${API}/auth/login`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ dni: 30111222, contrasena: 'provisoria123' }) })
  const tokenResponsable = (await login.json()).token
  const prohibido = await fetch(`${API}/periodos`, { headers: { Authorization: `Bearer ${tokenResponsable}` } })
  caso('Un responsable no accede a los períodos (RF-52)', prohibido.status === 403, String(prohibido.status))
  await pr.close()

  const inesperados = erroresConsola.filter((e) => !/status of (400|401|403|404|409) /.test(e))
  caso('Sin errores de JavaScript en la consola', inesperados.length === 0, JSON.stringify(inesperados))
  await navegador.close()
  console.log(`\n${resultados.filter(Boolean).length}/${resultados.length} casos OK`)
  process.exit(resultados.every(Boolean) ? 0 : 1)
})().catch((e) => { console.error(e); process.exit(1) })
