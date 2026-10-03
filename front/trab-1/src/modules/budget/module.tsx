import './styles.css'
import type { AppModule } from '../../core/modules.ts'
import BudgetPage from './BudgetPage.tsx'
export default { id: 'budget', label: 'Limites mensais', order: 40, Page: BudgetPage } satisfies AppModule
