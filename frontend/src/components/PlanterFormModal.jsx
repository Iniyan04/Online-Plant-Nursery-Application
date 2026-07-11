import { useState } from 'react'

const BLANK = {
  planterShape: '',
  planterheight: '',
  planterCapacity: '',
  drainageHoles: '',
  planterColor: '',
  planterStock: '',
  planterCost: '',
  plantId: '',
  seedId: ''
}

export default function PlanterFormModal({ initial, title, onSave, onClose, saving, error, plants, seeds }) {
  const [form, setForm] = useState(() => {
    if (!initial) return BLANK
    return {
      ...initial,
      plantId: initial.plants?.plantId ?? '',
      seedId: initial.seeds?.seedId ?? ''
    }
  })

  function update(field) {
    return (e) => setForm((f) => ({ ...f, [field]: e.target.value }))
  }

  function handleSubmit(e) {
    e.preventDefault()
    const payload = {
      planterShape: form.planterShape,
      planterheight: Number(form.planterheight) || 0,
      planterCapacity: Number(form.planterCapacity) || 0,
      drainageHoles: Number(form.drainageHoles) || 0,
      planterColor: Number(form.planterColor) || 0,
      planterStock: Number(form.planterStock) || 0,
      planterCost: Number(form.planterCost) || 0
    }
    if (form.plantId) {
      payload.plants = { plantId: Number(form.plantId) }
    }
    if (form.seedId) {
      payload.seeds = { seedId: Number(form.seedId) }
    }
    onSave(payload)
  }

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{title}</h2>
          <button className="modal-close" onClick={onClose} aria-label="Close">
            ×
          </button>
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="field">
            <label htmlFor="planterShape">Shape</label>
            <input id="planterShape" value={form.planterShape} onChange={update('planterShape')} placeholder="Round / Square / Oval" required />
          </div>

          <div className="field-row">
            <div className="field">
              <label htmlFor="planterheight">Height (cm)</label>
              <input id="planterheight" type="number" min="0" step="0.1" value={form.planterheight} onChange={update('planterheight')} />
            </div>
            <div className="field">
              <label htmlFor="planterCapacity">Capacity (L)</label>
              <input id="planterCapacity" type="number" min="0" value={form.planterCapacity} onChange={update('planterCapacity')} />
            </div>
          </div>

          <div className="field-row">
            <div className="field">
              <label htmlFor="drainageHoles">Drainage holes</label>
              <input id="drainageHoles" type="number" min="0" value={form.drainageHoles} onChange={update('drainageHoles')} />
            </div>
            <div className="field">
              <label htmlFor="planterColor">Color code</label>
              <input id="planterColor" type="number" min="0" value={form.planterColor} onChange={update('planterColor')} />
            </div>
          </div>

          <div className="field-row">
            <div className="field">
              <label htmlFor="planterStock">Stock</label>
              <input id="planterStock" type="number" min="0" value={form.planterStock} onChange={update('planterStock')} required />
            </div>
            <div className="field">
              <label htmlFor="planterCost">Cost (₹)</label>
              <input id="planterCost" type="number" min="0" value={form.planterCost} onChange={update('planterCost')} required />
            </div>
          </div>

          <div className="field-row">
            <div className="field">
              <label htmlFor="plantId">Linked plant (optional)</label>
              <select id="plantId" value={form.plantId} onChange={update('plantId')}>
                <option value="">None</option>
                {plants.map((p) => (
                  <option key={p.plantId} value={p.plantId}>{p.commonName}</option>
                ))}
              </select>
            </div>
            <div className="field">
              <label htmlFor="seedId">Linked seed (optional)</label>
              <select id="seedId" value={form.seedId} onChange={update('seedId')}>
                <option value="">None</option>
                {seeds.map((s) => (
                  <option key={s.seedId} value={s.seedId}>{s.commonName}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="modal-actions">
            <button type="button" className="btn btn-outline" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving…' : 'Save planter'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
