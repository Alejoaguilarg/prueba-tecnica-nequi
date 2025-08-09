# Franquicias API – Spring Boot WebFlux

> Prueba técnica: API reactiva para gestionar **franquicias**, **sucursales** y **productos** (stock).

## 1. Resumen
- **Stack:** Java 21, Spring Boot WebFlux, R2DBC, PostgreSQL, Gradle multi–módulo.
- **Arquitectura:** Hexagonal (puertos y adaptadores) basada en **Scaffold Clean Architecture**.
- **Persistencia:** Postgres vía R2DBC (no bloqueante).
- **Empaquetado:** Docker + Docker Compose (perfil `docker`).
- **Pruebas:** JUnit 5, WebTestClient, Jacoco (objetivo ≥ 70% global).
- **Logging:** Log4j2 + SLF4J.
- **Errores:** `GlobalErrorWebExceptionHandler` + `GlobalErrorAttributes` personalizados (400/404/500 coherentes REST).

---

## 2. Arquitectura
- **Domain (`:model`, `:usecase`):** entidades y casos de uso puros, sin dependencias de Spring.
- **Driven adapters (`:r2dbc-postgresql`):** repositorios reactivos y mapeos a entidades de persistencia.
- **Entry points (`:reactive-web`):** handlers/routers WebFlux, DTOs de entrada/salida, validaciones.
- **Boot (`:app-service`):** clase `@SpringBootApplication`, configuración de perfiles, CORS y observabilidad.

**Principios:**
- Separación de responsabilidades, inversión de dependencias, DTOs en el borde, dominio limpio en el centro.
- Librerías **compatibles con reactivo**: `spring-boot-starter-webflux`, `spring-boot-starter-data-r2dbc`, driver `r2dbc-postgresql`.

---

## 3. Esquema de datos
```sql
CREATE TABLE franchise (
  franchise_id BIGSERIAL PRIMARY KEY,
  name         VARCHAR(255) NOT NULL
);

CREATE TABLE branch (
  branch_id    BIGSERIAL PRIMARY KEY,
  name         VARCHAR(255) NOT NULL,
  franchise_id BIGINT       NOT NULL REFERENCES franchise(franchise_id)
);

CREATE TABLE products (
  id        BIGSERIAL PRIMARY KEY,
  name      VARCHAR(255) NOT NULL,
  stock     INTEGER      NOT NULL,
  branch_id BIGINT       NOT NULL REFERENCES branch(branch_id)
);
```

---

## 4. Endpoints (RESTful)
> Base: `http://localhost:8080`

### 4.1 Franquicias
- **POST** `/api/franchises` → crear franquicia.
    - Request: `{ "name": "Franquicia A" }`
    - Response: `201 Created` + `FranchiseResponse { id, name }`
- **PATCH** `/api/franchises/{id}` *(Plus)* → actualizar nombre.
    - Request: `{ "name": "Nuevo nombre" }`
    - Response: `200 OK` + `FranchiseResponse`

### 4.2 Sucursales
- **POST** `/api/franchises/{franchiseId}/branches` → crear sucursal en franquicia.
    - Request: `{ "name": "Sucursal Centro" }`
    - Response: `201 Created` + `BranchResponse { id, name, franchiseId }`
- **PATCH** `/api/branches/{id}` *(Plus)* → actualizar nombre.

### 4.3 Productos
- **POST** `/api/branches/{branchId}/products` → crear producto en sucursal.
    - Request: `{ "name": "Coca-Cola", "stock": 100 }`
    - Response: `201 Created` + `ProductResponse { id, name, stock, branchId }`
- **PATCH** `/api/products/{id}/stock` → modificar stock.
    - Request: `{ "stock": 140 }`
    - Response: `200 OK` + `ProductResponse`
- **DELETE** `/api/products/{id}` → eliminar producto.
    - Response: `204 No Content`
- **GET** `/api/franchises/{id}/max-stock-products` → **producto con más stock por sucursal** de la franquicia.
    - Response: `200 OK` + `[{ branchId, branchName, product: { id, name, stock } }]`

**Códigos y errores**
- `400 Bad Request`: validación/decoding (e.g., `stock` no numérico) → `errorCode=INVALID_INPUT`.
- `404 Not Found`: recurso inexistente.
- `500 Internal Server Error`: errores no controlados.

---

## 5. Cómo ejecutar (Docker Compose)
Requisitos: Docker Desktop.

1) **Variables** (compose ya las define):
```
DATABASE_HOST=db
DATABASE_PORT=5432
DATABASE_NAME=prueba_tecnica
DATABASE_USER=root
DATABASE_PASSWORD=1234567890
DATABASE_SCHEMA=public
```

2) **Levantar**
```bash
docker compose up -d --build
```

3) **Health**
```bash
curl http://localhost:8080/actuator/health  # {"status":"UP"}
```

4) **Datos iniciales (opcional y recomendado)**
   Coloca SQL en `deployment/db-init/*.sql` y monta la carpeta:
```yaml
db:
  volumes:
    - pgdata:/var/lib/postgresql/data
    - ./deployment/db-init:/docker-entrypoint-initdb.d
```
Reinicia limpio: `docker compose down -v && docker compose up -d --build`.

---

## 6. Ejemplos cURL rápidos
```bash
# Crear franquicia
curl -X POST http://localhost:8080/api/franchises   -H 'Content-Type: application/json'   -d '{"name":"Franquicia A"}'

# Crear sucursal
curl -X POST http://localhost:8080/api/franchises/1/branches   -H 'Content-Type: application/json'   -d '{"name":"Sucursal 1A"}'

# Crear producto
curl -X POST http://localhost:8080/api/branches/1/products   -H 'Content-Type: application/json'   -d '{"name":"Coca-Cola","stock":100}'

# Actualizar stock
curl -X PATCH http://localhost:8080/api/products/1/stock   -H 'Content-Type: application/json'   -d '{"stock":140}'

# Max stock por sucursal
curl http://localhost:8080/api/franchises/1/max-stock-products
```

---

## 7. Pruebas y cobertura
### Ejecutar tests
```bash
./gradlew clean test
```

### Cobertura global (Jacoco)
En el root hay tareas para **merge** y **reporte global**:
```bash
./gradlew --no-configuration-cache -x validateStructure clean test jacocoRootReport
# Reporte HTML: build/reports/jacoco/jacocoRootReport/html/index.html
```
Umbral recomendado: `>= 70%` global (configurable con `JacocoCoverageVerification`).

### Qué se prueba
- Handlers/routers con `WebTestClient`.
- Handler global de errores (400 por DecodingException, 404 por ResponseStatusException, 500 por fallback).
- Adapters de repositorio (save/update/delete y consultas reactivas).

---

## 8. Observabilidad y logging
- **Actuator:** `/actuator/health`, `/info`.
- **Logging:** Log4j2; niveles configurables por perfil.

---

## 9. Decisiones de diseño (resumen)
- **WebFlux + R2DBC**: IO no bloqueante para escalar con menos hilos.
- **Hexagonal**: dominio aislado; cambia la infraestructura sin afectar casos de uso.
- **DTOs de salida**: nunca exponemos entidades internas.
- **Manejo de errores centralizado**: respuestas consistentes; mapping explícito de 400/404/500.
- **CORS** vía YAML en perfil `docker` para evitar colisiones de beans.

