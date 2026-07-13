import { screen } from '@testing-library/react'
import AdminDashboard from './AdminDashboard.jsx'
import { renderRoute } from '../../testUtils.jsx'
import { getDashboard } from '../../api/client.js'

vi.mock('../../api/client.js', () => ({
  getDashboard: vi.fn()
}))

vi.mock('../../context/AuthContext.jsx', () => ({
  useAuth: () => ({
    auth: { role: 'admin', admin: { username: 'admin', password: 'admin123' } }
  })
}))

describe('AdminDashboard', () => {
  it('renders dashboard counts from the API', async () => {
    getDashboard.mockResolvedValue({
      totalCustomers: 4,
      totalPlants: 8,
      totalSeeds: 6,
      totalPlanters: 3,
      totalOrders: 5,
      activeOrders: 4,
      cancelledOrders: 1
    })

    renderRoute(<AdminDashboard />)

    expect(await screen.findByText('Total Customers')).toBeInTheDocument()
    expect(screen.getAllByText('4')).toHaveLength(2)
    expect(screen.getByText('Cancelled Orders')).toBeInTheDocument()
    expect(screen.getByText('1')).toBeInTheDocument()
  })
})
