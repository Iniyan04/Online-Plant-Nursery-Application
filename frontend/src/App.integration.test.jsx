import { MemoryRouter } from 'react-router-dom'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import App from './App.jsx'
import {
  cancelOrder,
  getDashboard,
  getOrdersByCustomer,
  getPlanterById,
  getCustomers,
  orderPlanter,
  searchCustomer,
  updateCustomer
} from './api/client.js'

vi.mock('./api/client.js', () => ({
  addPlant: vi.fn(),
  cancelOrder: vi.fn(),
  deleteCustomer: vi.fn(),
  deletePlant: vi.fn(),
  getCustomers: vi.fn(),
  getDashboard: vi.fn(),
  getOrdersByCustomer: vi.fn(),
  getPlanterById: vi.fn(),
  getPlants: vi.fn(),
  orderPlanter: vi.fn(),
  searchCustomer: vi.fn(),
  updateCustomer: vi.fn(),
  updatePlant: vi.fn()
}))

let mockedAuth

vi.mock('./context/AuthContext.jsx', () => ({
  useAuth: () => ({ auth: mockedAuth, logout: vi.fn() })
}))

function renderApp(route) {
  return render(
    <MemoryRouter initialEntries={[route]}>
      <App />
    </MemoryRouter>
  )
}

describe('new frontend routes', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('supports viewing and cancelling orders from the order history route', async () => {
    const user = userEvent.setup()
    vi.spyOn(window, 'confirm').mockReturnValue(true)
    mockedAuth = { role: 'customer', customer: { customerId: 9, customerName: 'Maya Green' } }
    getOrdersByCustomer
      .mockResolvedValueOnce([
        {
          bookingOrderId: 21,
          orderDate: '2026-07-13',
          itemName: 'Round Planter',
          itemType: 'PLANTER',
          quantity: 2,
          totalCost: 280,
          orderStatus: 'ACTIVE'
        }
      ])
      .mockResolvedValueOnce([
        {
          bookingOrderId: 21,
          orderDate: '2026-07-13',
          itemName: 'Round Planter',
          itemType: 'PLANTER',
          quantity: 2,
          totalCost: 280,
          orderStatus: 'CANCELLED'
        }
      ])
    cancelOrder.mockResolvedValue({ bookingOrderId: 21, orderStatus: 'CANCELLED' })

    renderApp('/orders')

    expect(await screen.findByRole('heading', { name: /my orders/i })).toBeInTheDocument()
    expect(await screen.findByText('Round Planter')).toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: /cancel/i }))

    await waitFor(() => {
      expect(cancelOrder).toHaveBeenCalledWith(21, 9)
    })
    expect(await screen.findByText(/cancelled successfully/i)).toBeInTheDocument()
  })

  it('supports the customer planter checkout flow', async () => {
    const user = userEvent.setup()
    mockedAuth = { role: 'customer', customer: { customerId: 9, customerName: 'Maya Green' } }
    getPlanterById.mockResolvedValue({
      planterId: 5,
      planterShape: 'Square',
      planterCost: 140,
      planterStock: 4
    })
    orderPlanter.mockResolvedValue({
      bookingOrderId: 45,
      totalCost: 420,
      transactionMode: 'UPI'
    })

    renderApp('/planters/5/order')

    expect(await screen.findByRole('heading', { name: /order square planter/i })).toBeInTheDocument()
    fireEvent.change(screen.getByLabelText(/quantity/i), { target: { value: '3' } })
    await user.click(screen.getByRole('button', { name: /place order/i }))

    await waitFor(() => {
      expect(orderPlanter).toHaveBeenCalledWith(9, 5, 3, 'UPI')
    })
    expect(await screen.findByText(/your planter order/i)).toBeInTheDocument()
  })

  it('supports the admin customer management flow', async () => {
    const user = userEvent.setup()
    mockedAuth = { role: 'admin', admin: { username: 'admin', password: 'admin123' } }
    getCustomers.mockResolvedValue([
      {
        customerId: 3,
        customerName: 'Asha Green',
        customerEmail: 'asha@example.com',
        username: 'asha'
      }
    ])
    searchCustomer.mockResolvedValue({
      customerId: 3,
      customerName: 'Asha Green',
      customerEmail: 'asha@example.com',
      username: 'asha'
    })
    updateCustomer.mockResolvedValue({
      customerId: 3,
      customerName: 'Asha Garden',
      customerEmail: 'asha@example.com',
      username: 'asha'
    })

    renderApp('/admin/customers')

    expect(await screen.findByRole('heading', { name: /manage customers/i })).toBeInTheDocument()
    await user.type(screen.getByLabelText(/search by username/i), 'asha')
    await user.click(screen.getByRole('button', { name: /^search$/i }))

    await waitFor(() => {
      expect(searchCustomer).toHaveBeenCalledWith('asha', { username: 'admin', password: 'admin123' })
    })

    await user.click(await screen.findByRole('button', { name: /edit/i }))
    const nameInput = screen.getByLabelText(/^name$/i)
    await user.clear(nameInput)
    await user.type(nameInput, 'Asha Garden')
    await user.click(screen.getByRole('button', { name: /save customer/i }))

    await waitFor(() => {
      expect(updateCustomer).toHaveBeenCalledWith(
        3,
        {
          customerName: 'Asha Garden',
          customerEmail: 'asha@example.com',
          username: 'asha'
        },
        { username: 'admin', password: 'admin123' }
      )
    })
  })

  it('supports the admin dashboard route with API data', async () => {
    mockedAuth = { role: 'admin', admin: { username: 'admin', password: 'admin123' } }
    getDashboard.mockResolvedValue({
      totalCustomers: 4,
      totalPlants: 8,
      totalSeeds: 6,
      totalPlanters: 5,
      totalOrders: 9,
      activeOrders: 7,
      cancelledOrders: 2
    })

    renderApp('/admin/dashboard')

    expect(await screen.findByRole('heading', { name: /admin dashboard/i })).toBeInTheDocument()
    expect(await screen.findByText('4')).toBeInTheDocument()
    expect(screen.getByText('9')).toBeInTheDocument()
  })
})
