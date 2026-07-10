import { createContext, useContext, useEffect, useState } from 'react'

const AuthContext = createContext(null)
const STORAGE_KEY = 'greenroot_auth'

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  })

  useEffect(() => {
    if (auth) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(auth))
    } else {
      localStorage.removeItem(STORAGE_KEY)
    }
  }, [auth])

  // The backend has no session tokens — for admins we must keep the
  // raw username/password in memory to resend as headers on every
  // protected request (adminUsername / adminPassword).
  function loginAsCustomer(customer) {
    setAuth({ role: 'customer', customer })
  }

  function loginAsAdmin(username, password, adminResponse) {
    setAuth({ role: 'admin', admin: { username, password, adminId: adminResponse.adminId } })
  }

  function logout() {
    setAuth(null)
  }

  return (
    <AuthContext.Provider value={{ auth, loginAsCustomer, loginAsAdmin, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}
