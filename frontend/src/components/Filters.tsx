import React, { useState } from 'react'

type Props = {
  onAgeRangeChange: (range: [number, number] | null) => void
  onRoleChange: (role: string | null) => void
  onCountryChange?: (country: string | null) => void
}

const Filters: React.FC<Props> = ({ onAgeRangeChange, onRoleChange, onCountryChange }) => {
  const [min, setMin] = useState<number | ''>('')
  const [max, setMax] = useState<number | ''>('')
  const [country, setCountry] = useState<string>('')

  const apply = () => {
    if (min === '' && max === '') {
      onAgeRangeChange(null)
    } else {
      const mi = min === '' ? 0 : Number(min)
      const ma = max === '' ? 200 : Number(max)
      onAgeRangeChange([mi, ma])
    }
    if (onCountryChange) onCountryChange(country || null)
  }

  return (
    <div className="bg-white p-4 rounded card-shadow">
      <h4 className="font-semibold mb-3">Filters</h4>
      <div className="mb-3">
        <label className="block text-sm text-slate-600 mb-1">Role</label>
        <select onChange={(e) => onRoleChange(e.target.value || null)} className="w-full rounded border px-2 py-1">
          <option value="">Any</option>
          <option value="admin">Admin</option>
          <option value="moderator">Moderator</option>
          <option value="user">User</option>
        </select>
      </div>
      <div className="mb-3">
        <label className="block text-sm text-slate-600 mb-1">Country</label>
        <input value={country} onChange={e => setCountry(e.target.value)} type="text" placeholder="e.g. United States" className="w-full rounded border px-2 py-1" />
      </div>
      <div className="mb-3">
        <label className="block text-sm text-slate-600 mb-1">Age range</label>
        <div className="flex gap-2">
          <input value={min} onChange={e => setMin(e.target.value === '' ? '' : Number(e.target.value))} type="number" placeholder="min" className="w-1/2 rounded border px-2 py-1" />
          <input value={max} onChange={e => setMax(e.target.value === '' ? '' : Number(e.target.value))} type="number" placeholder="max" className="w-1/2 rounded border px-2 py-1" />
        </div>
      </div>
      <div className="flex gap-2">
        <button onClick={apply} className="px-3 py-1 bg-primary text-white rounded">Apply</button>
        <button onClick={() => { setMin(''); setMax(''); setCountry(''); onRoleChange(null); onAgeRangeChange(null); if (onCountryChange) onCountryChange(null) }} className="px-3 py-1 border rounded">Reset</button>
      </div>
    </div>
  )
}

export default Filters
