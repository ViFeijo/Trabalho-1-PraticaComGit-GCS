import { useState } from 'react'
import type { FormEvent } from 'react'
import { Field, Notice } from '../../components/ui.tsx'
import { departamentos } from '../../core/seed.ts'
import { employeeService } from './employeeService.ts'

export default function EmployeeForm({ onCancel, onCreated }: { onCancel: () => void; onCreated: (name: string) => void }) {
  const [error, setError] = useState('')
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    try {
      const result = employeeService.create({ nome: String(data.get('nome')), cargo: String(data.get('cargo')), departamentoId: Number(data.get('departamento')) })
      onCreated(result.nome)
    } catch (err) { setError((err as Error).message) }
  }
  return <section className="panel form-panel" aria-labelledby="employee-form-title">
    <div className="section-heading"><div><h2 id="employee-form-title">Adicionar à equipe</h2></div></div>
    <form onSubmit={submit}><div className="form-grid">
      <Field id="nome" label="Nome completo"><input autoFocus id="nome" name="nome" required placeholder="Ex.: Mariana Oliveira" autoComplete="name" /></Field>
      <Field id="cargo" label="Cargo"><input id="cargo" name="cargo" required placeholder="Ex.: Analista financeiro" autoComplete="organization-title" /></Field>
      <Field id="departamento" label="Departamento"><select id="departamento" name="departamento" required defaultValue=""><option value="" disabled>Selecione</option>{departamentos.map(item => <option key={item.id} value={item.id}>{item.nome}</option>)}</select></Field>
    </div><Notice message={error} error /><div className="form-actions"><button type="button" className="button secondary" onClick={onCancel}>Cancelar</button><button className="button primary">Cadastrar funcionário</button></div></form>
  </section>
}
