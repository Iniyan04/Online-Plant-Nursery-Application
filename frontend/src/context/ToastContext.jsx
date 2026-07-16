import { createContext, useContext, useMemo, useRef, useState } from 'react'

const defaultToastContext = {
  showToast: () => {}
}

const ToastContext = createContext(defaultToastContext)

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([])
  const timersRef = useRef(new Map())

  function removeToast(id) {
    const timerId = timersRef.current.get(id)
    if (timerId) {
      window.clearTimeout(timerId)
      timersRef.current.delete(id)
    }
    setToasts((current) => current.filter((toast) => toast.id !== id))
  }

  function showToast(message, type = 'info') {
    const id = `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`
    setToasts((current) => [...current.slice(-2), { id, message, type }])

    const timerId = window.setTimeout(() => {
      removeToast(id)
    }, 3600)

    timersRef.current.set(id, timerId)
  }

  const value = useMemo(() => ({ showToast }), [])

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div className="toast-stack" aria-live="polite" aria-atomic="true">
        {toasts.map((toast) => (
          <div key={toast.id} className={`toast toast-${toast.type}`}>
            <span className="toast-dot" aria-hidden="true" />
            <span>{toast.message}</span>
            <button
              type="button"
              className="toast-close"
              onClick={() => removeToast(toast.id)}
              aria-label="Dismiss notification"
            >
              x
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  )
}

export function useToast() {
  return useContext(ToastContext)
}
