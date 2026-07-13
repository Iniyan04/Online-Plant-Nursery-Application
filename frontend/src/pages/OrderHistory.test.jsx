import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import OrderHistory from './OrderHistory.jsx'
import { renderRoute } from '../testUtils.jsx'
import { cancelOrder, getOrdersByCustomer } from '../api/client.js'

vi.mock('../api/client.js', () => ({
  cancelOrder: vi.fn(),
  getOrdersByCustomer: vi.fn()
}))

vi.mock('../context/AuthContext.jsx', () => ({
  useAuth: () => ({
    auth: { role: 'customer', customer: { customerId: 12, customerName: 'Asha Leaf' } }
  })
}))

describe('OrderHistory', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    vi.spyOn(window, 'confirm').mockReturnValue(true)
    getOrdersByCustomer.mockResolvedValue([
      {
        bookingOrderId: 10,
        orderDate: '2026-07-13',
        itemName: 'Round',
        itemType: 'Planter',
        quantity: 2,
        totalCost: 240,
        orderStatus: 'ACTIVE'
      },
      {
        bookingOrderId: 11,
        orderDate: '2026-07-13',
        itemName: 'Rose',
        itemType: 'Plant',
        quantity: 1,
        totalCost: 80,
        orderStatus: 'CANCELLED'
      }
    ])
    cancelOrder.mockResolvedValue({ bookingOrderId: 10, orderStatus: 'CANCELLED' })
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('lists orders and disables cancellation for cancelled orders', async () => {
    renderRoute(<OrderHistory />)

    expect(await screen.findByText('Round')).toBeInTheDocument()
    expect(screen.getByText('Rose')).toBeInTheDocument()
    expect(screen.getAllByRole('button', { name: /cancel/i })[1]).toBeDisabled()
  })

  it('confirms and cancels an active order', async () => {
    const user = userEvent.setup()
    renderRoute(<OrderHistory />)

    await screen.findByText('Round')
    await user.click(screen.getAllByRole('button', { name: /cancel/i })[0])

    await waitFor(() => {
      expect(cancelOrder).toHaveBeenCalledWith(10, 12)
    })
    expect(await screen.findByText(/cancelled successfully/i)).toBeInTheDocument()
  })
})
