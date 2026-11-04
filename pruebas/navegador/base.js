// Configuracion comun de los recorridos y acceso a la base de desarrollo.
const { spawnSync } = require('child_process')
const fs = require('fs')
const path = require('path')

const WEB = process.env.WEB || 'http://localhost:5173'
const API = process.env.API || 'http://localhost:8080/api'
const NAVEGADOR =
  process.env.NAVEGADOR || 'C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe'
const CAPTURAS = path.join(__dirname, 'capturas')
const RAIZ = path.join(__dirname, '..', '..')

fs.mkdirSync(CAPTURAS, { recursive: true })

function mysql(base, entrada, ...opciones) {
  const { DB_USERNAME, DB_PASSWORD } = process.env
  if (!DB_USERNAME) throw new Error('Falta la variable DB_USERNAME (ver el README)')
  const resultado = spawnSync(
    process.env.MYSQL_CLI || 'mysql',
    [`-u${DB_USERNAME}`, `-p${DB_PASSWORD || ''}`, '--default-character-set=utf8mb4', ...opciones, ...(base ? [base] : [])],
    { input: entrada },
  )
  if (resultado.error) throw resultado.error
  if (resultado.status !== 0) throw new Error(String(resultado.stderr))
}

const nombreBase = () => process.env.DB_NAME || 'rojasfc'

// Ejecuta una sentencia SQL en la base de desarrollo.
function ejecutarSql(sentencia) {
  mysql(nombreBase(), sentencia)
}

function cargarArchivo(relativo) {
  mysql(nombreBase(), fs.readFileSync(path.join(RAIZ, relativo)))
}

// Borra la base de desarrollo y la vuelve a crear desde schema.sql.
function reiniciarBase() {
  mysql(null, `DROP DATABASE IF EXISTS ${nombreBase()}; CREATE DATABASE ${nombreBase()};`)
  cargarArchivo('database/schema.sql')
}

module.exports = { WEB, API, NAVEGADOR, CAPTURAS, ejecutarSql, cargarArchivo, reiniciarBase }
