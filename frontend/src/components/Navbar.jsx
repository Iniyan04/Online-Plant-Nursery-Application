import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

export default function Navbar() {
  const { auth, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <header className="navbar">
      <div className="navbar-inner">
        <NavLink to="/plants" className="brand">
          Greenroot <span className="brand-mark">Nursery</span>
        </NavLink>

        <nav className="nav-links">
          <NavLink to="/plants" className={({ isActive }) => (isActive ? 'active' : '')}>
            Plants
          </NavLink>
          <NavLink to="/seeds" className={({ isActive }) => (isActive ? 'active' : '')}>
            Seeds
          </NavLink>
          <NavLink to="/planters" className={({ isActive }) => (isActive ? 'active' : '')}>
            Planters
          </NavLink>
          {auth?.role === 'admin' && (
            <>
              <NavLink to="/admin/plants" className={({ isActive }) => (isActive ? 'active' : '')}>
                Manage Plants
              </NavLink>
              <NavLink to="/admin/seeds" className={({ isActive }) => (isActive ? 'active' : '')}>
                Manage Seeds
              </NavLink>
              <NavLink to="/admin/planters" className={({ isActive }) => (isActive ? 'active' : '')}>
                Manage Planters
              </NavLink>
            </>
          )}
        </nav>

        <div className="nav-user">
          {!auth && (
            <NavLink to="/login" className="btn btn-outline btn-sm">
              Log in
            </NavLink>
          )}
          {auth?.role === 'customer' && (
            <>
              <span>Hi, {auth.customer.customerName.split(' ')[0]}</span>
              <button className="btn-ghost" onClick={handleLogout}>Log out</button>
            </>
          )}
          {auth?.role === 'admin' && (
            <>
              <span>Admin: {auth.admin.username}</span>
              <button className="btn-ghost" onClick={handleLogout}>Log out</button>
            </>
          )}
        </div>
      </div>
    </header>
  )
}
