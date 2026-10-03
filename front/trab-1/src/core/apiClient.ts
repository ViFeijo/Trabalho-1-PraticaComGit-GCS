export class ApiError extends Error {
  status: number
  data?: unknown

  constructor(message: string, status: number, data?: unknown) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.data = data
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  const contentType = response.headers.get('content-type')
  const isJson = contentType && contentType.includes('application/json')

  let data: unknown
  if (isJson) {
    try {
      data = await response.json()
    } catch {
      data = null
    }
  } else {
    try {
      data = await response.text()
    } catch {
      data = null
    }
  }

  if (!response.ok) {
    let message = `Erro na requisição: ${response.status} ${response.statusText}`
    if (data && typeof data === 'object') {
      const errObj = data as Record<string, unknown>
      if (typeof errObj.error === 'string') {
        message = errObj.error
      } else if (typeof errObj.message === 'string') {
        message = errObj.message
      }
    } else if (typeof data === 'string' && data.trim()) {
      message = data
    }
    throw new ApiError(message, response.status, data)
  }

  if (response.status === 204 || data === null) {
    return null as T
  }

  return data as T
}

export const apiClient = {
  async get<T>(url: string): Promise<T> {
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
      },
    })
    return handleResponse<T>(response)
  },

  async post<T>(url: string, body?: unknown): Promise<T> {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
    return handleResponse<T>(response)
  },

  async del<T>(url: string): Promise<T> {
    const response = await fetch(url, {
      method: 'DELETE',
      headers: {
        'Accept': 'application/json',
      },
    })
    return handleResponse<T>(response)
  },

  async getBlob(url: string): Promise<Blob> {
    const response = await fetch(url, {
      method: 'GET',
    })
    if (!response.ok) {
      throw new ApiError(`Erro ao baixar arquivo: ${response.status}`, response.status)
    }
    return response.blob()
  },
}
