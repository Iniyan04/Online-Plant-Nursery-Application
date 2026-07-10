import { Navigate, Route, Routes } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import AdminRoute from './components/AdminRoute.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import PlantCatalog from './pages/PlantCatalog.jsx'
import PlantDetail from './pages/PlantDetail.jsx'
import ManagePlants from './pages/admin/ManagePlants.jsx'
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
          <Route
            path="/admin/plants"
            element={
              <AdminRoute>
                <ManagePlants />
              </AdminRoute>
            }
          />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </main>
    </div>
  )
}
