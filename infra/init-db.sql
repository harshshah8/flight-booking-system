-- Create database for booking service
CREATE DATABASE flight_booking_db;

-- Grant permissions to the user for both databases
GRANT ALL PRIVILEGES ON DATABASE flight_inventory_db TO flight_user;
GRANT ALL PRIVILEGES ON DATABASE flight_booking_db TO flight_user;