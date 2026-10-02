import { useState } from 'react'
import { FiUsers, FiDollarSign, FiBarChart2, FiPieChart } from 'react-icons/fi'
import type { AppModule } from './core/modules.ts'
import { ModuleContext } from './core/moduleContext.ts'

const loaded = import.meta.glob<{ default: AppModule }>('./modules/*/module.tsx', { eager: true })
const modules = Object.values(loaded).map(item => item.default).sort((a, b) => a.order - b.order)
const pages = modules.filter(item => item.Page)
const icons = { employees: FiUsers, costs: FiDollarSign, dashboard: FiBarChart2, budget: FiPieChart }

export default function App() {
  const [active, setActive] = useState('employees')
  const current = pages.find(item => item.id === active) ?? pages[0]
  const Page = current?.Page

  return <ModuleContext.Provider value={modules}>
    <header className="app-header">
      <strong className="app-name">GCS</strong>
      <nav aria-label="Navegação principal">
        {pages.map(item => {
          const Icon = icons[item.id as keyof typeof icons]
          return <button
            key={item.id}
            className="nav-item"
            aria-current={current?.id === item.id ? 'page' : undefined}
            onClick={() => setActive(item.id)}
          >
            {Icon && <Icon aria-hidden="true" />} {item.label}
          </button>
        })}
      </nav>
      <div className="header-actions">
        {modules.map(item => item.Header ? <item.Header key={item.id} /> : null)}
      </div>
    </header>
    <main id="main-content" key={current?.id}>{Page && <Page />}</main>
  </ModuleContext.Provider>
}
