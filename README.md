# flight-booking-system

## Tech Stack
- **Backend**: Java 21 with Spring Boot
- **API Protocol**: REST
- **Containerization**: Docker
- **Cache**: Redis
- **Database**: PostgreSQL
- **Data Seeding**: Python scripts

## Service Architecture
- **Search Service**: Port 8081
- **Inventory Service**: Port 8082
- **Booking Service**: Port 8083
- **Payment Service**: Port 8084
- **PostgreSQL**: Port 5432
- **Redis**: Port 6379

## Quick Start

### 1. Start Database Services First
```bash
cd infra
docker-compose up postgres-db redis-cache -d
```

### 2. Wait for PostgreSQL to be Ready
```bash
# Wait for both databases to be healthy
docker exec flight-postgres-db pg_isready -U flight_user -d flight_inventory_db
docker exec flight-postgres-db pg_isready -U flight_user -d flight_booking_db
```

### 3. Seed Initial Data
```bash
cd ../data-seeding
python seed_flights.py
```

### 4. Verify Data was Seeded
```bash
docker exec flight-postgres-db psql -U flight_user -d flight_inventory_db -c "SELECT COUNT(*) FROM flights;"
```

### 5. Start All Services
```bash
cd ../infra
docker-compose up --build -d
```

### 6. Verify All Services Health
```bash
# Search Service
curl http://localhost:8081/health

# Inventory Service
curl http://localhost:8082/health

# Booking Service
curl http://localhost:8083/health

# Payment Service
curl http://localhost:8084/health

# Redis
docker exec flight-redis-cache redis-cli ping
```

## API Examples

### Search Flights
```bash
curl -X GET "http://localhost:8081/v1/search?source=NYC&destination=LAX&date=2024-12-01&criteria=CHEAPEST"
```

### Get Flight Details
```bash
curl -X GET "http://localhost:8082/v1/flights/{flightId}"
```

### Get All Flights
```bash
curl -X GET "http://localhost:8082/v1/flights/all"
```

### Get Flights by Route
```bash
curl -X GET "http://localhost:8082/v1/flights/route?source=NYC&destination=LAX"
```

### Create Booking
```bash
curl -X POST "http://localhost:8083/v1/bookings" \
-H "Content-Type: application/json" \
-d '{
  "flightId": "flight-uuid-here",
  "customerEmail": "test@example.com",
  "numberOfSeats": 2
}'
```

### Get Booking by ID
```bash
curl -X GET "http://localhost:8083/v1/bookings/{bookingId}"
```

### Get Bookings by Email
```bash
curl -X GET "http://localhost:8083/v1/bookings?customerEmail=test@example.com"
```

### Process Payment
```bash
curl -X POST "http://localhost:8084/v1/payments/process" \
-H "Content-Type: application/json" \
-d '{
  "bookingId": "booking-uuid-here",
  "amount": 299.99,
  "currency": "USD",
  "paymentMethod": "CREDIT_CARD"
}'
```

## Clean Restart

### Stop and Remove All Containers
```bash
cd infra
docker-compose down -v
```

### Remove All Data
```bash
docker volume rm infra_postgres_data infra_redis_data
```

### Then Follow Quick Start Steps
After cleanup, follow the **Quick Start** section above for proper startup sequence.

## Troubleshooting

### Check Container Logs
```bash
# View all services
docker-compose logs

# View specific service
docker-compose logs booking-service
docker-compose logs inventory-service
docker-compose logs search-service
docker-compose logs payment-service
```

### Database Connection Issues
```bash
# Connect to PostgreSQL - Inventory Database
docker exec -it flight-postgres-db psql -U flight_user -d flight_inventory_db

# Connect to PostgreSQL - Booking Database
docker exec -it flight-postgres-db psql -U flight_user -d flight_booking_db

# Check tables (after connecting to desired database)
\dt
```

### Redis Connection Issues
```bash
# Connect to Redis
docker exec -it flight-redis-cache redis-cli

# Check cache
keys *
```

### Service Not Ready
If services show as unhealthy, wait longer for startup. Booking service depends on inventory service, which depends on PostgreSQL.

### Port Conflicts
If ports are already in use, modify the port mappings in docker-compose.yml:
```yaml
ports:
  - "NEW_PORT:CONTAINER_PORT"
```

## Complete API Reference

### Search Service (Port 8081)
- `GET /health` - Health check
- `GET /v1/search` - Search flights (params: source, destination, date, criteria)

### Inventory Service (Port 8082)
- `GET /health` - Health check
- `GET /v1/flights/all` - Get all flights
- `GET /v1/flights/{flightId}` - Get flight details
- `GET /v1/flights/route` - Get flights by route (params: source, destination)
- `GET /v1/flights/source/{source}` - Get flights by source
- `POST /v1/flights/{flightId}/reserve-seats` - Reserve seats (param: numberOfSeats)
- `POST /v1/flights/{flightId}/release-seats` - Release seats (param: numberOfSeats)
- `POST /admin/flights` - Add new flight (Admin API)
- `PUT /admin/flights/{flightId}/cancel` - Cancel flight (Admin API, publishes event)

### Booking Service (Port 8083)
- `GET /health` - Health check
- `POST /v1/bookings` - Create booking
- `GET /v1/bookings/{bookingId}` - Get booking by ID
- `GET /v1/bookings` - Get bookings by customer email

### Payment Service (Port 8084)
- `GET /health` - Health check
- `POST /v1/payments/process` - Process payment