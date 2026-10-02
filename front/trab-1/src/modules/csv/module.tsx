import type { AppModule } from '../../core/modules.ts'
import CsvAction from './CsvAction.tsx'
export default { id: 'csv', label: 'Exportação CSV', order: 50, CostAction: CsvAction } satisfies AppModule
