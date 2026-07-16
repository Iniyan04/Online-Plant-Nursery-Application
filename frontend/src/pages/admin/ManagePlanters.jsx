import { useEffect, useMemo, useState } from 'react'
import { getPlanters, getPlants, getSeeds, addPlanter, updatePlanter, deletePlanter } from '../../api/client.js'
import EmptyState from '../../components/EmptyState.jsx'
import { TableSkeleton } from '../../components/LoadingBlock.jsx'
import { useAuth } from '../../context/AuthContext.jsx'
import PlanterFormModal from '../../components/PlanterFormModal.jsx'

export default function ManagePlanters() {
  const { auth } = useAuth()
  const [planters, setPlanters] = useState([])
  const [plants, setPlants] = useState([])
  const [seeds, setSeeds] = useState([])
  const [loading, setLoading] = useState(true)
  const [listError, setListError] = useState('')
  const [search, setSearch] = useState('')

  const [modalMode, setModalMode] = useState(null)
  const [editingPlanter, setEditingPlanter] = useState(null)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState('')

  const [deletingId, setDeletingId] = useState(null)

  function loadPlanters() {
    setLoading(true)
    setListError('')
    Promise.all([getPlanters(), getPlants(), getSeeds()])
      .then(([planterData, plantData, seedData]) => {
        setPlanters(planterData)
        setPlants(plantData)
        setSeeds(seedData)
      })
      .catch((err) => setListError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(loadPlanters, [])

  const visiblePlanters = useMemo(() => {
    const query = search.trim().toLowerCase()
    if (!query) return planters
    return planters.filter((planter) =>
      [planter.planterShape, `${planter.planterCapacity} L`].some((value) =>
        String(value || '').toLowerCase().includes(query)
      )
    )
  }, [planters, search])

  function openAdd() {
    setEditingPlanter(null)
    setFormError('')
    setModalMode('add')
  }

  function openEdit(planter) {
    setEditingPlanter(planter)
    setFormError('')
    setModalMode('edit')
  }

  function closeModal() {
    setModalMode(null)
    setEditingPlanter(null)
  }

  async function handleSave(formValues) {
    setSaving(true)
    setFormError('')
    try {
      if (modalMode === 'add') {
        await addPlanter(formValues, auth.admin)
      } else {
        await updatePlanter(editingPlanter.planterId, formValues, auth.admin)
      }
      closeModal()
      loadPlanters()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(planter) {
    if (!window.confirm(`Delete "${planter.planterShape}" planter? This can't be undone.`)) return
    setDeletingId(planter.planterId)
    try {
      await deletePlanter(planter.planterId, auth.admin)
      loadPlanters()
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
          <h1 className="page-title">Manage planters</h1>
          <p className="page-sub">Add new pots, update details, or retire planters from the catalog.</p>
        </div>
        <button className="btn btn-primary admin-add-btn" onClick={openAdd}>
          + Add planter
        </button>
      </div>

      <div className="toolbar admin-toolbar">
        <div className="field search-field admin-search-field">
          <label htmlFor="plantersTableSearch">Search planters</label>
          <input
            id="plantersTableSearch"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by shape or capacity"
          />
        </div>
        <div className="admin-toolbar-note">
          <span>{visiblePlanters.length}</span>
          <small>planter records</small>
        </div>
      </div>

      {listError && <div className="alert alert-error--dark">{listError}</div>}

      {loading && <TableSkeleton rows={5} columns={5} />}

      {!loading && !listError && visiblePlanters.length === 0 && (
        <EmptyState
          icon="Planter"
          eyebrow="Inventory empty"
          title="No planters found"
          message={search ? 'Try a different search term to find existing planter records.' : 'Add your first planter to get the catalog started.'}
        />
      )}

      {!loading && visiblePlanters.length > 0 && (
        <div className="table-wrap admin-table-wrap">
          <table className="data-table admin-data-table">
            <thead>
              <tr>
                <th>Shape</th>
                <th>Capacity</th>
                <th>Stock</th>
                <th>Cost</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {visiblePlanters.map((planter, index) => (
                <tr key={planter.planterId} className={index % 2 === 0 ? 'zebra-row' : ''}>
                  <td>
                    <div className="admin-row-primary">
                      <strong>{planter.planterShape}</strong>
                    </div>
                  </td>
                  <td>{planter.planterCapacity} L</td>
                  <td className="num">{planter.planterStock}</td>
                  <td className="num">Rs. {Number(planter.planterCost).toFixed(2)}</td>
                  <td>
                    <div className="row-actions">
                      <button className="btn btn-outline btn-sm" onClick={() => openEdit(planter)}>
                        Edit
                      </button>
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleDelete(planter)}
                        disabled={deletingId === planter.planterId}
                      >
                        {deletingId === planter.planterId ? 'Deleting...' : 'Delete'}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {modalMode && (
        <PlanterFormModal
          title={modalMode === 'add' ? 'Add planter' : `Edit ${editingPlanter.planterShape}`}
          initial={editingPlanter}
          plants={plants}
          seeds={seeds}
          onSave={handleSave}
          onClose={closeModal}
          saving={saving}
          error={formError}
        />
      )}
    </div>
  )
}
