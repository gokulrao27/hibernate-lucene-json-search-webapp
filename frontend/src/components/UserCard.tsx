import React from 'react'
import { User } from '../api/useUsers'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faEnvelope, faMapMarkerAlt, faIdCard, faUser } from '@fortawesome/free-solid-svg-icons'

import placeholder from '../assets/avatar-placeholder.svg'

type Props = {
  user: User
}

const UserCard: React.FC<Props> = ({ user }) => {
  return (
    <div className="bg-white rounded-lg p-4 card-shadow flex flex-col sm:flex-row gap-4 items-start">
      <img
        src={user.avatarUrl ?? placeholder}
        alt={`${user.firstName ?? ''} avatar`}
        className="h-20 w-20 rounded-full object-cover flex-shrink-0"
        onError={(e) => { (e.target as HTMLImageElement).src = placeholder }}
      />

      <div className="flex-1">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
          <div>
            <h3 className="text-lg font-semibold">{user.firstName} {user.lastName}</h3>
            <div className="text-sm text-slate-500">{user.email ?? ''}</div>
          </div>

          <div className="text-right">
            <div className="text-sm text-slate-500">Age: <span className="font-medium text-slate-700">{user.age ?? '-'}</span></div>
            {user.role && (
              <div className="mt-2 inline-block text-xs px-2 py-1 bg-primary/10 text-primary rounded-full">{user.role}</div>
            )}
          </div>
        </div>

        <div className="mt-3 grid grid-cols-1 sm:grid-cols-3 gap-2 text-sm text-slate-600">
          <div className="flex items-center gap-2">
            <FontAwesomeIcon icon={faIdCard} className="text-slate-400" />
            <span>SSN: {user.ssn ?? '-'}</span>
          </div>
          <div className="flex items-center gap-2">
            <FontAwesomeIcon icon={faMapMarkerAlt} className="text-slate-400" />
            <span>{user.city ?? '-'}{user.country ? `, ${user.country}` : ''}</span>
          </div>
          <div className="flex items-center gap-2">
            <FontAwesomeIcon icon={faEnvelope} className="text-slate-400" />
            <span>{user.email ?? '-'}</span>
          </div>
        </div>

        <div className="mt-3 text-xs text-slate-400 flex items-center gap-2">
          <FontAwesomeIcon icon={faUser} />
          <span className="italic">ID: {user.id ?? '-'}</span>
        </div>
      </div>
    </div>
  )
}

export default UserCard
