# Сборка coaldust 1.62.0 через GitHub Actions

Этот репозиторий настроен для автоматической сборки мода в JAR через GitHub Actions — бесплатно, без установки JDK/Gradle на твою машину.

## Шаг 1. Залей исходник на GitHub

```bash
cd /path/to/coaldust   # папка с распакованным исходником
git init
git add .
git commit -m "coaldust 1.62.0 source"
git branch -M main
git remote add origin https://github.com/<твой-ник>/coaldust.git
git push -u origin main
```

Если репозиторий ещё не создан — зайди на https://github.com/new, создай **публичный** репо с именем `coaldust` (не ставь галочки «Initialize with README»).

## Шаг 2. Дождись сборки

1. Открой свой репо на GitHub
2. Вкладка **Actions**
3. Увидишь запущенный workflow «Build mod» (запускается автоматически при пуше)
4. Жди 5-15 минут. Зелёная галочка = сборка успешна, красный крестик = упала

## Шаг 3. Скачай JAR

1. Вкладка **Actions** → кликни на успешный прогон
2. Внизу страницы — раздел **Artifacts**
3. Скачай `coaldust-1.62.0-jar` (это ZIP-архив, внутри которого лежит `.jar`)
4. Распакуй архив — там будет `coaldust-1.62.0.jar`
5. Положи в `~/.minecraft/mods/` (или `%APPDATA%/.minecraft/mods/` на Windows)

## Шаг 4. Если сборка упала

1. Открой упавший прогон
2. Кликни на job `build`
3. Прокрути лог до ошибки — там будет полный stack trace
4. Если упало на этапе компиляции (например, `error: incompatible types`) — это декомпиляторный артефакт, поправимо. Пришли мне лог:
   - Открой прогон на GitHub
   - Справа вверху — `...` → `Download logs`
   - Пришли мне `logs.zip` или скопируй последние 50 строк с ошибкой

5. Также рядом с ошибкой в **Artifacts** будет `build-log` — там детальные отчёты компилятора

## Шаг 5. Повторная сборка после правок

После любого изменения в исходнике:

```bash
git add .
git commit -m "fix: something"
git push
```

GitHub сам запустит новую сборку. Через 10 минут — свежий JAR в Artifacts.

Или **ручной запуск** без коммита: вкладка Actions → «Build mod» → справа кнопка «Run workflow».

## Шаг 6. (Опционально) Приватный репо

Если не хочешь публичный исходник:
- GitHub даёт 2000 минут/месяц бесплатных Actions для приватных репо
- Создай приватный репо при создании
- Сборка работает идентично

## Что внутри

- `.github/workflows/build.yml` — определение workflow (GitHub Actions)
- `.gitignore` — игнорирует кэш Gradle, build/, IDE-файлы
- `gradle.properties` — `mod_version=1.62.0`, MC 1.21.1, NeoForge 21.1.209
- `build.gradle` — плагин `net.neoforged.moddev` 2.0.107, Java 21
- `src/main/` — 86 Java-классов + ассеты + лут-таблицы + локализация

## Требования к самой сборке

GitHub Actions runner (ubuntu-latest) имеет:
- 4 ядра CPU
- 16 ГБ RAM
- 14 ГБ SSD
- Свободный доступ ко всем maven-репозиториям NeoForge

Этого более чем достаточно для сборки NeoForge-мода.

## Проверь, что у тебя в `build.gradle`

Должен быть плагин `net.neoforged.moddev` (он есть в твоём исходнике). Если в твоём старом `build.gradle` используется `net.minecraftforge.gradle` — сборка НЕ пройдёт, нужно менять плагин (NeoForge и Forge — разные экосистемы).
