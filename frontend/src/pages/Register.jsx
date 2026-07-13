import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { registerCustomer } from '../api/client.js'

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

  const navigate = useNavigate()

  function update(field) {
    return (e) => setForm((f) => ({ ...f, [field]: e.target.value }))
  }

  function validateForm() {
    const email = form.customerEmail.trim()
    const username = form.username.trim()
    const password = form.password
    const pincode = form.pincode.trim()

    if (!form.customerName.trim()) return 'Full name is required.'
    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) return 'Enter a valid email address.'
    if (username.length < 3) return 'Username must be at least 3 characters long.'
    if (password.length < 6) return 'Password must be at least 6 characters long.'
    if (!form.houseNo.trim()) return 'House / flat number is required.'
    if (!form.colony.trim()) return 'Street / colony is required.'
    if (!form.city.trim()) return 'City is required.'
    if (!form.state.trim()) return 'State is required.'
    if (!/^\d{6}$/.test(pincode)) return 'Pincode must be exactly 6 digits.'
    return ''
  }

  async function handleSubmit(e) {
    e.preventDefault()
    const validationError = validateForm()
    setError(validationError)
    if (validationError) return

    setLoading(true)
    try {
      const payload = {
        ...form,
        customerName: form.customerName.trim(),
        customerEmail: form.customerEmail.trim(),
        username: form.username.trim(),
        houseNo: form.houseNo.trim(),
        colony: form.colony.trim(),
        city: form.city.trim(),
        state: form.state.trim(),
        pincode: Number(form.pincode)
      }
      await registerCustomer(payload)
      navigate('/login', {
        replace: true,
        state: { success: 'Account created successfully. Please log in to continue.' }
      })
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
            <input id="username" minLength="3" value={form.username} onChange={update('username')} required />
          </div>
          <div className="field">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              minLength="6"
              value={form.password}
              onChange={update('password')}
              required
            />
          </div>
        </div>

        <div className="field">
          <label htmlFor="houseNo">House / flat no.</label>
          <input id="houseNo" value={form.houseNo} onChange={update('houseNo')} required />
        </div>

        <div className="field">
          <label htmlFor="colony">Street / colony</label>
          <input id="colony" value={form.colony} onChange={update('colony')} required />
        </div>

        <div className="field-row">
          <div className="field">
            <label htmlFor="city">City</label>
            <input id="city" value={form.city} onChange={update('city')} required />
          </div>
          <div className="field">
            <label htmlFor="state">State</label>
            <input id="state" value={form.state} onChange={update('state')} required />
          </div>
        </div>

        <div className="field">
          <label htmlFor="pincode">Pincode</label>
          <input
            id="pincode"
            inputMode="numeric"
            pattern="\d{6}"
            maxLength="6"
            value={form.pincode}
            onChange={update('pincode')}
            required
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
