# BondMap Frontend — как запустить

Нужны **backend** и **Android Studio**.

## 1. Backend

Из корня репозитория:

```powershell
cd bondmap-backend
copy .env.example .env
docker compose up --build
```

Проверка: http://localhost:8080/health → `OK`

## 2. Frontend

1. Android Studio → **Open** → папка `frontend` (рядом с `bondmap-backend`, не внутри неё).
2. Дождитесь Gradle Sync.
3. Запустите на эмуляторе (API уже указывает на `http://10.0.2.2:8080/`).

Адрес API можно переопределить без правки кода:

```powershell
$env:API_BASE_URL="http://192.168.0.15:8080/"
.\gradlew.bat assembleDebug
```

или `-PAPI_BASE_URL=http://192.168.0.15:8080/`

## Тесты клиента (без эмулятора)

```powershell
cd frontend
.\gradlew.bat test
```

Экраны: список → детали; лупа → поиск; ⓘ → «О данных».
