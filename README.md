# 🚀 Delivery Microservices Platform

Микросервисная платформа для управления доставкой заказов. Проект демонстрирует полный цикл разработки high-load системы
с использованием современного технологического стека.

## 📋 Содержание

- [Архитектура](#-архитектура)
- [Технологический стек](#-технологический-стек)
- [Запуск проекта](#-запуск-проекта)
- [Мониторинг](#-мониторинг)
- [Дашборды Grafana](#-дашборды-grafana)
- [Метрики](#-метрики)
- [Тестирование](#-тестирование)
- [Работа с Git](#-работа-с-git)

---

## 📋 Архитектура

### Микросервисы

| Сервис              | Порт | Описание                    |
|---------------------|------|-----------------------------|
| **session-service** | 8081 | Управление сменами курьеров |
| **order-service**   | 8082 | Обработка заказов           |
| **payout-service**  | 8083 | Выплаты курьерам            |

### Инфраструктура

| Компонент                                 | Назначение                           |
|-------------------------------------------|--------------------------------------|
| **PostgreSQL** ×3                         | Базы данных для каждого микросервиса |
| **Redis**                                 | Кэширование и идемпотентность        |
| **Kafka** + **Zookeeper**                 | Событийная шина                      |
| **Prometheus**                            | Сбор метрик                          |
| **Grafana**                               | Визуализация метрик                  |
| **ELK** (Elasticsearch, Logstash, Kibana) | Централизованное логирование         |
| **Jaeger**                                | Распределённая трассировка           |

---

## 🛠 Технологический стек

- **Java 17**, **Spring Boot 3.1.5**
- **Spring Data JPA**, **Hibernate**
- **PostgreSQL**, **Liquibase**
- **Redis**, **Apache Kafka**
- **Docker**, **Docker Compose**
- **Prometheus**, **Grafana**, **ELK**, **Jaeger**
- **JUnit 5**, **Mockito**, **Testcontainers**

---

## 🚀 Запуск проекта

### 1. Сборка проектов

# Полная сборка всех модулей

mvn clean package -DskipTests

# Сборка конкретного модуля

mvn clean package -pl session-service -am -DskipTests
mvn clean package -pl order-service -am -DskipTests
mvn clean package -pl payout-service -am -DskipTests

# Запустить все сервисы

docker-compose up -d

# Проверить статус

docker-compose ps

Просмотр логов
docker-compose logs -f [service-name]

📊 Мониторинг

Prometheus: http://localhost:9090 Сбор метрик
Grafana: http://localhost:3000 Визуализация метрик
Kibana: http://localhost:5601 Визуализация логов
Jaeger: http://localhost:16686 Трассировка запросов
Kafka Exporter:    http://localhost:8080/metrics Метрики Kafka

📈 Метрики

Все микросервисы предоставляют метрики в Prometheus:
/actuator/prometheus — JVM метрики
/actuator/health — health checks

---

## 📊 Дашборды Grafana

📥 Быстрый импорт

Способ 1: Через ID
Grafana → + → Import

Введите ID дашборда:

Дашборд ID Описание
Spring Boot 3.x 12900 Метрики JVM, HTTP, Tomcat
JVM Micrometer 4701 Детальная статистика JVM
Kafka Exporter 7589 Метрики Kafka
PostgreSQL 9628 Метрики PostgreSQL
Redis 763 Метрики Redis
Node Exporter 1860 Системные метрики (CPU, RAM, Disk)
Docker Monitoring 893 Статистика контейнеров
Docker System 179 Расширенный мониторинг Docker
Выберите источник данных Prometheus

Нажмите Import

Способ 2: Через JSON файлы

# Все дашборды находятся в папке grafana-dashboards/

cd grafana-dashboards
ls *.json
Grafana → + → Import

Нажмите Upload JSON file
Выберите нужный файл
Выберите источник данных Prometheus
Нажмите Import

📂 Структура файлов дашбордов

grafana-dashboards/
├── spring-boot-3x-stats.json # Spring Boot 3.x
├── jvm-micrometer.json # JVM метрики
├── kafka-exporter.json # Kafka
├── postgresql-dashboard.json # PostgreSQL
├── redis-dashboard.json # Redis
├── node-exporter-full.json # Node Exporter
├── docker-monitoring.json # Docker контейнеры
└── docker-system-monitoring.json # Docker система

🔄 Обновление дашбордов

# Запустить скрипт экспорта

python export-dashboards.py

📈 Метрики

Все микросервисы предоставляют метрики через Actuator:

Эндпоинт Описание
/actuator/prometheus JVM метрики в формате Prometheus
/actuator/health Health checks
/actuator/info Информация о приложении

Примеры PromQL запросов

# Загрузка CPU

system_cpu_usage{application="session-service"}

# Использование памяти

jvm_memory_used_bytes{area="heap", application="order-service"}

# Количество HTTP запросов

http_server_requests_seconds_count{application="payout-service"}

# Активные потоки

jvm_threads_live_threads{application="session-service"}

🧪 Тестирование

# Запустить все тесты

mvn test

# Запустить только юнит-тесты

mvn test -Dtest=*Test

# Запустить только интеграционные тесты

mvn test -Dtest=*IntegrationTest

# Запустить конкретный тестовый класс

mvn test -Dtest=SessionServiceTest

🌿 Работа с Git

Структура веток

main (стабильная версия)
↑
develop (разработка)
↑
feature/* (новые фичи)

Основные команды

# Создать новую ветку

git checkout -b feature/название-фичи

# Отправить изменения

git add .
git commit -m "описание изменений"
git push origin feature/название-фичи

# Слить изменения в develop

git checkout develop
git pull origin develop
git merge feature/название-фичи
git push origin develop

Коммиты (Conventional Commits)

feat:     новый функционал
fix:      исправление бага
docs:     изменения в документации
style:    форматирование кода
refactor: рефакторинг без изменения функциональности
test:     добавление тестов
chore:    обновление зависимостей, настройки

Примеры:

git commit -m "feat: добавлен Redis кэш для SessionService"
git commit -m "fix: исправлен NPE в методе getSessionById"
git commit -m "test: добавлены интеграционные тесты для OrderService"
git commit -m "docs: обновлен README.md"

Полезные команды

# Посмотреть статус

git status

# Посмотреть историю

git log --oneline --graph

# Отменить изменения

git restore файл
git reset HEAD~1

# Работа с удалённым репозиторием

git remote -v
git fetch origin
git pull origin develop
git push origin develop

🐳 Docker Compose файлы

docker-compose.yml — основной файл со всеми сервисами
jmx-exporter-config.yml — конфигурация JMX Exporter для Kafka
prometheus.yml — конфигурация Prometheus
logstash.conf — конфигурация Logstash

Запуск всех сервисов через Docker Compose

# Запустить все контейнеры в фоне

docker-compose up -d

# Запустить с пересборкой образов

docker-compose up -d --build

# Запустить конкретный сервис

docker-compose up -d session-service

Проверка статуса

# Просмотр всех запущенных контейнеров

docker-compose ps

# Детальная информация о контейнерах

docker ps

# Ожидаемый результат: 17 контейнеров в статусе Up

Остановка и перезапуск

# Остановить все контейнеры

docker-compose down

# Остановить и удалить volumes (базы данных)

docker-compose down -v

# Перезапустить конкретный сервис

docker-compose restart session-service

# Перезапустить все сервисы

docker-compose restart

docker-compose.yml структура:

# Микросервисы

session-service: 8081
order-service:   8082
payout-service:  8083

# Базы данных

postgres-session: 5432
postgres-order:   5433
postgres-payout:  5434

# Инфраструктура

redis:         6379
zookeeper:     2181
kafka:         9092, 9999
kafka-exporter: 8080

# Мониторинг

prometheus:     9090
grafana:        3000
elasticsearch:  9200
logstash:       5000
kibana:         5601
jaeger:         16686

Prometheus эндпоинты

Все микросервисы предоставляют метрики через Actuator:

Сервис URL для метрик
session-service    http://localhost:8081/actuator/prometheus
order-service    http://localhost:8082/actuator/prometheus
payout-service    http://localhost:8083/actuator/prometheus

✅ Проверка работоспособности

# Проверить все микросервисы

curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health

Просмотр логов

# Логи всех сервисов

docker-compose logs -f

# Логи конкретного сервиса

docker-compose logs -f session-service
docker-compose logs -f order-service
docker-compose logs -f payout-service
docker-compose logs -f kafka
docker-compose logs -f prometheus

# Последние N строк лога

docker-compose logs --tail 100 session-service

# Проверить Kafka

docker exec -it delivery-microservices-kafka-1 \
kafka-topics --bootstrap-server localhost:9092 --list

# Проверить Redis

docker exec -it delivery-microservices-redis-1 redis-cli ping

# Проверить PostgreSQL

docker exec -it delivery-microservices-postgres-session-1 \
psql -U user -d sessiondb -c "SELECT * FROM sessions;"

📁 Структура проекта

delivery-microservices/
├── session-service/ # Микросервис управления сменами
├── order-service/ # Микросервис обработки заказов
├── payout-service/ # Микросервис выплат
├── grafana-dashboards/ # JSON дашборды для Grafana
├── docker-compose.yml # Оркестрация всех сервисов
├── prometheus.yml # Конфигурация Prometheus
├── logstash.conf # Конфигурация Logstash
├── jmx-exporter-config.yml # JMX Exporter для Kafka
├── export-dashboards.py # Скрипт для экспорта дашбордов
└── pom.xml # Родительский Maven проект

Примеры PromQL запросов

# Загрузка CPU

system_cpu_usage{application="session-service"}

# Использование памяти (heap)

jvm_memory_used_bytes{area="heap", application="order-service"}

# Количество HTTP запросов

http_server_requests_seconds_count{application="payout-service"}

# Активные потоки

jvm_threads_live_threads{application="session-service"}

# Время ответа (p95)

histogram_quantile(0.95,
sum(rate(http_server_requests_seconds_bucket[5m])) by (le, application))

# Количество созданных смен (кастомная метрика)

sessions_created_total{application="session-service"}

# Kafka consumer lag

kafka_consumer_fetch_manager_records_lag{}

# Использование Redis

redis_memory_used_bytes{}

🧪 Тестирование

Запуск тестов

# Запустить все тесты во всех модулях

mvn test

# Запустить только юнит-тесты (быстрые)

mvn test -Dtest=*Test

# Запустить только интеграционные тесты (медленные)

mvn test -Dtest=*IntegrationTest

# Запустить конкретный тестовый класс

mvn test -Dtest=SessionServiceTest
mvn test -Dtest=OrderServiceIntegrationTest

# Запустить с детальным выводом

mvn test -X

# Пропустить тесты при сборке

mvn clean package -DskipTests

Виды тестов

Тип теста Назначение Пример класса
Unit тесты Тестирование бизнес-логики изолированно SessionServiceTest
Integration тесты Тестирование с реальными компонентами SessionServiceIntegrationTest
Controller тесты Тестирование REST API SessionControllerTest
Repository тесты Тестирование запросов к БД SessionRepositoryTest

Используемые библиотеки

JUnit 5 — основа тестирования
Mockito — мокирование зависимостей
AssertJ — читаемые assertions
Testcontainers — реальные БД в тестах

✅ Проверка работоспособности

1. Проверка микросервисов

# Session-service

curl http://localhost:8081/actuator/health

# Ожидаемый результат: {"status":"UP"}

# Order-service

curl http://localhost:8082/actuator/health

# Payout-service

curl http://localhost:8083/actuator/health

2. Проверка базы данных

# PostgreSQL session

docker exec -it delivery-microservices-postgres-session-1 \
psql -U user -d sessiondb -c "SELECT * FROM sessions;"

# PostgreSQL order

docker exec -it delivery-microservices-postgres-order-1 \
psql -U user -d orderdb -c "\dt"

# PostgreSQL payout

docker exec -it delivery-microservices-postgres-payout-1 \
psql -U user -d payoutdb -c "\dt"

3. Проверка Redis

# Пинг Redis

docker exec -it delivery-microservices-redis-1 redis-cli ping

# Должен вернуть: PONG

# Посмотреть ключи

docker exec -it delivery-microservices-redis-1 redis-cli KEYS "*"

# Получить значение

docker exec -it delivery-microservices-redis-1 redis-cli GET "sessions::1"

4. Проверка Kafka

# Список топиков

docker exec -it delivery-microservices-kafka-1 \
kafka-topics --bootstrap-server localhost:9092 --list

# Ожидаемый результат: __consumer_offsets, order-events

# Описание топика

docker exec -it delivery-microservices-kafka-1 \
kafka-topics --bootstrap-server localhost:9092 --describe --topic order-events

5. Проверка метрик Kafka

# Kafka Exporter метрики

curl http://localhost:8080/metrics | grep kafka

# Prometheus targets

curl http://localhost:9090/api/v1/targets | jq .

6. Проверка Elasticsearch

   curl http://localhost:9200

# Должен вернуть JSON с информацией о кластере

7. Проверка API микросервисов

# Создать смену

curl -X POST http://localhost:8081/api/sessions/start \
-H "Content-Type: application/json" \
-d '{"courierId":"test-courier"}'

# Проверить активность курьера

curl http://localhost:8081/api/sessions/active/test-courier

# Получить смену по ID

curl http://localhost:8081/api/sessions/1

docker-compose.yml структура

# Микросервисы

session-service: 8081
order-service:   8082
payout-service:  8083

# Базы данных

postgres-session: 5432
postgres-order:   5433
postgres-payout:  5434

# Инфраструктура

redis:         6379
zookeeper:     2181
kafka:         9092, 9999
kafka-exporter: 8080

# Мониторинг

prometheus:     9090
grafana:        3000
elasticsearch:  9200
logstash:       5000
kibana:         5601
jaeger:         16686

prometheus.yml конфигурация

global:
scrape_interval: 15s

scrape_configs:

- job_name: 'session-service'
  metrics_path: '/actuator/prometheus'
  static_configs:
    - targets: ['session-service:8081']

- job_name: 'order-service'
  metrics_path: '/actuator/prometheus'
  static_configs:
    - targets: ['order-service:8082']

- job_name: 'payout-service'
  metrics_path: '/actuator/prometheus'
  static_configs:
    - targets: ['payout-service:8083']

- job_name: 'kafka'
  static_configs:
    - targets: ['kafka-exporter:8080']

logstash.conf конфигурация

input {
tcp {
port => 5000
codec => json
}
}

output {
elasticsearch {
hosts => ["elasticsearch:9200"]
index => "delivery-logs-%{+YYYY.MM.dd}"}}

jmx-exporter-config.yml

hostPort: kafka:9999
startDelaySeconds: 0
ssl: false
lowercaseOutputName: true
lowercaseOutputLabelNames: true
rules:

- pattern: "kafka.consumer<type=(.+), name=(.+)><>Value"
  name: kafka_consumer_$1_$2
  type: GAUGE
- pattern: "kafka.producer<type=(.+), name=(.+)><>Value"
  name: kafka_producer_$1_$2
  type: GAUGE
- pattern: "kafka.server<type=(.+), name=(.+)><>Value"
  name: kafka_server_$1_$2
  type: GAUGE

🚀 Быстрый старт (шпаргалка)

# 1. Клонировать репозиторий

git clone https://github.com/Tomas-Mart/delivery-microservices.git
cd delivery-microservices

# 2. Собрать проекты

mvn clean package -DskipTests

# 3. Запустить все сервисы

docker-compose up -d

# 4. Проверить статус

docker-compose ps

# 5. Открыть Grafana

start http://localhost:3000

# Логин: admin, пароль: admin

# 6. Импортировать дашборды (ID: 12900, 4701, 7589, 9628)

# 7. Проверить микросервисы

curl http://localhost:8081/actuator/health

📝 Полезные ссылки

GitHub репозиторий: https://github.com/Tomas-Mart/delivery-microservices

Grafana дашборды: https://grafana.com/grafana/dashboards

Prometheus документация: https://prometheus.io/docs/

Spring Boot Actuator: https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html

Testcontainers: https://www.testcontainers.org/

🎉 Благодарности

Проект создан для демонстрации навыков разработки high-load микросервисных систем с полным observability стеком (
мониторинг, логирование, трассировка).
Особая благодарность команде за поддержку и code review.

📄 Лицензия
MIT

🚀 Приятной работы с проектом! 🎉

📁 Структура проекта

delivery-microservices/
├── session-service/ # Микросервис управления сменами
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/org/example/
│ │ │ │ ├── SessionServiceApplication.java
│ │ │ │ ├── config/
│ │ │ │ │ └── RedisConfig.java
│ │ │ │ ├── exception/
│ │ │ │ │ ├── CourierAlreadyHasActiveSessionException.java
│ │ │ │ │ ├── SessionNotFoundException.java
│ │ │ │ │ └── advice/
│ │ │ │ │ ├── ErrorResponse.java
│ │ │ │ │ └── GlobalExceptionHandler.java
│ │ │ │ ├── model/
│ │ │ │ │ ├── Session.java
│ │ │ │ │ └── SessionStatus.java
│ │ │ │ ├── repository/
│ │ │ │ │ └── SessionRepository.java
│ │ │ │ ├── rest/
│ │ │ │ │ ├── SessionController.java
│ │ │ │ │ └── dto/
│ │ │ │ │ ├── SessionDto.java
│ │ │ │ │ └── StartSessionRequest.java
│ │ │ │ └── service/
│ │ │ │ ├── SessionService.java
│ │ │ │ └── impl/
│ │ │ │ └── SessionServiceImpl.java
│ │ │ └── resources/
│ │ │ ├── application.yml
│ │ │ └── db/changelog/
│ │ │ ├── db.changelog-master.xml
│ │ │ └── changes/
│ │ │ └── V1__create_session_table.sql
│ │ └── test/
│ │ └── java/org/example/
│ │ ├── repository/
│ │ │ └── SessionRepositoryTest.java
│ │ ├── rest/
│ │ │ └── SessionControllerTest.java
│ │ └── service/
│ │ ├── SessionServiceIntegrationTest.java
│ │ └── SessionServiceTest.java
│ ├── Dockerfile
│ └── pom.xml
│
├── order-service/ # Микросервис обработки заказов
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/org/example/
│ │ │ │ ├── OrderServiceApplication.java
│ │ │ │ ├── dto/
│ │ │ │ │ └── OrderDto.java
│ │ │ │ ├── event/
│ │ │ │ │ └── OrderDeliveredEvent.java
│ │ │ │ ├── feign/
│ │ │ │ │ └── SessionServiceClient.java
│ │ │ │ ├── model/
│ │ │ │ │ ├── Order.java
│ │ │ │ │ └── OrderStatus.java
│ │ │ │ ├── repository/
│ │ │ │ │ └── OrderRepository.java
│ │ │ │ ├── rest/
│ │ │ │ │ └── OrderController.java
│ │ │ │ └── service/
│ │ │ │ ├── OrderService.java
│ │ │ │ └── impl/
│ │ │ │ └── OrderServiceImpl.java
│ │ │ └── resources/
│ │ │ ├── application.yml
│ │ │ └── db/changelog/
│ │ │ ├── db.changelog-master.xml
│ │ │ └── changes/
│ │ │ └── V1__create_order_tables.sql
│ │ └── test/
│ │ └── java/org/example/service/
│ │ └── OrderServiceIntegrationTest.java
│ ├── Dockerfile
│ └── pom.xml
│
├── payout-service/ # Микросервис выплат
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/org/example/
│ │ │ │ ├── PayoutServiceApplication.java
│ │ │ │ ├── consumer/
│ │ │ │ │ ├── OrderEventConsumer.java
│ │ │ │ │ └── impl/
│ │ │ │ │ └── OrderEventConsumerImpl.java
│ │ │ │ ├── event/
│ │ │ │ │ └── OrderDeliveredEvent.java
│ │ │ │ ├── model/
│ │ │ │ │ ├── PendingPayout.java
│ │ │ │ │ └── PayoutStatus.java
│ │ │ │ ├── repository/
│ │ │ │ │ └── PayoutRepository.java
│ │ │ │ └── service/
│ │ │ │ ├── PayoutService.java
│ │ │ │ └── impl/
│ │ │ │ └── PayoutServiceImpl.java
│ │ │ └── resources/
│ │ │ ├── application.yml
│ │ │ └── db/changelog/
│ │ │ ├── db.changelog-master.xml
│ │ │ └── changes/
│ │ │ ├── V1__create_payout_tables.sql
│ │ │ └── V2__add_status_to_payouts.sql
│ │ └── test/
│ │ └── java/org/example/
│ │ └── service/
│ │ └── PayoutServiceTest.java
│ ├── Dockerfile
│ └── pom.xml
│
├── grafana-dashboards/ # JSON дашборды для Grafana
│ ├── docker-monitoring.json
│ ├── docker-system-monitoring.json
│ ├── jvm-micrometer.json
│ ├── kafka-exporter.json
│ ├── node-exporter-full.json
│ ├── postgresql-dashboard.json
│ ├── redis-dashboard.json
│ ├── spring-boot-3x-stats.json
│ └── README.md
│
├── docker-compose.yml # Оркестрация всех сервисов
├── prometheus.yml # Конфигурация Prometheus
├── logstash.conf # Конфигурация Logstash
├── jmx-exporter-config.yml # JMX Exporter для Kafka
├── export-dashboards.py # Скрипт для экспорта дашбордов
├── pom.xml # Родительский Maven проект
├── .gitignore # Игнорируемые файлы Git
├── .gitlab-ci.yml # CI/CD пайплайн
└── README.md # Документация проекта (этот файл)