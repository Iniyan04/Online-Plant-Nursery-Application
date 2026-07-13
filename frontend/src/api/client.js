import axios from 'axios'

// Base URL of the Spring Boot backend (mvn spring-boot:run default port).
const BASE_URL = 'http://localhost:8080'

const client = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' }
})

// The backend's GlobalExceptionHandler always returns
// { timestamp, status, error, message } on failures — surface `message`
// consistently so every page can show one readable string.
function unwrapError(err) {
  const message =
    err?.response?.data?.message ||
    err?.message ||
    'Something went wrong talking to the server.'
  return new Error(message)
}

function adminHeaders(admin) {
  return {
    headers: {
      adminUsername: admin.username,
      adminPassword: admin.password
    }
  }
}

// ---------- US-001: Login ----------

export async function loginCustomer(username, password) {
  try {
    const res = await client.post('/api/customers/login', { username, password })
    return res.data // CustomerResponse: { customerId, customerName, customerEmail, username }
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function loginAdmin(username, password) {
  try {
    const res = await client.post('/api/admins/login', { username, password })
    return res.data // AdminResponse: { adminId, adminUsername }
  } catch (err) {
    throw unwrapError(err)
  }
}

// ---------- US-002: Customer Registration ----------

export async function registerCustomer(payload) {
  try {
    const res = await client.post('/api/customers/register', payload)
    return res.data // CustomerResponse
  } catch (err) {
    throw unwrapError(err)
  }
}

// ---------- US-003: View Plants ----------

export async function getPlants(type) {
  try {
    const res = await client.get('/api/plants', { params: type ? { type } : {} })
    return res.data // Plant[]
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function getPlantById(id) {
  try {
    const res = await client.get(`/api/plants/${id}`)
    return res.data // Plant
  } catch (err) {
    throw unwrapError(err)
  }
}

// ---------- US-004/005/006: Admin Plant CRUD ----------

export async function addPlant(plant, admin) {
  try {
    const res = await client.post('/api/plants', plant, adminHeaders(admin))
    return res.data
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function updatePlant(id, plant, admin) {
  try {
    const res = await client.put(`/api/plants/${id}`, plant, adminHeaders(admin))
    return res.data
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function deletePlant(id, admin) {
  try {
    await client.delete(`/api/plants/${id}`, adminHeaders(admin))
  } catch (err) {
    throw unwrapError(err)
  }
}

// ---------- US-007: View Seeds ----------

export async function getSeeds(type) {
  try {
    const res = await client.get('/api/seeds', { params: type ? { type } : {} })
    return res.data // Seed[]
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function getSeedById(id) {
  try {
    const res = await client.get(`/api/seeds/${id}`)
    return res.data // Seed
  } catch (err) {
    throw unwrapError(err)
  }
}

// ---------- US-008: Manage Seeds CRUD ----------

export async function addSeed(seed, admin) {
  try {
    const res = await client.post('/api/seeds', seed, adminHeaders(admin))
    return res.data
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function updateSeed(id, seed, admin) {
  try {
    const res = await client.put(`/api/seeds/${id}`, seed, adminHeaders(admin))
    return res.data
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function deleteSeed(id, admin) {
  try {
    await client.delete(`/api/seeds/${id}`, adminHeaders(admin))
  } catch (err) {
    throw unwrapError(err)
  }
}

// ---------- US-009: View Planters ----------

export async function getPlanters(minCost, maxCost) {
  try {
    const params = minCost != null && maxCost != null ? { minCost, maxCost } : {}
    const res = await client.get('/api/planters', { params })
    return res.data // Planter[]
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function getPlanterById(id) {
  try {
    const res = await client.get(`/api/planters/${id}`)
    return res.data // Planter
  } catch (err) {
    throw unwrapError(err)
  }
}

// ---------- US-010: Manage Planters CRUD ----------

export async function addPlanter(planter, admin) {
  try {
    const res = await client.post('/api/planters', planter, adminHeaders(admin))
    return res.data
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function updatePlanter(id, planter, admin) {
  try {
    const res = await client.put(`/api/planters/${id}`, planter, adminHeaders(admin))
    return res.data
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function deletePlanter(id, admin) {
  try {
    await client.delete(`/api/planters/${id}`, adminHeaders(admin))
  } catch (err) {
    throw unwrapError(err)
  }
}

// ---------- US-011/012: Order Plants & Seeds ----------

export async function orderPlant(customerId, plantId, quantity, transactionMode) {
  try {
    const res = await client.post('/api/orders/plants', {
      customerId,
      plantId,
      quantity,
      transactionMode
    })
    return res.data // Order
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function orderSeed(customerId, seedId, quantity, transactionMode) {
  try {
    const res = await client.post('/api/orders/seeds', {
      customerId,
      seedId,
      quantity,
      transactionMode
    })
    return res.data // Order
  } catch (err) {
    throw unwrapError(err)
  }
}

// ---------- US-013: Order Planters ----------

export async function orderPlanter(customerId, planterId, quantity, transactionMode) {
  try {
    const res = await client.post('/api/orders/planters', {
      customerId,
      planterId,
      quantity,
      transactionMode
    })
    return res.data
  } catch (err) {
    throw unwrapError(err)
  }
}

// ---------- US-014/015: Order History & Cancellation ----------

export async function getOrdersByCustomer(customerId) {
  try {
    const res = await client.get(`/api/orders/customer/${customerId}`)
    return res.data
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function cancelOrder(orderId, customerId) {
  try {
    const res = await client.put(`/api/orders/${orderId}/cancel`, { customerId })
    return res.data
  } catch (err) {
    throw unwrapError(err)
  }
}

// ---------- US-016: Admin Customer Management ----------

export async function getCustomers(admin) {
  try {
    const res = await client.get('/api/customers', adminHeaders(admin))
    return res.data
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function searchCustomer(username, admin) {
  try {
    const res = await client.get(`/api/customers/search/${encodeURIComponent(username)}`, adminHeaders(admin))
    return res.data
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function updateCustomer(customerId, payload, admin) {
  try {
    const res = await client.put(`/api/customers/${customerId}`, payload, adminHeaders(admin))
    return res.data
  } catch (err) {
    throw unwrapError(err)
  }
}

export async function deleteCustomer(customerId, admin) {
  try {
    await client.delete(`/api/customers/${customerId}`, adminHeaders(admin))
  } catch (err) {
    throw unwrapError(err)
  }
}

// ---------- US-017: Admin Dashboard ----------

export async function getDashboard(admin) {
  try {
    const res = await client.get('/api/admins/dashboard', adminHeaders(admin))
    return res.data
  } catch (err) {
    throw unwrapError(err)
  }
}
