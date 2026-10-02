import type { Custo } from '../../core/models.ts'

// Contrato equivalente ao CsvExportService Java, sem necessidade de API HTTP.
export interface CsvService { generate(custos: Custo[]): string }
function cell(value: string | number) {
  let text = String(value)
  const first = [...text].find(char => !/\s/.test(char) && char.charCodeAt(0) > 31)
  if (first && '=+@-'.includes(first)) text = `'${text}`
  return `"${text.replaceAll('"', '""')}"`
}
export const csvService: CsvService = {
  generate(custos) {
    const rows: (string | number)[][] = [
      ['ID', 'Data', 'Descrição', 'Valor', 'Categoria', 'Departamento', 'Funcionário'],
      ...custos.map(item => [item.id, item.data, item.descricao, item.valor.toFixed(2).replace('.', ','), item.categoria.nome, item.departamento.nome, item.funcionario.nome]),
    ]
    return '\uFEFF' + rows.map(row => row.map(cell).join(';')).join('\r\n') + '\r\n'
  },
}
export function downloadCsv(custos: Custo[]) {
  const blob = new Blob([csvService.generate(custos)], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = 'custos.csv'
  document.body.append(link)
  link.click()
  link.remove()
  setTimeout(() => URL.revokeObjectURL(url), 1000)
}
