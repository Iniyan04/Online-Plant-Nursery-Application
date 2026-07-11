import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { getSeedById, orderSeed } from '../api/client.js'
import { useAuth } from '../context/AuthContext.jsx'

const PAYMENT_MODES = ['UPI', 'Card', 'Cash']

export default function OrderSeed() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { auth } = useAuth()
  const [seed, setSeed] = useState(null)
  const [quantity, setQuantity] = useState(1)
  const [transactionMode, setTransactionMode] = useState('UPI')
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(null)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError('')

    getSeedById(id)
      .then((data) => {
        if (!cancelled) setSeed(data)
      })
      .catch((err) => {
        if (!cancelled) setError(err.message)
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [id])

  const total = seed ? seed.seedsCost * quantity : 0

  async function handleSubmit(e) {
    e.preventDefault()
    setSubmitting(true)
    setError('')
    try {
      const order = await orderSeed(
        auth.customer.customerId,
        seed.seedId,
        quantity,
        transactionMode
      )
      setSuccess(order)
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <div className="state-block">
        <p className="page-title">Loading checkout…</p>
      </div>
    )
  }

  if (error && !seed) {
    return (
      <div className="state-block">
        <p className="page-title">Couldn't load seed</p>
        <p>{error}</p>
        <Link className="btn btn-outline mt-24" to="/seeds">
          Back to catalog
        </Link>
      </div>
    )
  }

  if (success) {
    return (
      <div className="form-card checkout-card">
        <p className="eyebrow">Order placed</p>
        <h1 className="page-title">Thank you!</h1>
        <p className="page-sub">
          Your seed order <strong>#{success.bookingOrderId}</strong> has been confirmed.
          Total: ₹{success.totalCost.toFixed(2)} via {success.transactionMode}.
        </p>
        <div className="checkout-actions">
          <Link to="/seeds" className="btn btn-outline">Back to seeds</Link>
          <button className="btn btn-primary" onClick={() => navigate('/seeds')}>
            Continue shopping
          </button>
        </div>
      </div>
    )
  }

  return (
    <div>
      <Link to={`/seeds/${id}`} className="btn-ghost" style={{ marginBottom: 24, display: 'inline-block' }}>
        ← Back to seed
      </Link>

      <div className="form-card checkout-card">
        <p className="eyebrow">Checkout</p>
        <h1 className="page-title">Order {seed.commonName}</h1>
        <p className="page-sub">
          ₹{seed.seedsCost.toFixed(2)} per packet · {seed.seedsStock} in stock
        </p>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="field">
            <label htmlFor="quantity">Quantity</label>
            <input
              id="quantity"
              type="number"
              min="1"
              max={seed.seedsStock}
              value={quantity}
              onChange={(e) => setQuantity(Math.max(1, Number(e.target.value) || 1))}
              required
            />
          </div>

          <div className="field">
            <label htmlFor="transactionMode">Payment method</label>
            <select
              id="transactionMode"
              value={transactionMode}
              onChange={(e) => setTransactionMode(e.target.value)}
              required
            >
              {PAYMENT_MODES.map((mode) => (
                <option key={mode} value={mode}>{mode}</option>
              ))}
            </select>
          </div>

          <div className="checkout-total">
            <span>Total</span>
            <span className="checkout-total-amount">₹{total.toFixed(2)}</span>
          </div>

          <button
            type="submit"
            className="btn btn-primary btn-block"
            disabled={submitting || quantity > seed.seedsStock}
          >
            {submitting ? 'Placing order…' : 'Place order'}
          </button>
        </form>
      </div>
    </div>
  )
}
