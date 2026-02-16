# 🚀 Delivery Microservices Platform

Микросервисная платформа для управления доставкой заказов с полным observability стеком (мониторинг, логирование, трассировка).

## 📋 Содержание
- [Архитектура](#-архитектура)
- [Технологический стек](#-технологический-стек)
- [Структура проекта](#-структура-проекта)
- [Запуск проекта](#-запуск-проекта)
- [Мониторинг](#-мониторинг)
- [Дашборды Grafana](#-дашборды-grafana)
- [Метрики](#-метрики)
- [Тестирование](#-тестирование)
- [API Endpoints](#-api-endpoints)
- [Работа с Git](#-работа-с-git)
- [Проверка работоспособности](#-проверка-работоспособности)
- [Шпаргалка](#-шпаргалка)

---

## 🏗 Архитектура

### Микросервисы
| Сервис | Порт | Описание |
|--------|------|----------|
| **session-service** | 8081 | Управление сменами курьеров |
| **order-service** | 8082 | Обработка заказов |
| **payout-service** | 8083 | Выплаты курьерам |

### Инфраструктура
| Компонент | Порт | Назначение |
|-----------|------|------------|
| **postgres-session** | 5432 | База данных смен |
| **postgres-order** | 5433 | База данных заказов |
| **postgres-payout** | 5434 | База данных выплат |
| **redis** | 6379 | Кэширование, идемпотентность |
| **zookeeper** | 2181 | Координация Kafka |
| **kafka** | 9092, 9999 | Событийная шина |
| **kafka-exporter** | 8080 | JMX метрики Kafka |
| **prometheus** | 9090 | Сбор метрик |
| **grafana** | 3000 | Визуализация |
| **elasticsearch** | 9200 | Хранение логов |
| **logstash** | 5000 | Сбор логов |
| **kibana** | 5601 | Визуализация логов |
| **jaeger** | 16686 | Трассировка |

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

## 📁 Структура проекта
```
delivery-microservices/
├── session-service/           # Микросервис управления сменами
├── order-service/             # Микросервис обработки заказов
├── payout-service/            # Микросервис выплат
├── shared/                     # Общий модуль (DTO, enums)
├── grafana-dashboards/        # JSON дашборды Grafana
├── grafana/                    # Конфигурация Grafana
├── docker-compose.yml          # Оркестрация всех сервисов
├── prometheus.yml              # Конфигурация Prometheus
├── logstash.conf               # Конфигурация Logstash
├── jmx-exporter-config.yml     # JMX Exporter для Kafka
├── export-dashboards.py        # Скрипт экспорта дашбордов
├── .gitignore                  # Игнорируемые файлы Git
├── .gitlab-ci.yml              # CI/CD пайплайн
└── pom.xml                     # Родительский Maven проект
```

---

## 🚀 Запуск проекта

### 1. Клонирование
```bash
git clone https://github.com/Tomas-Mart/delivery-microservices.git
cd delivery-microservices
```

### 2. Сборка Maven проектов
```bash
# Полная сборка всех модулей
mvn clean package -DskipTests

# Сборка конкретного модуля
mvn clean package -pl session-service -am -DskipTests
mvn clean package -pl order-service -am -DskipTests
mvn clean package -pl payout-service -am -DskipTests
```

### 3. Запуск Docker контейнеров
```bash
# Запустить все сервисы
docker-compose up -d

# Запустить с пересборкой образов
docker-compose up -d --build

# Запустить конкретный сервис
docker-compose up -d session-service
```

### 4. Проверка статуса
```bash
docker-compose ps
# Ожидаемый результат: 17 контейнеров в статусе Up

# Детальная информация
docker ps
```

### 5. Просмотр логов
```bash
# Логи всех сервисов
docker-compose logs -f

# Логи конкретного сервиса
docker-compose logs -f session-service
docker-compose logs -f order-service
docker-compose logs -f payout-service
docker-compose logs -f kafka
docker-compose logs -f prometheus

# Последние N строк
docker-compose logs --tail 100 session-service
```

### 6. Остановка и перезапуск
```bash
# Остановить все контейнеры
docker-compose down

# Остановить и удалить volumes (базы данных)
docker-compose down -v

# Перезапустить конкретный сервис
docker-compose restart session-service

# Перезапустить все сервисы
docker-compose restart
```

---

## 📊 Мониторинг

### Доступ к интерфейсам
| Сервис | URL | Логин/Пароль |
|--------|-----|--------------|
| Prometheus | http://localhost:9090 | - |
| Grafana | http://localhost:3000 | admin/admin |
| Kibana | http://localhost:5601 | - |
| Jaeger | http://localhost:16686 | - |
| Kafka Exporter | http://localhost:8080/metrics | - |

### Prometheus эндпоинты
| Сервис | URL метрик |
|--------|------------|
| session-service | http://localhost:8081/actuator/prometheus |
| order-service | http://localhost:8082/actuator/prometheus |
| payout-service | http://localhost:8083/actuator/prometheus |

### Конфигурационные файлы
- `prometheus.yml` - настройки Prometheus
- `logstash.conf` - настройки Logstash
- `jmx-exporter-config.yml` - JMX Exporter для Kafka

---

## 📈 Дашборды Grafana

### 📥 Быстрый импорт через ID
Grafana → + → Import → Введите ID:

| Дашборд | ID | Описание |
|---------|----|----------|
| Spring Boot 3.x | 12900 | Метрики JVM, HTTP, Tomcat |
| JVM Micrometer | 4701 | Детальная статистика JVM |
| Kafka Exporter | 7589 | Метрики Kafka |
| PostgreSQL | 9628 | Метрики PostgreSQL |
| Redis | 763 | Метрики Redis |
| Node Exporter | 1860 | Системные метрики |
| Docker Monitoring | 893 | Статистика контейнеров |
| Docker System | 179 | Расширенный мониторинг Docker |

### 📂 Импорт через JSON файлы
```bash
# Все дашборды в папке grafana-dashboards/
cd grafana-dashboards
ls *.json
```
Grafana → + → Import → Upload JSON file → Выбрать файл → Prometheus → Import

---

## 📈 Метрики и PromQL

### Примеры запросов
```promql
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

# Kafka consumer lag
kafka_consumer_fetch_manager_records_lag{}

# Использование Redis
redis_memory_used_bytes{}
```

---

## 🧪 Тестирование

### Запуск тестов
```bash
# Все тесты во всех модулях
mvn test

# Только юнит-тесты (быстрые)
mvn test -Dtest=*Test

# Только интеграционные тесты
mvn test -Dtest=*IntegrationTest

# Конкретный тестовый класс
mvn test -Dtest=SessionServiceTest
mvn test -Dtest=OrderServiceIntegrationTest

# С детальным выводом
mvn test -X

# Пропустить тесты при сборке
mvn clean package -DskipTests
```

### Виды тестов
| Тип теста | Назначение | Пример |
|-----------|------------|--------|
| Unit тесты | Бизнес-логика изолированно | `SessionServiceTest` |
| Integration тесты | С реальными компонентами | `SessionServiceIntegrationTest` |
| Controller тесты | REST API | `SessionControllerTest` |
| Repository тесты | Запросы к БД | `SessionRepositoryTest` |

### Библиотеки
- **JUnit 5** - основа тестирования
- **Mockito** - мокирование зависимостей
- **AssertJ** - читаемые assertions
- **Testcontainers** - реальные БД в тестах

---

## 🔌 API Endpoints

### Session Service (порт 8081)
```bash
# Создать смену
curl -X POST http://localhost:8081/api/sessions/start \
  -H "Content-Type: application/json" \
  -d '{"courierId":"test-courier"}'

# Проверить активность курьера
curl http://localhost:8081/api/sessions/active/test-courier

# Получить смену по ID
curl http://localhost:8081/api/sessions/1

# Завершить смену
curl -X POST http://localhost:8081/api/sessions/1/end
```

### Order Service (порт 8082)
```bash
# Создать заказ
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{"description":"Test order","amount":100.00}'

# Назначить курьера
curl -X PUT http://localhost:8082/api/orders/1/assign \
  -H "Content-Type: application/json" \
  -d '{"courierId":"test-courier"}'

# Отметить доставку
curl -X PUT http://localhost:8082/api/orders/1/deliver \
  -H "Content-Type: application/json" \
  -d '{"courierId":"test-courier"}'
```

### Payout Service (порт 8083)
```bash
# Получить выплаты курьера
curl http://localhost:8083/api/payouts/courier/test-courier

# Получить статус выплаты по заказу
curl http://localhost:8083/api/payouts/order/1

# Тестовое Kafka сообщение (из контейнера)
echo '{"orderId":1002,"courierId":"test-success"}' | \
  docker exec -i delivery-microservices-kafka-1 \
  kafka-console-producer --bootstrap-server localhost:9092 --topic order-events
```

---

## ✅ Проверка работоспособности

### Микросервисы
```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
# Ожидаемый результат: {"status":"UP"}
```

### Базы данных
```bash
# PostgreSQL session
docker exec -it delivery-microservices-postgres-session-1 \
  psql -U user -d sessiondb -c "SELECT * FROM sessions;"

# PostgreSQL order
docker exec -it delivery-microservices-postgres-order-1 \
  psql -U user -d orderdb -c "\dt"

# PostgreSQL payout
docker exec -it delivery-microservices-postgres-payout-1 \
  psql -U user -d payoutdb -c "SELECT * FROM pending_payouts;"
```

### Redis
```bash
# Пинг Redis
docker exec -it delivery-microservices-redis-1 redis-cli ping
# Должен вернуть: PONG

# Посмотреть ключи
docker exec -it delivery-microservices-redis-1 redis-cli KEYS "*"

# Получить значение
docker exec -it delivery-microservices-redis-1 redis-cli GET "sessions::1"
```

### Kafka
```bash
# Список топиков
docker exec -it delivery-microservices-kafka-1 \
  kafka-topics --bootstrap-server localhost:9092 --list
# Ожидаемый результат: __consumer_offsets, order-events

# Описание топика
docker exec -it delivery-microservices-kafka-1 \
  kafka-topics --bootstrap-server localhost:9092 --describe --topic order-events

# Отправить тестовое сообщение
echo '{"orderId":1001,"courierId":"test"}' | \
  docker exec -i delivery-microservices-kafka-1 \
  kafka-console-producer --bootstrap-server localhost:9092 --topic order-events
```

### Мониторинг
```bash
# Kafka Exporter метрики
curl http://localhost:8080/metrics | grep kafka

# Prometheus targets
curl http://localhost:9090/api/v1/targets | jq .

# Elasticsearch
curl http://localhost:9200
```

---

## 🌿 Работа с Git

### Структура веток
```
main (стабильная)
  ↑
develop (разработка)
  ↑
feature/* (новые фичи)
```

### Основные команды
```bash
# Создать новую ветку
git checkout -b feature/название-фичи

# Отправить изменения
git add .
git commit -m "описание изменений"
git push origin feature/название-фичи

# Слить в develop
git checkout develop
git pull origin develop
git merge feature/название-фичи
git push origin develop
```

### Conventional Commits
| Тип | Описание |
|-----|----------|
| `feat:` | новый функционал |
| `fix:` | исправление бага |
| `docs:` | изменения в документации |
| `style:` | форматирование кода |
| `refactor:` | рефакторинг |
| `test:` | добавление тестов |
| `chore:` | обновление зависимостей |

Примеры:
```bash
git commit -m "feat: добавлен Redis кэш для SessionService"
git commit -m "fix: исправлен NPE в методе getSessionById"
git commit -m "test: добавлены интеграционные тесты для OrderService"
git commit -m "docs: обновлен README.md"
```

### Полезные команды
```bash
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
```

### .gitignore
```gitignore
# Maven
target/
*.jar
*.war
*.ear
*.log

# IDE
.idea/
*.iml
.classpath
.project
.settings/
.vscode/

# Docker
.dockerignore

# Logs
logs/
*.log

# Temp files
*.tmp
*.temp
*~

# Application properties
application-local.yml
application-dev.yml
```

---

## 🚀 Шпаргалка (быстрый старт)

```bash
# 1. Клонировать
git clone https://github.com/Tomas-Mart/delivery-microservices.git
cd delivery-microservices

# 2. Собрать Maven проекты
mvn clean package -DskipTests

# 3. Запустить Docker
docker-compose up -d

# 4. Проверить статус
docker-compose ps

# 5. Открыть Grafana (http://localhost:3000, admin/admin)
# Импортировать дашборды (ID: 12900, 4701, 7589, 9628)

# 6. Проверить микросервисы
curl http://localhost:8081/actuator/health

# 7. Отправить тестовое сообщение в Kafka
echo '{"orderId":1001,"courierId":"test"}' | \
  docker exec -i delivery-microservices-kafka-1 \
  kafka-console-producer --bootstrap-server localhost:9092 --topic order-events
```

### Одна строка для полного перезапуска
```bash
cd /mnt/c/Users/Ami/IdeaProjects/delivery-microservices && \
mvn clean install -DskipTests && \
docker-compose down && \
docker-compose up -d --build && \
docker-compose logs -f
```

---

## 📝 Полезные ссылки
- [GitHub репозиторий](https://github.com/Tomas-Mart/delivery-microservices)
- [Grafana дашборды](https://grafana.com/grafana/dashboards)
- [Prometheus документация](https://prometheus.io/docs/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Testcontainers](https://www.testcontainers.org/)

---

## 🎉 Благодарности
Проект создан для демонстрации навыков разработки high-load микросервисных систем с полным observability стеком (мониторинг, логирование, трассировка). Особая благодарность команде за поддержку и code review.

---

## 📄 Лицензия
MIT

---

🚀 **Приятной работы с проектом!** 🎉