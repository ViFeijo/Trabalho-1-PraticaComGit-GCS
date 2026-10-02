import { FiPlus } from 'react-icons/fi'
import { useRef, useState } from 'react'
import { Empty, Notice, PageHeading, Person, Stat } from '../../components/ui.tsx'
import { normalize } from '../../core/format.ts'
import { useAppState } from '../../core/store.ts'
import EmployeeForm from './EmployeeForm.tsx'

export default function EmployeePage() {
  const { funcionarios } = useAppState()
  const [search, setSearch] = useState('')
  const [adding, setAdding] = useState(false)
  const [message, setMessage] = useState('')
  const trigger = useRef<HTMLButtonElement>(null)
  const filtered = funcionarios.filter(item => normalize(`${item.nome} ${item.cargo}`).includes(normalize(search.trim())))
  function close() { setAdding(false); requestAnimationFrame(() => trigger.current?.focus()) }
  return <>
    <PageHeading title="Funcionários" action={<button ref={trigger} className="button primary" disabled={adding} aria-expanded={adding} onClick={() => { setAdding(true); setMessage('') }}><FiPlus aria-hidden="true" /> Novo funcionário</button>} />
    <div className="stats two"><Stat label="Pessoas na equipe" value={funcionarios.length.toString().padStart(2, '0')} /><Stat label="Departamentos representados" value={new Set(funcionarios.map(item => item.departamento.id)).size.toString().padStart(2, '0')} /></div>
    <Notice message={message} />
    {adding && <EmployeeForm onCancel={close} onCreated={name => { setSearch(''); setMessage(`Cadastro de ${name} realizado com sucesso.`); close() }} />}
    <section className="panel"><div className="section-heading"><div><h2>Nossa equipe <span className="badge">{funcionarios.length}</span></h2></div><div className="search-field"><label className="sr-only" htmlFor="employee-search">Buscar por nome ou cargo</label><input type="search" id="employee-search" placeholder="Buscar por nome ou cargo…" value={search} onChange={event => setSearch(event.target.value)} /></div></div>
      {filtered.length ? <div className="table-wrap"><table className="responsive-table"><thead><tr><th scope="col">Funcionário</th><th scope="col">Cargo</th><th scope="col">Departamento</th></tr></thead><tbody>{filtered.map(item => <tr key={item.id}><td><Person name={item.nome} detail={`ID ${String(item.id).padStart(3, '0')}`} /></td><td data-label="Cargo">{item.cargo}</td><td data-label="Departamento"><span className="tag">{item.departamento.nome}</span></td></tr>)}</tbody></table></div> : <Empty title="Nenhum funcionário encontrado" />}
      <div className="panel-footer" role="status">Exibindo {filtered.length} de {funcionarios.length} funcionários</div>
    </section>
  </>
}
