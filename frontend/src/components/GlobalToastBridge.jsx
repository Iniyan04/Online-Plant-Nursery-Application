import { useEffect, useRef } from 'react'
import { useToast } from '../context/ToastContext.jsx'

function getToastType(alertNode) {
  if (alertNode.classList.contains('alert-success')) return 'success'
  if (alertNode.classList.contains('alert-error')) return 'error'
  if (alertNode.classList.contains('alert-error--dark')) return 'error'
  return 'info'
}

export default function GlobalToastBridge() {
  const { showToast } = useToast()
  const seenRef = useRef(new Set())

  useEffect(() => {
    let frameId = null

    function scanAlerts() {
      document.querySelectorAll('.alert').forEach((node) => {
        const message = node.textContent?.trim()
        if (!message) return

        const type = getToastType(node)
        const key = `${type}:${message}`
        if (seenRef.current.has(key)) return

        seenRef.current.add(key)
        showToast(message, type)

        window.setTimeout(() => {
          seenRef.current.delete(key)
        }, 4500)
      })
    }

    function scheduleScan() {
      if (frameId) return
      frameId = window.requestAnimationFrame(() => {
        frameId = null
        scanAlerts()
      })
    }

    scanAlerts()

    const observer = new MutationObserver(scheduleScan)
    observer.observe(document.body, {
      childList: true,
      subtree: true,
      characterData: true
    })

    return () => {
      observer.disconnect()
      if (frameId) window.cancelAnimationFrame(frameId)
    }
  }, [showToast])

  return null
}
