import requests
import json
import os
import re

# Настройки Grafana
GRAFANA_URL = "http://localhost:3000"
AUTH = ("admin", "admin")  # логин/пароль

# ID и имена дашбордов для скачивания с grafana.com
DASHBOARDS_FROM_COM = {
    # Spring Boot / JVM
    "12900": "spring-boot-3x-stats",
    "4701": "jvm-micrometer",
    "10280": "spring-boot-apm",

    # Инфраструктура
    "7589": "kafka-exporter",
    "763": "redis-dashboard",
    "9628": "postgresql-dashboard",
    "1860": "node-exporter-full",

    # Docker
    "893": "docker-monitoring",
    "179": "docker-system-monitoring"
}

# ID дашбордов для экспорта из локальной Grafana
DASHBOARDS_FROM_LOCAL = {
    "12900": "spring-boot-12900",
    "4701": "jvm-4701",
    "7589": "kafka-7589",
    "9628": "postgres-9628",
    "1860": "node-exporter-1860"
}

def safe_filename(name):
    """Преобразует строку в безопасное имя файла"""
    # Заменяем недопустимые символы на подчеркивания
    name = re.sub(r'[\\/*?:"<>|/]', '_', name)
    # Убираем лишние подчеркивания
    name = re.sub(r'_+', '_', name)
    # Убираем подчеркивания в начале и конце
    name = name.strip('_')
    return name

# Создаем папку для дашбордов
os.makedirs("grafana-dashboards", exist_ok=True)

print("=" * 60)
print("🚀 УТИЛИТА ДЛЯ ЭКСПОРТА ДАШБОРДОВ GRAFANA")
print("=" * 60)

# ЧАСТЬ 1: СКАЧИВАНИЕ С GRAFANA.COM
print("\n📥 ЧАСТЬ 1: Скачивание дашбордов с grafana.com")
print("-" * 50)

downloaded_count = 0
for dash_id, dash_name in DASHBOARDS_FROM_COM.items():
    try:
        print(f"🔄 Загрузка дашборда ID {dash_id} ({dash_name})...")

        # Запрашиваем дашборд с grafana.com
        response = requests.get(
            f"https://grafana.com/api/dashboards/{dash_id}/revisions/latest/download",
            headers={
                "Accept": "application/json"
            },
            timeout=30
        )

        if response.status_code == 200:
            dashboard_data = response.json()

            # Очищаем id (чтобы при импорте создавался новый)
            if 'id' in dashboard_data:
                dashboard_data['id'] = None

            # Сохраняем в файл с безопасным именем
            filename = f"grafana-dashboards/{dash_name}.json"
            with open(filename, 'w', encoding='utf-8') as f:
                json.dump(dashboard_data, f, indent=2, ensure_ascii=False)

            print(f"   ✅ Сохранен: {filename}")
            print(f"   📊 Название: {dashboard_data.get('title', 'Неизвестно')}")
            downloaded_count += 1
            print()
        else:
            print(f"   ❌ Ошибка {response.status_code} для дашборда {dash_id}")
            print()

    except requests.exceptions.Timeout:
        print(f"   ⏱️ Таймаут при загрузке дашборда {dash_id}")
        print()
    except requests.exceptions.ConnectionError:
        print(f"   🔌 Ошибка подключения при загрузке дашборда {dash_id}")
        print()
    except Exception as e:
        print(f"   ❌ Непредвиденная ошибка при загрузке {dash_id}: {e}")
        print()

print(f"📊 Загружено дашбордов с grafana.com: {downloaded_count}")

# ЧАСТЬ 2: ЭКСПОРТ ИЗ ЛОКАЛЬНОЙ GRAFANA
print("\n📤 ЧАСТЬ 2: Экспорт дашбордов из локальной Grafana")
print("-" * 50)

try:
    # Проверяем подключение к локальной Grafana
    print("🔄 Подключение к локальной Grafana...")
    test_response = requests.get(f"{GRAFANA_URL}/api/health", auth=AUTH, timeout=10)

    if test_response.status_code == 200:
        print(f"   ✅ Подключение успешно (версия: {test_response.json().get('version', 'неизвестно')})")
        print()

        # Получаем список дашбордов
        response = requests.get(
            f"{GRAFANA_URL}/api/search",
            auth=AUTH,
            timeout=30
        )

        if response.status_code == 200:
            dashboards = response.json()
            print(f"📊 Найдено {len(dashboards)} дашбордов в локальной Grafana")
            print()

            exported_count = 0
            for dashboard in dashboards:
                uid = dashboard['uid']
                title = dashboard['title']
                print(f"🔄 Экспорт дашборда: {title} (uid: {uid})")

                # Получаем JSON дашборда
                dash_response = requests.get(
                    f"{GRAFANA_URL}/api/dashboards/uid/{uid}",
                    auth=AUTH,
                    timeout=30
                )

                if dash_response.status_code == 200:
                    dash_data = dash_response.json()

                    # Очищаем и сохраняем
                    clean_dashboard = dash_data['dashboard']
                    clean_dashboard['id'] = None

                    # Формируем безопасное имя файла
                    safe_title = safe_filename(title)
                    filename = f"grafana-dashboards/local_{safe_title}.json"

                    with open(filename, 'w', encoding='utf-8') as f:
                        json.dump(clean_dashboard, f, indent=2, ensure_ascii=False)

                    print(f"   ✅ Сохранен: {filename}")
                    print()
                    exported_count += 1
                else:
                    print(f"   ❌ Ошибка {dash_response.status_code} при получении дашборда {title}")
                    print()

            print(f"📊 Итого экспортировано: {exported_count} дашбордов")
        else:
            print(f"❌ Ошибка {response.status_code} при получении списка дашбордов")
    else:
        print(f"❌ Ошибка подключения к Grafana: {test_response.status_code}")
        print("   Убедитесь, что Grafana запущена на http://localhost:3000")

except requests.exceptions.Timeout:
    print("⏱️ Таймаут при подключении к локальной Grafana")
except requests.exceptions.ConnectionError:
    print("🔌 Ошибка подключения к локальной Grafana")
    print("   Убедитесь, что Grafana запущена на http://localhost:3000")
except Exception as e:
    print(f"❌ Непредвиденная ошибка: {e}")

# ИТОГ
print("\n" + "=" * 60)
print("🎉 РАБОТА ЗАВЕРШЕНА")
print("=" * 60)
print(f"📁 Все файлы сохранены в папке: {os.path.abspath('grafana-dashboards')}")
print("\n📋 Список сохраненных файлов:")
print("-" * 50)

# Показываем список файлов
try:
    files = os.listdir("grafana-dashboards")
    if files:
        for i, file in enumerate(sorted(files), 1):
            if file.endswith('.json'):
                file_path = os.path.join("grafana-dashboards", file)
                file_size = os.path.getsize(file_path)
                print(f"{i:2}. {file:<50} ({file_size:>8} bytes)")
    else:
        print("   Папка пуста")
except Exception as e:
    print(f"   Не удалось прочитать список файлов: {e}")

print("\n💡 Чтобы импортировать дашборды в Grafana:")
print("   1. Откройте Grafana (http://localhost:3000)")
print("   2. Нажмите '+' → 'Import'")
print("   3. Загрузите JSON файл или введите ID дашборда")
print("   4. Выберите источник данных 'Prometheus'")
print("   5. Нажмите 'Import'")
print("\n" + "=" * 60)