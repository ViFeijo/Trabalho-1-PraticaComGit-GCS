import test from 'node:test'
import assert from 'node:assert/strict'
import { appStore } from '../src/core/store.ts'
import { createInitialState } from '../src/core/seed.ts'
import { operatorService } from '../src/modules/operator/operatorService.ts'

test('operador começa vazio, pode ser trocado e valida referência', () => {
  appStore.update(createInitialState)
  assert.equal(appStore.getSnapshot().operadorId, null)
  operatorService.select(1)
  assert.equal(appStore.getSnapshot().operadorId, 1)
  operatorService.select(4)
  assert.equal(appStore.getSnapshot().operadorId, 4)
  assert.throws(() => operatorService.select(999))
  operatorService.select(null)
  assert.equal(appStore.getSnapshot().operadorId, null)
})
