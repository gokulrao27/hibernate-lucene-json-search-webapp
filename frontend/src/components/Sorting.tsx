import React from 'react'

type Props = {
  sortDesc: boolean
  onToggle: () => void
}

const Sorting: React.FC<Props> = ({ sortDesc, onToggle }) => {
  return (
    <div className="bg-white p-4 rounded card-shadow mt-4">
      <h4 className="font-semibold mb-3">Sorting</h4>
      <div className="flex items-center justify-between">
        <div className="text-sm text-slate-600">Sort by age</div>
        <button onClick={onToggle} className="px-3 py-1 bg-primary text-white rounded">
          {sortDesc ? 'Descending' : 'Ascending'}
        </button>
      </div>
    </div>
  )
}

export default Sorting

