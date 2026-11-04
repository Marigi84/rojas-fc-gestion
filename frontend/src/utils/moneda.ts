const PESOS = new Intl.NumberFormat('es-AR', {
  style: 'currency',
  currency: 'ARS',
})

// Importe en pesos con el formato local, por ejemplo $ 1.100,00.
export function formatearMonto(monto: number): string {
  return PESOS.format(monto)
}
