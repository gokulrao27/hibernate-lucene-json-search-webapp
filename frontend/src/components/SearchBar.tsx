import React, { useState, useRef, useEffect } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons'
import clsx from 'clsx'

type Props = {
  onSearch: (q: string) => void
}

const DEBOUNCE_MS = 400

const SearchBar: React.FC<Props> = ({ onSearch }) => {
  const [value, setValue] = useState('')
  const inputRef = useRef<HTMLInputElement | null>(null)
  const timerRef = useRef<number | null>(null)

  const triggerSearch = (q?: string) => {
    const trimmed = (q !== undefined ? q : value).trim()
    if (trimmed.length >= 3) onSearch(trimmed)
    else onSearch('')
  }

  const onKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      if (timerRef.current) window.clearTimeout(timerRef.current)
      triggerSearch()
    }
  }

  useEffect(() => {
    if (timerRef.current) window.clearTimeout(timerRef.current)
    timerRef.current = window.setTimeout(() => {
      triggerSearch(value)
    }, DEBOUNCE_MS)
    return () => {
      if (timerRef.current) window.clearTimeout(timerRef.current)
    }
  }, [value])

  return (
    <div className="flex items-center bg-white rounded-full px-3 py-2 card-shadow">
      <input
        ref={inputRef}
        aria-label="Search users"
        className={clsx('flex-1 outline-none bg-transparent placeholder-slate-400 px-3')}
        placeholder="Search by name, email or SSN (min 3 chars)"
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onKeyDown={onKeyDown}
      />
      <button
        aria-label="Execute search"
        onClick={() => {
          if (timerRef.current) window.clearTimeout(timerRef.current)
          triggerSearch()
        }}
        className="p-2 rounded-full hover:bg-slate-100"
      >
        <FontAwesomeIcon icon={faMagnifyingGlass} className="text-slate-500" />
      </button>
    </div>
  )
}

export default SearchBar
