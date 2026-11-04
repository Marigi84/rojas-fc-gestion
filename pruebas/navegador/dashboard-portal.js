// Recorrido del Dashboard y del Portal del Responsable, con database/datos-demo.sql.
const { chromium } = require('playwright-core')
const path = require('path')
const { WEB, API, NAVEGADOR, CAPTURAS } = require('./base')

const CLAVE = 'Demo12345'
const resultados = []
const erroresConsola = []

function caso(nombre, ok, detalle = '') {
  resultados.push(ok)
  console.log(`${ok ? 'OK   ' : 'FALLA'} ${nombre}${detalle ? `  -> ${detalle}` : ''}`)
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

function nuevaPagina(contexto) {
  return contexto.newPage().then((p) => {
    p.on('console', (m) => m.type() === 'error' && erroresConsola.push(m.text()))
    p.on('pageerror', (e) => erroresConsola.push(String(e)))
    return p
  })
}

;(async () => {
  const navegador = await chromium.launch({ executablePath: NAVEGADOR, headless: true })
  const contexto = await navegador.newContext({ viewport: { width: 1280, height: 900 } })

  // --- Dashboard ---
  const p = await nuevaPagina(contexto)
  await entrar(p, '20111222')
  await p.waitForURL(`${WEB}/`)
  caso('El Administrador entra al Dashboard', await p.getByRole('heading', { name: 'Dashboard' }).isVisible())
  await tarjeta(p, 'Alumnos activos').getByText('7', { exact: true }).waitFor()
  caso('Muestra los alumnos activos: 7 de 8, sin el dado de baja (RF-45)', true)
  const categorias = await tarjeta(p, 'Alumnos activos por categoría').getByRole('row').allTextContents()
  caso('Muestra los alumnos por categoría (RF-46)', JSON.stringify(categorias.slice(1)) === JSON.stringify(['20172', '20162', '20151', '20142']), JSON.stringify(categorias))
  const morosos = await tarjeta(p, 'Alumnos morosos').locator('p').first().textContent()
  caso('Informa si hay alumnos morosos (RF-47)', Number(morosos) >= 3, `morosos=${morosos}`)
  const recaudacion = await tarjeta(p, 'Recaudación del mes').locator('p').first().textContent()
  caso('Muestra la recaudación del mes: dos pagos de $ 25.000 (RF-48)', recaudacion.includes('50.000,00'), recaudacion)
  await captura(p, '50-dashboard')
  await tarjeta(p, 'Alumnos morosos').getByRole('link', { name: 'Ver el listado' }).click()
  await p.waitForURL(`${WEB}/cuotas`)
  await tarjeta(p, 'Alumnos morosos').getByRole('link', { name: 'Gómez, Mateo' }).waitFor()
  caso('Desde el Dashboard se llega al listado de morosos (RF-47)', true)
  const fila = await tarjeta(p, 'Alumnos morosos').getByRole('row', { name: /Romero, Julieta/ }).textContent()
  caso('Julieta debe dos meses con interés (RF-23)', fila.includes('55.000,00') || fila.includes('80.000,00') || fila.includes('82.500,00'), fila)
  await captura(p, '51-morosos-demo')

  // El detalle de una alumna con pago tardío y otra con pago anulado.
  await p.goto(`${WEB}/alumnos/46555666`)
  await tarjeta(p, 'Cuotas').getByText('Vencida').first().waitFor()
  caso('Un pago anulado deja la cuota vencida otra vez (RN-32)', true)
  await captura(p, '52-detalle-pago-anulado')
  await p.getByRole('button', { name: 'Cerrar sesión' }).click()
  await p.waitForURL(`${WEB}/login`)

  // --- Portal ---
  const r = await nuevaPagina(contexto)
  await entrar(r, '30111222')
  await r.waitForURL(`${WEB}/portal`)
  caso('La responsable entra a su portal', await r.getByRole('heading', { name: 'Portal del Responsable' }).isVisible())
  caso('En el menú solo ve el portal (RF-52)', JSON.stringify(await r.locator('nav a').allTextContents()) === '["Portal del Responsable"]')
  await r.getByRole('heading', { name: 'Mateo Gómez' }).waitFor()
  caso('Ve sus dos alumnos (RF-12)', await r.getByRole('heading', { name: 'Sofía Gómez' }).isVisible())
  caso('No ve a los alumnos de otra familia (RNF-03)', (await r.getByRole('heading', { name: 'Lucía Pérez' }).count()) === 0)
  const mateo = tarjeta(r, 'Mateo Gómez')
  caso('Mateo figura con deuda vencida', await mateo.getByText('Con deuda vencida').isVisible())
  caso('Se ven sus cuotas (RF-13)', (await mateo.getByText('Vencida', { exact: true }).count()) >= 1 && (await mateo.getByText('Pagada', { exact: true }).count()) === 1)
  const sofia = tarjeta(r, 'Sofía Gómez')
  caso('Se ve el historial de pagos, con el pago con interés (RF-13)', await sofia.getByRole('cell', { name: '$ 27.500,00' }).first().isVisible())
  caso('Se ven los recibos (RF-14)', (await sofia.locator('h3:has-text("Recibos") + div').getByRole('row').count()) === 4)
  await captura(r, '53-portal-responsable')

  // Otro responsable: recibo anulado.
  await r.getByRole('button', { name: 'Cerrar sesión' }).click()
  await r.waitForURL(`${WEB}/login`)
  await entrar(r, '30111223')
  await r.waitForURL(`${WEB}/portal`)
  const lucia = tarjeta(r, 'Lucía Pérez')
  await lucia.waitFor()
  caso('Un pago y su recibo anulados figuran como anulados (RF-33)', (await lucia.getByRole('cell', { name: 'Anulado' }).count()) === 2)
  caso('Otro responsable ve solo los suyos', (await r.getByRole('heading', { name: 'Mateo Gómez' }).count()) === 0)
  await captura(r, '54-portal-recibo-anulado')

  // Permisos con la API.
  const login = await fetch(`${API}/auth/login`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ dni: 30111223, contrasena: CLAVE }) })
  const { token } = await login.json()
  const prohibido = await fetch(`${API}/dashboard`, { headers: { Authorization: `Bearer ${token}` } })
  caso('Un responsable no accede al Dashboard (RF-52)', prohibido.status === 403, String(prohibido.status))

  // Celular
  const movil = await navegador.newContext({ viewport: { width: 390, height: 844 } })
  const m = await nuevaPagina(movil)
  await entrar(m, '30111222')
  await m.waitForURL(`${WEB}/portal`)
  await m.getByRole('heading', { name: 'Mateo Gómez' }).waitFor()
  const a1 = await m.evaluate(() => document.documentElement.scrollWidth)
  await captura(m, '55-portal-celular')
  const m2 = await nuevaPagina(await navegador.newContext({ viewport: { width: 390, height: 844 } }))
  await entrar(m2, '20111222')
  await m2.getByRole('heading', { name: 'Dashboard' }).waitFor()
  await m2.getByText('Alumnos activos por categoría').waitFor()
  const a2 = await m2.evaluate(() => document.documentElement.scrollWidth)
  await captura(m2, '56-dashboard-celular')
  caso('Celular sin desborde horizontal', a1 <= 390 && a2 <= 390, `portal=${a1} dashboard=${a2}`)

  const inesperados = erroresConsola.filter((e) => !/status of (401|403|404|409) /.test(e))
  caso('Sin errores de JavaScript en la consola', inesperados.length === 0, JSON.stringify(inesperados))
  await navegador.close()
  console.log(`\n${resultados.filter(Boolean).length}/${resultados.length} casos OK`)
  process.exit(resultados.every(Boolean) ? 0 : 1)
})().catch((e) => { console.error(e); process.exit(1) })
