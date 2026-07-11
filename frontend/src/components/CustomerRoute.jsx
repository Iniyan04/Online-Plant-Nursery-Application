import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

// Guards routes that require a logged-in customer (order checkout pages).
export default function CustomerRoute({ children }) {
  const { auth } = useAuth()

  if (!auth || auth.role !== 'customer') {
    return <Navigate to="/login" replace />
  }

  return children
}
