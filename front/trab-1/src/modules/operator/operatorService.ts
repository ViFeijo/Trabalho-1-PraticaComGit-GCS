import { appStore } from '../../core/store.ts'
export interface OperatorService { select(id: number | null): void }
export const operatorService: OperatorService = {
  select(id) {
    if (id !== null && !appStore.getSnapshot().funcionarios.some(item => item.id === id)) throw new Error('Funcionário não encontrado.')
    appStore.update(state => ({ ...state, operadorId: id }))
  },
}
