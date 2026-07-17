# 🌿 Greenroot — Online Plant Nursery Application

A full-stack Online Plant Nursery web application built as a micro-project at Navikenz. The system allows customers to browse and order plants, seeds, and planters, while admins manage inventory, customers, and orders through a dedicated dashboard. Features an AI-powered plant care assistant chatbot.

---

## 👥 Team

| Member | User Stories | Scope |
|---|---|---|
| **Tamil Iniyan KS** | US-001 to US-006 | Customer login, registration, Plant CRUD + frontend |
| **Anubhav Sharma** | US-007 to US-012 | Seeds, Planters, Order placement (Plants & Seeds) + frontend |
| **Priyani Naresh** | US-013 to US-019 | Order placement (Planters), Customer management, Admin Dashboard, Testing + frontend |

---

## 🛠️ Tech Stack

### Backend
| Technology | Purpose |
|---|---|
| Java 17 (Temurin) | Core language |
| Spring Boot 3.2.5 | REST API framework |
| Hibernate 6.4.4 (JPA) | ORM — manual config via `persistence.xml` |
| MySQL | Production database |
| Maven 3.9+ | Build tool |
| JUnit 5 + Mockito | Unit and integration testing |

### Frontend
| Technology | Purpose |
|---|---|
| React 18 | UI framework |
| Vite | Dev server and build tool |
| React Router v6 | Client-side routing |
| Axios | HTTP client |
| Groq API (Llama 3.3) | AI plant care chatbot |
| Custom CSS | Design system (no UI library) |

---

## 📋 User Stories

| ID | Story | Owner | Status |
|---|---|---|---|
| US-001 | Customer Login | Tamil Iniyan | ✅ Done |
| US-002 | Customer Registration | Tamil Iniyan | ✅ Done |
| US-003 | View Plants | Tamil Iniyan | ✅ Done |
| US-004 | Add Plant | Tamil Iniyan | ✅ Done |
| US-005 | Update Plant | Tamil Iniyan | ✅ Done |
| US-006 | Delete Plant | Tamil Iniyan | ✅ Done |
| US-007 | View Seeds | Anubhav | ✅ Done |
| US-008 | Manage Seeds (Admin) | Anubhav | ✅ Done |
| US-009 | View Planters | Anubhav | ✅ Done |
| US-010 | Manage Planters (Admin) | Anubhav | ✅ Done |
| US-011 | Order Plants | Anubhav | ✅ Done |
| US-012 | Order Seeds | Anubhav | ✅ Done |
| US-013 | Order Planters | Priyani | ✅ Done |
| US-014 | View Order History | Priyani | ✅ Done |
| US-015 | Cancel Order | Priyani | ✅ Done |
| US-016 | Manage Customers (Admin) | Priyani | ✅ Done |
| US-017 | Admin Dashboard | Priyani | ✅ Done |
| US-018 | Testing | Priyani | ✅ Done |
| US-019 | Testing | Priyani | ✅ Done |

---

## 🏗️ Project Structure

```
plant-nursery/
├── src/
│   ├── main/
│   │   ├── java/com/nursery/
│   │   │   ├── config/          # AppConfig, CorsConfig, AdminBootstrap
│   │   │   ├── controller/      # REST controllers + GlobalExceptionHandler
│   │   │   ├── dto/             # Request/Response DTOs
│   │   │   ├── entity/          # JPA entities (Admin, Customer, Plant, Seed, Planter, Order, Address)
│   │   │   ├── exception/       # Custom exceptions
│   │   │   ├── repository/      # Repository interfaces + Hibernate implementations
│   │   │   ├── service/         # Service interfaces + business logic implementations
│   │   │   └── util/            # JPAUtil (EntityManagerFactory helper)
│   │   └── resources/
│   │       └── META-INF/
│   │           └── persistence.xml   # Hibernate/MySQL config
│   └── test/
│       └── java/com/nursery/
│           ├── controller/      # Controller tests
│           ├── repository/      # Integration tests (real DB)
│           └── service/         # Unit tests (Mockito)
│
├── frontend/
│   ├── src/
│   │   ├── api/                 # Axios client (all backend calls in one place)
│   │   ├── components/          # Reusable components (Navbar, Modals, Tags, Chatbot)
│   │   ├── context/             # AuthContext (login state management)
│   │   ├── pages/               # Page components
│   │   │   ├── Login.jsx
│   │   │   ├── Register.jsx
│   │   │   ├── PlantCatalog.jsx
│   │   │   ├── PlantDetail.jsx  # Includes AI chatbot
│   │   │   ├── SeedCatalog.jsx
│   │   │   ├── SeedDetail.jsx
│   │   │   ├── PlanterCatalog.jsx
│   │   │   ├── PlanterDetail.jsx
│   │   │   ├── OrderPlant.jsx
│   │   │   ├── OrderSeed.jsx
│   │   │   ├── OrderPlanter.jsx
│   │   │   ├── OrderHistory.jsx
│   │   │   └── admin/
│   │   │       ├── AdminDashboard.jsx
│   │   │       ├── ManagePlants.jsx
│   │   │       ├── ManageSeeds.jsx
│   │   │       ├── ManagePlanters.jsx
│   │   │       └── ManageCustomers.jsx
│   │   ├── index.css            # Full design system (tokens, components, layout)
│   │   └── main.jsx
│   ├── .env.example             # Environment variable template
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── .gitignore
├── pom.xml
└── README.md
```

---

## 🔌 REST API Endpoints

### Auth
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/customers/login` | Customer login | None |
| POST | `/api/customers/register` | Customer registration | None |
| POST | `/api/admins/login` | Admin login | None |
| GET | `/api/admins/dashboard` | Dashboard stats | Admin |

### Plants
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/plants` | Get all plants (`?type=` filter) | None |
| GET | `/api/plants/{id}` | Get plant by ID | None |
| GET | `/api/plants/by-name/{name}` | Get plant by name | None |
| POST | `/api/plants` | Add plant | Admin |
| PUT | `/api/plants/{id}` | Update plant | Admin |
| DELETE | `/api/plants/{id}` | Delete plant | Admin |

### Seeds
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/seeds` | Get all seeds (`?type=` filter) | None |
| GET | `/api/seeds/{id}` | Get seed by ID | None |
| GET | `/api/seeds/by-name/{name}` | Get seed by name | None |
| POST | `/api/seeds` | Add seed | Admin |
| PUT | `/api/seeds/{id}` | Update seed | Admin |
| DELETE | `/api/seeds/{id}` | Delete seed | Admin |

### Planters
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/planters` | Get all planters (`?minCost=&maxCost=` filter) | None |
| GET | `/api/planters/{id}` | Get planter by ID | None |
| GET | `/api/planters/by-shape/{shape}` | Get planters by shape | None |
| POST | `/api/planters` | Add planter | Admin |
| PUT | `/api/planters/{id}` | Update planter | Admin |
| DELETE | `/api/planters/{id}` | Delete planter | Admin |

### Customers
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/customers` | Get all customers | Admin |
| GET | `/api/customers/{id}` | Get customer by ID | Admin |
| GET | `/api/customers/search/{username}` | Search by username | Admin |
| PUT | `/api/customers/{id}` | Update customer | Admin |
| DELETE | `/api/customers/{id}` | Delete customer | Admin |

### Orders
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/orders/plants` | Place plant order | None |
| POST | `/api/orders/seeds` | Place seed order | None |
| POST | `/api/orders/planters` | Place planter order | None |
| GET | `/api/orders/{id}` | Get order by ID | None |
| GET | `/api/orders` | Get all orders | None |
| GET | `/api/orders/customer/{customerId}` | Get orders by customer | None |
| PUT | `/api/orders/{id}/cancel` | Cancel order | None |

> **Admin authentication:** Admin-protected endpoints require `adminUsername` and `adminPassword` as request headers (no token system — header-based auth as per project design).

---

## 🎨 Frontend Pages

| Route | Page | Access |
|---|---|---|
| `/login` | Login (customer / admin toggle) | Public |
| `/register` | Customer registration | Public |
| `/plants` | Plant catalog with type filters | Public |
| `/plants/:id` | Plant detail + AI chatbot | Public |
| `/seeds` | Seed catalog | Public |
| `/seeds/:id` | Seed detail | Public |
| `/planters` | Planter catalog | Public |
| `/planters/:id` | Planter detail | Public |
| `/plants/:id/order` | Place plant order | Customer |
| `/seeds/:id/order` | Place seed order | Customer |
| `/planters/:id/order` | Place planter order | Customer |
| `/orders` | Order history | Customer |
| `/admin/dashboard` | Admin dashboard | Admin |
| `/admin/plants` | Manage plants | Admin |
| `/admin/seeds` | Manage seeds | Admin |
| `/admin/planters` | Manage planters | Admin |
| `/admin/customers` | Manage customers | Admin |

---

## 🤖 AI Plant Care Chatbot

Each Plant Detail page includes a floating AI chatbot powered by **Groq (Llama 3.3-70b)**. The chatbot is plant-aware — the full details of the plant currently being viewed (name, type, difficulty, temperature, description etc.) are injected into the system prompt automatically, so all answers are specific to that plant rather than generic advice.

**Example questions customers can ask:**
- How often should I water this plant?
- Is it safe for pets and children?
- Why are the leaves turning yellow?
- What pot size does it need?
- How much sunlight does it require?

The chatbot runs entirely on the frontend — no backend changes required. API calls go directly from the browser to Groq's API.

---

## ⚙️ Setup & Running Locally

### Prerequisites
- JDK 17 (Temurin recommended)
- Maven 3.6+
- MySQL 8.0+ running locally
- Node.js 18+
- A free Groq API key from [console.groq.com](https://console.groq.com)

### 1. Database setup
```sql
CREATE DATABASE nurserydb;
```
Or let Hibernate create it automatically via `createDatabaseIfNotExist=true` in `persistence.xml`.

### 2. Configure database credentials
Open `src/main/resources/META-INF/persistence.xml` and replace:
```xml
<property name="jakarta.persistence.jdbc.user" value="root"/>
<property name="jakarta.persistence.jdbc.password" value="CHANGE_ME_LOCALLY"/>
```
with your actual MySQL username and password.

> ⚠️ Never commit your real password to GitHub — keep `CHANGE_ME_LOCALLY` as the placeholder in the committed version.

### 3. Run the backend
```bash
mvn clean compile
mvn spring-boot:run
```
Hibernate will auto-create all tables on first run. A default admin account (`admin` / `admin123`) is created automatically on startup.

Backend runs at: `http://localhost:8080`

### 4. Configure frontend environment
Inside the `frontend/` folder, create a `.env` file:
```
VITE_GROQ_API_KEY=your_groq_api_key_here
```
Copy from `frontend/.env.example` as a template.

### 5. Run the frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend runs at: `http://localhost:5173`

### 6. Run tests
```bash
mvn test
```

---

## 🔐 Default Admin Credentials

| Username | Password |
|---|---|
| `admin` | `admin123` |

Created automatically on every backend startup via `AdminBootstrap.java`.

---

## 🗂️ Key Design Decisions

- **Manual JPA/Hibernate** instead of Spring Data JPA — all persistence is managed via `JPAUtil` and `EntityManager` directly, with `persistence.xml` as the single config file. `HibernateJpaAutoConfiguration` is excluded from Spring Boot's auto-configuration.
- **Header-based admin auth** — no JWT or session tokens. Admin credentials are sent as `adminUsername` / `adminPassword` headers on every protected request. The frontend stores these in `AuthContext` + `localStorage` after admin login.
- **Image URLs** — plant, seed, and planter images are stored as URL strings in the database. Admins paste any image URL (Unsplash, Wikipedia Commons etc.) when adding or editing items. No file upload or cloud storage needed.
- **TDD approach** — all service and repository classes were written test-first (failing test → implementation → passing test) using JUnit 5 and Mockito.
- **CORS** — `CorsConfig.java` allows requests from `http://localhost:5173` (Vite dev server) to the Spring Boot backend.

---

## 📦 Postman Collection

A Postman collection covering all endpoints is included at the root of the repo:
`Plant-Nursery-API.postman_collection.json`

Import it into Postman to test all endpoints. It includes:
- Collection variables (`baseUrl`, `adminUsername`, `adminPassword`)
- Auto-chaining test scripts that save returned IDs (`plantId`, `customerId`, `orderId` etc.) for use in subsequent requests
- Sample request bodies for all POST/PUT endpoints