import { useState } from 'react'

const BLANK = {
  commonName: '',
  bloomTime: '',
  watering: '',
  difficultyLevel: '',
  temparature: '',
  typeOfSeeds: '',
  seedsDescription: '',
  seedsStock: '',
  seedsCost: '',
  seedsPerPacket: ''
}

export default function SeedFormModal({ initial, title, onSave, onClose, saving, error }) {
  const [form, setForm] = useState(() => (initial ? { ...initial } : BLANK))

  function update(field) {
    return (e) => setForm((f) => ({ ...f, [field]: e.target.value }))
  }

  function handleSubmit(e) {
    e.preventDefault()
    onSave({
      ...form,
      seedsStock: Number(form.seedsStock) || 0,
      seedsCost: Number(form.seedsCost) || 0,
      seedsPerPacket: Number(form.seedsPerPacket) || 0
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
              <label htmlFor="typeOfSeeds">Type</label>
              <input id="typeOfSeeds" value={form.typeOfSeeds} onChange={update('typeOfSeeds')} placeholder="Herb / Vegetable" required />
            </div>
            <div className="field">
              <label htmlFor="difficultyLevel">Difficulty</label>
              <input id="difficultyLevel" value={form.difficultyLevel} onChange={update('difficultyLevel')} placeholder="Easy / Medium / Hard" />
            </div>
          </div>

          <div className="field-row">
            <div className="field">
              <label htmlFor="bloomTime">Bloom time</label>
              <input id="bloomTime" value={form.bloomTime} onChange={update('bloomTime')} placeholder="e.g. Summer" />
            </div>
            <div className="field">
              <label htmlFor="watering">Watering</label>
              <input id="watering" value={form.watering} onChange={update('watering')} placeholder="e.g. Daily" />
            </div>
          </div>

          <div className="field">
            <label htmlFor="temparature">Ideal temperature</label>
            <input id="temparature" value={form.temparature} onChange={update('temparature')} placeholder="e.g. Warm" />
          </div>

          <div className="field">
            <label htmlFor="seedsDescription">Description</label>
            <textarea
              id="seedsDescription"
              rows={3}
              value={form.seedsDescription}
              onChange={update('seedsDescription')}
            />
          </div>

          <div className="field-row">
            <div className="field">
              <label htmlFor="seedsPerPacket">Seeds per packet</label>
              <input id="seedsPerPacket" type="number" min="0" value={form.seedsPerPacket} onChange={update('seedsPerPacket')} />
            </div>
            <div className="field">
              <label htmlFor="seedsStock">Stock</label>
              <input id="seedsStock" type="number" min="0" value={form.seedsStock} onChange={update('seedsStock')} required />
            </div>
          </div>

          <div className="field">
            <label htmlFor="seedsCost">Cost (₹)</label>
            <input id="seedsCost" type="number" min="0" step="0.01" value={form.seedsCost} onChange={update('seedsCost')} required />
          </div>

          <div className="modal-actions">
            <button type="button" className="btn btn-outline" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving…' : 'Save seed'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
