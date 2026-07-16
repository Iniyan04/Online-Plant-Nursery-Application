import { useEffect, useMemo, useState } from 'react'
import { getSeeds, addSeed, updateSeed, deleteSeed } from '../../api/client.js'
import EmptyState from '../../components/EmptyState.jsx'
import { TableSkeleton } from '../../components/LoadingBlock.jsx'
import { useAuth } from '../../context/AuthContext.jsx'
import SeedFormModal from '../../components/SeedFormModal.jsx'

export default function ManageSeeds() {
  const { auth } = useAuth()
  const [seeds, setSeeds] = useState([])
  const [loading, setLoading] = useState(true)
  const [listError, setListError] = useState('')
  const [search, setSearch] = useState('')

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

  const visibleSeeds = useMemo(() => {
    const query = search.trim().toLowerCase()
    if (!query) return seeds
    return seeds.filter((seed) =>
      [seed.commonName, seed.typeOfSeeds].some((value) =>
        String(value || '').toLowerCase().includes(query)
      )
    )
  }, [search, seeds])

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
    <div className="page-fade-in admin-page-shell">
      <div className="admin-header-card">
        <div>
          <p className="eyebrow">Catalog management</p>
          <h1 className="page-title">Manage seeds</h1>
          <p className="page-sub">Add new packets, update details, or retire seeds from the catalog.</p>
        </div>
        <button className="btn btn-primary admin-add-btn" onClick={openAdd}>
          + Add seed
        </button>
      </div>

      <div className="toolbar admin-toolbar">
        <div className="field search-field admin-search-field">
          <label htmlFor="seedsTableSearch">Search seeds</label>
          <input
            id="seedsTableSearch"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by common name or type"
          />
        </div>
        <div className="admin-toolbar-note">
          <span>{visibleSeeds.length}</span>
          <small>seed records</small>
        </div>
      </div>

      {listError && <div className="alert alert-error--dark">{listError}</div>}

      {loading && <TableSkeleton rows={5} columns={5} />}

      {!loading && !listError && visibleSeeds.length === 0 && (
        <EmptyState
          icon="Seed"
          eyebrow="Inventory empty"
          title="No seeds found"
          message={search ? 'Try a different search term to find existing seed records.' : 'Add your first seed packet to get the catalog started.'}
        />
      )}

      {!loading && visibleSeeds.length > 0 && (
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
              {visibleSeeds.map((seed, index) => (
                <tr key={seed.seedId} className={index % 2 === 0 ? 'zebra-row' : ''}>
                  <td>
                    <div className="admin-row-primary">
                      <strong>{seed.commonName}</strong>
                    </div>
                  </td>
                  <td><span className="table-badge">{seed.typeOfSeeds}</span></td>
                  <td className="num">{seed.seedsStock}</td>
                  <td className="num">Rs. {seed.seedsCost.toFixed(2)}</td>
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
                        {deletingId === seed.seedId ? 'Deleting...' : 'Delete'}
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
