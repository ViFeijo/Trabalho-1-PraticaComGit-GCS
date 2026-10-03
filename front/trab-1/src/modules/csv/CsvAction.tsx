import { FiDownload } from 'react-icons/fi'
import { useState } from 'react'
import type { CostActionProps } from '../../core/modules.ts'
import { downloadCsv, downloadCsvFromApi } from './csvService.ts'

export default function CsvAction({ custos }: CostActionProps) {
  const [message, setMessage] = useState('')
  const [exporting, setExporting] = useState(false)

  async function handleExport() {
    try {
      setExporting(true)
      await downloadCsvFromApi()
      setMessage(`Exportação de ${custos.length} registros realizada com sucesso via API.`)
    } catch {
      downloadCsv(custos)
      setMessage(`Exportação de ${custos.length} registros iniciada.`)
    } finally {
      setExporting(false)
    }
  }

  return <div className="csv-action"><button className="button secondary" disabled={!custos.length || exporting} onClick={handleExport}><FiDownload aria-hidden="true" /> {exporting ? 'Exportando…' : 'Exportar CSV'}</button><span role="status" className="sr-only">{message}</span></div>
}
