import type { Custo, Departamento, Funcionario } from '../../core/models.ts'
import { departamentos } from '../../core/seed.ts'
import { sumCosts } from '../../core/format.ts'
import { apiClient } from '../../core/apiClient.ts'

export interface DashboardData {
  months: { key: string; label: string }[]
  total: number
  count: number
  ranking: { funcionario: Funcionario; total: number }[]
  departments: { departamento: Departamento; values: number[] }[]
}

export async function fetchDashboard(referencia?: string): Promise<DashboardData> {
  const query = referencia ? `?referencia=${encodeURIComponent(referencia)}` : ''
  return await apiClient.get<DashboardData>(`/api/dashboard${query}`)
}

// Adaptador alinhado com o contrato e regras de negócio do DashboardService do backend (ranking top 3 e trimestre [2, 1, 0]).
export function getDashboard(custos: Custo[], now = new Date()): DashboardData {
  const months = [2, 1, 0].map(offset => {
    const date = new Date(now.getFullYear(), now.getMonth() - offset, 1)
    return { key: `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`, label: date.toLocaleDateString('pt-BR', { month: 'short', year: 'numeric' }) }
  })
  const current = custos.filter(item => item.data.startsWith(months[2].key))
  const ids = [...new Set(custos.map(item => item.funcionario.id))]
  const ranking = ids.map(id => {
    const items = custos.filter(item => item.funcionario.id === id)
    return { funcionario: items[0].funcionario, total: sumCosts(items) }
  }).sort((a, b) => b.total - a.total || a.funcionario.id - b.funcionario.id).slice(0, 3)
  return {
    months, total: sumCosts(current), count: current.length, ranking,
    departments: departamentos.map(departamento => ({ departamento, values: months.map(month => sumCosts(custos.filter(item => item.departamento.id === departamento.id && item.data.startsWith(month.key)))) })),
  }
}
