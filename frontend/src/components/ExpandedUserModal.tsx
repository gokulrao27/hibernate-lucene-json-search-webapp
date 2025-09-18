import React, { useEffect, useState } from 'react'
import { User } from '../api/useUsers'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faEnvelope, faMapMarkerAlt, faIdCard, faUser, faXmark, faCompany, faMoneyCheckAlt, faClipboard } from '@fortawesome/free-solid-svg-icons'
import { http } from '../api/http'
import Spinner from './Spinner'

type Props = {
  user: User | null
  onClose: () => void
}

const ExpandedUserModal: React.FC<Props> = ({ user, onClose }) => {
  if (!user) return null

  const [detail, setDetail] = useState<any | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [tab, setTab] = useState<'overview' | 'company' | 'bank' | 'raw'>('overview')
  const [copied, setCopied] = useState(false)

  useEffect(() => {
    setDetail(null)
    setError(null)
    setCopied(false)
    setTab('overview')
    if (!user?.id) return
    const controller = new AbortController()
    setLoading(true)
    http.get(`/api/users/${user.id}`, { signal: controller.signal })
      .then(resp => setDetail(resp.data))
      .catch((e: any) => {
        if (e.name === 'CanceledError' || e.message === 'canceled') return
        setError(e?.message || 'Failed to load user details')
      })
      .finally(() => setLoading(false))

    return () => controller.abort()
  }, [user])

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose()
    }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [onClose])

  const doCopy = async () => {
    try {
      await navigator.clipboard.writeText(JSON.stringify(detail ?? user, null, 2))
      setCopied(true)
      setTimeout(() => setCopied(false), 1500)
    } catch {
      setCopied(false)
    }
  }

  return (
    <div className={`expanded-overlay show`} role="dialog" aria-modal="true" onClick={onClose}>
      <div className="expanded-panel" onClick={(e) => e.stopPropagation()}>
        <div className="flex items-start justify-between gap-4">
          <div className="flex items-center gap-4">
            <img src={detail?.image ?? user.avatarUrl ?? '/src/assets/avatar-placeholder.svg'} alt="avatar" className="h-24 w-24 rounded-full object-cover" />
            <div>
              <h2 className="text-2xl font-semibold">{detail?.firstName ?? user.firstName} {detail?.lastName ?? user.lastName}</h2>
              <div className="text-sm text-slate-600">{detail?.role ? <span className="px-2 py-1 bg-primary/10 text-primary rounded-full text-xs">{detail.role}</span> : null}</div>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <button onClick={doCopy} className="text-slate-600 hover:text-slate-800 p-2 rounded" title="Copy JSON">
              <FontAwesomeIcon icon={faClipboard} />
            </button>
            <button onClick={onClose} className="text-slate-600 hover:text-slate-800 p-2 rounded">
              <FontAwesomeIcon icon={faXmark} />
            </button>
          </div>
        </div>

        <div className="mt-4">
          <div className="flex gap-2 border-b pb-2">
            <button onClick={() => setTab('overview')} className={`px-3 py-1 ${tab==='overview' ? 'border-b-2 border-primary text-primary' : 'text-slate-600'}`}>Overview</button>
            <button onClick={() => setTab('company')} className={`px-3 py-1 ${tab==='company' ? 'border-b-2 border-primary text-primary' : 'text-slate-600'}`}>Company</button>
            <button onClick={() => setTab('bank')} className={`px-3 py-1 ${tab==='bank' ? 'border-b-2 border-primary text-primary' : 'text-slate-600'}`}>Bank</button>
            <button onClick={() => setTab('raw')} className={`px-3 py-1 ml-auto ${tab==='raw' ? 'border-b-2 border-primary text-primary' : 'text-slate-600'}`}>Raw</button>
          </div>

          <div className="mt-4">
            {loading ? (
              <div className="flex items-center justify-center py-8"><Spinner /></div>
            ) : error ? (
              <div className="text-red-600">{error}</div>
            ) : tab === 'overview' ? (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <div className="flex items-center gap-2 text-sm text-slate-700"><FontAwesomeIcon icon={faEnvelope} /><span>{detail?.email ?? user.email ?? '-'}</span></div>
                  <div className="flex items-center gap-2 text-sm text-slate-700"><FontAwesomeIcon icon={faIdCard} /><span>SSN: {detail?.ssn ?? user.ssn ?? '-'}</span></div>
                  <div className="flex items-center gap-2 text-sm text-slate-700"><FontAwesomeIcon icon={faMapMarkerAlt} /><span>{detail?.address?.city ?? detail?.city ?? user.city ?? '-'}{detail?.address?.country || detail?.country ? `, ${detail?.address?.country ?? detail?.country ?? user.country ?? ''}` : ''}</span></div>
                  <div className="text-sm text-slate-700">Age: <span className="font-medium">{detail?.age ?? user.age ?? '-'}</span></div>
                </div>

                <div className="space-y-2 text-sm text-slate-700">
                  <div><span className="font-semibold">ID:</span> {detail?.id ?? user.id}</div>
                  <div><span className="font-semibold">Gender:</span> {detail?.gender ?? user.gender ?? '-'}</div>
                  <div><span className="font-semibold">Phone:</span> {detail?.phone ?? '-'}</div>
                  <div><span className="font-semibold">University:</span> {detail?.university ?? '-'}</div>
                </div>
              </div>
            ) : tab === 'company' ? (
              <div className="space-y-3">
                <div className="text-sm text-slate-700"><span className="font-semibold">Company:</span> {detail?.company?.name ?? '-'}</div>
                <div className="text-sm text-slate-700"><span className="font-semibold">Department:</span> {detail?.company?.department ?? '-'}</div>
                <div className="text-sm text-slate-700"><span className="font-semibold">Title:</span> {detail?.company?.title ?? '-'}</div>
                <div className="text-sm text-slate-700"><span className="font-semibold">Address:</span> {detail?.company?.companyAddress ?? detail?.company?.address?.address ?? '-'}</div>
              </div>
            ) : tab === 'bank' ? (
              <div className="space-y-3">
                <div className="text-sm text-slate-700"><span className="font-semibold">Card Number:</span> {detail?.bank?.cardNumber ?? '-'}</div>
                <div className="text-sm text-slate-700"><span className="font-semibold">Card Type:</span> {detail?.bank?.cardType ?? '-'}</div>
                <div className="text-sm text-slate-700"><span className="font-semibold">Currency:</span> {detail?.bank?.currency ?? '-'}</div>
                <div className="text-sm text-slate-700"><span className="font-semibold">IBAN:</span> {detail?.bank?.iban ?? '-'}</div>
              </div>
            ) : (
              <div>
                <div className="flex items-center justify-between mb-2">
                  <div className="text-sm text-slate-600">Raw JSON</div>
                  <div className="text-sm text-slate-500">{copied ? 'Copied' : ''}</div>
                </div>
                <pre className="bg-slate-50 p-3 rounded text-xs max-h-96 overflow-auto">{JSON.stringify(detail ?? user, null, 2)}</pre>
              </div>
            )}
          </div>
        </div>

        <div className="mt-6 text-right">
          <button onClick={onClose} className="px-4 py-2 bg-primary text-white rounded">Close</button>
        </div>
      </div>
    </div>
  )
}

export default ExpandedUserModal
