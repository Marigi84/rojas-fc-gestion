// Configuracion comun de los recorridos y acceso a la base de desarrollo.
const { spawnSync } = require('child_process')
const fs = require('fs')
const path = require('path')

const WEB = process.env.WEB || 'http://localhost:5173'
const API = process.env.API || 'http://localhost:8080/api'
// Sin NAVEGADOR, Playwright busca el navegador instalado: Edge en Windows, Chrome en el resto.
const OPCIONES_NAVEGADOR = process.env.NAVEGADOR
  ? { executablePath: process.env.NAVEGADOR }
  : { channel: process.platform === 'win32' ? 'msedge' : 'chrome' }
const CAPTURAS = path.join(__dirname, 'capturas')
const RAIZ = path.join(__dirname, '..', '..')

fs.mkdirSync(CAPTURAS, { recursive: true })

function mysql(base, entrada, ...opciones) {
  const { DB_USERNAME, DB_PASSWORD } = process.env
  if (!DB_USERNAME) throw new Error('Falta la variable DB_USERNAME (ver el README)')
  const resultado = spawnSync(
    process.env.MYSQL_CLI || 'mysql',
    [`-u${DB_USERNAME}`, '--default-character-set=utf8mb4', ...opciones, ...(base ? [base] : [])],
    { input: entrada, env: { ...process.env, MYSQL_PWD: DB_PASSWORD || '' } },
  )
  if (resultado.error) throw resultado.error
  if (resultado.status !== 0) throw new Error(String(resultado.stderr))
}

// Base propia de las pruebas: se borra en cada corrida, asi que no es la de desarrollo.
// No se toma de DB_NAME para no borrar por error la base con la que trabaja el backend.
const nombreBase = () => process.env.PRUEBAS_DB_NAME || 'rojasfc_pruebas'

// Saca los saltos de linea y los caracteres de control de un texto antes de mostrarlo.
function textoSeguro(texto) {
  return String(texto).replace(/[\p{Cc}]/gu, ' ')
}

// Ejecuta una sentencia SQL en la base de las pruebas.
function ejecutarSql(sentencia) {
  mysql(nombreBase(), sentencia)
}

function cargarArchivo(relativo) {
  mysql(nombreBase(), fs.readFileSync(path.join(RAIZ, relativo)))
}

// Borra la base de las pruebas y la vuelve a crear desde schema.sql.
function reiniciarBase() {
  mysql(null, `DROP DATABASE IF EXISTS ${nombreBase()}; CREATE DATABASE ${nombreBase()};`)
  cargarArchivo('database/schema.sql')
}

module.exports = { WEB, API, OPCIONES_NAVEGADOR, CAPTURAS, textoSeguro, ejecutarSql, cargarArchivo, reiniciarBase }
