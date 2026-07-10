import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

// Guards routes that require an admin to be logged in (US-004/005/006 pages).
// The backend authorizes via adminUsername/adminPassword headers rather than
// a token, so this guard just checks that we have those credentials in
// context before letting the page render.
export default function AdminRoute({ children }) {
  const { auth } = useAuth()

  if (!auth || auth.role !== 'admin') {
    return <Navigate to="/login" replace />
  }

  return children
}
