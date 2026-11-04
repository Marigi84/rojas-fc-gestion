// Recorrido de la gestion de alumnos, entrando con el login. Base recien creada.
const { chromium } = require('playwright-core')
const path = require('path')
const { WEB, API, NAVEGADOR, CAPTURAS } = require('./base')

const resultados = []
const erroresConsola = []

function caso(nombre, ok, detalle = '') {
  resultados.push(ok)
  console.log(`${ok ? 'OK   ' : 'FALLA'} ${nombre}${detalle ? `  -> ${detalle}` : ''}`)
}

async function captura(pagina, nombre) {
  await pagina.screenshot({ path: path.join(CAPTURAS, `${nombre}.png`), fullPage: true })
}

// Ingreso por la pantalla de login (#38): DNI y contraseña.
async function ingresar(pagina, dni, contrasena) {
  await pagina.getByLabel('DNI').fill(dni)
  await pagina.getByLabel('Contraseña', { exact: true }).fill(contrasena)
  await pagina.getByRole('button', { name: 'Ingresar' }).click()
}

const tarjeta = (pagina, titulo) =>
  pagina.locator(`section:has(> h2:text-is("${titulo}"))`)

;(async () => {
  const conf = await fetch(`${API}/configuracion-inicial`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ dni: 20111222, nombre: 'Marta', apellido: 'Admin', email: 'marta@rojasfc.test', contrasena: 'AdminSegura1' }),
  })
  if (!conf.ok) throw new Error(`configuración inicial: ${conf.status}`)

  const navegador = await chromium.launch({
    executablePath: NAVEGADOR,
    headless: true,
  })
  const contexto = await navegador.newContext({ viewport: { width: 1280, height: 900 } })
  const pagina = await contexto.newPage()
  pagina.on('console', (m) => m.type() === 'error' && erroresConsola.push(m.text()))
  pagina.on('pageerror', (e) => erroresConsola.push(String(e)))

  // --- Ingreso real: pedir Alumnos sin sesión lleva al login y vuelve a Alumnos ---
  await pagina.goto(`${WEB}/alumnos`)
  await pagina.waitForURL(`${WEB}/login`)
  caso('Sin sesión, Alumnos lleva a la pantalla de ingreso', true)
  await ingresar(pagina, '20111222', 'AdminSegura1')
  await pagina.waitForURL(`${WEB}/alumnos`)
  await pagina.getByRole('button', { name: 'Nuevo alumno' }).waitFor()
  caso('Pantalla Alumnos con botón "Nuevo alumno"', await pagina.getByRole('button', { name: 'Nuevo alumno' }).isVisible())
  await captura(pagina, '01-alumnos')

  // --- Alta con responsable nuevo ---
  await pagina.getByRole('button', { name: 'Nuevo alumno' }).click()
  await pagina.waitForURL(`${WEB}/alumnos/nuevo`)
  const datos = tarjeta(pagina, 'Datos del alumno')
  await datos.getByLabel('DNI').fill('45111222')
  await datos.getByLabel('DNI').blur()
  await datos.getByLabel('Nombre').fill('Mateo')
  await datos.getByLabel('Apellido').fill('Gómez')
  await datos.getByLabel('Fecha de nacimiento').fill('2015-03-10')
  await datos.getByLabel('Dirección').fill('San Martín 123')
  const r1 = pagina.locator('fieldset').nth(0)
  await r1.getByLabel('DNI').fill('30111222')
  await r1.getByLabel('DNI').blur()
  await r1.getByLabel('Vínculo con el alumno').selectOption('MADRE')
  await r1.getByLabel('Nombre').fill('Laura')
  await r1.getByLabel('Apellido').fill('Gómez')
  await r1.getByLabel('Teléfono').fill('3511234567')
  await r1.getByLabel('Contraseña provisoria').fill('provisoria123')
  await captura(pagina, '02-alta-formulario')
  await pagina.getByRole('button', { name: 'Registrar alumno' }).click()
  await pagina.waitForURL(`${WEB}/alumnos/45111222`)
  await pagina.getByRole('heading', { name: 'Mateo Gómez' }).waitFor()
  caso('Alta -> redirige al detalle con nombre, Activo y responsable',
    (await pagina.getByText('Activo', { exact: true }).isVisible()) &&
    (await pagina.getByRole('cell', { name: 'Gómez, Laura' }).isVisible()))
  caso('Detalle muestra categoría y fecha formateada',
    (await tarjeta(pagina, 'Datos').getByText('2015', { exact: true }).isVisible()) &&
    (await pagina.getByText('10/03/2015').isVisible()))
  await captura(pagina, '03-detalle')

  // --- Baja con confirmación (RNF-13) ---
  let mensajeDialogo = ''
  pagina.once('dialog', (d) => { mensajeDialogo = d.message(); d.accept() })
  await pagina.getByRole('button', { name: 'Dar de baja' }).click()
  await pagina.getByText('Inactivo', { exact: true }).waitFor()
  caso('Baja pide confirmación y pasa a Inactivo', mensajeDialogo.includes('¿Dar de baja a Mateo Gómez?'), mensajeDialogo)
  caso('Historial muestra la baja', !(await pagina.getByRole('cell', { name: 'Vigente' }).isVisible()))
  await captura(pagina, '04-baja')

  // --- Reactivación el mismo día: deshace la baja ---
  pagina.once('dialog', (d) => d.accept())
  await pagina.getByRole('button', { name: 'Reactivar' }).click()
  await pagina.getByText('Activo', { exact: true }).waitFor()
  caso('Reactivar el mismo día deshace la baja (activo, una sola permanencia vigente)',
    (await pagina.getByRole('cell', { name: 'Vigente' }).count()) === 1 &&
    (await tarjeta(pagina, 'Historial en la escuela').getByRole('row').count()) === 2)
  await captura(pagina, '05-reactivar-mismo-dia')

  // --- Modificación ---
  await pagina.getByRole('button', { name: 'Modificar datos' }).click()
  await pagina.waitForURL(`${WEB}/alumnos/45111222/editar`)
  caso('Edición precarga los datos', (await pagina.getByLabel('Dirección').inputValue()) === 'San Martín 123')
  await pagina.getByLabel('Dirección').fill('Belgrano 456')
  await captura(pagina, '06-edicion')
  await pagina.getByRole('button', { name: 'Guardar cambios' }).click()
  await pagina.waitForURL(`${WEB}/alumnos/45111222`)
  caso('Guardar vuelve al detalle con el dato nuevo', await pagina.getByText('Belgrano 456').isVisible())

  // --- Alta reutilizando un responsable existente (RN-01) ---
  await pagina.goto(`${WEB}/alumnos/nuevo`)
  await tarjeta(pagina, 'Datos del alumno').getByLabel('DNI').fill('45333444')
  const r2 = pagina.locator('fieldset').nth(0)
  await r2.getByLabel('DNI').fill('30111222')
  await r2.getByLabel('DNI').blur()
  await pagina.getByText('Ya es responsable de otro alumno').waitFor()
  caso('DNI de responsable existente: completa el nombre, lo bloquea y oculta teléfono y contraseña',
    (await r2.getByLabel('Nombre').inputValue()) === 'Laura' &&
    (await r2.getByLabel('Nombre').getAttribute('readonly')) !== null &&
    (await r2.getByLabel('Teléfono').count()) === 0)
  await captura(pagina, '07-responsable-existente')
  await r2.getByLabel('DNI').fill('30999000')
  caso('Cambiar el DNI borra el nombre que venía de otra persona y lo desbloquea',
    (await r2.getByLabel('Nombre').inputValue()) === '' &&
    (await r2.getByLabel('Nombre').getAttribute('readonly')) === null &&
    (await r2.getByLabel('Teléfono').count()) === 1)
  // Caso de Silvia: teléfono y contraseña de una persona nueva no deben
  // reaparecer después de pasar por un responsable existente.
  await r2.getByLabel('Teléfono').fill('3519999999')
  await r2.getByLabel('Contraseña provisoria').fill('clave12345')
  await r2.getByLabel('DNI').fill('30111222')
  await r2.getByLabel('DNI').blur()
  await pagina.getByText('Ya es responsable de otro alumno').waitFor()
  await r2.getByLabel('DNI').fill('30888000')
  caso('Pasar de un responsable existente a uno nuevo no arrastra teléfono ni contraseña',
    (await r2.getByLabel('Teléfono').inputValue()) === '' &&
    (await r2.getByLabel('Contraseña provisoria').inputValue()) === '')
  await r2.getByLabel('DNI').fill('30111222')
  await r2.getByLabel('DNI').blur()
  await pagina.getByText('Ya es responsable de otro alumno').waitFor()
  const d2 = tarjeta(pagina, 'Datos del alumno')
  await d2.getByLabel('Nombre').fill('Sofía')
  await d2.getByLabel('Apellido').fill('Gómez')
  await d2.getByLabel('Fecha de nacimiento').fill('2016-07-01')
  await d2.getByLabel('Dirección').fill('San Martín 123')
  await r2.getByLabel('Vínculo con el alumno').selectOption('MADRE')
  await pagina.getByRole('button', { name: 'Registrar alumno' }).click()
  await pagina.waitForURL(`${WEB}/alumnos/45333444`)
  caso('Alta con responsable existente -> detalle con Laura', await pagina.getByRole('cell', { name: 'Gómez, Laura' }).isVisible())

  // --- Alumno ya registrado ---
  await pagina.goto(`${WEB}/alumnos/nuevo`)
  const d3 = tarjeta(pagina, 'Datos del alumno')
  await d3.getByLabel('DNI').fill('45333444')
  await d3.getByLabel('DNI').blur()
  await pagina.getByText('Ya hay un alumno con el DNI 45333444').waitFor()
  caso('DNI de alumno existente: avisa, ofrece verlo y desactiva Registrar',
    await pagina.getByRole('button', { name: 'Registrar alumno' }).isDisabled())
  await captura(pagina, '08-alumno-existente')

  // --- Caso de Silvia: el aviso de "Alumno inactivo" no sobrevive a un cambio de DNI ---
  await pagina.goto(`${WEB}/alumnos/45333444`)
  pagina.once('dialog', (d) => d.accept())
  await pagina.getByRole('button', { name: 'Dar de baja' }).click()
  await pagina.getByText('Inactivo', { exact: true }).waitFor()
  await pagina.goto(`${WEB}/alumnos/nuevo`)
  const d4 = tarjeta(pagina, 'Datos del alumno')
  await d4.getByLabel('DNI').fill('45444555')
  await d4.getByLabel('Nombre').fill('Tomás')
  await d4.getByLabel('Apellido').fill('Gómez')
  await d4.getByLabel('Fecha de nacimiento').fill('2017-01-15')
  await d4.getByLabel('Dirección').fill('San Martín 123')
  const r4 = pagina.locator('fieldset').nth(0)
  await r4.getByLabel('DNI').fill('30111222')
  await r4.getByLabel('DNI').blur()
  await pagina.getByText('Ya es responsable de otro alumno').waitFor()
  // Enter en el DNI: se envía sin pasar por la búsqueda al salir del campo.
  await d4.getByLabel('DNI').fill('45333444')
  await d4.getByLabel('DNI').press('Enter')
  await pagina.getByRole('button', { name: 'Reactivar alumno' }).waitFor()
  caso('Alta de un alumno dado de baja ofrece reactivarlo', true)
  await captura(pagina, '09-alumno-inactivo')
  await d4.getByLabel('DNI').fill('45444556')
  caso('Al cambiar el DNI desaparece el aviso y el botón Reactivar del DNI anterior',
    (await pagina.getByRole('button', { name: 'Reactivar alumno' }).count()) === 0)

  // --- Consultar por DNI desde el listado de Alumnos (#41) ---
  await pagina.goto(`${WEB}/alumnos`)
  await pagina.getByLabel('Buscar', { exact: true }).fill('45111222')
  await pagina.getByRole('button', { name: 'Buscar', exact: true }).click()
  await pagina.getByRole('link', { name: 'Gómez, Mateo' }).click()
  await pagina.waitForURL(`${WEB}/alumnos/45111222`)
  await pagina.getByRole('heading', { name: 'Mateo Gómez' }).waitFor()
  caso('Buscar por DNI en el listado y abrir el detalle', await pagina.getByRole('heading', { name: 'Mateo Gómez' }).isVisible())

  // --- Inexistente y sin sesión ---
  await pagina.goto(`${WEB}/alumnos/99999999`)
  await pagina.getByRole('alert').waitFor()
  caso('Alumno inexistente muestra el error', (await pagina.getByRole('alert').textContent()).includes('No hay ningún alumno'))
  // Sesión vencida o borrada: vuelve al login y, al ingresar, al alumno pedido.
  await pagina.evaluate(() => localStorage.removeItem('rojas_fc_sesion'))
  await pagina.goto(`${WEB}/alumnos/45111222`)
  await pagina.waitForURL(`${WEB}/login`)
  await captura(pagina, '10-sin-sesion')
  await ingresar(pagina, '20111222', 'AdminSegura1')
  await pagina.waitForURL(`${WEB}/alumnos/45111222`)
  await pagina.getByRole('heading', { name: 'Mateo Gómez' }).waitFor()
  caso('Sin sesión pide ingresar y después vuelve al alumno pedido', true)

  // --- Un responsable no entra a la gestión de alumnos (RF-52) ---
  await pagina.getByRole('button', { name: 'Cerrar sesión' }).click()
  await pagina.waitForURL(`${WEB}/login`)
  await ingresar(pagina, '30111222', 'provisoria123')
  await pagina.waitForURL(`${WEB}/cambiar-contrasena`)
  await pagina.getByLabel('Contraseña provisoria').fill('provisoria123')
  await pagina.getByLabel('Contraseña nueva', { exact: true }).fill('LauraNueva123')
  await pagina.getByLabel('Repetir contraseña nueva').fill('LauraNueva123')
  await pagina.getByRole('button', { name: 'Cambiar contraseña' }).click()
  await pagina.waitForURL(`${WEB}/portal`)
  await pagina.goto(`${WEB}/alumnos/45111222`)
  await pagina.waitForURL(`${WEB}/portal`)
  caso('La responsable creada en el alta ingresa con su contraseña provisoria, la cambia y no puede abrir Alumnos', true)
  await captura(pagina, '10b-responsable-sin-acceso')

  // --- Celular ---
  const movil = await navegador.newContext({ viewport: { width: 390, height: 844 } })
  const pm = await movil.newPage()
  await pm.goto(`${WEB}/alumnos/nuevo`)
  await pm.waitForURL(`${WEB}/login`)
  await ingresar(pm, '20111222', 'AdminSegura1')
  await pm.waitForURL(`${WEB}/alumnos/nuevo`)
  await pm.getByRole('heading', { name: 'Nuevo alumno' }).waitFor()
  const anchoPagina = await pm.evaluate(() => document.documentElement.scrollWidth)
  caso('Alta en celular sin desborde horizontal', anchoPagina <= 390, `ancho=${anchoPagina}`)
  await captura(pm, '11-alta-celular')
  await pm.goto(`${WEB}/alumnos/45111222`)
  await pm.getByRole('heading', { name: 'Mateo Gómez' }).waitFor()
  await captura(pm, '12-detalle-celular')

  caso('Sin errores en la consola del navegador (solo respuestas 401/404/409 esperadas, sin errores de JavaScript)',
    erroresConsola.every((e) => /status of (401|404|409) /.test(e)), `${erroresConsola.length} respuestas esperadas`)

  await navegador.close()
  console.log(`\n${resultados.filter(Boolean).length}/${resultados.length} casos OK`)
  process.exit(resultados.every(Boolean) ? 0 : 1)
})().catch((e) => { console.error(e); process.exit(1) })
