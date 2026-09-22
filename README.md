# BondMap

Учебное клиент-серверное приложение (анализ облигаций) для курса SRE.

```text
.
├── Отчёт.md              ← отчёт к домашнему заданию (обязательный артефакт)
├── README.md             ← этот файл
├── bondmap-backend/      ← backend (Kotlin, Spring Boot, PostgreSQL, Docker)
└── frontend/             ← Android-клиент (Compose, Retrofit)
```

## Быстрая проверка

### 1. Backend

```powershell
cd bondmap-backend
copy .env.example .env
docker compose up --build
```

| Что | URL |
|---|---|
| Health | http://localhost:8080/health |
| Swagger | http://localhost:8080/swagger-ui/index.html |
| API | http://localhost:8080/api/bonds |

Остановка: `docker compose down`

### 2. Frontend

1. Backend запущен.  
2. Android Studio → **Open** → папка `frontend`.  
3. Run на эмуляторе.

## Что реализовано

- Frontend + backend  
- PostgreSQL + Flyway  
- CRUD облигаций по HTTP  
- История цен, доходность, поиск  
- Соответствие Twelve-Factor App (см. `Отчёт.md`)  
- Docker Compose (backend + postgres)

## Чеклист перед сдачей архива

Собрать zip так, чтобы внутри была структура выше (можно назвать корень `bondmap`).

**Включить:**

- [ ] `Отчёт.md`
- [ ] `README.md`
- [ ] `bondmap-backend/` — исходники, `Dockerfile`, `docker-compose.yml`, `.env.example`, `gradlew*`, миграции
- [ ] `frontend/` — исходники Android (`app/`, gradle-файлы)

**Не включать:**

- [ ] `.env` (секреты)
- [ ] `**/build/`, `**/.gradle/`, `**/.idea/`
- [ ] `frontend/local.properties` (путь к SDK на вашей машине)
- [ ] `**/captures/`, эмуляторные кэши

Пример исключения при архивации вручную: не тащить каталоги `build` и `.gradle`.

## Критерии задания — покрытие

| Требование | Где смотреть |
|---|---|
| Frontend + backend | `frontend/`, `bondmap-backend/` |
| PostgreSQL | Compose + JPA/Flyway |
| CRUD | `/api/bonds` (+ Swagger) |
| Twelve-Factor | `Отчёт.md` §4 |
| Отчёт.md | корень репозитория |
