import test from 'node:test'
import assert from 'node:assert/strict'
import { apiClient, ApiError } from '../src/core/apiClient.ts'

test('apiClient e ApiError tratam mensagens de erro e status HTTP', async () => {
  const err = new ApiError('Falha de validação', 400, { error: 'Falha de validação' })
  assert.equal(err.message, 'Falha de validação')
  assert.equal(err.status, 400)
  assert.deepEqual(err.data, { error: 'Falha de validação' })
  assert.equal(err.name, 'ApiError')

  // Mock global fetch para testar tratamento de resposta JSON e erros
  const originalFetch = globalThis.fetch
  try {
    globalThis.fetch = (async () => ({
      ok: true,
      status: 200,
      headers: new Headers({ 'content-type': 'application/json' }),
      json: async () => ({ status: 'ok' }),
    })) as typeof globalThis.fetch

    const data = await apiClient.get<{ status: string }>('/api/test')
    assert.equal(data.status, 'ok')

    // Mock erro 400 com JSON { error: 'Descrição inválida' }
    globalThis.fetch = (async () => ({
      ok: false,
      status: 400,
      statusText: 'Bad Request',
      headers: new Headers({ 'content-type': 'application/json' }),
      json: async () => ({ error: 'Descrição inválida' }),
    })) as typeof globalThis.fetch

    await assert.rejects(
      async () => apiClient.post('/api/test', {}),
      (error: Error) => {
        assert.ok(error instanceof ApiError)
        assert.equal(error.status, 400)
        assert.equal(error.message, 'Descrição inválida')
        return true
      },
    )

    // Mock 204 No Content
    globalThis.fetch = (async () => ({
      ok: true,
      status: 204,
      headers: new Headers(),
      json: async () => null,
      text: async () => '',
    })) as typeof globalThis.fetch

    const empty = await apiClient.del('/api/test/1')
    assert.equal(empty, null)
  } finally {
    globalThis.fetch = originalFetch
  }
})
