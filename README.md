# morder26 - Event-Driven System

A simple order reservation application with event-driven architecture built with **Spring Boot 3**, **Apache Kafka**, **MySQL 8**, and **Docker Compose**.

## Services

| Service           | Port | Description                                 |
|-------------------|------|---------------------------------------------|
| order-service     | 8080 | Exposes REST API, publishes Kafka events    |
| item-service      | 8081 | Consumes Kafka events, manages stock in DB  |
| kafka             | 9092 | Message broker                              |
| mysql             | 3306 | Persistent item storage                     |

---

## Quick Start

### 1 Start

```bash
docker compose up --build
```

### 2 Send an order

```bash
curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -H "X-API-KEY: 38f1b725-fa63-484d-a904-0551321ae2f5" -d "{\"orderId\":\"123\",\"itemId\":\"item-1\",\"quantity\":2}"
```

**Expected response (202 Accepted):**
```json
{
  "status": "ACCEPTED",
  "orderId": "123",
  "message": "Order received and event published to Kafka"
}
```

### 3 Check logs and database

You should see lines like:
```
[OrderService] Message published successfully to topic=orders.topic
[CONSUMER] Received event: OrderMessage(orderId=123, itemId=item-1, quantity=2, createdAt=2026-05-21T22:41:57.861365064Z)
[ItemService] Reserved 2 unit(s) of 'item-1'
[CONSUMER] Reservation result: status=RESERVED message=Reserved 2 unit(s) of 'item-1'
```

More test cases can be found in the Postman collection: [morder26.postman_collection.json](morder26.postman_collection.json)

## 4 Stopping

```bash
docker compose down -v       # stop + delete volumes (resets DB)
```
