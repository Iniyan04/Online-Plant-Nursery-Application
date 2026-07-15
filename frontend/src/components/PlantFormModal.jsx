import { useState } from 'react'

const BLANK = {
  commonName: '',
  plantHeight: '',
  plantSpread: '',
  bloomTime: '',
  medicinalOrCulinaryUse: '',
  difficultyLevel: '',
  temparature: '',
  typeOfPlant: '',
  plantDescription: '',
  plantsStock: '',
  plantCost: '',
  imageUrl: ''
}

export default function PlantFormModal({ initial, title, onSave, onClose, saving, error }) {
  const [form, setForm] = useState(() => (initial ? { ...initial } : BLANK))

  function update(field) {
    return (e) => setForm((f) => ({ ...f, [field]: e.target.value }))
  }

  function handleSubmit(e) {
    e.preventDefault()
    onSave({
      ...form,
      plantHeight: Number(form.plantHeight) || 0,
      plantsStock: Number(form.plantsStock) || 0,
      plantCost: Number(form.plantCost) || 0
    })
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
            <label htmlFor="commonName">Common name</label>
            <input id="commonName" value={form.commonName} onChange={update('commonName')} required />
          </div>

          <div className="field-row">
            <div className="field">
              <label htmlFor="typeOfPlant">Type</label>
              <input id="typeOfPlant" value={form.typeOfPlant} onChange={update('typeOfPlant')} placeholder="Indoor / Outdoor" required />
            </div>
            <div className="field">
              <label htmlFor="difficultyLevel">Difficulty</label>
              <input id="difficultyLevel" value={form.difficultyLevel} onChange={update('difficultyLevel')} placeholder="Easy / Medium / Hard" />
            </div>
          </div>

          <div className="field-row">
            <div className="field">
              <label htmlFor="plantHeight">Height (cm)</label>
              <input id="plantHeight" type="number" min="0" value={form.plantHeight} onChange={update('plantHeight')} required />
            </div>
            <div className="field">
              <label htmlFor="plantSpread">Spread</label>
              <input id="plantSpread" value={form.plantSpread} onChange={update('plantSpread')} placeholder="e.g. Medium" />
            </div>
          </div>

          <div className="field-row">
            <div className="field">
              <label htmlFor="bloomTime">Bloom time</label>
              <input id="bloomTime" value={form.bloomTime} onChange={update('bloomTime')} placeholder="e.g. Summer" />
            </div>
            <div className="field">
              <label htmlFor="temparature">Ideal temperature</label>
              <input id="temparature" value={form.temparature} onChange={update('temparature')} placeholder="e.g. Warm" />
            </div>
          </div>

          <div className="field">
            <label htmlFor="medicinalOrCulinaryUse">Medicinal / culinary use</label>
            <input
              id="medicinalOrCulinaryUse"
              value={form.medicinalOrCulinaryUse}
              onChange={update('medicinalOrCulinaryUse')}
            />
          </div>

          <div className="field">
            <label htmlFor="plantDescription">Description</label>
            <textarea
              id="plantDescription"
              rows={3}
              value={form.plantDescription}
              onChange={update('plantDescription')}
            />
          </div>

          <div className="field-row">
            <div className="field">
              <label htmlFor="plantsStock">Stock</label>
              <input id="plantsStock" type="number" min="0" value={form.plantsStock} onChange={update('plantsStock')} required />
            </div>
            <div className="field">
              <label htmlFor="plantCost">Cost (₹)</label>
              <input id="plantCost" type="number" min="0" step="0.01" value={form.plantCost} onChange={update('plantCost')} required />
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
              <img src={form.imageUrl} alt="preview" className="img-preview"
                onError={(e) => { e.target.style.display='none'; e.target.nextSibling.style.display='flex' }} />
            ) : null}
            <div className="img-preview-placeholder" style={{ display: form.imageUrl ? 'none' : 'flex' }}>
              No image yet — paste a URL above to preview
            </div>
          </div>

          <div className="modal-actions">
            <button type="button" className="btn btn-outline" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving…' : 'Save plant'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
