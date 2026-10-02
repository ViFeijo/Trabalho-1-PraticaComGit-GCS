import { useState } from 'react'
import type { FormEvent } from 'react'
import { Field, Notice } from '../../components/ui.tsx'
import { categorias } from '../../core/seed.ts'
import { today } from '../../core/format.ts'
import { useAppState } from '../../core/store.ts'
import { costService } from './costService.ts'

export default function CostForm({ onCancel, onCreated }: { onCancel: () => void; onCreated: () => void }) {
  const { funcionarios, operadorId } = useAppState()
  const operador = funcionarios.find(item => item.id === operadorId)
  const [error, setError] = useState('')
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    try {
      costService.create({ descricao: String(data.get('descricao')), valor: Number(data.get('valor')), data: String(data.get('data')), categoriaId: Number(data.get('categoria')) })
      onCreated()
    } catch (err) { setError((err as Error).message) }
  }
  return <section className="panel form-panel" aria-labelledby="cost-form-title"><div className="section-heading"><div><h2 id="cost-form-title">Novo custo</h2><p>{operador ? `${operador.nome} · ${operador.departamento.nome}` : 'Selecione um operador no topo da página para continuar.'}</p></div></div>
    <form onSubmit={submit}><div className="form-grid four">
      <Field id="descricao" label="Descrição"><input autoFocus id="descricao" name="descricao" required placeholder="Ex.: Material para a equipe" /></Field>
      <Field id="valor" label="Valor (R$)"><input id="valor" name="valor" type="number" min="0.01" step="0.01" required placeholder="0,00" /></Field>
      <Field id="data" label="Data do custo"><input id="data" name="data" type="date" required defaultValue={today()} /></Field>
      <Field id="categoria" label="Categoria"><select id="categoria" name="categoria" required defaultValue=""><option value="" disabled>Selecione</option>{categorias.map(item => <option key={item.id} value={item.id}>{item.nome}</option>)}</select></Field>
    </div><Notice message={error} error /><div className="form-actions"><button className="button secondary" type="button" onClick={onCancel}>Cancelar</button><button className="button primary" disabled={!operador}>Registrar custo</button></div></form>
  </section>
}
