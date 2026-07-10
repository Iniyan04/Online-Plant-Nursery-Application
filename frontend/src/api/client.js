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
