import React, { useState, useMemo } from 'react'
import SearchBar from './components/SearchBar'
import UserGrid from './components/UserGrid'
import Filters from './components/Filters'
import Sorting from './components/Sorting'
import { useUsers } from './api/useUsers'
import Logo from './assets/logo.svg'

const App: React.FC = () => {
  const [query, setQuery] = useState('')
  const [ageRange, setAgeRange] = useState<[number, number] | null>(null)
  const [role, setRole] = useState<string | null>(null)
  const [country, setCountry] = useState<string | null>(null)
  const [sortByAgeDesc, setSortByAgeDesc] = useState(false)

  const canSearch = query.trim().length >= 3
  const { data, isLoading, isError, fetchNextPage, hasNextPage, refetch } = useUsers(query)

  const users = useMemo(() => {
    const list = data?.pages.flatMap(p => p.items) ?? []
    let filtered = list
    if (role) filtered = filtered.filter(u => (u.role ?? '').toLowerCase() === role.toLowerCase())
    if (country) filtered = filtered.filter(u => (u.country ?? '').toLowerCase() === country.toLowerCase())
    if (ageRange) filtered = filtered.filter(u => (u.age ?? 0) >= ageRange[0] && (u.age ?? 0) <= ageRange[1])
    filtered = filtered.slice().sort((a, b) => (a.age ?? 0) - (b.age ?? 0))
    if (sortByAgeDesc) filtered = filtered.reverse()
    return filtered
  }, [data, role, ageRange, sortByAgeDesc, country])

  return (
    <div className="min-h-screen flex flex-col">
      <header className="py-6 px-4 sm:px-8 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <img src={Logo} alt="Logo" className="h-10 w-10" />
          <h1 className="text-2xl font-semibold">Dummy Users</h1>
        </div>
      </header>

      <main className="flex-1 container mx-auto px-4 sm:px-8">
        <section className="hero rounded-lg p-8 my-6 flex flex-col items-center text-center">
          <h2 className="text-3xl font-semibold mb-4">Find people in the Dummy dataset</h2>
          <p className="mb-6 text-slate-600 max-w-xl">Search by name, email or SSN. Start typing at least 3 characters and press Enter or click search.</p>
          <div className="w-full max-w-2xl">
            <SearchBar onSearch={q => setQuery(q)} />
          </div>
        </section>

        <section className="flex flex-col md:flex-row gap-6">
          <aside className="md:w-64">
            <Filters onAgeRangeChange={setAgeRange} onRoleChange={setRole} onCountryChange={setCountry} />
            <Sorting sortDesc={sortByAgeDesc} onToggle={() => setSortByAgeDesc(s => !s)} />
          </aside>

          <div className="flex-1">
            {isError && (
              <div className="p-4 bg-red-50 text-red-700 rounded">Failed to load results. Try again later.</div>
            )}

            {!canSearch ? (
              <div className="p-6 bg-white rounded card-shadow text-center text-slate-600">Please enter at least 3 characters to search or browse the list below.</div>
            ) : (
              <UserGrid
                users={users}
                isLoading={isLoading}
                loadMore={() => fetchNextPage()}
                hasMore={!!hasNextPage}
                refetch={refetch}
              />
            )}
          </div>
        </section>
      </main>
    </div>
  )
}

export default App
