import test from 'node:test'
import assert from 'node:assert/strict'
import { appStore } from '../src/core/store.ts'
import { createInitialState } from '../src/core/seed.ts'
import { costService, emptyFilters, filterCosts, sortCosts } from '../src/modules/costs/costService.ts'

test('custos validam operador, valor, data e referências', () => {
  appStore.update(createInitialState)
  const input = { valor: 12.5, descricao: ' Café ', data: '2026-10-01', categoriaId: 3 }
  assert.throws(() => costService.create(input), /operador/)
  appStore.update(state => ({ ...state, operadorId: 4 }))
  for (const valor of [0, -1, NaN, Infinity, 1.234]) assert.throws(() => costService.create({ ...input, valor }))
  assert.throws(() => costService.create({ ...input, descricao: ' ' }))
  assert.throws(() => costService.create({ ...input, data: '2026-02-30' }))
  assert.throws(() => costService.create({ ...input, categoriaId: 999 }))
  const created = costService.create(input)
  assert.equal(created.descricao, 'Café')
  assert.equal(created.departamento.id, 4)
  assert.equal(created.funcionario.id, 4)
})
test('filtros combinados e exclusão consideram a lista inteira', () => {
  appStore.update(createInitialState)
  const state = appStore.getSnapshot()
  assert.deepEqual(sortCosts(state.custos).map(item => item.id), [6, 5, 4, 3, 2, 1])
  const filtered = filterCosts(state.custos, { descricao: 'REUNIAO', categoria: '2', departamento: '2', inicio: '2026-09-12', fim: '2026-09-12' })
  assert.deepEqual(filtered.map(item => item.id), [2])
  assert.throws(() => costService.remove(2), /mais recente/)
  costService.remove(6)
  assert.equal(appStore.getSnapshot().custos.length, 5)
  assert.equal(costService.list(emptyFilters)[0].id, 5)
  assert.deepEqual(filterCosts(state.custos, { ...emptyFilters, descricao: 'inexistente' }), [])
  const tied = [{ ...state.custos[0], id: 7 }, { ...state.custos[0], id: 8 }]
  assert.equal(sortCosts(tied)[0].id, 8)
})
