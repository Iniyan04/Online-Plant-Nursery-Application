import { useState } from 'react'

export default function CustomerFormModal({ mode, customer, onSave, onClose, saving, error }) {
  const [form, setForm] = useState(() => ({ ...customer }))
  const readonly = mode === 'view'

  function update(field) {
    return (e) => setForm((current) => ({ ...current, [field]: e.target.value }))
  }

  function handleSubmit(e) {
    e.preventDefault()
    onSave(form)
  }

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{readonly ? 'Customer details' : `Edit ${customer.customerName}`}</h2>
          <button className="modal-close" onClick={onClose} aria-label="Close">
            x
          </button>
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <div className="field">
            <label htmlFor="customerName">Name</label>
            <input id="customerName" value={form.customerName} onChange={update('customerName')} readOnly={readonly} required />
          </div>

          <div className="field">
            <label htmlFor="customerEmail">Email</label>
            <input id="customerEmail" type="email" value={form.customerEmail} onChange={update('customerEmail')} readOnly={readonly} required />
          </div>

          <div className="field">
            <label htmlFor="username">Username</label>
            <input id="username" value={form.username} onChange={update('username')} readOnly={readonly} required />
          </div>

          <div className="modal-actions">
            <button type="button" className="btn btn-outline" onClick={onClose}>
              {readonly ? 'Close' : 'Cancel'}
            </button>
            {!readonly && (
              <button type="submit" className="btn btn-primary" disabled={saving}>
                {saving ? 'Saving...' : 'Save customer'}
              </button>
            )}
          </div>
        </form>
      </div>
    </div>
  )
}
