import { fireEvent, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import OrderPlanter from './OrderPlanter.jsx'
import { renderRoute } from '../testUtils.jsx'
import { getPlanterById, orderPlanter } from '../api/client.js'

vi.mock('../api/client.js', () => ({
  getPlanterById: vi.fn(),
  orderPlanter: vi.fn()
}))

vi.mock('../context/AuthContext.jsx', () => ({
  useAuth: () => ({
    auth: { role: 'customer', customer: { customerId: 7, customerName: 'Maya Green' } }
  })
}))

describe('OrderPlanter', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    getPlanterById.mockResolvedValue({
      planterId: 3,
      planterShape: 'Round',
      planterCost: 120,
      planterStock: 8
    })
    orderPlanter.mockResolvedValue({
      bookingOrderId: 44,
      totalCost: 240,
      transactionMode: 'UPI'
    })
  })

  it('renders checkout details and submits a planter order', async () => {
    const user = userEvent.setup()
    renderRoute(<OrderPlanter />, { route: '/planters/3/order', path: '/planters/:id/order' })

    expect(await screen.findByRole('heading', { name: /order round planter/i })).toBeInTheDocument()

    fireEvent.change(screen.getByLabelText(/quantity/i), { target: { value: '2' } })
    await user.click(screen.getByRole('button', { name: /place order/i }))

    await waitFor(() => {
      expect(orderPlanter).toHaveBeenCalledWith(7, 3, 2, 'UPI')
    })
    expect(await screen.findByText(/your planter order/i)).toBeInTheDocument()
  })

  it('shows an API error when the order fails', async () => {
    orderPlanter.mockRejectedValue(new Error('Not enough stock'))
    const user = userEvent.setup()
    renderRoute(<OrderPlanter />, { route: '/planters/3/order', path: '/planters/:id/order' })

    await screen.findByRole('heading', { name: /order round planter/i })
    await user.click(screen.getByRole('button', { name: /place order/i }))

    expect(await screen.findByText('Not enough stock')).toBeInTheDocument()
  })
})
