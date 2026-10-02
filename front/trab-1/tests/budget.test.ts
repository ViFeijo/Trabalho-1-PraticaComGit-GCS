import test from 'node:test'
import assert from 'node:assert/strict'
import { createInitialState } from '../src/core/seed.ts'
import { budgetService } from '../src/modules/budget/budgetService.ts'

test('limite por mês/departamento, consumo, saldo e excesso informativo', () => {
  const { custos } = createInitialState()
  assert.equal(budgetService.consumption(4, '2026-09', custos).limite, null)
  for (const value of [0, -10, Infinity, 10.123]) assert.throws(() => budgetService.configure(4, '2026-09', value))
  assert.throws(() => budgetService.configure(4, '2026-13', 100))
  assert.throws(() => budgetService.configure(999, '2026-09', 100))
  budgetService.configure(4, '2026-09', 1000)
  assert.deepEqual(budgetService.consumption(4, '2026-09', custos), { limite: 1000, consumo: 1250, saldo: -250, percentual: 125, excedido: true })
  assert.equal(budgetService.consumption(4, '2026-10', custos).limite, null)
  assert.equal(budgetService.consumption(2, '2026-09', custos).limite, null)
  budgetService.configure(4, '2026-09', 1250)
  assert.equal(budgetService.consumption(4, '2026-09', custos).excedido, false)
  assert.equal(custos.length, 6)
})
