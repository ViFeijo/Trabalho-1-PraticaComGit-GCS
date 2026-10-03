import { FiPlus } from 'react-icons/fi'
import { useRef, useState } from 'react'
import { Empty, Notice, PageHeading, Stat } from '../../components/ui.tsx'
import { displayDate, money, sumCosts } from '../../core/format.ts'
import { useAppState } from '../../core/store.ts'
import { useModules } from '../../core/moduleContext.ts'
import { costService, emptyFilters, filterCosts, sortCosts } from './costService.ts'
import CostFilterBar from './CostFilterBar.tsx'
import CostForm from './CostForm.tsx'

export default function CostPage() {
  const { custos, operadorId } = useAppState()
  const modules = useModules()
  const [filters, setFilters] = useState(emptyFilters)
  const [pendingDelete, setPendingDelete] = useState<number | null>(null)
  const [adding, setAdding] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const trigger = useRef<HTMLButtonElement>(null)
  const invalidRange = !!(filters.inicio && filters.fim && filters.inicio > filters.fim)
  const filtered = invalidRange ? [] : filterCosts(custos, filters)
  const latestId = sortCosts(custos)[0]?.id
  function close() { setAdding(false); requestAnimationFrame(() => trigger.current?.focus()) }
  function remove(id: number) {
    try { costService.remove(id); setMessage('Custo excluído com sucesso.'); setError('') }
    catch (err) { setError((err as Error).message); setMessage('') }
    setPendingDelete(null)
    requestAnimationFrame(() => trigger.current?.focus())
  }
  return <>
    <PageHeading title="Custos" action={<button ref={trigger} className="button primary" disabled={adding} aria-expanded={adding} onClick={() => { setAdding(true); setMessage('') }}><FiPlus aria-hidden="true" /> Novo custo</button>} />
    <div className="stats two"><Stat label="Total da seleção" value={money(sumCosts(filtered))} /><Stat label="Registros encontrados" value={filtered.length.toString().padStart(2, '0')} /></div>
    {!operadorId && <div className="notice warning">Selecione um operador no topo para cadastrar custos. A consulta continua disponível.</div>}
    <Notice message={message} /><Notice message={error || (invalidRange ? 'A data inicial deve ser anterior ou igual à final.' : '')} error />
    {pendingDelete !== null && <section className="notice warning" role="alertdialog" aria-labelledby="delete-title" aria-describedby="delete-description" onKeyDown={event => { if (event.key === 'Escape') setPendingDelete(null) }}><h2 id="delete-title">Excluir o custo mais recente?</h2><p id="delete-description">Esta ação não pode ser desfeita nesta sessão.</p><div className="confirm-actions"><button autoFocus className="button secondary" onClick={() => { setPendingDelete(null); requestAnimationFrame(() => trigger.current?.focus()) }}>Manter registro</button><button className="button primary" onClick={() => remove(pendingDelete)}>Confirmar exclusão</button></div></section>}
    {adding && <CostForm onCancel={close} onCreated={() => { setFilters(emptyFilters); setMessage('Custo registrado com sucesso.'); close() }} />}
    <section className="panel"><div className="section-heading"><div><h2>Registros de custos</h2></div><div className="inline-actions">{modules.map(item => item.CostAction ? <item.CostAction key={item.id} custos={filtered} /> : null)}</div></div>
      <CostFilterBar filters={filters} onChange={setFilters} />
      {filtered.length ? <div className="table-wrap"><table className="responsive-table costs-table"><thead><tr><th scope="col">Descrição / categoria</th><th scope="col">Responsável</th><th scope="col">Data</th><th scope="col">Valor</th><th scope="col"><span className="sr-only">Ações</span></th></tr></thead><tbody>{filtered.map(item => <tr key={item.id}><td><strong>{item.descricao}</strong><small>{item.categoria.nome}</small></td><td data-label="Responsável"><div>{item.funcionario.nome}<small>{item.departamento.nome}</small></div></td><td data-label="Data">{displayDate(item.data)}</td><td data-label="Valor" className="money">{money(item.valor)}</td><td>{item.id === latestId ? <button className="text-button destructive" aria-label={`Excluir custo: ${item.descricao}`} onClick={() => setPendingDelete(item.id)}>Excluir</button> : <span className="muted">—</span>}</td></tr>)}</tbody></table></div> : <Empty title="Nenhum custo encontrado" />}
      <div className="panel-footer" role="status">{filtered.length} registros · Somente o custo mais recente da lista completa pode ser excluído.</div>
    </section>
  </>
}
