import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import ManageCustomers from './ManageCustomers.jsx'
import { renderRoute } from '../../testUtils.jsx'
import { deleteCustomer, getCustomers, searchCustomer, updateCustomer } from '../../api/client.js'

vi.mock('../../api/client.js', () => ({
  deleteCustomer: vi.fn(),
  getCustomers: vi.fn(),
  searchCustomer: vi.fn(),
  updateCustomer: vi.fn()
}))

vi.mock('../../context/AuthContext.jsx', () => ({
  useAuth: () => ({
    auth: { role: 'admin', admin: { username: 'admin', password: 'admin123' } }
  })
}))

const customers = [
  { customerId: 1, customerName: 'Maya Green', customerEmail: 'maya@example.com', username: 'maya' },
  { customerId: 2, customerName: 'Asha Leaf', customerEmail: 'asha@example.com', username: 'asha' }
]

describe('ManageCustomers', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    vi.spyOn(window, 'confirm').mockReturnValue(true)
    getCustomers.mockResolvedValue(customers)
    searchCustomer.mockResolvedValue(customers[1])
    updateCustomer.mockResolvedValue(customers[0])
    deleteCustomer.mockResolvedValue()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('loads customers and searches by username', async () => {
    const user = userEvent.setup()
    renderRoute(<ManageCustomers />)

    expect(await screen.findByText('Maya Green')).toBeInTheDocument()
    await user.type(screen.getByLabelText(/search by username/i), 'asha')
    await user.click(screen.getByRole('button', { name: /^search$/i }))

    await waitFor(() => {
      expect(searchCustomer).toHaveBeenCalledWith('asha', { username: 'admin', password: 'admin123' })
    })
    expect(await screen.findByText('Asha Leaf')).toBeInTheDocument()
  })

  it('validates edits before saving', async () => {
    const user = userEvent.setup()
    renderRoute(<ManageCustomers />)

    await screen.findByText('Maya Green')
    await user.click(screen.getAllByRole('button', { name: /edit/i })[0])
    await user.clear(screen.getByLabelText(/email/i))
    await user.type(screen.getByLabelText(/email/i), 'bad-email')
    await user.click(screen.getByRole('button', { name: /save customer/i }))

    expect(await screen.findByText(/valid email/i)).toBeInTheDocument()
    expect(updateCustomer).not.toHaveBeenCalled()
  })

  it('deletes a customer after confirmation', async () => {
    const user = userEvent.setup()
    renderRoute(<ManageCustomers />)

    await screen.findByText('Maya Green')
    await user.click(screen.getAllByRole('button', { name: /delete/i })[0])

    await waitFor(() => {
      expect(deleteCustomer).toHaveBeenCalledWith(1, { username: 'admin', password: 'admin123' })
    })
  })
})
