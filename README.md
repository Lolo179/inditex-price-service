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

## Swagger UI

Available at **http://localhost:8080/swagger-ui.html** once the application is running.

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

```
Controller  →  Service  →  Repository
    ↓              ↓
 DTO Mapper   Entity Mapper
    ↓              ↓
 PriceResponse   Price (domain record)
```

- **Contract-first**: `contract/swagger-contract.yaml` is the source of truth. The API interface and DTOs are generated at compile time via `openapi-generator-maven-plugin`.
- **MapStruct** handles all object mapping between layers.
- **H2 in-memory** database — schema created by Hibernate, seeded by `data.sql`.
