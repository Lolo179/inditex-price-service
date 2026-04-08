# AGENTS

Notas para agentes de IA o revisores automatizados que trabajen con este repositorio.

## Estructura del proyecto

```
price-service/
├── contract/
│   └── swagger-contract.yaml        # Fuente de verdad de la API (contract-first)
├── src/
│   ├── main/java/com/inditex/prices/
│   │   ├── config/                  # OpenApiConfig
│   │   ├── controller/              # PriceController + mappers generados
│   │   ├── exception/               # GlobalExceptionHandler, ErrorMessages, PriceNotFoundException
│   │   ├── repository/              # PriceRepository + PriceEntity
│   │   └── service/                 # PriceService, PriceServiceImpl, mappers, domain
│   ├── main/resources/
│   │   ├── application.properties
│   │   └── data.sql                 # Datos de prueba (4 filas)
│   └── test/
│       ├── java/.../
│       │   ├── PriceControllerTest          # Full-stack @SpringBootTest
│       │   ├── PriceRepositoryTest          # @DataJpaTest (capa repositorio)
│       │   ├── PriceServiceImplTest         # Unit Mockito (capa servicio)
│       │   └── PriceServiceIntegrationTest  # Integración servicio + H2 real
│       └── resources/
│           ├── application-test.properties
│           └── price-service.postman_collection.json
├── .github/
│   └── workflows/
│       └── ci.yml               # GitHub Actions CI (triggered on PR to main)
├── Dockerfile                       # Multi-stage build (Maven builder + JRE 21 runtime)
├── pom.xml
└── README.md
```

## Clases generadas (no editar manualmente)

Las siguientes clases se generan automáticamente en cada `mvn compile` a partir de `contract/swagger-contract.yaml` y están excluidas de git:

- `src/main/java/com/inditex/prices/controller/api/PricesApi.java`
- `src/main/java/com/inditex/prices/controller/dto/PriceResponse.java`
- `src/main/java/com/inditex/prices/controller/dto/ErrorResponse.java`
- `src/main/java/com/inditex/prices/controller/dto/Currency.java`

Para modificar la API, editar **únicamente** `contract/swagger-contract.yaml`.

## Comandos útiles

```bash
# Compilar (también regenera las clases del contrato)
.\mvnw.cmd compile

# Ejecutar todos los tests
.\mvnw.cmd test

# Arrancar la aplicación
.\mvnw.cmd spring-boot:run

# Construir el JAR
.\mvnw.cmd package -DskipTests

# Construir imagen Docker
docker build -t price-service .

# Ejecutar contenedor
docker run -d -p 8080:8080 --name price-service price-service
```

## Convenciones

- Spring Boot 4.0.5 / Java 21
- Los paquetes de test slice (`@DataJpaTest`, `@AutoConfigureMockMvc`) están en sus propios starters Boot 4.x: `spring-boot-starter-data-jpa-test` y `spring-boot-starter-webmvc-test`
- `Currency` existe como enum en dos sitios: `service/domain/Currency.java` (dominio) y el generado `controller/dto/Currency.java` (DTO). MapStruct los mapea automáticamente.
- Los mensajes de error están centralizados en `exception/ErrorMessages.java`
- La colección Postman en `src/test/resources/price-service.postman_collection.json` cubre los 5 casos del ejercicio más 4 casos de error
