# Price Service

REST service that returns the applicable price for a product in an Inditex brand at a given date and time, resolving by priority when multiple rates overlap.

## Requirements

- Java 21
- Maven (or use the included Maven Wrapper — no installation needed)

## Build & Run

### Using Maven Wrapper (recommended)

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
.\mvnw.cmd spring-boot:run
```

### Using local Maven

```bash
mvn spring-boot:run
```

The application starts on **http://localhost:8080**

---

## API

### Get applicable price

```
GET /api/v1/prices
```

| Parameter         | Type     | Required | Description                              |
|-------------------|----------|----------|------------------------------------------|
| `applicationDate` | datetime | Yes      | ISO 8601 format — `2020-06-14T10:00:00` |
| `productId`       | integer  | Yes      | Product identifier                       |
| `brandId`         | integer  | Yes      | Brand identifier (1 = ZARA)              |

**Example request:**

```bash
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T10:00:00&productId=35455&brandId=1"
```

**Example response (200):**

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 1,
  "startDate": "2020-06-14T00:00:00",
  "endDate": "2020-12-31T23:59:59",
  "price": 35.50,
  "currency": "EUR"
}
```

**Error responses:**

| Status | Description                        |
|--------|------------------------------------|
| 400    | Missing or invalid parameter       |
| 404    | No applicable price found          |
| 500    | Unexpected server error            |

---

## Docker

### Build the image

```bash
docker build -t price-service .
```

### Run the container

```bash
docker run -d -p 8080:8080 --name price-service price-service
```

The application starts on **http://localhost:8080**

### Stop and remove

```bash
docker stop price-service && docker rm price-service
```

---

## CI

The repository includes a GitHub Actions workflow at `.github/workflows/ci.yml` that runs automatically on every pull request targeting `main`.

**Steps:**
1. Checkout code
2. Set up Java 21 (Temurin) with Maven cache
3. `./mvnw verify` — compiles, runs all 26 tests, and validates the build
4. Uploads Surefire test reports as a build artifact

The workflow must pass before a PR can be merged into `main` (enforced via branch protection rules).

---

## Swagger UI

Available at **http://localhost:8080/swagger-ui.html** once the application is running.

To explore the contract without running the app, open it directly in Swagger Editor:

[Open in Swagger Editor](https://editor.swagger.io/?url=https://raw.githubusercontent.com/Lolo179/inditex-price-service/main/contract/swagger-contract.yaml)

---

## Tests

```bash
# Linux / macOS
./mvnw test

# Windows
.\mvnw.cmd test
```

26 tests across four layers:

| Class                        | Type                           | Tests |
|------------------------------|--------------------------------|-------|
| `PriceServiceImplTest`       | Unit (Mockito)                 | 3     |
| `PriceRepositoryTest`        | JPA integration (@DataJpaTest) | 6     |
| `PriceServiceIntegrationTest`| Service + H2 (@SpringBootTest) | 9     |
| `PriceControllerTest`        | Full-stack (@SpringBootTest)   | 7     |
| `PriceServiceApplicationTests` | Context load                 | 1     |

---

## Postman collection

Importar desde `src/test/resources/price-service.postman_collection.json`.

Contiene 9 requests con assertions automáticas:
- Los 5 casos requeridos del ejercicio
- 404 producto inexistente
- 404 fecha fuera de rango
- 400 formato de fecha inválido
- 400 parámetro requerido ausente

Requiere la aplicación corriendo en `http://localhost:8080`.

---

## Architecture

```mermaid
flowchart TD
    subgraph API["API Layer (generated from contract)"]
        contract["contract/swagger-contract.yaml"]
        gen["openapi-generator-maven-plugin"]
        api["PricesApi (interface)"]
        dto["PriceResponse / ErrorResponse / Currency (DTOs)"]
        contract --> gen --> api & dto
    end

    subgraph Controller["Controller Layer"]
        ctrl["PriceController"]
        mapper_dto["PriceDtoMapper (MapStruct)"]
    end

    subgraph Service["Service Layer"]
        svc["PriceServiceImpl"]
        domain["Price (domain record)"]
    end

    subgraph Repository["Repository Layer"]
        repo["PriceRepository (JPA)"]
        entity["PriceEntity"]
    end

    subgraph DB["Database"]
        h2["H2 in-memory\n(PRICES table)"]
    end

    client["HTTP Client"] -->|GET /api/v1/prices| ctrl
    api -.implements.- ctrl
    ctrl --> mapper_dto --> domain
    ctrl --> svc
    svc --> repo
    repo --> entity --> h2
    svc --> domain
```

- **Contract-first**: `contract/swagger-contract.yaml` is the source of truth. The API interface and DTOs are generated at compile time via `openapi-generator-maven-plugin`.
- **MapStruct** handles all object mapping between layers.
- **H2 in-memory** database — schema created by Hibernate, seeded by `data.sql`.
