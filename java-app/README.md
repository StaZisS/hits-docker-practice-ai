# Java Application (ShopAPI)

## Description

Backend test task for SHIFT Lab 2022.  
Simple API for an electronics store.  

Spring Boot + PostgreSQL.

## Links

- OpenAPI/Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Архитектура: `docs/ARCHITECTURE.md`

## Run

### Docker Compose (recommended)

```sh
docker compose up --build
```

После старта:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### Locally (without Docker)

По умолчанию приложение ожидает PostgreSQL по адресу `db:5432` (см. `src/main/resources/application.properties`).

```sh
./mvnw clean package
```

```sh
./mvnw spring-boot:run
```

Чтобы запустить без PostgreSQL, используйте профиль `local` (H2 in-memory):

```sh
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

### Tests

Тесты используют in-memory H2 (профиль `test`):

```sh
./mvnw test
```

## API

### CRUD endpoints

- `GET /api/laptops`, `GET /api/laptops/{id}`, `POST /api/laptops/add`, `PUT /api/laptops/{id}`
- `GET /api/monitors`, `GET /api/monitors/{id}`, `POST /api/monitors/add`, `PUT /api/monitors/{id}`
- `GET /api/hdds`, `GET /api/hdds/{id}`, `POST /api/hdds/add`, `PUT /api/hdds/{id}`
- `GET /api/pcs`, `GET /api/pcs/{id}`, `POST /api/pcs/add`, `PUT /api/pcs/{id}`

### Analytics endpoints

- `GET /api/analytics/db` — количество сущностей по типам
- `GET /api/analytics/requests` — метрики запросов (in-memory, с момента старта)
- `GET /api/analytics/summary` — объединённый ответ

## Configuration

Основной конфиг: `src/main/resources/application.properties`. Для контейнеризации/CI удобнее переопределять через переменные окружения:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
