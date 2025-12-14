# Load testing (k6)

## Requirements

- Running ShopAPI on `http://localhost:8080` (or set `BASE_URL`)
- k6 (or Docker), либо локальный Java-скрипт (без зависимостей)

## Run via Docker

```sh
docker run --rm -i grafana/k6 run - < loadtest/k6.js
```

## Run with custom parameters

```sh
docker run --rm -i -e BASE_URL=http://localhost:8080 -e VUS=50 -e DURATION=60s grafana/k6 run - < loadtest/k6.js
```

Скрипт сам создаёт тестовые данные через `POST /api/*/add`, затем генерирует нагрузку (в основном `GET` на списки, немного `GET` по id).

## Local load test (no Docker)

Если Docker/k6 недоступны, можно запустить локальный нагрузочный тест на Java:

```sh
bash loadtest/run-local.sh
```

Результаты примера запуска: `loadtest/RESULTS_LOCAL.md`
