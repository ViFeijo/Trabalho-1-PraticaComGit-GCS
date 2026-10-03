import test from 'node:test'
import assert from 'node:assert/strict'
import { createInitialState } from '../src/core/seed.ts'
import { getDashboard } from '../src/modules/dashboard/dashboardService.ts'

test('dashboard cobre mês atual, trimestre com virada de ano e ranking', () => {
  const { custos } = createInitialState()
  const september = getDashboard(custos, new Date(2026, 8, 30))
  assert.equal(september.total, 2571.25)
  assert.equal(september.count, 6)
  assert.deepEqual(september.ranking.map(item => item.funcionario.id), [4, 5, 3])
  assert.equal(september.departments[2].values[2], 370.75)
  const october = getDashboard(custos, new Date(2026, 9, 1))
  assert.equal(october.total, 0)
  assert.equal(october.departments[3].values[1], 1250)
  assert.deepEqual(getDashboard([], new Date(2027, 0, 1)).months.map(item => item.key), ['2026-11', '2026-12', '2027-01'])
  assert.deepEqual(getDashboard([]).ranking, [])
})
