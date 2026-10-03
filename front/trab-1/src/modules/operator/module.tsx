import './styles.css'
import type { AppModule } from '../../core/modules.ts'
import OperatorSelector from './OperatorSelector.tsx'
export default { id: 'operator', label: 'Operador', order: 15, Header: OperatorSelector } satisfies AppModule
