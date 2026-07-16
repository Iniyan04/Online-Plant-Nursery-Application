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
  seedId: '',
  imageUrl: ''
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
    return (e) => setForm((current) => ({ ...current, [field]: e.target.value }))
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
      planterCost: Number(form.planterCost) || 0,
      imageUrl: form.imageUrl || null
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
      <div className="modal-card modal-card-premium" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div>
            <span className="modal-icon-badge" aria-hidden="true">T</span>
            <h2>{title}</h2>
          </div>
          <button className="modal-close" onClick={onClose} aria-label="Close">
            x
          </button>
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="modal-section-title">Planter details</div>
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

          <div className="modal-section-title">Pricing and links</div>
          <div className="field-row">
            <div className="field">
              <label htmlFor="planterStock">Stock</label>
              <input id="planterStock" type="number" min="0" value={form.planterStock} onChange={update('planterStock')} required />
            </div>
            <div className="field">
              <label htmlFor="planterCost">Cost (Rs.)</label>
              <input id="planterCost" type="number" min="0" value={form.planterCost} onChange={update('planterCost')} required />
            </div>
          </div>

          <div className="field-row">
            <div className="field">
              <label htmlFor="plantId">Linked plant (optional)</label>
              <select id="plantId" value={form.plantId} onChange={update('plantId')}>
                <option value="">None</option>
                {plants.map((plant) => (
                  <option key={plant.plantId} value={plant.plantId}>{plant.commonName}</option>
                ))}
              </select>
            </div>
            <div className="field">
              <label htmlFor="seedId">Linked seed (optional)</label>
              <select id="seedId" value={form.seedId} onChange={update('seedId')}>
                <option value="">None</option>
                {seeds.map((seed) => (
                  <option key={seed.seedId} value={seed.seedId}>{seed.commonName}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="field">
            <label htmlFor="imageUrl">Image URL</label>
            <input
              id="imageUrl"
              value={form.imageUrl || ''}
              onChange={update('imageUrl')}
              placeholder="Paste an image URL from Unsplash or Google Images"
            />
            {form.imageUrl ? (
              <img
                src={form.imageUrl}
                alt="preview"
                className="img-preview"
                onError={(e) => {
                  e.target.style.display = 'none'
                  e.target.nextSibling.style.display = 'flex'
                }}
              />
            ) : null}
            <div className="img-preview-placeholder" style={{ display: form.imageUrl ? 'none' : 'flex' }}>
              No image yet - paste a URL above to preview
            </div>
          </div>

          <div className="modal-actions">
            <button type="button" className="btn btn-outline" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving...' : 'Save planter'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
