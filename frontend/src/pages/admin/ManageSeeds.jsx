import { useEffect, useState } from 'react'
import { getSeeds, addSeed, updateSeed, deleteSeed } from '../../api/client.js'
import { useAuth } from '../../context/AuthContext.jsx'
import SeedFormModal from '../../components/SeedFormModal.jsx'

export default function ManageSeeds() {
  const { auth } = useAuth()
  const [seeds, setSeeds] = useState([])
  const [loading, setLoading] = useState(true)
  const [listError, setListError] = useState('')

  const [modalMode, setModalMode] = useState(null)
  const [editingSeed, setEditingSeed] = useState(null)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState('')

  const [deletingId, setDeletingId] = useState(null)

  function loadSeeds() {
    setLoading(true)
    setListError('')
    getSeeds()
      .then(setSeeds)
      .catch((err) => setListError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(loadSeeds, [])

  function openAdd() {
    setEditingSeed(null)
    setFormError('')
    setModalMode('add')
  }

  function openEdit(seed) {
    setEditingSeed(seed)
    setFormError('')
    setModalMode('edit')
  }

  function closeModal() {
    setModalMode(null)
    setEditingSeed(null)
  }

  async function handleSave(formValues) {
    setSaving(true)
    setFormError('')
    try {
      if (modalMode === 'add') {
        await addSeed(formValues, auth.admin)
      } else {
        await updateSeed(editingSeed.seedId, formValues, auth.admin)
      }
      closeModal()
      loadSeeds()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(seed) {
    if (!window.confirm(`Delete "${seed.commonName}"? This can't be undone.`)) return
    setDeletingId(seed.seedId)
    try {
      await deleteSeed(seed.seedId, auth.admin)
      loadSeeds()
    } catch (err) {
      alert(err.message)
    } finally {
      setDeletingId(null)
    }
  }

  return (
    <div>
      <h1 className="page-title">Manage seeds</h1>
      <p className="page-sub">Add new packets, update details, or retire seeds from the catalog.</p>

      <div className="toolbar">
        <div />
        <button className="btn btn-primary" onClick={openAdd}>
          + Add seed
        </button>
      </div>

      {listError && <div className="alert alert-error--dark">{listError}</div>}

      {loading && (
        <div className="state-block">
          <p className="page-title">Loading seeds…</p>
        </div>
      )}

      {!loading && !listError && seeds.length === 0 && (
        <div className="state-block">
          <p className="page-title">No seeds yet</p>
          <p>Add your first seed packet to get the catalog started.</p>
        </div>
      )}

      {!loading && seeds.length > 0 && (
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
              {seeds.map((seed) => (
                <tr key={seed.seedId}>
                  <td>{seed.commonName}</td>
                  <td>{seed.typeOfSeeds}</td>
                  <td className="num">{seed.seedsStock}</td>
                  <td className="num">₹{seed.seedsCost.toFixed(2)}</td>
                  <td>
                    <div className="row-actions">
                      <button className="btn btn-outline btn-sm" onClick={() => openEdit(seed)}>
                        Edit
                      </button>
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleDelete(seed)}
                        disabled={deletingId === seed.seedId}
                      >
                        {deletingId === seed.seedId ? 'Deleting…' : 'Delete'}
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
        <SeedFormModal
          title={modalMode === 'add' ? 'Add seed' : `Edit ${editingSeed.commonName}`}
          initial={editingSeed}
          onSave={handleSave}
          onClose={closeModal}
          saving={saving}
          error={formError}
        />
      )}
    </div>
  )
}
