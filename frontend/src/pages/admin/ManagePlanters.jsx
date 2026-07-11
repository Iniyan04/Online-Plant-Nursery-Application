import { useEffect, useState } from 'react'
import { getPlanters, getPlants, getSeeds, addPlanter, updatePlanter, deletePlanter } from '../../api/client.js'
import { useAuth } from '../../context/AuthContext.jsx'
import PlanterFormModal from '../../components/PlanterFormModal.jsx'

export default function ManagePlanters() {
  const { auth } = useAuth()
  const [planters, setPlanters] = useState([])
  const [plants, setPlants] = useState([])
  const [seeds, setSeeds] = useState([])
  const [loading, setLoading] = useState(true)
  const [listError, setListError] = useState('')

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
    <div>
      <h1 className="page-title">Manage planters</h1>
      <p className="page-sub">Add new pots, update details, or retire planters from the catalog.</p>

      <div className="toolbar">
        <div />
        <button className="btn btn-primary" onClick={openAdd}>
          + Add planter
        </button>
      </div>

      {listError && <div className="alert alert-error--dark">{listError}</div>}

      {loading && (
        <div className="state-block">
          <p className="page-title">Loading planters…</p>
        </div>
      )}

      {!loading && !listError && planters.length === 0 && (
        <div className="state-block">
          <p className="page-title">No planters yet</p>
          <p>Add your first planter to get the catalog started.</p>
        </div>
      )}

      {!loading && planters.length > 0 && (
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>Shape</th>
                <th>Capacity</th>
                <th>Stock</th>
                <th>Cost</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {planters.map((planter) => (
                <tr key={planter.planterId}>
                  <td>{planter.planterShape}</td>
                  <td>{planter.planterCapacity} L</td>
                  <td className="num">{planter.planterStock}</td>
                  <td className="num">₹{planter.planterCost}</td>
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
                        {deletingId === planter.planterId ? 'Deleting…' : 'Delete'}
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
