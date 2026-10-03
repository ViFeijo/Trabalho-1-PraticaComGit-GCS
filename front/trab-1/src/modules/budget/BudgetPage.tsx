import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { Field, Notice, PageHeading } from '../../components/ui.tsx'
import { currentMonth, money, validMonth } from '../../core/format.ts'
import { departamentos } from '../../core/seed.ts'
import { useAppState } from '../../core/store.ts'
import { budgetService, fetchBudgets, saveBudget, useBudgets } from './budgetService.ts'
import type { DepartmentBudgetResult } from './budgetService.ts'

export default function BudgetPage() {
  const { custos } = useAppState()
  useBudgets()
  const [month, setMonth] = useState(currentMonth())
  const [remoteBudgets, setRemoteBudgets] = useState<DepartmentBudgetResult[] | null>(null)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!validMonth(month)) return
    let active = true
    fetchBudgets(month)
      .then(data => {
        if (active) {
          setRemoteBudgets(data)
          setError('')
        }
      })
      .catch(err => {
        if (active) setError(err.message)
      })
    return () => { active = false }
  }, [month, custos])

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const form = event.currentTarget
    const data = new FormData(form)
    const depId = Number(data.get('departamento'))
    const limite = Number(data.get('limite'))
    try {
      setLoading(true)
      await saveBudget(depId, month, limite)
      budgetService.configure(depId, month, limite)
      const updated = await fetchBudgets(month)
      setRemoteBudgets(updated)
      setMessage('Limite mensal atualizado.')
      setError('')
      form.reset()
    } catch (err) {
      setError((err as Error).message)
      setMessage('')
    } finally {
      setLoading(false)
    }
  }

  return <>
    <PageHeading title="Limites mensais" action={<Field id="budget-month" label="Mês de referência"><input id="budget-month" type="month" required value={month} onChange={event => { setMonth(event.target.value); setMessage(''); setError('') }} /></Field>} />
    <section className="panel form-panel"><div className="section-heading"><div><h2>Configurar limite</h2><p>O limite é informativo e não impede o registro de novos custos.</p></div></div><form onSubmit={submit}><div className="form-grid budget-form">
      <Field id="budget-department" label="Departamento"><select id="budget-department" name="departamento" required defaultValue=""><option value="" disabled>Selecione um departamento</option>{departamentos.map(item => <option key={item.id} value={item.id}>{item.nome}</option>)}</select></Field>
      <Field id="budget-value" label="Limite mensal (R$)"><input id="budget-value" name="limite" type="number" required min="0.01" step="0.01" placeholder="Ex.: 5000,00" /></Field>
      <button className="button primary" disabled={!validMonth(month) || loading}>{loading ? 'Salvando…' : 'Salvar limite'}</button>
    </div></form></section>
    <Notice message={message} /><Notice message={error || (!validMonth(month) ? 'Selecione um mês válido para consultar os limites.' : '')} error />
    {validMonth(month) && <div className="budget-grid">{departamentos.map(departamento => {
      const remote = remoteBudgets?.find(b => b.departamentoId === departamento.id)
      const local = budgetService.consumption(departamento.id, month, custos)
      const result = remote ? {
        limite: remote.limite,
        consumo: remote.consumo,
        saldo: remote.saldo,
        percentual: remote.percentual,
        excedido: remote.excedido,
      } : local

      return <section className="panel budget-card" key={departamento.id}><div className="budget-card-heading"><h2>{departamento.nome}</h2><span className="tag">{result.limite === null ? 'Sem limite' : result.excedido ? 'Acima do limite' : 'Dentro do limite'}</span></div>
        <p className="budget-consumption">{money(result.consumo)}<small>consumidos no mês</small></p>
        <div className="progress" aria-hidden="true"><span style={{ width: `${Math.min(100, result.percentual ?? 0)}%` }} /></div>
        <div className="budget-details"><span>Limite<strong>{result.limite === null ? 'Não configurado' : money(result.limite)}</strong></span><span>{result.excedido ? 'Excedente' : 'Saldo'}<strong>{result.saldo === null ? '—' : money(Math.abs(result.saldo))}</strong></span></div>
        {result.percentual !== null && <p className="budget-note">{result.percentual.toLocaleString('pt-BR', { maximumFractionDigits: 1 })}% do limite utilizado.</p>}
      </section>
    })}</div>}
  </>
}
