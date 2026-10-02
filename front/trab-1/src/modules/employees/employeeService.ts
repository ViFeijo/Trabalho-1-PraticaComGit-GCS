import type { Funcionario } from '../../core/models.ts'
import { appStore } from '../../core/store.ts'
import { departamentos } from '../../core/seed.ts'

export interface EmployeeInput { nome: string; cargo: string; departamentoId: number }
export interface EmployeeService { create(input: EmployeeInput): Funcionario }
export const employeeService: EmployeeService = {
  create(input) {
    const nome = input.nome.trim(), cargo = input.cargo.trim()
    const departamento = departamentos.find(item => item.id === input.departamentoId)
    if (!nome || !cargo || !departamento) throw new Error('Preencha nome, cargo e departamento.')
    const funcionario = { id: Math.max(0, ...appStore.getSnapshot().funcionarios.map(item => item.id)) + 1, nome, cargo, departamento }
    appStore.update(state => ({ ...state, funcionarios: [...state.funcionarios, funcionario] }))
    return funcionario
  },
}
