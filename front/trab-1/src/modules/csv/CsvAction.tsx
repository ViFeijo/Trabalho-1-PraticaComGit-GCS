import { FiDownload } from 'react-icons/fi'
import { useState } from 'react'
import type { CostActionProps } from '../../core/modules.ts'
import { downloadCsv } from './csvService.ts'

export default function CsvAction({ custos }: CostActionProps) {
  const [message, setMessage] = useState('')
  return <div className="csv-action"><button className="button secondary" disabled={!custos.length} onClick={() => { downloadCsv(custos); setMessage(`Exportação de ${custos.length} registros iniciada.`) }}><FiDownload aria-hidden="true" /> Exportar CSV</button><span role="status" className="sr-only">{message}</span></div>
}
