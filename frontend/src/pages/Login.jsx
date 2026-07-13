import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { loginCustomer, loginAdmin } from '../api/client.js'
import { useAuth } from '../context/AuthContext.jsx'

export default function Login() {
  const [role, setRole] = useState('customer')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const { loginAsCustomer, loginAsAdmin } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      if (role === 'customer') {
        const customer = await loginCustomer(username, password)
        loginAsCustomer(customer)
        navigate('/plants')
      } else {
        const admin = await loginAdmin(username, password)
        loginAsAdmin(username, password, admin)
        navigate('/admin/plants')
      }
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="center" style={{ minHeight: '70vh' }}>
      <form className="form-card" onSubmit={handleSubmit}>
        <p className="eyebrow">Welcome back</p>
        <h1 className="page-title">Log in</h1>
        <p className="page-sub">Sign in to browse the catalog or manage the nursery.</p>

        <div className="role-toggle" role="tablist" aria-label="Login as">
          <button
            type="button"
            className={role === 'customer' ? 'active' : ''}
            onClick={() => setRole('customer')}
          >
            Customer
          </button>
          <button
            type="button"
            className={role === 'admin' ? 'active' : ''}
            onClick={() => setRole('admin')}
          >
            Admin
          </button>
        </div>

        {location.state?.success && <div className="alert alert-success">{location.state.success}</div>}
        {error && <div className="alert alert-error">{error}</div>}

        <div className="field">
          <label htmlFor="username">Username</label>
          <input
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            required
          />
        </div>

        <div className="field">
          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
          />
        </div>

        <button className="btn btn-primary btn-block" type="submit" disabled={loading}>
          {loading ? 'Signing in…' : 'Log in'}
        </button>

        {role === 'customer' && (
          <p className="form-footer-link">
            New here? <Link to="/register">Create an account</Link>
          </p>
        )}
      </form>
    </div>
  )
}
