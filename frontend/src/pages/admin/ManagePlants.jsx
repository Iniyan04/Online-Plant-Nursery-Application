import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { getPlants, addPlant, deletePlant } from '../../api/client.js'
import { useAuth } from '../../context/AuthContext.jsx'
import PlantFormModal from '../../components/PlantFormModal.jsx'

export default function ManagePlants() {
  const { auth } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const [plants, setPlants] = useState([])
  const [loading, setLoading] = useState(true)
  const [listError, setListError] = useState('')
  const [successMessage, setSuccessMessage] = useState(location.state?.success || '')

  const [showAddModal, setShowAddModal] = useState(false)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState('')

  const [deletingId, setDeletingId] = useState(null)

  function loadPlants() {
    setLoading(true)
    setListError('')
    getPlants()
      .then(setPlants)
      .catch((err) => setListError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(loadPlants, [])

  useEffect(() => {
    if (!location.state?.success) return
    setSuccessMessage(location.state.success)
    navigate(location.pathname, { replace: true, state: null })
  }, [location.pathname, location.state, navigate])

  function openAdd() {
    setFormError('')
    setShowAddModal(true)
  }

  function closeModal() {
    setShowAddModal(false)
  }

  async function handleSave(formValues) {
    setSaving(true)
    setFormError('')
    try {
      await addPlant(formValues, auth.admin)
      closeModal()
      loadPlants()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(plant) {
    if (!window.confirm(`Delete "${plant.commonName}"? This can't be undone.`)) return
    setDeletingId(plant.plantId)
    try {
      await deletePlant(plant.plantId, auth.admin)
      loadPlants()
    } catch (err) {
      alert(err.message)
    } finally {
      setDeletingId(null)
    }
  }

  return (
    <div>
      
      <h1 className="page-title">Manage plants</h1>
      <p className="page-sub">Add new stock, update details, or retire plants from the catalog.</p>

      <div className="toolbar">
        <div />
        <button className="btn btn-primary" onClick={openAdd}>
          + Add plant
        </button>
      </div>

      {successMessage && <div className="alert alert-success">{successMessage}</div>}
      {listError && <div className="alert alert-error--dark">{listError}</div>}

      {loading && (
        <div className="state-block">
          <p className="page-title">Loading plants…</p>
        </div>
      )}

      {!loading && !listError && plants.length === 0 && (
        <div className="state-block">
          <p className="page-title">No plants yet</p>
          <p>Add your first plant to get the catalog started.</p>
        </div>
      )}

      {!loading && plants.length > 0 && (
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>Common name</th>
                <th>Type</th>
                <th>Stock</th>
                <th>Cost</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {plants.map((plant) => (
                <tr key={plant.plantId}>
                  <td>{plant.commonName}</td>
                  <td>{plant.typeOfPlant}</td>
                  <td className="num">{plant.plantsStock}</td>
                  <td className="num">₹{plant.plantCost.toFixed(2)}</td>
                  <td>
                    <div className="row-actions">
                      <Link className="btn btn-outline btn-sm" to={`/admin/plants/${plant.plantId}/edit`}>
                        Edit
                      </Link>
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleDelete(plant)}
                        disabled={deletingId === plant.plantId}
                      >
                        {deletingId === plant.plantId ? 'Deleting…' : 'Delete'}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showAddModal && (
        <PlantFormModal
          title="Add plant"
          onSave={handleSave}
          onClose={closeModal}
          saving={saving}
          error={formError}
        />
      )}
    </div>
  )
}
