export interface Departamento { id: number; nome: string }
export interface Categoria { id: number; nome: string }
export interface Funcionario { id: number; nome: string; cargo: string; departamento: Departamento }
export interface Custo {
  id: number
  valor: number
  descricao: string
  data: string
  categoria: Categoria
  departamento: Departamento
  funcionario: Funcionario
}
export interface AppState {
  funcionarios: Funcionario[]
  custos: Custo[]
  operadorId: number | null
}
