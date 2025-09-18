import axios from 'axios'

const BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export const http = axios.create({
  baseURL: BASE,
  headers: {
    'Content-Type': 'application/json'
  }
})

;(function attachInterceptors() {
  try {
    http.interceptors.request.use((config) => {
      return config
    })

    http.interceptors.response.use((resp) => {
      return resp
    }, (err) => {
      return Promise.reject(err)
    })
  } catch (e) {
  }
})()

const pendingRequests = new Map<string, Promise<any>>()

export function requestDedup<T = any>(config: { url: string; method?: 'get' | 'post' | 'put' | 'delete'; params?: any; data?: any; signal?: AbortSignal }) {
  const key = `${config.method || 'get'}::${config.url}::${JSON.stringify(config.params || {})}::${JSON.stringify(config.data || {})}`
  if (pendingRequests.has(key)) {
    return pendingRequests.get(key) as Promise<T>
  }
  const promise = (async () => {
    try {
      const resp = await http.request<T>({ url: config.url, method: config.method || 'get', params: config.params, data: config.data, signal: config.signal })
      return resp
    } finally {
      pendingRequests.delete(key)
    }
  })()
  pendingRequests.set(key, promise)
  return promise
}
