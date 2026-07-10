import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { registerCustomer } from '../api/client.js'
import { useAuth } from '../context/AuthContext.jsx'

const EMPTY_FORM = {
  customerName: '',
  customerEmail: '',
  username: '',
  password: '',
  houseNo: '',
  colony: '',
  city: '',
  state: '',
  pincode: ''
}

export default function Register() {
  const [form, setForm] = useState(EMPTY_FORM)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const { loginAsCustomer } = useAuth()
  const navigate = useNavigate()

  function update(field) {
    return (e) => setForm((f) => ({ ...f, [field]: e.target.value }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const payload = { ...form, pincode: Number(form.pincode) || 0 }
      const customer = await registerCustomer(payload)
      loginAsCustomer(customer)
      navigate('/plants')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="center" style={{ minHeight: '70vh' }}>
      <form className="form-card" style={{ maxWidth: 520 }} onSubmit={handleSubmit}>
        <p className="eyebrow">Join the nursery</p>
        <h1 className="page-title">Create an account</h1>
        <p className="page-sub">Register to browse plants and place orders.</p>

        {error && <div className="alert alert-error">{error}</div>}

        <div className="field">
          <label htmlFor="customerName">Full name</label>
          <input id="customerName" value={form.customerName} onChange={update('customerName')} required />
        </div>

        <div className="field">
          <label htmlFor="customerEmail">Email</label>
          <input
            id="customerEmail"
            type="email"
            value={form.customerEmail}
            onChange={update('customerEmail')}
            required
          />
        </div>

        <div className="field-row">
          <div className="field">
            <label htmlFor="username">Username</label>
            <input id="username" value={form.username} onChange={update('username')} required />
          </div>
          <div className="field">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={form.password}
              onChange={update('password')}
              required
            />
          </div>
        </div>

        <div className="field">
          <label htmlFor="houseNo">House / flat no.</label>
          <input id="houseNo" value={form.houseNo} onChange={update('houseNo')} />
        </div>

        <div className="field">
          <label htmlFor="colony">Street / colony</label>
          <input id="colony" value={form.colony} onChange={update('colony')} />
        </div>

        <div className="field-row">
          <div className="field">
            <label htmlFor="city">City</label>
            <input id="city" value={form.city} onChange={update('city')} />
          </div>
          <div className="field">
            <label htmlFor="state">State</label>
            <input id="state" value={form.state} onChange={update('state')} />
          </div>
        </div>

        <div className="field">
          <label htmlFor="pincode">Pincode</label>
          <input
            id="pincode"
            inputMode="numeric"
            value={form.pincode}
            onChange={update('pincode')}
          />
        </div>

        <button className="btn btn-primary btn-block" type="submit" disabled={loading}>
          {loading ? 'Creating account…' : 'Create account'}
        </button>

        <p className="form-footer-link">
          Already have an account? <Link to="/login">Log in</Link>
        </p>
      </form>
    </div>
  )
}
