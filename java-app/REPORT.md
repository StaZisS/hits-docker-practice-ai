# Отчёт по доработкам (ShopAPI)

## Использованные AI-инструменты

- Codex CLI (модель: GPT-5.2) — генерация тестов, рефакторинг, добавление функциональности, подготовка CI/CD, документации и сценариев нагрузочного тестирования.

## Примеры промптов (как использовался LLM)

Просто отправил задание из ГК

## Рефакторинг

- Несовместимость сборки с современными JDK из‑за Lombok → обновил Lombok, чтобы проект стабильно компилировался (это “проблемный участок”, потому что блокировал любые тесты/CI): pom.xml:62.
- Невозможность запускать тесты без внешнего PostgreSQL (в application.properties был жёсткий URL на db) → добавил тестовый профиль на H2 для изолированных прогонов: src/test/resources/application-test.properties:1, и внешний конфиг через env-переменные (без изменения логики приложения): src/main/
  resources/application.properties:1.
- Некорректный контракт API по ошибкам “not found”: сервисы бросали ResourceNotFoundException, но без явного маппинга это давало не тот HTTP/формат → добавил единый обработчик 404 + стабильный JSON-ответ: src/main/java/testtask/shift/shopapi/error/RestExceptionHandler.java:12, src/main/java/testtask/
  shift/shopapi/error/ApiError.java:5.
- Нестандартные/хрупкие аннотации и мелкие дефекты в контроллерах (использование com.sun.istack.NotNull, отсутствие consumes, опечатка в имени метода) → убрал com.sun.istack.NotNull, добавил consumes=application/json, исправил имя метода создания ноутбука (поведение эндпоинтов сохранилось):
  src/main/java/testtask/shift/shopapi/controller/LaptopController.java:1 (аналогично в src/main/java/testtask/shift/shopapi/controller/HardDriveController.java:1, src/main/java/testtask/shift/shopapi/controller/MonitorController.java:1, src/main/java/testtask/shift/shopapi/controller/
  PersonalComputerController.java:1).
- 