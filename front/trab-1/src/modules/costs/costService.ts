import type { Custo } from '../../core/models.ts'
import { appStore } from '../../core/store.ts'
import { categorias } from '../../core/seed.ts'
import { cents, normalize, validDate } from '../../core/format.ts'
import { apiClient } from '../../core/apiClient.ts'

export interface CostFilters { descricao: string; categoria: string; departamento: string; inicio: string; fim: string }
export const emptyFilters: CostFilters = { descricao: '', categoria: '', departamento: '', inicio: '', fim: '' }
export interface CostInput { valor: number; descricao: string; data: string; categoriaId: number }
export interface CostService {
  create(input: CostInput): Custo
  remove(id: number): void | Promise<void>
  list(filters: CostFilters): Custo[]
  fetchAll(filters?: CostFilters): Promise<Custo[]>
}
export function sortCosts(items: Custo[]) { return [...items].sort((a, b) => b.data.localeCompare(a.data) || b.id - a.id) }
export function filterCosts(items: Custo[], filters: CostFilters) {
  return sortCosts(items.filter(item =>
    normalize(item.descricao).includes(normalize(filters.descricao.trim())) &&
    (!filters.categoria || item.categoria.id === Number(filters.categoria)) &&
    (!filters.departamento || item.departamento.id === Number(filters.departamento)) &&
    (!filters.inicio || item.data >= filters.inicio) && (!filters.fim || item.data <= filters.fim),
  ))
}
export const costService: CostService = {
  list: filters => filterCosts(appStore.getSnapshot().custos, filters),
  async fetchAll(filters?: CostFilters) {
    let url = '/api/custos'
    if (filters) {
      const params = new URLSearchParams()
      if (filters.descricao) params.set('descricao', filters.descricao)
      if (filters.categoria) params.set('categoriaId', filters.categoria)
      if (filters.departamento) params.set('departamentoId', filters.departamento)
      if (params.toString()) url += '?' + params.toString()
    }
    const items = await apiClient.get<Custo[]>(url)
    appStore.update(state => ({ ...state, custos: items }))
    return items
  },
  create(input) {
    const state = appStore.getSnapshot()
    const funcionario = state.funcionarios.find(item => item.id === state.operadorId)
    if (!funcionario) throw new Error('Selecione o operador atual antes de cadastrar um custo.')
    const categoria = categorias.find(item => item.id === input.categoriaId)
    if (!Number.isFinite(input.valor) || input.valor <= 0 || !Number.isSafeInteger(cents(input.valor)) || Math.abs(cents(input.valor) / 100 - input.valor) > 0.0000001) throw new Error('Informe um valor positivo com até duas casas decimais.')
    if (!input.descricao.trim() || !validDate(input.data) || !categoria) throw new Error('Preencha descrição, data válida e categoria.')
    const custo: Custo = { id: Math.max(0, ...state.custos.map(item => item.id)) + 1, valor: cents(input.valor) / 100, descricao: input.descricao.trim(), data: input.data, categoria, departamento: funcionario.departamento, funcionario }

    if (typeof window !== 'undefined') {
      const promise = (async () => {
        const created = await apiClient.post<Custo>('/api/custos', {
          valor: custo.valor,
          descricao: custo.descricao,
          data: custo.data,
          categoriaId: input.categoriaId,
          departamentoId: funcionario.departamento.id,
          funcionarioId: funcionario.id,
        })
        appStore.update(current => ({ ...current, custos: [created, ...current.custos.filter(c => c.id !== created.id)] }))
        return created
      })()
      Object.assign(promise, custo)
      return promise as unknown as Custo
    }

    appStore.update(current => ({ ...current, custos: [...current.custos, custo] }))
    const promise = Promise.resolve(custo)
    Object.assign(promise, custo)
    return promise as unknown as Custo
  },
  remove(id) {
    if (sortCosts(appStore.getSnapshot().custos)[0]?.id !== id) throw new Error('Somente o custo mais recente pode ser excluído.')

    if (typeof window !== 'undefined') {
      const promise = (async () => {
        await apiClient.del(`/api/custos/${id}`)
        appStore.update(state => ({ ...state, custos: state.custos.filter(item => item.id !== id) }))
      })()
      return promise as unknown as void
    }

    appStore.update(state => ({ ...state, custos: state.custos.filter(item => item.id !== id) }))
  },
}
