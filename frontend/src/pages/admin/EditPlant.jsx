import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { getPlantById, updatePlant } from '../../api/client.js'
import { useAuth } from '../../context/AuthContext.jsx'

export default function EditPlant() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { auth } = useAuth()
  const [form, setForm] = useState(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError('')

    getPlantById(id)
      .then((plant) => {
        if (!cancelled) setForm({ ...plant })
      })
      .catch((err) => {
        if (!cancelled) setError(err.message)
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [id])

  function update(field) {
    return (e) => setForm((current) => ({ ...current, [field]: e.target.value }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setSaving(true)
    setError('')

    try {
      await updatePlant(
        id,
        {
          ...form,
          plantHeight: Number(form.plantHeight) || 0,
          plantsStock: Number(form.plantsStock) || 0,
          plantCost: Number(form.plantCost) || 0
        },
        auth.admin
      )

      navigate('/admin/plants', {
        replace: true,
        state: { success: 'Plant updated successfully.' }
      })
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <div className="state-block">
        <p className="page-title">Loading plant...</p>
      </div>
    )
  }

  if (error && !form) {
    return (
      <div className="state-block">
        <p className="page-title">Could not load plant</p>
        <p>{error}</p>
        <Link className="btn btn-outline mt-24" to="/admin/plants">
          Back to manage plants
        </Link>
      </div>
    )
  }

  return (
    <div>
      <Link to="/admin/plants" className="btn-ghost admin-back-link">
        Back to manage plants
      </Link>

      <form className="form-card admin-form-card" onSubmit={handleSubmit}>
        <p className="eyebrow">Admin</p>
        <h1 className="page-title">Edit {form.commonName}</h1>
        <p className="page-sub">Update the plant details and save the changes back to the catalog.</p>

        {error && <div className="alert alert-error">{error}</div>}

        <div className="field">
          <label htmlFor="commonName">Common name</label>
          <input id="commonName" value={form.commonName} onChange={update('commonName')} required />
        </div>

        <div className="field-row">
          <div className="field">
            <label htmlFor="typeOfPlant">Type</label>
            <input id="typeOfPlant" value={form.typeOfPlant} onChange={update('typeOfPlant')} required />
          </div>
          <div className="field">
            <label htmlFor="difficultyLevel">Difficulty</label>
            <input id="difficultyLevel" value={form.difficultyLevel || ''} onChange={update('difficultyLevel')} />
          </div>
        </div>

        <div className="field-row">
          <div className="field">
            <label htmlFor="plantHeight">Height (cm)</label>
            <input id="plantHeight" type="number" min="0" value={form.plantHeight} onChange={update('plantHeight')} required />
          </div>
          <div className="field">
            <label htmlFor="plantSpread">Spread</label>
            <input id="plantSpread" value={form.plantSpread || ''} onChange={update('plantSpread')} />
          </div>
        </div>

        <div className="field-row">
          <div className="field">
            <label htmlFor="bloomTime">Bloom time</label>
            <input id="bloomTime" value={form.bloomTime || ''} onChange={update('bloomTime')} />
          </div>
          <div className="field">
            <label htmlFor="temparature">Ideal temperature</label>
            <input id="temparature" value={form.temparature || ''} onChange={update('temparature')} />
          </div>
        </div>

        <div className="field">
          <label htmlFor="medicinalOrCulinaryUse">Medicinal / culinary use</label>
          <input
            id="medicinalOrCulinaryUse"
            value={form.medicinalOrCulinaryUse || ''}
            onChange={update('medicinalOrCulinaryUse')}
          />
        </div>

        <div className="field">
          <label htmlFor="plantDescription">Description</label>
          <textarea
            id="plantDescription"
            rows={4}
            value={form.plantDescription || ''}
            onChange={update('plantDescription')}
          />
        </div>

        <div className="field-row">
          <div className="field">
            <label htmlFor="plantsStock">Stock</label>
            <input id="plantsStock" type="number" min="0" value={form.plantsStock} onChange={update('plantsStock')} required />
          </div>
          <div className="field">
            <label htmlFor="plantCost">Cost</label>
            <input id="plantCost" type="number" min="0" step="0.01" value={form.plantCost} onChange={update('plantCost')} required />
          </div>
        </div>

        <div className="modal-actions">
          <Link to="/admin/plants" className="btn btn-outline">
            Cancel
          </Link>
          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? 'Saving...' : 'Save changes'}
          </button>
        </div>
      </form>
    </div>
  )
}
