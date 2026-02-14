# 🚀 Delivery Microservices Platform

Микросервисная платформа для управления доставкой заказов.

## 📋 Архитектура

### Микросервисы:
- **session-service** (порт 8081) — управление сменами курьеров
- **order-service** (порт 8082) — обработка заказов
- **payout-service** (порт 8083) — выплаты

### Инфраструктура:
- **PostgreSQL** ×3 — базы данных
- **Redis** — кэширование и идемпотентность
- **Kafka** + **Zookeeper** — событийная шина
- **Prometheus** + **Grafana** — мониторинг
- **ELK** (Elasticsearch, Logstash, Kibana) — логирование
- **Jaeger** — распределённая трассировка

## 🛠 Технологический стек

- **Java 17**, **Spring Boot 3.1.5**
- **Spring Data JPA**, **Hibernate**
- **PostgreSQL**, **Liquibase**
- **Redis**, **Apache Kafka**
- **Docker**, **Docker Compose**
- **Prometheus**, **Grafana**, **ELK**, **Jaeger**
- **JUnit 5**, **Mockito**, **Testcontainers**

## 🚀 Запуск проекта

```bash
# Собрать проекты
mvn clean package -DskipTests

# Запустить все сервисы
docker-compose up -d

# Проверить статус
docker-compose ps

📊 Мониторинг

Grafana: http://localhost:3000 (admin/admin)
Prometheus: http://localhost:9090
Kibana: http://localhost:5601
Jaeger: http://localhost:16686

📈 Метрики
Все микросервисы предоставляют метрики в Prometheus:

/actuator/prometheus — JVM метрики
/actuator/health — health checks

---

## ✅ **Проверка результата**

1. Откройте браузер: `https://github.com/ВАШ_ЛОГИН/delivery-microservices`
2. Убедитесь, что все файлы загружены
3. Проверьте, что `.gitignore` сработал (нет лишних файлов)

## 🎉 **Готово! Проект на GitHub!**