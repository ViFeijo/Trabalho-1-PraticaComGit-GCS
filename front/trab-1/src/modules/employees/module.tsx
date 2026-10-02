import type { AppModule } from '../../core/modules.ts'
import EmployeePage from './EmployeePage.tsx'
export default { id: 'employees', label: 'Funcionários', order: 10, Page: EmployeePage } satisfies AppModule
