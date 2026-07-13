import { useEffect, useState } from 'react'
import { deleteCustomer, getCustomers, searchCustomer, updateCustomer } from '../../api/client.js'
import { useAuth } from '../../context/AuthContext.jsx'
import CustomerFormModal from '../../components/CustomerFormModal.jsx'

const PAGE_SIZE = 5

export default function ManageCustomers() {
  const { auth } = useAuth()
  const [customers, setCustomers] = useState([])
  const [query, setQuery] = useState('')
  const [page, setPage] = useState(1)
  const [loading, setLoading] = useState(true)
  const [listError, setListError] = useState('')
  const [emptyMessage, setEmptyMessage] = useState('Try a different username or reset the search.')
  const [success, setSuccess] = useState('')

  const [modalMode, setModalMode] = useState(null)
  const [selectedCustomer, setSelectedCustomer] = useState(null)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState('')
  const [deletingId, setDeletingId] = useState(null)

  function loadCustomers() {
    setLoading(true)
    setListError('')
    setEmptyMessage('No customer records are available yet.')
    getCustomers(auth.admin)
      .then((data) => {
        setCustomers(data)
        setPage(1)
      })
      .catch((err) => setListError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(loadCustomers, [auth.admin.username, auth.admin.password])

  async function handleSearch(e) {
    e.preventDefault()
    const username = query.trim()
    if (!username) {
      loadCustomers()
      return
    }

    setLoading(true)
    setListError('')
    setSuccess('')
    try {
      const customer = await searchCustomer(username, auth.admin)
      setCustomers([customer])
      setPage(1)
    } catch (err) {
      setCustomers([])
      setEmptyMessage(`No customer found for "${username}".`)
    } finally {
      setLoading(false)
    }
  }

  function openView(customer) {
    setSelectedCustomer(customer)
    setModalMode('view')
  }

  function openEdit(customer) {
    setSelectedCustomer(customer)
    setFormError('')
    setModalMode('edit')
  }

  function closeModal() {
    setSelectedCustomer(null)
    setModalMode(null)
  }

  async function handleSave(formValues) {
    if (!formValues.customerName.trim()) {
      setFormError('Customer name is required.')
      return
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formValues.customerEmail.trim())) {
      setFormError('Enter a valid email address.')
      return
    }
    if (formValues.username.trim().length < 3) {
      setFormError('Username must be at least 3 characters long.')
      return
    }

    setSaving(true)
    setFormError('')
    setSuccess('')
    try {
      await updateCustomer(selectedCustomer.customerId, {
        customerName: formValues.customerName.trim(),
        customerEmail: formValues.customerEmail.trim(),
        username: formValues.username.trim()
      }, auth.admin)
      setSuccess('Customer updated successfully.')
      closeModal()
      loadCustomers()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(customer) {
    if (!window.confirm(`Delete "${customer.customerName}"? This cannot be undone.`)) return
    setDeletingId(customer.customerId)
    setListError('')
    setSuccess('')
    try {
      await deleteCustomer(customer.customerId, auth.admin)
      setSuccess('Customer deleted successfully.')
      loadCustomers()
    } catch (err) {
      setListError(err.message)
    } finally {
      setDeletingId(null)
    }
  }

  const totalPages = Math.max(1, Math.ceil(customers.length / PAGE_SIZE))
  const pageCustomers = customers.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE)

  return (
    <div>
      <h1 className="page-title">Manage customers</h1>
      <p className="page-sub">Search, review, update, or remove customer records.</p>

      <form className="toolbar" onSubmit={handleSearch}>
        <div className="field search-field">
          <label htmlFor="customerSearch">Search by username</label>
          <input
            id="customerSearch"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="e.g. greenbuyer"
          />
        </div>
        <div className="row-actions">
          <button className="btn btn-primary" type="submit">Search</button>
          <button className="btn btn-outline" type="button" onClick={() => { setQuery(''); loadCustomers() }}>
            Reset
          </button>
        </div>
      </form>

      {success && <div className="alert alert-success">{success}</div>}
      {listError && <div className="alert alert-error--dark">{listError}</div>}

      {loading && (
        <div className="state-block">
          <p className="page-title">Loading customers...</p>
        </div>
      )}

      {!loading && !listError && customers.length === 0 && (
        <div className="state-block">
          <p className="page-title">No customers found</p>
          <p>{emptyMessage}</p>
        </div>
      )}

      {!loading && customers.length > 0 && (
        <>
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Username</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {pageCustomers.map((customer) => (
                  <tr key={customer.customerId}>
                    <td>{customer.customerName}</td>
                    <td>{customer.customerEmail}</td>
                    <td>{customer.username}</td>
                    <td>
                      <div className="row-actions">
                        <button className="btn btn-outline btn-sm" onClick={() => openView(customer)}>
                          View
                        </button>
                        <button className="btn btn-outline btn-sm" onClick={() => openEdit(customer)}>
                          Edit
                        </button>
                        <button
                          className="btn btn-danger btn-sm"
                          onClick={() => handleDelete(customer)}
                          disabled={deletingId === customer.customerId}
                        >
                          {deletingId === customer.customerId ? 'Deleting...' : 'Delete'}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="pagination-row">
            <button className="btn btn-outline btn-sm" disabled={page === 1} onClick={() => setPage((p) => p - 1)}>
              Previous
            </button>
            <span>Page {page} of {totalPages}</span>
            <button className="btn btn-outline btn-sm" disabled={page === totalPages} onClick={() => setPage((p) => p + 1)}>
              Next
            </button>
          </div>
        </>
      )}

      {modalMode && (
        <CustomerFormModal
          mode={modalMode}
          customer={selectedCustomer}
          onSave={handleSave}
          onClose={closeModal}
          saving={saving}
          error={formError}
        />
      )}
    </div>
  )
}
