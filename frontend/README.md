# BondMap Frontend — как запустить

Нужны две вещи: **backend уже работает** и **Android Studio**.

---

## Шаг 0. Что должно быть установлено

1. [Android Studio](https://developer.android.com/studio) (скачай и установи, если ещё нет).
2. Docker с уже запущенным backend (см. ниже).

---

## Шаг 1. Запусти backend

Открой PowerShell:

```powershell
cd C:\Users\MAN-MADE\Downloads\bondmap-backend\bondmap-backend
docker compose up
```

Оставь это окно открытым.

Проверка в браузере: http://localhost:8080/health  
Должно показать: `OK`

Без этого Android-приложение не получит данные.

---

## Шаг 2. Открой проект frontend в Android Studio

1. Запусти **Android Studio**.
2. На стартовом экране нажми **Open** (Открыть).
3. Выбери папку:

```text
C:\Users\MAN-MADE\Downloads\bondmap-backend\frontend
```

Важно: открывай именно `frontend`, не `bondmap-backend`.

4. Нажми **OK**.
5. Дождись надписи внизу вроде **Gradle Sync** / синхронизации.  
   Первый раз это может занять несколько минут (скачиваются библиотеки).

Если Studio предложит создать/обновить Gradle Wrapper — согласись (**OK** / **Create**).

---

## Шаг 3. Создай эмулятор телефона (если ещё нет)

1. В Android Studio сверху: иконка телефона / **Device Manager**.
2. **Create Device** → выбери любой телефон (например Pixel 6) → **Next**.
3. Скачай системный образ (например **API 34**) → **Next** → **Finish**.

---

## Шаг 4. Запусти приложение

1. Сверху в выпадающем списке устройств выбери свой эмулятор.
2. Нажми зелёную кнопку **Run** ▶ (или `Shift+F10`).
3. Дождись запуска эмулятора и установки приложения.

Должен открыться экран **BondMap** со списком облигаций  
(если в базе пусто — будет «Облигаций пока нет», это нормально).

---

## Что делать, если ошибка

### «Unable to resolve host» / сеть / пустой экран с ошибкой

1. Backend точно запущен? Проверь http://localhost:8080/health  
2. Запускаешь на **эмуляторе**, не на своём телефоне?  
   Для эмулятора адрес уже прописан: `http://10.0.2.2:8080/`  
   (это «localhost» твоего компьютера с точки зрения эмулятора).

### Запуск на своём реальном телефоне

1. Телефон и компьютер в одной Wi‑Fi сети.
2. Узнай IP компьютера (`ipconfig` → IPv4, например `192.168.0.15`).
3. В файле `app\build.gradle.kts` найди строку:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080/\"")
```

Замени на:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://192.168.0.15:8080/\"")
```

(подставь свой IP).

4. Sync Gradle → Run снова.

---

## Кратко (шпаргалка)

| Что | Куда |
|---|---|
| Backend | `docker compose up` в папке `bondmap-backend` |
| Frontend | Android Studio → Open → папка `frontend` → Run ▶ на эмуляторе |
| Проверка API | http://localhost:8080/health |

Экраны в приложении: список → тап по облигации = детали; иконка лупы = поиск.
