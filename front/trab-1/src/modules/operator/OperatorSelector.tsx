import { useEffect } from 'react'
import { useAppState } from '../../core/store.ts'
import { operatorService } from './operatorService.ts'

export default function OperatorSelector() {
  const { funcionarios, operadorId } = useAppState()
  const current = funcionarios.find(item => item.id === operadorId)

  useEffect(() => {
    operatorService.fetchCurrent().catch(() => {})
  }, [])

  return (
    <div className="operator">
      <div>
        <label htmlFor="operator">Operador atual</label>
        <small>{current?.departamento.nome ?? 'Selecione para registrar custos'}</small>
      </div>
      <select
        id="operator"
        value={operadorId ?? ''}
        onChange={event => operatorService.select(event.target.value ? Number(event.target.value) : null)}
      >
        <option value="">Selecionar operador</option>
        {funcionarios.map(item => <option key={item.id} value={item.id}>{item.nome}</option>)}
      </select>
    </div>
  )
}
