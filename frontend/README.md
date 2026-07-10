# Greenroot Nursery — Frontend (Iniyan's modules)

React implementation of US-001 to US-006:

- **Login** (`/login`) — customer/admin toggle — US-001
- **Register** (`/register`) — customer sign-up — US-002
- **Plant Catalog** (`/plants`) — browse + filter by type — US-003
- **Plant Detail** (`/plants/:id`) — full plant view — US-003
- **Manage Plants** (`/admin/plants`, admin-only) — add/edit/delete — US-004, US-005, US-006

## Prerequisites

- Node.js 18+ and npm
- The Spring Boot backend running on `http://localhost:8080`
- **Important:** copy `CorsConfig.java` (included alongside this README) into your backend at
  `src/main/java/com/nursery/config/CorsConfig.java`, then restart the backend. Without it, the
  browser will block all requests from this app with a CORS error.

## Run it

```bash
npm install
npm run dev
```

Opens at `http://localhost:5173`.

## Notes

- The backend has no session tokens. For admin actions, this app keeps the admin's
  username/password in memory (and localStorage) after login, and resends them as the
  `adminUsername` / `adminPassword` headers the backend expects on every protected call.
  This is fine for a class project demo, but isn't how you'd store credentials in production.
- H2 resets on backend restart — if you restart the Spring Boot app, re-log in and re-add plants.
- Default admin: `admin` / `admin123`.
- API base URL is hardcoded in `src/api/client.js` (`http://localhost:8080`) — change it there
  if your backend runs on a different port.
