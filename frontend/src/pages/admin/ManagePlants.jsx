import { useEffect, useState } from 'react'
import { getPlants, addPlant, updatePlant, deletePlant } from '../../api/client.js'
import { useAuth } from '../../context/AuthContext.jsx'
import PlantFormModal from '../../components/PlantFormModal.jsx'

export default function ManagePlants() {
  const { auth } = useAuth()
  const [plants, setPlants] = useState([])
  const [loading, setLoading] = useState(true)
  const [listError, setListError] = useState('')

  const [modalMode, setModalMode] = useState(null) // null | 'add' | 'edit'
  const [editingPlant, setEditingPlant] = useState(null)
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

  function openAdd() {
    setEditingPlant(null)
    setFormError('')
    setModalMode('add')
  }

  function openEdit(plant) {
    setEditingPlant(plant)
    setFormError('')
    setModalMode('edit')
  }

  function closeModal() {
    setModalMode(null)
    setEditingPlant(null)
  }

  async function handleSave(formValues) {
    setSaving(true)
    setFormError('')
    try {
      if (modalMode === 'add') {
        await addPlant(formValues, auth.admin)
      } else {
        await updatePlant(editingPlant.plantId, formValues, auth.admin)
      }
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
                      <button className="btn btn-outline btn-sm" onClick={() => openEdit(plant)}>
                        Edit
                      </button>
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

      {modalMode && (
        <PlantFormModal
          title={modalMode === 'add' ? 'Add plant' : `Edit ${editingPlant.commonName}`}
          initial={editingPlant}
          onSave={handleSave}
          onClose={closeModal}
          saving={saving}
          error={formError}
        />
      )}
    </div>
  )
}
