import test from 'node:test'
import assert from 'node:assert/strict'
import { appStore } from '../src/core/store.ts'
import { createInitialState } from '../src/core/seed.ts'
import { employeeService } from '../src/modules/employees/employeeService.ts'

test('cadastro valida espaços, gera ID e preserva departamento', () => {
  appStore.update(createInitialState)
  assert.throws(() => employeeService.create({ nome: '  ', cargo: 'Analista', departamentoId: 1 }))
  assert.throws(() => employeeService.create({ nome: 'Teste', cargo: '  ', departamentoId: 1 }))
  assert.throws(() => employeeService.create({ nome: 'Teste', cargo: 'Analista', departamentoId: 99 }))
  const result = employeeService.create({ nome: ' João ', cargo: ' Analista ', departamentoId: 4 })
  assert.equal(result.id, 6)
  assert.equal(result.nome, 'João')
  assert.equal(result.cargo, 'Analista')
  assert.equal(result.departamento.nome, 'Tecnologia')
  assert.equal(appStore.getSnapshot().funcionarios.length, 6)
})
