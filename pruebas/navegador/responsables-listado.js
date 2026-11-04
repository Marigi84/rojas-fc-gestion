// Recorrido de responsables y del listado. Corre despues de alumnos.js, sobre la misma base.
const { chromium } = require('playwright-core')
const path = require('path')
const { WEB, API, OPCIONES_NAVEGADOR, textoSeguro, CAPTURAS } = require('./base')

const resultados = []
const erroresConsola = []

function caso(nombre, ok, detalle = '') {
  resultados.push(ok)
  console.log(`${ok ? 'OK   ' : 'FALLA'} ${nombre}${detalle ? `  -> ${textoSeguro(detalle)}` : ''}`)
}
const captura = (p, n) => p.screenshot({ path: path.join(CAPTURAS, `${n}.png`), fullPage: true })
const tarjeta = (p, t) => p.locator(`section:has(> h2:text-is("${t}"))`)

async function api(token, metodo, ruta, cuerpo) {
  const r = await fetch(`${API}${ruta}`, {
    method: metodo,
    headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
    body: cuerpo ? JSON.stringify(cuerpo) : undefined,
  })
  return r
}

async function filasListado(p) {
  await p.waitForLoadState('networkidle')
  await p.getByText('Cargando alumnos…').waitFor({ state: 'detached' }).catch(() => {})
  return tarjeta(p, 'Alumnos').getByRole('link').allTextContents()
}

;(async () => {
  const login = await fetch(`${API}/auth/login`, {
    method: 'POST', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ dni: 20111222, contrasena: 'AdminSegura1' }),
  })
  const { token } = await login.json()
  // Un tercer alumno, con otro responsable, para asociarle a Laura.
  const alta = await api(token, 'POST', '/alumnos', {
    dni: 46555666, nombre: 'Lucía', apellido: 'Pérez', fechaNacimiento: '2016-05-20', direccion: 'Mitre 50',
    responsables: [{ dni: 31444555, nombre: 'Jorge', apellido: 'Pérez', vinculo: 'PADRE', telefono: '3517777777', contrasenaProvisoria: 'provisoria123' }],
  })
  if (!alta.ok) throw new Error(`alta de prueba: ${alta.status} ${await alta.text()}`)

  const navegador = await chromium.launch({ ...OPCIONES_NAVEGADOR, headless: true })
  const p = await (await navegador.newContext({ viewport: { width: 1280, height: 900 } })).newPage()
  p.on('console', (m) => m.type() === 'error' && erroresConsola.push(m.text()))
  p.on('pageerror', (e) => erroresConsola.push(String(e)))
  p.on('dialog', (d) => d.accept())

  await p.goto(`${WEB}/alumnos`)
  await p.getByLabel('DNI').fill('20111222')
  await p.getByLabel('Contraseña', { exact: true }).fill('AdminSegura1')
  await p.getByRole('button', { name: 'Ingresar' }).click()
  await p.waitForURL(`${WEB}/alumnos`)

  // ---------- #59 listado ----------
  let filas = await filasListado(p)
  caso('Listado: por defecto solo activos, ordenados por apellido', JSON.stringify(filas) === JSON.stringify(['Gómez, Mateo', 'Pérez, Lucía']), JSON.stringify(filas))
  await captura(p, '20-listado')
  await p.getByLabel('Estado').selectOption('todos')
  await p.getByLabel('Buscar', { exact: true }).fill('sofia')
  await p.getByRole('button', { name: 'Buscar', exact: true }).click()
  filas = await filasListado(p)
  caso('Listado: "Todos" + búsqueda sin tilde encuentra a la alumna dada de baja', JSON.stringify(filas) === JSON.stringify(['Gómez, Sofía']), JSON.stringify(filas))
  await p.getByLabel('Buscar', { exact: true }).fill('%')
  await p.getByRole('button', { name: 'Buscar', exact: true }).click()
  await p.getByText('No hay alumnos para esos filtros.').waitFor()
  caso('Listado: "%" se busca como texto, no como comodín', true)
  await p.getByLabel('Buscar', { exact: true }).fill('')
  await p.getByLabel('Estado').selectOption('activo')
  await p.getByLabel('Categoría (año de nacimiento)').fill('2016')
  await p.getByRole('button', { name: 'Buscar', exact: true }).click()
  filas = await filasListado(p)
  caso('Listado: filtro por categoría', JSON.stringify(filas) === JSON.stringify(['Pérez, Lucía']), JSON.stringify(filas))

  // Baja desde el detalle y vuelta al listado por el menú (sin recargar).
  await p.goto(`${WEB}/alumnos/46555666`)
  await p.getByRole('button', { name: 'Dar de baja' }).click()
  await p.getByText('Inactivo', { exact: true }).waitFor()
  await p.getByRole('link', { name: 'Alumnos', exact: true }).first().click()
  filas = await filasListado(p)
  caso('Listado: después de una baja, el alumno ya no figura entre los activos', !filas.includes('Pérez, Lucía'), JSON.stringify(filas))
  await p.goto(`${WEB}/alumnos/46555666`)
  await p.getByRole('button', { name: 'Reactivar' }).click()
  await p.getByText('Activo', { exact: true }).waitFor()

  // ---------- #58 responsables ----------
  // Agregar un responsable nuevo a Mateo (RF-06).
  await p.goto(`${WEB}/alumnos/45111222`)
  await p.getByRole('button', { name: 'Agregar responsable' }).click()
  await p.waitForURL(`${WEB}/alumnos/45111222/responsables/nuevo`)
  const f = p.locator('fieldset').nth(0)
  await f.getByLabel('DNI').fill('30222333')
  await f.getByLabel('DNI').blur()
  await f.getByLabel('Vínculo con el alumno').selectOption('PADRE')
  await f.getByLabel('Nombre').fill('Pedro')
  await f.getByLabel('Apellido').fill('Gómez')
  await f.getByLabel('Teléfono').fill('3512222222')
  await f.getByLabel('Contraseña provisoria').fill('provisoria123')
  await captura(p, '21-agregar-responsable')
  await p.getByRole('button', { name: 'Agregar responsable' }).click()
  await p.waitForURL(`${WEB}/alumnos/45111222`)
  await p.getByRole('link', { name: 'Gómez, Pedro' }).waitFor()
  caso('Agregar responsable a un alumno existente (RF-06)', await p.getByRole('link', { name: 'Gómez, Laura' }).isVisible())

  // Agregar el mismo responsable otra vez: 409.
  await p.getByRole('button', { name: 'Agregar responsable' }).click()
  await f.getByLabel('DNI').fill('30222333')
  await f.getByLabel('DNI').blur()
  await p.getByText('Ya es responsable de otro alumno').waitFor()
  await p.getByRole('button', { name: 'Agregar responsable' }).click()
  await p.getByRole('alert').waitFor()
  caso('Agregar un responsable que ya está asociado muestra el error', (await p.getByRole('alert').textContent()).includes('ya está asociado'), await p.getByRole('alert').textContent())

  // Detalle y edición de Laura (RF-07, RF-08).
  await p.goto(`${WEB}/alumnos/45111222`)
  await p.getByRole('link', { name: 'Gómez, Laura' }).click()
  await p.waitForURL(`${WEB}/responsables/30111222`)
  await tarjeta(p, 'Alumnos a cargo').getByRole('link', { name: 'Gómez, Mateo' }).waitFor()
  filas = await tarjeta(p, 'Alumnos a cargo').getByRole('link').allTextContents()
  caso('Detalle del responsable con sus alumnos y estado (RF-07)', filas.length === 2 && (await p.getByRole('cell', { name: 'Inactivo' }).count()) === 1, JSON.stringify(filas))
  await captura(p, '22-detalle-responsable')
  await p.getByRole('button', { name: 'Modificar datos' }).click()
  await p.getByLabel('Teléfono').fill('3510000000')
  await p.getByRole('button', { name: 'Guardar cambios' }).click()
  await p.waitForURL(`${WEB}/responsables/30111222`)
  caso('Modificar el teléfono del responsable (RF-08)', await p.getByText('3510000000').isVisible())

  // Asociar a Laura con Lucía (RF-09) y repetirlo (409).
  await p.getByRole('button', { name: 'Asociar a otro alumno' }).click()
  await p.getByLabel('DNI del alumno').fill('46555666')
  await p.getByLabel('Vínculo con el alumno').selectOption('TUTOR')
  await p.getByRole('button', { name: 'Asociar alumno' }).click()
  await p.waitForURL(`${WEB}/responsables/30111222`)
  await p.getByRole('link', { name: 'Pérez, Lucía' }).waitFor()
  caso('Asociar el responsable a otro alumno (RF-09, RN-11)', true)
  await p.getByRole('button', { name: 'Asociar a otro alumno' }).click()
  await p.getByLabel('DNI del alumno').fill('46555666')
  await p.getByRole('button', { name: 'Asociar alumno' }).click()
  await p.getByRole('alert').waitFor()
  caso('Asociar dos veces al mismo alumno muestra el error', (await p.getByRole('alert').textContent()).includes('ya está asociado'))
  // El detalle de Lucía, abierto antes, muestra a Laura sin recargar.
  await p.goto(`${WEB}/alumnos/46555666`)
  const vista = await p.getByRole('link', { name: 'Gómez, Laura' }).waitFor({ timeout: 5000 }).then(() => true, () => false)
  caso('El detalle del alumno muestra al responsable asociado', vista)

  // Desvincular desde el detalle del responsable (RF-10).
  await p.goto(`${WEB}/responsables/30111222`)
  await tarjeta(p, 'Alumnos a cargo').getByRole('row', { name: /Pérez, Lucía/ }).getByRole('button', { name: 'Desvincular' }).click()
  await p.getByRole('link', { name: 'Pérez, Lucía' }).waitFor({ state: 'detached' })
  caso('Desvincular desde el detalle del responsable', (await tarjeta(p, 'Alumnos a cargo').getByRole('link').count()) === 2)

  // Desvincular a Pedro de Mateo: se queda sin alumnos y se borra la persona (RN-02).
  await p.goto(`${WEB}/alumnos/45111222`)
  await p.getByRole('button', { name: 'Desvincular a Pedro Gómez del alumno' }).click()
  await p.getByRole('link', { name: 'Gómez, Pedro' }).waitFor({ state: 'detached' })
  const persona = await api(token, 'GET', '/personas/30222333')
  caso('Al quedar sin alumnos se eliminan el responsable y la persona (RN-02)', persona.status === 404, `GET /personas -> ${persona.status}`)
  caso('Con un solo responsable, "Desvincular" queda deshabilitado (RN-05)',
    await p.getByRole('button', { name: 'Desvincular a Laura Gómez del alumno' }).isDisabled())
  await captura(p, '23-un-responsable')

  // RN-05 en el backend, aunque el botón esté deshabilitado.
  const ultimo = await api(token, 'DELETE', '/responsables/30111222/alumnos/45111222')
  caso('El backend rechaza desvincular al último responsable (RN-05)', ultimo.status === 409, `${ultimo.status} ${await ultimo.text()}`)

  // Responsables desde el menú.
  await p.getByRole('link', { name: 'Responsables', exact: true }).first().click()
  await p.getByLabel('DNI').fill('30111222')
  await p.getByRole('button', { name: 'Ver responsable' }).click()
  await p.waitForURL(`${WEB}/responsables/30111222`)
  caso('Menú Responsables: consultar por DNI', true)

  // Celular
  const pm = await (await navegador.newContext({ viewport: { width: 390, height: 844 } })).newPage()
  await pm.goto(`${WEB}/alumnos`)
  await pm.getByLabel('DNI').fill('20111222')
  await pm.getByLabel('Contraseña', { exact: true }).fill('AdminSegura1')
  await pm.getByRole('button', { name: 'Ingresar' }).click()
  await pm.waitForURL(`${WEB}/alumnos`)
  await filasListado(pm)
  const a1 = await pm.evaluate(() => document.documentElement.scrollWidth)
  await captura(pm, '24-listado-celular')
  await pm.goto(`${WEB}/responsables/30111222`)
  await pm.getByText('Alumnos a cargo').waitFor()
  const a2 = await pm.evaluate(() => document.documentElement.scrollWidth)
  await captura(pm, '25-responsable-celular')
  caso('Celular: listado y detalle del responsable sin desborde horizontal', a1 <= 390 && a2 <= 390, `listado=${a1} responsable=${a2}`)

  caso('Sin errores de JavaScript en la consola', erroresConsola.every((e) => /status of (401|404|409) /.test(e)), JSON.stringify(erroresConsola.filter((e) => !/status of (401|404|409) /.test(e))))
  await navegador.close()
  console.log(`\n${resultados.filter(Boolean).length}/${resultados.length} casos OK`)
  process.exit(resultados.every(Boolean) ? 0 : 1)
})().catch((e) => { console.error(e); process.exit(1) })
