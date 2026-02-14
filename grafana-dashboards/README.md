# 📊 Дашборды Grafana для Delivery Microservices

## 🚀 Быстрый импорт

### Способ 1: Через ID (рекомендуется)

1. Grafana → + → Import
2. Введите ID дашборда:
    - Spring Boot 3.x: `12900`
    - JVM Micrometer: `4701`
    - Kafka Exporter: `7589`
    - PostgreSQL: `9628`
    - Node Exporter: `1860`
3. Выберите источник данных `Prometheus`

### Способ 2: Через JSON файлы

1. Grafana → + → Import
2. Нажмите `Upload JSON file`
3. Выберите нужный файл из этой папки
4. Выберите источник данных `Prometheus`

## 📂 Структура файлов

- `spring-boot-12900.json` - метрики Spring Boot
- `jvm-4701.json` - JVM статистика
- `kafka-7589.json` - метрики Kafka
- `postgres-9628.json` - метрики PostgreSQL
- `node-exporter-1860.json` - системные метрики

## 🔄 Обновление дашбордов

```bash
python ../export-dashboards.py

---

## 🔧 **АВТОМАТИЧЕСКИЙ ИМПОРТ ПРИ ЗАПУСКЕ**

### **Вариант 1: Через provisioning (профессионально)**

Создайте файл `grafana/provisioning/dashboards/dashboards.yaml`:

```yaml
apiVersion: 1

providers:
  - name: 'delivery-microservices'
    orgId: 1
    folder: 'Delivery Microservices'
    type: file
    disableDeletion: false
    editable: true
    updateIntervalSeconds: 10
    options:
      path: /etc/grafana/provisioning/dashboards/json