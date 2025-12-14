# Архитектура ShopAPI

## Кратко

Приложение — Spring Boot REST API для магазина электроники.

- **Controller слой**: HTTP API (`src/main/java/testtask/shift/shopapi/controller`)
- **Service слой**: бизнес-логика/обёртка над репозиториями (`src/main/java/testtask/shift/shopapi/service`)
- **Repository слой**: доступ к БД через Spring Data (`src/main/java/testtask/shift/shopapi/repository`)
- **Model слой**: JPA-сущности (`src/main/java/testtask/shift/shopapi/model`)

## Хранение данных

Сущности наследуются от общего `Product` (id, seriesNumber, producer, price, numberOfProductsInStock) и расширяются специфическими полями:

- `Laptop` → `size`
- `Monitor` → `diagonal`
- `HardDrive` → `capacity`
- `PersonalComputer` → `formFactor`

БД по умолчанию — PostgreSQL (конфиг в `src/main/resources/application.properties`).

## Ошибки API

Если сущность не найдена, сервисы кидают `ResourceNotFoundException`. Глобальный обработчик ошибок (`src/main/java/testtask/shift/shopapi/error/RestExceptionHandler.java`) возвращает HTTP 404 и JSON:

```json
{
  "timestamp": "2025-01-01T00:00:00Z",
  "status": 404,
  "message": "Laptop not found",
  "path": "/api/laptops/99"
}
```

## Аналитика

Добавлены эндпоинты `/api/analytics/*`:

- `/api/analytics/db` — количество сущностей по типам в БД
- `/api/analytics/requests` — in-memory метрики запросов (с момента старта приложения)
- `/api/analytics/summary` — объединённый ответ

Метрики запросов собираются через `HandlerInterceptor` (`src/main/java/testtask/shift/shopapi/analytics/RequestMetricsInterceptor.java`).

