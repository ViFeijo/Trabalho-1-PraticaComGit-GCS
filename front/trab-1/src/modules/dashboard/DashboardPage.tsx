import { Empty, PageHeading, Person, Stat } from '../../components/ui.tsx'
import { money } from '../../core/format.ts'
import { useAppState } from '../../core/store.ts'
import { getDashboard } from './dashboardService.ts'

export default function DashboardPage() {
  const { custos } = useAppState()
  const data = getDashboard(custos)
  return <>
    <PageHeading title="Dashboard" />
    <div className="stats three"><Stat label={`Custos em ${data.months[2].label}`} value={money(data.total)} /><Stat label={`Registros em ${data.months[2].label}`} value={data.count.toString().padStart(2, '0')} /><Stat label="Departamentos" value={data.departments.length.toString().padStart(2, '0')} /></div>
    <section className="panel"><div className="section-heading"><div><h2>Custos por departamento</h2></div><span className="tag">Últimos 3 meses</span></div>
      <div className="table-wrap"><table className="dashboard-table"><thead><tr><th scope="col">Departamento</th>{data.months.map(month => <th key={month.key}>{month.label}</th>)}</tr></thead><tbody>{data.departments.map(item => <tr key={item.departamento.id}><td><strong>{item.departamento.nome}</strong></td>{item.values.map((value, index) => <td key={index}><span className="money">{money(value)}</span></td>)}</tr>)}</tbody></table></div>
    </section>
    <section className="panel ranking"><div className="section-heading"><div><h2>Top 3 funcionários — todo o histórico</h2></div></div>
      {data.ranking.length ? <ol className="ranking-list">{data.ranking.map((item, index) => <li key={item.funcionario.id}><span className="rank">0{index + 1}</span><Person name={item.funcionario.nome} detail={item.funcionario.departamento.nome} /><strong className="money">{money(item.total)}</strong></li>)}</ol> : <Empty title="Ainda não há dados para o ranking" />}
    </section>
  </>
}
