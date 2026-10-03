import { Field } from '../../components/ui.tsx'
import { categorias, departamentos } from '../../core/seed.ts'
import type { CostFilters } from './costService.ts'
import { emptyFilters } from './costService.ts'

export default function CostFilterBar({ filters, onChange }: { filters: CostFilters; onChange: (next: CostFilters) => void }) {
  const set = (key: keyof CostFilters, value: string) => onChange({ ...filters, [key]: value })
  return <div className="filter-section"><div className="filters">
    <Field id="filter-description" label="Descrição"><input type="search" id="filter-description" placeholder="Buscar um custo…" value={filters.descricao} onChange={event => set('descricao', event.target.value)} /></Field>
    <Field id="filter-category" label="Categoria"><select id="filter-category" value={filters.categoria} onChange={event => set('categoria', event.target.value)}><option value="">Todas</option>{categorias.map(item => <option key={item.id} value={item.id}>{item.nome}</option>)}</select></Field>
    <Field id="filter-department" label="Departamento"><select id="filter-department" value={filters.departamento} onChange={event => set('departamento', event.target.value)}><option value="">Todos</option>{departamentos.map(item => <option key={item.id} value={item.id}>{item.nome}</option>)}</select></Field>
    <Field id="filter-start" label="De"><input type="date" id="filter-start" value={filters.inicio} onChange={event => set('inicio', event.target.value)} /></Field>
    <Field id="filter-end" label="Até"><input type="date" id="filter-end" value={filters.fim} onChange={event => set('fim', event.target.value)} /></Field>
  </div><div className="filter-bottom"><button className="text-button" onClick={() => onChange(emptyFilters)}>Limpar filtros</button></div></div>
}
