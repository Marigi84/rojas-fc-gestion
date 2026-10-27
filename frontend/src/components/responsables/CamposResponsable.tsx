import { Boton } from '../ui/Boton'
import { Campo } from '../ui/Campo'
import { Selector } from '../ui/Selector'
import { VINCULOS, type Vinculo } from '../../services/alumnos'
import type { FilaResponsable } from './filaResponsable'

interface PropsCamposResponsable {
  numero: number
  fila: FilaResponsable
  onCambiar: (cambios: Partial<FilaResponsable>) => void
  onCambiarDni: (dni: string) => void
  onRevisarDni: (dni: string) => void
  // Sin onQuitar no se muestra el botón (por ejemplo, si es el único).
  onQuitar?: () => void
}

// Campos de un responsable: DNI, vínculo, nombre y, si todavía no es
// responsable, teléfono y contraseña provisoria (RF-06, RF-11, RN-12).
export function CamposResponsable({
  numero,
  fila,
  onCambiar,
  onCambiarDni,
  onRevisarDni,
  onQuitar,
}: PropsCamposResponsable) {
  return (
    <fieldset className="rounded-lg border border-borde p-4">
      <legend className="px-1 font-semibold">Responsable {numero}</legend>
      <div className="grid gap-4 sm:grid-cols-2">
        <Campo
          etiqueta="DNI"
          inputMode="numeric"
          pattern="[0-9]{1,10}"
          title="Solo números"
          required
          value={fila.dni}
          onChange={(e) => onCambiarDni(e.target.value.trim())}
          onBlur={(e) => onRevisarDni(e.target.value.trim())}
        />
        <Selector
          etiqueta="Vínculo con el alumno"
          opciones={VINCULOS}
          value={fila.vinculo}
          onChange={(e) => onCambiar({ vinculo: e.target.value as Vinculo })}
        />
        <Campo
          etiqueta="Nombre"
          required
          maxLength={60}
          readOnly={fila.estado !== 'nueva'}
          value={fila.nombre}
          onChange={(e) => onCambiar({ nombre: e.target.value })}
        />
        <Campo
          etiqueta="Apellido"
          required
          maxLength={60}
          readOnly={fila.estado !== 'nueva'}
          value={fila.apellido}
          onChange={(e) => onCambiar({ apellido: e.target.value })}
        />
        {fila.estado !== 'responsable' && (
          <>
            <Campo
              etiqueta="Teléfono"
              type="tel"
              required
              maxLength={20}
              value={fila.telefono}
              onChange={(e) => onCambiar({ telefono: e.target.value })}
            />
            <Campo
              etiqueta="Contraseña provisoria"
              type="password"
              autoComplete="new-password"
              required
              minLength={8}
              maxLength={72}
              value={fila.contrasena}
              onChange={(e) => onCambiar({ contrasena: e.target.value })}
            />
          </>
        )}
      </div>
      <p className="mt-3 text-sm text-texto-suave">
        {fila.estado === 'responsable'
          ? 'Ya es responsable de otro alumno: se asocia sin cambiar sus datos ni su contraseña.'
          : 'La contraseña es provisoria: el responsable la cambia en su primer ingreso al portal.'}
      </p>
      {onQuitar && (
        <Boton variante="secundario" className="mt-3" onClick={onQuitar}>
          Quitar responsable
        </Boton>
      )}
    </fieldset>
  )
}
