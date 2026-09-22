# BondMap Backend

HTTP API для анализа облигаций (Kotlin + Spring Boot + PostgreSQL).

## Быстрый старт (Docker)

```bash
cp .env.example .env
# при необходимости измените пароли в .env

docker compose up --build
```

Сервисы:

| Сервис | URL |
|---|---|
| Backend API | http://localhost:8080 |
| Health | http://localhost:8080/health |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| PostgreSQL | localhost:5432 |

Остановка: `docker compose down`.

## Локальный запуск без контейнера backend

1. Поднять только БД: `docker compose up postgres -d`
2. Задать переменные (или скопировать из `.env.example`):

```text
DATABASE_URL=jdbc:postgresql://localhost:5432/bondmap
DATABASE_USERNAME=bondmap
DATABASE_PASSWORD=bondmap_password
PORT=8080
```

3. Запустить приложение:

```bash
./gradlew bootRun
```

Windows: `gradlew.bat bootRun`.

## Сборка

```bash
./gradlew build
./gradlew bootJar
```

## Конфигурация (Twelve-Factor)

Секреты и адреса БД задаются окружением, не коммитятся в Git:

| Переменная | Описание |
|---|---|
| `PORT` | порт HTTP (по умолчанию 8080) |
| `DATABASE_URL` | JDBC URL |
| `DATABASE_USERNAME` | пользователь |
| `DATABASE_PASSWORD` | пароль |

Образец: `.env.example`.

## Тесты

```bash
./gradlew test
```

## Документация курса

См. [Отчёт.md](Отчёт.md) — домен, стек, сущности, соответствие Twelve-Factor App.
