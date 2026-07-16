import { useEffect, useMemo, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { getPlants, addPlant, deletePlant } from '../../api/client.js'
import EmptyState from '../../components/EmptyState.jsx'
import { TableSkeleton } from '../../components/LoadingBlock.jsx'
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
  const [search, setSearch] = useState('')

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

  const visiblePlants = useMemo(() => {
    const query = search.trim().toLowerCase()
    if (!query) return plants
    return plants.filter((plant) =>
      [plant.commonName, plant.typeOfPlant].some((value) =>
        String(value || '').toLowerCase().includes(query)
      )
    )
  }, [plants, search])

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
    <div className="page-fade-in admin-page-shell">
      <div className="admin-header-card">
        <div>
          <p className="eyebrow">Catalog management</p>
          <h1 className="page-title">Manage plants</h1>
          <p className="page-sub">Add new stock, update details, or retire plants from the catalog.</p>
        </div>
        <button className="btn btn-primary admin-add-btn" onClick={openAdd}>
          + Add plant
        </button>
      </div>

      <div className="toolbar admin-toolbar">
        <div className="field search-field admin-search-field">
          <label htmlFor="plantsTableSearch">Search plants</label>
          <input
            id="plantsTableSearch"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by common name or type"
          />
        </div>
        <div className="admin-toolbar-note">
          <span>{visiblePlants.length}</span>
          <small>plant records</small>
        </div>
      </div>

      {successMessage && <div className="alert alert-success">{successMessage}</div>}
      {listError && <div className="alert alert-error--dark">{listError}</div>}

      {loading && <TableSkeleton rows={5} columns={5} />}

      {!loading && !listError && visiblePlants.length === 0 && (
        <EmptyState
          icon="Plant"
          eyebrow="Inventory empty"
          title="No plants found"
          message={search ? 'Try a different search term to find existing plant records.' : 'Add your first plant to get the catalog started.'}
        />
      )}

      {!loading && visiblePlants.length > 0 && (
        <div className="table-wrap admin-table-wrap">
          <table className="data-table admin-data-table">
            <thead>
              <tr>
                <th>Common name</th>
                <th>Type</th>
                <th>Stock</th>
                <th>Cost</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {visiblePlants.map((plant, index) => (
                <tr key={plant.plantId} className={index % 2 === 0 ? 'zebra-row' : ''}>
                  <td>
                    <div className="admin-row-primary">
                      <strong>{plant.commonName}</strong>
                    </div>
                  </td>
                  <td><span className="table-badge">{plant.typeOfPlant}</span></td>
                  <td className="num">{plant.plantsStock}</td>
                  <td className="num">Rs. {plant.plantCost.toFixed(2)}</td>
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
                        {deletingId === plant.plantId ? 'Deleting...' : 'Delete'}
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
