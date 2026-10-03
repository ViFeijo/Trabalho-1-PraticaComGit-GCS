import type { Funcionario } from '../../core/models.ts'
import { appStore } from '../../core/store.ts'
import { departamentos } from '../../core/seed.ts'
import { apiClient } from '../../core/apiClient.ts'

export interface EmployeeInput { nome: string; cargo: string; departamentoId: number }
export interface EmployeeService {
  create(input: EmployeeInput): Funcionario
  fetchAll(): Promise<Funcionario[]>
}

export const employeeService: EmployeeService = {
  async fetchAll() {
    const list = await apiClient.get<Funcionario[]>('/api/funcionarios')
    appStore.update(state => ({ ...state, funcionarios: list }))
    return list
  },
  create(input) {
    const nome = input.nome.trim(), cargo = input.cargo.trim()
    const departamento = departamentos.find(item => item.id === input.departamentoId)
    if (!nome || !cargo || !departamento) throw new Error('Preencha nome, cargo e departamento.')

    const funcionario: Funcionario = {
      id: Math.max(0, ...appStore.getSnapshot().funcionarios.map(item => item.id)) + 1,
      nome,
      cargo,
      departamento,
    }

    if (typeof window !== 'undefined') {
      const promise = (async () => {
        const created = await apiClient.post<Funcionario>('/api/funcionarios', {
          nome,
          cargo,
          departamentoId: input.departamentoId,
        })
        appStore.update(state => ({
          ...state,
          funcionarios: [...state.funcionarios.filter(f => f.id !== created.id), created],
        }))
        return created
      })()
      Object.assign(promise, funcionario)
      return promise as unknown as Funcionario
    }

    appStore.update(state => ({ ...state, funcionarios: [...state.funcionarios, funcionario] }))
    const promise = Promise.resolve(funcionario)
    Object.assign(promise, funcionario)
    return promise as unknown as Funcionario
  },
}
