// Recorrido del circuito de pago completo, con database/datos-demo.sql: cuota, pago,
// recibo, anulacion y nuevo pago, y como se refleja en morosos, Dashboard y Portal.
const { chromium } = require('playwright-core')
const path = require('path')
const { WEB, API, OPCIONES_NAVEGADOR, textoSeguro, CAPTURAS } = require('./base')

const CLAVE = 'Demo12345'
const resultados = []
const erroresConsola = []

function caso(nombre, ok, detalle = '') {
  resultados.push(ok)
  console.log(`${ok ? 'OK   ' : 'FALLA'} ${nombre}${detalle ? `  -> ${textoSeguro(detalle)}` : ''}`)
}
const captura = (p, n) => p.screenshot({ path: path.join(CAPTURAS, `${n}.png`), fullPage: true })
const tarjeta = (p, t) => p.locator(`section:has(> h2:text-is("${t}"))`)

async function entrar(p, dni) {
  await p.goto(`${WEB}/login`)
  await p.getByLabel('DNI').fill(dni)
  await p.getByLabel('Contraseña', { exact: true }).fill(CLAVE)
  await p.getByRole('button', { name: 'Ingresar' }).click()
  await p.waitForLoadState('networkidle')
}

async function verPagos(p, dni) {
  await p.goto(`${WEB}/pagos`)
  await p.getByLabel('DNI').fill(dni)
  await p.getByRole('button', { name: 'Ver pagos' }).click()
  await tarjeta(p, 'Historial de pagos').waitFor()
  await p.waitForLoadState('networkidle')
}

async function api(token, metodo, ruta, cuerpo) {
  return fetch(`${API}${ruta}`, {
    method: metodo,
    headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
    body: cuerpo ? JSON.stringify(cuerpo) : undefined,
  })
}

async function recaudacion(token) {
  return (await (await api(token, 'GET', '/dashboard')).json()).recaudacionDelMes
}

;(async () => {
  const entrada = await fetch(`${API}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ dni: 20111222, contrasena: CLAVE }),
  })
  const { token } = await entrada.json()
  const antes = await recaudacion(token)
  const cuotas = await (await api(token, 'GET', '/alumnos/45111222/cuotas')).json()
  const vencida = cuotas.find((c) => c.estado === 'vencida')
  const mes = vencida.periodo
  const conInteres = vencida.montoAPagar

  const navegador = await chromium.launch({ ...OPCIONES_NAVEGADOR, headless: true })
  const contexto = await navegador.newContext({ viewport: { width: 1280, height: 900 }, acceptDownloads: true })
  const p = await contexto.newPage()
  p.on('console', (m) => m.type() === 'error' && erroresConsola.push(m.text()))
  p.on('pageerror', (e) => erroresConsola.push(String(e)))
  await entrar(p, '20111222')

  // --- Pago de una cuota vencida ---
  await verPagos(p, '45111222')
  await captura(p, '60-pagos')
  caso('Muestra las cuotas y el historial del alumno (RF-22, RF-27)', await tarjeta(p, 'Cuotas del alumno').isVisible())
  await tarjeta(p, 'Cuotas del alumno').getByRole('button', { name: /Registrar el pago de/ }).last().click()
  const registrar = tarjeta(p, 'Registrar pago')
  const texto = await registrar.textContent()
  caso('El importe a pagar incluye el interés (RF-21, RF-25)', /27\.500,00/.test(texto), texto.slice(0, 90))
  await registrar.getByLabel('Medio de pago').selectOption('transferencia')
  await registrar.getByRole('button', { name: 'Registrar pago' }).click()
  await tarjeta(p, 'Historial de pagos').getByRole('link', { name: /N° / }).first().waitFor()
  await p.waitForLoadState('networkidle')
  await captura(p, '61-pago-registrado')

  const c2 = await (await api(token, 'GET', '/alumnos/45111222/cuotas')).json()
  const pagada = c2.find((c) => c.periodo === mes)
  caso('La cuota queda pagada, con su pago asociado (RF-22)', pagada.estado === 'pagada' && pagada.idPago != null, JSON.stringify(pagada))
  const despues = await recaudacion(token)
  caso('La recaudación del mes suma el pago (RF-48)', Number(despues) === Number(antes) + Number(conInteres), `${antes} -> ${despues}`)
  const morosos = await (await api(token, 'GET', '/cuotas/morosos')).json()
  caso('Mateo deja de figurar entre los morosos (RN-23)', !morosos.some((m) => m.dni === 45111222), JSON.stringify(morosos.map((m) => m.dni)))

  // --- Recibo ---
  await tarjeta(p, 'Historial de pagos').getByRole('link', { name: /N° / }).first().click()
  await p.getByRole('heading', { name: /Recibo N° / }).waitFor()
  const recibo = await tarjeta(p, 'Datos del recibo').textContent()
  caso('El recibo trae número, alumno, concepto, importe y medio (RF-29)', /Mateo/.test(recibo) && /45111222/.test(recibo) && /27\.500,00/.test(recibo) && /Transferencia/i.test(recibo), recibo.slice(0, 130))
  await captura(p, '62-recibo')
  const [descarga] = await Promise.all([p.waitForEvent('download'), p.getByRole('button', { name: 'Descargar' }).click()])
  caso('El recibo se descarga (RF-31)', /recibo-\d+/.test(descarga.suggestedFilename()), descarga.suggestedFilename())

  // --- Pagar dos veces: rechazado ---
  const repetido = await api(token, 'POST', '/pagos', { dniAlumno: 45111222, periodo: mes, medioPago: 'efectivo' })
  caso('No se puede pagar dos veces la misma cuota (RN-29)', repetido.status === 409, String(repetido.status))

  // --- Anulacion ---
  await verPagos(p, '45111222')
  const historial = await (await api(token, 'GET', '/alumnos/45111222/pagos')).json()
  const pago = historial.find((h) => h.periodo === mes)
  await tarjeta(p, 'Historial de pagos').getByRole('row', { name: /27\.500,00/ }).getByRole('button', { name: /Anular el pago/ }).click()
  const anular = tarjeta(p, 'Anular pago')
  await anular.getByRole('button', { name: 'Anular pago' }).click()
  caso('El motivo es obligatorio (RN-31)', await anular.getByLabel('Motivo de la anulación').evaluate((e) => e.validity.valueMissing))
  await anular.getByLabel('Motivo de la anulación').fill('Se cargó en el medio equivocado')
  await anular.getByRole('button', { name: 'Anular pago' }).click()
  await tarjeta(p, 'Historial de pagos').getByText('Anulado').first().waitFor()
  await p.waitForLoadState('networkidle')
  await captura(p, '63-pago-anulado')

  const c3 = await (await api(token, 'GET', '/alumnos/45111222/cuotas')).json()
  caso('La cuota vuelve a estar vencida (RN-32)', c3.find((c) => c.periodo === mes).estado === 'vencida')
  caso('La recaudación no cuenta el pago anulado (RN-32)', Number(await recaudacion(token)) === Number(antes))
  const rec = await (await api(token, 'GET', `/recibos/${pago.numeroRecibo}`)).json()
  caso('El recibo se conserva como anulado (RF-33)', rec.anulado === true)
  const dosVeces = await api(token, 'POST', `/pagos/${pago.idPago}/anulacion`, { motivo: 'otra vez' })
  caso('No se puede anular dos veces (RN-30)', dosVeces.status === 409, String(dosVeces.status))

  // --- Nuevo pago despues de anular ---
  const nuevo = await api(token, 'POST', '/pagos', { dniAlumno: 45111222, periodo: mes, medioPago: 'efectivo' })
  const pagoNuevo = await nuevo.json()
  caso('Después de anular se puede volver a pagar, con otro recibo (RN-29)', nuevo.status === 201 && pagoNuevo.numeroRecibo !== pago.numeroRecibo, `${nuevo.status} recibo ${pagoNuevo.numeroRecibo}`)

  // --- Datos invalidos ---
  const medio = await api(token, 'POST', '/pagos', { dniAlumno: 45111222, periodo: mes, medioPago: 'cheque' })
  caso('Medio de pago inválido (RN-27)', medio.status === 400, String(medio.status))
  const sinCuota = await api(token, 'POST', '/pagos', { dniAlumno: 45111222, periodo: '2000-01', medioPago: 'efectivo' })
  caso('Período sin cuota', sinCuota.status === 404, String(sinCuota.status))
  const largo = await api(token, 'POST', `/pagos/${pagoNuevo.idPago}/anulacion`, { motivo: 'x'.repeat(201) })
  caso('Motivo demasiado largo', largo.status === 400, String(largo.status))

  // --- Permisos y portal ---
  const responsable = await fetch(`${API}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ dni: 30111222, contrasena: CLAVE }),
  })
  const tokenR = (await responsable.json()).token
  const prohibido = await api(tokenR, 'POST', '/pagos', { dniAlumno: 45111222, periodo: mes, medioPago: 'efectivo' })
  caso('Un responsable no registra pagos (RF-52)', prohibido.status === 403, String(prohibido.status))
  const reciboAjeno = await api(tokenR, 'GET', `/recibos/${pagoNuevo.numeroRecibo}`)
  caso('Un responsable no consulta recibos por la ruta del administrador', reciboAjeno.status === 403, String(reciboAjeno.status))

  const r = await (await navegador.newContext({ viewport: { width: 1280, height: 900 } })).newPage()
  await entrar(r, '30111222')
  await r.waitForURL(`${WEB}/portal`)
  const mateo = tarjeta(r, 'Mateo Gómez')
  await mateo.waitFor()
  caso('El portal muestra el pago anulado y el nuevo', (await mateo.getByRole('cell', { name: 'Anulado' }).count()) === 2 && (await mateo.getByRole('cell', { name: 'Vigente' }).count()) >= 2)

  // --- Celular ---
  const m = await (await navegador.newContext({ viewport: { width: 390, height: 844 } })).newPage()
  await entrar(m, '20111222')
  await verPagos(m, '45111222')
  const ancho = await m.evaluate(() => document.documentElement.scrollWidth)
  await captura(m, '64-pagos-celular')
  caso('Celular sin desborde horizontal', ancho <= 390, `ancho=${ancho}`)

  const inesperados = erroresConsola.filter((e) => !/status of (400|401|403|404|409) /.test(e))
  caso('Sin errores de JavaScript en la consola', inesperados.length === 0, JSON.stringify(inesperados))
  await navegador.close()
  console.log(`\n${resultados.filter(Boolean).length}/${resultados.length} casos OK`)
  process.exit(resultados.every(Boolean) ? 0 : 1)
})().catch((e) => { console.error(e); process.exit(1) })
