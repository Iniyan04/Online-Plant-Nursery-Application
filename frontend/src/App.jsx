import { Navigate, Route, Routes } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import AdminRoute from './components/AdminRoute.jsx'
import CustomerRoute from './components/CustomerRoute.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import PlantCatalog from './pages/PlantCatalog.jsx'
import PlantDetail from './pages/PlantDetail.jsx'
import SeedCatalog from './pages/SeedCatalog.jsx'
import SeedDetail from './pages/SeedDetail.jsx'
import PlanterCatalog from './pages/PlanterCatalog.jsx'
import PlanterDetail from './pages/PlanterDetail.jsx'
import OrderPlant from './pages/OrderPlant.jsx'
import OrderSeed from './pages/OrderSeed.jsx'
import ManagePlants from './pages/admin/ManagePlants.jsx'
import ManageSeeds from './pages/admin/ManageSeeds.jsx'
import ManagePlanters from './pages/admin/ManagePlanters.jsx'
import NotFound from './pages/NotFound.jsx'

export default function App() {
  return (
    <div className="app-shell">
      <Navbar />
      <main className="app-main">
        <Routes>
          <Route path="/" element={<Navigate to="/plants" replace />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/plants" element={<PlantCatalog />} />
          <Route path="/plants/:id" element={<PlantDetail />} />
          <Route path="/seeds" element={<SeedCatalog />} />
          <Route path="/seeds/:id" element={<SeedDetail />} />
          <Route path="/planters" element={<PlanterCatalog />} />
          <Route path="/planters/:id" element={<PlanterDetail />} />
          <Route
            path="/plants/:id/order"
            element={
              <CustomerRoute>
                <OrderPlant />
              </CustomerRoute>
            }
          />
          <Route
            path="/seeds/:id/order"
            element={
              <CustomerRoute>
                <OrderSeed />
              </CustomerRoute>
            }
          />
          <Route
            path="/admin/plants"
            element={
              <AdminRoute>
                <ManagePlants />
              </AdminRoute>
            }
          />
          <Route
            path="/admin/seeds"
            element={
              <AdminRoute>
                <ManageSeeds />
              </AdminRoute>
            }
          />
          <Route
            path="/admin/planters"
            element={
              <AdminRoute>
                <ManagePlanters />
              </AdminRoute>
            }
          />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </main>
    </div>
  )
}
