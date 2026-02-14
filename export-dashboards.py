import requests
import json
import os

# Настройки Grafana
GRAFANA_URL = "http://localhost:3000"
AUTH = ("admin", "admin")  # логин/пароль

# ID дашбордов, которые хотим сохранить
DASHBOARD_IDS = {
    "12900": "spring-boot-12900",
    "4701": "jvm-4701",
    "7589": "kafka-7589",
    "9628": "postgres-9628",
    "1860": "node-exporter-1860"
}

# Создаем папку для сохранения
os.makedirs("grafana-dashboards", exist_ok=True)

# Получаем список дашбордов
response = requests.get(
    f"{GRAFANA_URL}/api/search",
    auth=AUTH
)

if response.status_code == 200:
    dashboards = response.json()

    for dashboard in dashboards:
        uid = dashboard['uid']
        title = dashboard['title']

        # Получаем JSON дашборда
        dash_response = requests.get(
            f"{GRAFANA_URL}/api/dashboards/uid/{uid}",
            auth=AUTH
        )

        if dash_response.status_code == 200:
            dash_data = dash_response.json()

            # Очищаем и сохраняем
            clean_dashboard = dash_data['dashboard']
            clean_dashboard['id'] = None

            # Сохраняем в файл
            filename = f"grafana-dashboards/{title.replace(' ', '_')}.json"
            with open(filename, 'w', encoding='utf-8') as f:
                json.dump(clean_dashboard, f, indent=2, ensure_ascii=False)

            print(f"✅ Сохранен: {filename}")
else:
    print("❌ Ошибка подключения к Grafana")