# Docker Commands Reference - Flight Booking System

## 🐳 Docker Compose Commands

### Start Services
```bash
# Start all services in background
docker-compose -f infra/docker-compose.yml up -d

# Start all services with logs
docker-compose -f infra/docker-compose.yml up

# Start specific services only
docker-compose -f infra/docker-compose.yml up -d redis postgres

# Rebuild and start (after code changes)
docker-compose -f infra/docker-compose.yml up -d --build
```

### Stop & Clean Services
```bash
# Stop all services
docker-compose -f infra/docker-compose.yml down

# Stop and remove volumes (⚠️ deletes data)
docker-compose -f infra/docker-compose.yml down -v

# Stop and remove images
docker-compose -f infra/docker-compose.yml down --rmi all

# Force recreate containers
docker-compose -f infra/docker-compose.yml up -d --force-recreate
```

### Monitor Services
```bash
# View running containers
docker-compose -f infra/docker-compose.yml ps

# View service logs
docker-compose -f infra/docker-compose.yml logs

# Follow logs for specific service
docker-compose -f infra/docker-compose.yml logs -f redis
docker-compose -f infra/docker-compose.yml logs -f postgres

# View resource usage
docker stats
```

---

## 📊 Redis Commands

### Connect to Redis
```bash
# Connect to Redis CLI
docker exec -it flight-booking-system-redis-1 redis-cli

# Connect with password (if configured)
docker exec -it flight-booking-system-redis-1 redis-cli -a yourpassword
```

### Redis Operations
```bash
# Inside Redis CLI:

# View all keys
KEYS *

# View search cache keys
KEYS *:*:*:CHEAPEST
KEYS *:*:*:FASTEST

# Get specific cache entry
GET "AMD:BLR:2024-01-15:CHEAPEST"

# View cache statistics
INFO memory
INFO keyspace

# Flush all cache (⚠️ clears all data)
FLUSHALL

# Flush specific database
FLUSHDB

# Check Redis connection
PING

# Exit Redis CLI
exit
```

### Redis Monitoring
```bash
# Monitor Redis commands in real-time
docker exec -it flight-booking-system-redis-1 redis-cli MONITOR

# Get Redis configuration
docker exec -it flight-booking-system-redis-1 redis-cli CONFIG GET "*"

# Check memory usage
docker exec -it flight-booking-system-redis-1 redis-cli INFO memory
```

---

## 🐘 PostgreSQL Commands

### Connect to PostgreSQL
```bash
# Connect as postgres user
docker exec -it flight-booking-system-postgres-1 psql -U postgres

# Connect to specific database
docker exec -it flight-booking-system-postgres-1 psql -U postgres -d flightdb
```

### PostgreSQL Operations
```sql
-- Inside PostgreSQL CLI:

-- List databases
\l

-- Connect to database
\c flightdb

-- List tables
\dt

-- View table structure
\d flights

-- Sample queries
SELECT COUNT(*) FROM flights;
SELECT source, destination, COUNT(*) FROM flights GROUP BY source, destination;

-- View running queries
SELECT * FROM pg_stat_activity;

-- Exit PostgreSQL CLI
\q
```

### Database Management
```bash
# Create database backup
docker exec flight-booking-system-postgres-1 pg_dump -U postgres flightdb > backup.sql

# Restore database
docker exec -i flight-booking-system-postgres-1 psql -U postgres flightdb < backup.sql

# Check PostgreSQL logs
docker logs flight-booking-system-postgres-1
```

---

## 🔧 Application Services

### Build & Run Services
```bash
# Build all services
./mvnw clean package -DskipTests

# Run individual services (after building)
java -jar inventory-service/target/inventory-service-0.0.1-SNAPSHOT.jar
java -jar search-service/target/search-service-0.0.1-SNAPSHOT.jar
java -jar booking-service/target/booking-service-0.0.1-SNAPSHOT.jar
java -jar payment-service/target/payment-service-0.0.1-SNAPSHOT.jar

# Run with specific profile
java -jar -Dspring.profiles.active=dev search-service/target/search-service-0.0.1-SNAPSHOT.jar
```

### Health Check URLs
```bash
# Check service health
curl http://localhost:8081/v1/health  # Search Service
curl http://localhost:8082/v1/health  # Inventory Service
curl http://localhost:8083/v1/health  # Booking Service
curl http://localhost:8084/v1/health  # Payment Service
```

---

## 🛠 Troubleshooting Commands

### Container Debugging
```bash
# View container details
docker inspect flight-booking-system-redis-1
docker inspect flight-booking-system-postgres-1

# Execute bash inside container
docker exec -it flight-booking-system-redis-1 bash
docker exec -it flight-booking-system-postgres-1 bash

# View container resource usage
docker stats flight-booking-system-redis-1
docker stats flight-booking-system-postgres-1

# Restart specific container
docker restart flight-booking-system-redis-1
```

### Network & Port Issues
```bash
# Check port usage
lsof -i :6379  # Redis
lsof -i :5432  # PostgreSQL
lsof -i :8081  # Search Service

# View Docker networks
docker network ls
docker network inspect flight-booking-system_default
```

### Cleanup Commands
```bash
# Remove stopped containers
docker container prune

# Remove unused images
docker image prune

# Remove unused volumes (⚠️ deletes data)
docker volume prune

# Remove everything unused (⚠️ destructive)
docker system prune -a --volumes
```

---

## 🧪 Testing & Development

### Load Sample Data
```bash
# Run data seeding script
cd scripts
pip install -r requirements.txt
python seed_data.py
```

### API Testing
```bash
# Test search functionality
curl "http://localhost:8081/v1/search?source=AMD&destination=BLR&date=2024-01-15&criteria=CHEAPEST"

# Test inventory service
curl "http://localhost:8082/v1/flights/all"

# Get flight by ID
curl "http://localhost:8082/v1/flights/{flight-id}"
```

### Cache Testing
```bash
# Check cache performance
time curl "http://localhost:8081/v1/search?source=AMD&destination=BLR&date=2024-01-15&criteria=FASTEST"

# Monitor Redis during search
docker exec -it flight-booking-system-redis-1 redis-cli MONITOR
```

---

## 🚀 Production Commands

### Environment Specific
```bash
# Production deployment
docker-compose -f infra/docker-compose.prod.yml up -d

# Staging environment
docker-compose -f infra/docker-compose.staging.yml up -d

# Set environment variables
export SPRING_PROFILES_ACTIVE=prod
export DB_PASSWORD=secure_password
```

### Backup & Recovery
```bash
# Backup Redis data
docker exec flight-booking-system-redis-1 redis-cli BGSAVE
docker cp flight-booking-system-redis-1:/data/dump.rdb ./redis-backup.rdb

# Backup PostgreSQL
docker exec flight-booking-system-postgres-1 pg_dumpall -U postgres > full-backup.sql
```

---

## 📋 Quick Reference

| Service | Port | Health Check | Purpose |
|---------|------|-------------|---------|
| Search Service | 8081 | `/v1/health` | Flight search with K-shortest paths |
| Inventory Service | 8082 | `/v1/health` | Flight data management |
| Booking Service | 8083 | `/v1/health` | Flight booking operations |
| Payment Service | 8084 | `/v1/health` | Payment processing |
| Redis | 6379 | `PING` | Search results caching |
| PostgreSQL | 5432 | `\l` | Flight data storage |

## 🔥 Emergency Commands
```bash
# Stop everything immediately
docker stop $(docker ps -q)

# Force kill all containers
docker kill $(docker ps -q)

# Emergency Redis flush
docker exec flight-booking-system-redis-1 redis-cli FLUSHALL

# Reset everything (⚠️ DESTRUCTIVE)
docker-compose -f infra/docker-compose.yml down -v --rmi all
```

---
> 💡 **Tip**: Always test commands in development environment before using in production!