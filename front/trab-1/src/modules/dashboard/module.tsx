import './styles.css'
import type { AppModule } from '../../core/modules.ts'
import DashboardPage from './DashboardPage.tsx'
export default { id: 'dashboard', label: 'Dashboard', order: 30, Page: DashboardPage } satisfies AppModule
