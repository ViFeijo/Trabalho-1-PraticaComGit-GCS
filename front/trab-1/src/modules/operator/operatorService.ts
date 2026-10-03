import type { Funcionario } from '../../core/models.ts'
import { appStore } from '../../core/store.ts'
import { apiClient } from '../../core/apiClient.ts'

export interface OperatorService {
  select(id: number | null): void
  fetchCurrent(): Promise<{ operadorId: number | null; operador: Funcionario | null }>
}

export const operatorService: OperatorService = {
  async fetchCurrent() {
    const data = await apiClient.get<{ operadorId: number | null; operador: Funcionario | null }>('/api/operador')
    appStore.update(state => ({ ...state, operadorId: data.operadorId }))
    return data
  },
  select(id) {
    if (id !== null && !appStore.getSnapshot().funcionarios.some(item => item.id === id)) {
      throw new Error('Funcionário não encontrado.')
    }
    appStore.update(state => ({ ...state, operadorId: id }))
    if (typeof window !== 'undefined') {
      apiClient.post('/api/operador', { id }).catch(console.error)
    }
  },
}
