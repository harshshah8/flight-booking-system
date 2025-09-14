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

### 1. Start All Services
```bash
cd infra
docker-compose up --build -d
```

### 2. Verify Services Health
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

# PostgreSQL
docker exec flight-postgres-db pg_isready -U flight_user -d flight_inventory_db
```

### 3. Seed Initial Data
```bash
cd ../data-seeding
python seed_flights.py
```

## API Examples

### Search Flights
```bash
curl -X GET "http://localhost:8081/v1/flights/search?origin=NYC&destination=LAX&departureDate=2024-12-01&passengers=2"
```

### Get Flight Details
```bash
curl -X GET "http://localhost:8082/v1/flights/{flightId}"
```

### Create Booking
```bash
curl -X POST "http://localhost:8083/v1/bookings" \
-H "Content-Type: application/json" \
-d '{
  "flightId": "flight-uuid-here",
  "customerEmail": "test@example.com",
  "numberOfSeats": 2,
  "passengerDetails": [
    {
      "firstName": "John",
      "lastName": "Doe",
      "email": "john@example.com",
      "phoneNumber": "+1234567890"
    }
  ]
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

### Fresh Start
```bash
docker-compose up --build -d
# Wait for services to be healthy, then seed data
cd ../data-seeding
python seed_flights.py
```

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
# Connect to PostgreSQL
docker exec -it flight-postgres-db psql -U flight_user -d flight_inventory_db

# Check tables
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
- `GET /v1/flights/search` - Search flights

### Inventory Service (Port 8082)
- `GET /health` - Health check
- `GET /v1/flights/{flightId}` - Get flight details
- `POST /v1/flights/{flightId}/reserve-seats` - Reserve seats (internal)
- `POST /v1/flights/{flightId}/release-seats` - Release seats (internal)

### Booking Service (Port 8083)
- `GET /health` - Health check
- `POST /v1/bookings` - Create booking
- `GET /v1/bookings/{bookingId}` - Get booking by ID
- `GET /v1/bookings` - Get bookings by customer email

### Payment Service (Port 8084)
- `GET /health` - Health check
- `POST /v1/payments/process` - Process payment