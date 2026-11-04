// Corre todos los recorridos, cada uno con los datos que necesita. BORRA la base
// de desarrollo: no usar con datos que se quieran conservar.
const { spawnSync } = require('child_process')
const path = require('path')
const { reiniciarBase, cargarArchivo } = require('./base')

const GRUPOS = [
  { recorridos: ['alumnos.js', 'responsables-listado.js'] },
  { recorridos: ['cuotas.js'] },
  { recorridos: ['dashboard-portal.js'], datos: 'database/datos-demo.sql' },
]

let fallaron = 0
for (const { recorridos, datos } of GRUPOS) {
  reiniciarBase()
  if (datos) cargarArchivo(datos)
  for (const recorrido of recorridos) {
    console.log(`\n=== ${recorrido}`)
    const { status } = spawnSync(process.execPath, [path.join(__dirname, recorrido)], {
      stdio: 'inherit',
    })
    if (status !== 0) fallaron += 1
  }
}

console.log(fallaron === 0 ? '\nTodos los recorridos pasaron.' : `\nFallaron ${fallaron} recorridos.`)
process.exit(fallaron === 0 ? 0 : 1)
