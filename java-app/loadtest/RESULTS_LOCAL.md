# Load test results (local profile, H2)

Команда:

```sh
CONCURRENCY=30 DURATION_SECONDS=30 SEED=100 bash loadtest/run-local.sh
```

Вывод:

```text
=== Load test results ===
Base URL: http://localhost:8080
Concurrency: 30
Elapsed: 30.01s
Requests: 407714
RPS: 13588.03
Errors: 0 (rate=0.0000)
Avg latency: 2.20 ms
Max latency: 115.55 ms
p50 latency: 1.77 ms
p95 latency: 3.63 ms
p99 latency: 6.12 ms
Status codes: {200=407714}
```

Примечания:

- Для запуска используется профиль `local` (`src/main/resources/application-local.properties`) и in-memory H2.
- Нагрузка — микс `GET /api/laptops`, `GET /api/laptops/{id}`, `GET /api/analytics/summary` (см. `loadtest/LoadTest.java`).

