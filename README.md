# Online Plant Nursery Application

A micro-project building an online plant nursery system, developed by a 3-person team
as part of a structured learning program.

## Team

| Member | Owns |
|---|---|
| **Tamil Ks** | Login Management, Customer Registration, Plants Management (full CRUD) |
| Anubhav Sharma | Seeds Management, Planters Management, Order Booking (Plants & Seeds) |
| Priyani Naresh | Order Booking (Planters), Customer Management (admin), Admin Dashboard, Testing |

## Tech Stack (current phase)

This phase of the project is **Core Java + JPA with Hibernate** — a plain Maven
project, no Spring Boot or web layer yet. Later phases will add a Spring Boot REST
API and an Angular front end.

- **Language:** Java 17
- **Build tool:** Maven
- **ORM:** Hibernate 6.4.4 (JPA implementation)
- **Database:** H2 (in-memory)
- **Testing:** JUnit 5 + Mockito

## Project Structure

```
src/main/java/com/nursery/
├── entity/          # JPA entities: Admin, Address, Customer, Plant, Seed, Planter, Order
├── service/          # Service interfaces + implementations (business logic)
├── repository/       # Repository interfaces + implementations (data access via Hibernate)
├── exception/        # Custom exceptions for validation/not-found/auth failures
└── util/             # JPAUtil - EntityManagerFactory helper

src/main/resources/META-INF/
└── persistence.xml    # JPA/Hibernate config (H2 in-memory datasource)

src/test/java/com/nursery/
├── service/           # Unit tests (Mockito-mocked repositories)
└── repository/        # Integration tests (real H2 database via Hibernate)
```

## Implemented User Stories (Tamil Ks)

All stories below were built test-first (TDD: failing test → implementation →
passing test), with both a unit test (mocked repository) and an integration test
(real H2 database) per feature.

| ID | Story | Status |
|---|---|---|
| US-001 | User Login | ✅ Done |
| US-002 | Customer Registration | ✅ Done |
| US-003 | View Plants | ✅ Done |
| US-004 | Add Plant | ✅ Done |
| US-005 | Update Plant | ✅ Done |
| US-006 | Delete Plant | ✅ Done |

Full requirements, acceptance criteria, and the rest of the team's stories are in
`User_Stories.xlsx` (not included in this repo — see your project files) or your
team's shared tracker.

### Validation rules implemented

**Customer registration / login:**
- Username and password required (not blank) for login
- Name, username, password, and email required for registration
- Email must match a basic `local@domain.tld` shape
- Duplicate usernames are rejected at registration

**Plant CRUD:**
- `commonName` and `typeOfPlant` are required (not blank)
- `plantCost` and `plantsStock` must not be negative
- Update/Delete operations require the plant to already exist

## Running the project

### Prerequisites
- JDK 17 (Temurin recommended)
- Maven 3.6+

### Build
```bash
mvn compile
```

### Run all tests
```bash
mvn test
```

Expect all tests to pass (`BUILD SUCCESS`). No separate database setup is needed —
H2 runs in-memory and is configured entirely through `persistence.xml`.

### Run the server
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

### Current REST endpoints

| Method | Path | Story |
|---|---|---|
| POST | `/api/customers/login` | US-001 |
| POST | `/api/customers/register` | US-002 |
| GET | `/api/plants` (optional `?type=`) | US-003 |
| GET | `/api/plants/{id}` | US-003 |
| GET | `/api/plants/by-name/{commonName}` | US-003 |
| POST | `/api/plants` | US-004 |
| PUT | `/api/plants/{id}` | US-005 |
| DELETE | `/api/plants/{id}` | US-006 |

## Notes for the rest of the team

- The `ICustomerRepository` interface has one method beyond the original class
  diagram: `findByUsername(String)`, added to support duplicate-username checking
  during registration (US-002). Flagging this since it's a shared interface.
- Entities, service interfaces, and repository interfaces for **all** modules
  (Plant, Seed, Planter, Customer, Order) are already scaffolded from the shared
  class design — only the `*ServiceImpl` / `*RepositoryImpl` classes for Login,
  Registration, and Plant CRUD have been implemented so far. Seeds, Planters, and
  Order Booking implementations are still open for Anubhav and Priyani.
