import React, { useEffect, useRef, useCallback, useState } from 'react'
import { User } from '../api/useUsers'
import UserCard from './UserCard'
import Spinner from './Spinner'
import ExpandedUserModal from './ExpandedUserModal'

type Props = {
  users: User[]
  isLoading: boolean
  loadMore: () => void
  hasMore: boolean
  refetch: () => void
}

const UserGrid: React.FC<Props> = ({ users, isLoading, loadMore, hasMore, refetch }) => {
  const loaderRef = useRef<HTMLDivElement | null>(null)
  const [selectedUser, setSelectedUser] = useState<User | null>(null)
  const [flippedId, setFlippedId] = useState<number | string | null>(null)

  const onIntersect: IntersectionObserverCallback = useCallback((entries) => {
    const first = entries[0]
    if (first.isIntersecting && hasMore && !isLoading) {
      loadMore()
    }
  }, [hasMore, isLoading, loadMore])

  useEffect(() => {
    const node = loaderRef.current
    if (!node) return
    // Don't attach observer until we have some results and not currently loading to avoid duplicate initial fetches
    if (isLoading || users.length === 0) return
    const observer = new IntersectionObserver(onIntersect, { root: null, rootMargin: '200px', threshold: 0.1 })
    observer.observe(node)
    return () => observer.disconnect()
  }, [onIntersect, isLoading, users.length])

  const handleCardClick = (user: User, key: number | string) => {
    setFlippedId(key)
    // show modal after flip animation
    window.setTimeout(() => setSelectedUser(user), 260)
  }

  const handleClose = () => {
    setSelectedUser(null)
    // flip back after modal close
    window.setTimeout(() => setFlippedId(null), 200)
  }

  return (
    <div>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {users.map((u, idx) => {
          const key = u.id ?? `idx-${idx}`
          const isFlipped = flippedId === key
          return (
            <div key={key} className="card-wrapper card-content">
              <div className={`card-inner ${isFlipped ? 'card-flip' : ''}`} onClick={() => handleCardClick(u, key)}>
                <div className="card-face">
                  <UserCard user={u} />
                </div>
                {/* back face could be used for a compact view; keep same for now */}
                <div className="card-face" aria-hidden>{/* back face placeholder */}</div>
              </div>
            </div>
          )
        })}
      </div>

      <div ref={loaderRef} className="mt-6 flex justify-center">
        {isLoading ? (
          <Spinner />
        ) : hasMore ? (
          <button onClick={() => loadMore()} className="px-4 py-2 bg-primary text-white rounded">Load more</button>
        ) : (
          <div className="text-slate-500">No more results</div>
        )}
      </div>

      {users.length === 0 && !isLoading && (
        <div className="mt-6 text-center text-slate-600">No results. Try a different query.</div>
      )}

      <ExpandedUserModal user={selectedUser} onClose={handleClose} />
    </div>
  )
}

export default UserGrid
