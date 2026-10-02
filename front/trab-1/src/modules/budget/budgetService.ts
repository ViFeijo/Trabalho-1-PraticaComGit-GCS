import { useSyncExternalStore } from 'react'
import type { Custo } from '../../core/models.ts'
import { createStore } from '../../core/store.ts'
import { departamentos } from '../../core/seed.ts'
import { cents, sumCosts, validMonth } from '../../core/format.ts'

export interface Budget { departamentoId: number; mes: string; limite: number }
export interface BudgetResult { limite: number | null; consumo: number; saldo: number | null; percentual: number | null; excedido: boolean }
export interface BudgetService {
  configure(departamentoId: number, mes: string, limite: number): void
  consumption(departamentoId: number, mes: string, custos: Custo[]): BudgetResult
}
const store = createStore<Budget[]>([])
export const useBudgets = () => useSyncExternalStore(store.subscribe, store.getSnapshot)
export const budgetService: BudgetService = {
  configure(departamentoId, mes, limite) {
    if (!departamentos.some(item => item.id === departamentoId) || !validMonth(mes)) throw new Error('Informe um departamento e um mês válidos.')
    if (!Number.isFinite(limite) || limite <= 0 || !Number.isSafeInteger(cents(limite)) || Math.abs(cents(limite) / 100 - limite) > 0.0000001) throw new Error('Informe um limite positivo com até duas casas decimais.')
    store.update(current => [...current.filter(item => !(item.departamentoId === departamentoId && item.mes === mes)), { departamentoId, mes, limite: cents(limite) / 100 }])
  },
  consumption(departamentoId, mes, custos) {
    const limite = store.getSnapshot().find(item => item.departamentoId === departamentoId && item.mes === mes)?.limite ?? null
    const consumo = sumCosts(custos.filter(item => item.departamento.id === departamentoId && item.data.startsWith(`${mes}-`)))
    return { limite, consumo, saldo: limite === null ? null : (cents(limite) - cents(consumo)) / 100, percentual: limite === null ? null : consumo / limite * 100, excedido: limite !== null && consumo > limite }
  },
}
