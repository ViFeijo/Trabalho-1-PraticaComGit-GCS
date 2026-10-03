import './styles.css'
import type { AppModule } from '../../core/modules.ts'
import CostPage from './CostPage.tsx'
export default { id: 'costs', label: 'Custos', order: 20, Page: CostPage } satisfies AppModule
