import test from 'node:test'
import assert from 'node:assert/strict'
import { createInitialState } from '../src/core/seed.ts'
import { csvService } from '../src/modules/csv/csvService.ts'

test('CSV preserva seleção e ordem, BOM, decimal, aspas e quebras', () => {
  const { custos } = createInitialState()
  const result = csvService.generate([custos[4], custos[1]])
  assert.ok(result.startsWith('\uFEFF"ID";'))
  assert.ok(result.includes('"180,50"'))
  assert.ok(result.indexOf('"5";') < result.indexOf('"2";'))
  assert.equal(result.split('\r\n').length, 4)
  assert.ok(!result.includes('Compra de equipamentos'))
  const quoted = csvService.generate([{ ...custos[0], descricao: 'A; "B"\nC' }])
  assert.ok(quoted.includes('"A; ""B""\nC"'))
  assert.equal(csvService.generate([]).split('\r\n').length, 2)
})
test('CSV neutraliza fórmulas em todos os campos textuais', () => {
  const custo = createInitialState().custos[0]
  for (const descricao of ['=1+1', '+2', '-3', '@SUM(A1)', '  =1', '\t=1', '\u0001=1', '\u00a0=1']) {
    assert.ok(csvService.generate([{ ...custo, descricao }]).includes(`"'${descricao}"`))
  }
  assert.ok(csvService.generate([{ ...custo, funcionario: { ...custo.funcionario, nome: '=name' } }]).includes('"\'=name"'))
})
