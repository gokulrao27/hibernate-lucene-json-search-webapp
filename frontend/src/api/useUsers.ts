import { useInfiniteQuery } from '@tanstack/react-query'
import axios from 'axios'
import { http, requestDedup } from './http'

export type User = {
  id?: number
  firstName?: string
  lastName?: string
  ssn?: string
  email?: string
  age?: number
  gender?: string
  avatarUrl?: string | null
  role?: string | null
  city?: string | null
  country?: string | null
}


type PageResponse = {
  content?: any[]
  totalElements?: number
  totalPages?: number
  number?: number
  size?: number
  
  users?: any[]
  total?: number
  skip?: number
  limit?: number
}

const PAGE_SIZE = 20

function mapToUser(src: any): User {
  if (!src) return {}
  return {
    id: src.id,
    firstName: src.firstName || src.name?.first || src.first_name,
    lastName: src.lastName || src.name?.last || src.last_name,
    ssn: src.ssn || src.socialSecurityNumber || null,
    email: src.email,
    age: src.age !== undefined ? Number(src.age) : undefined,
    gender: src.gender,
    avatarUrl: src.avatarUrl || src.image || src.picture || null,
    role: src.role || src.userRole || null,
    city: src.address?.city || null,
    country: src.address?.country || null
  }
}

export const useUsers = (query: string) => {
  return useInfiniteQuery(
    ['users', query],
    async ({ pageParam = 0, signal }: any) => {
      const params: any = { page: pageParam, size: PAGE_SIZE }
      
      if (query && query.length >= 3) params.q = `"${query}"`

      try {
        
        const resp = await requestDedup<{ data: PageResponse }>({ url: '/api/users', method: 'get', params, signal })
        
        const data = resp.data

        
        if (Array.isArray(data)) {
          const items = (data || []).map(mapToUser)
          return { items, page: pageParam, totalPages: 0 }
        }

        if (!data) {
          console.error('useUsers: empty response data', { params })
          
          return { items: [], page: pageParam, totalPages: 0 }
        }

        
        if (Array.isArray((data as any).content)) {
          const items = (data.content || []).map(mapToUser)
          return { items, page: data.number ?? pageParam, totalPages: data.totalPages ?? 0 }
        }

        if (Array.isArray((data as any).users)) {
          const users = (data.users || []).map(mapToUser)
          const total = Number(data.total ?? users.length)
          const limit = Number(data.limit ?? PAGE_SIZE)
          const skip = Number(data.skip ?? pageParam * limit)
          const page = Math.floor(skip / limit)
          const totalPages = Math.ceil(total / limit)
          return { items: users, page, totalPages }
        }

        
        return { items: [], page: pageParam, totalPages: 0 }
      } catch (err: any) {
        
        if (axios.isCancel && axios.isCancel(err)) {
          
          return { items: [], page: pageParam, totalPages: 0 }
        }
        
        const message = err?.message || 'Request failed'
        console.error('useUsers error', { message, params: { q: params.q, pageParam } })
        
        return { items: [], page: pageParam, totalPages: 0 }
      }
    },
    {
      keepPreviousData: true,
      enabled: query.length >= 3, 
      getNextPageParam: (last) => {
        if (last.page + 1 < (last.totalPages ?? 0)) return last.page + 1
        return undefined
      },
      staleTime: 1000 * 60, 
    }
  )
}
