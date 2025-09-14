#!/usr/bin/env python3
"""
Comprehensive Flight Data Generator
Generates realistic flight data for Indian airports with varied routes, costs, and schedules
Optimized for K-shortest path search algorithms
"""

import psycopg2
import random
from datetime import time

# Database connection parameters
DB_CONFIG = {
    'host': 'localhost',
    'port': 5432,
    'database': 'flight_inventory_db',
    'user': 'flight_user',
    'password': 'flight_password'
}

# Indian Airports (50+ airports for comprehensive testing)
AIRPORTS = {
    # Major Hubs (Tier 1)
    'DEL': {'city': 'Delhi', 'tier': 1},
    'BOM': {'city': 'Mumbai', 'tier': 1},
    'BLR': {'city': 'Bengaluru', 'tier': 1},
    'MAA': {'city': 'Chennai', 'tier': 1},
    'CCU': {'city': 'Kolkata', 'tier': 1},
    'HYD': {'city': 'Hyderabad', 'tier': 1},
    
    # Secondary Cities (Tier 2)
    'AMD': {'city': 'Ahmedabad', 'tier': 2},
    'PNQ': {'city': 'Pune', 'tier': 2},
    'GOI': {'city': 'Goa', 'tier': 2},
    'COK': {'city': 'Kochi', 'tier': 2},
    'TRV': {'city': 'Thiruvananthapuram', 'tier': 2},
    'JAI': {'city': 'Jaipur', 'tier': 2},
    'LKO': {'city': 'Lucknow', 'tier': 2},
    'PAT': {'city': 'Patna', 'tier': 2},
    'BHU': {'city': 'Bhubaneswar', 'tier': 2},
    'GAU': {'city': 'Guwahati', 'tier': 2},
    
    # Regional Airports (Tier 3)
    'IXC': {'city': 'Chandigarh', 'tier': 3},
    'STV': {'city': 'Surat', 'tier': 3},
    'IXR': {'city': 'Ranchi', 'tier': 3},
    'IXJ': {'city': 'Jammu', 'tier': 3},
    'SXR': {'city': 'Srinagar', 'tier': 3},
    'ATQ': {'city': 'Amritsar', 'tier': 3},
    'UDR': {'city': 'Udaipur', 'tier': 3},
    'JDH': {'city': 'Jodhpur', 'tier': 3},
    'VNS': {'city': 'Varanasi', 'tier': 3},
    'VTZ': {'city': 'Visakhapatnam', 'tier': 3},
    'VGA': {'city': 'Vijayawada', 'tier': 3},
    'CJB': {'city': 'Coimbatore', 'tier': 3},
    'TRZ': {'city': 'Tiruchirapalli', 'tier': 3},
    'MDU': {'city': 'Madurai', 'tier': 3},
    'IXM': {'city': 'Mangalore', 'tier': 3},
    'CNN': {'city': 'Kannur', 'tier': 3},
    'CLT': {'city': 'Calicut', 'tier': 3},
    'DED': {'city': 'Dehradun', 'tier': 3},
    'PBD': {'city': 'Porbandar', 'tier': 3},
    'BHJ': {'city': 'Bhuj', 'tier': 3},
    'TEZ': {'city': 'Tezpur', 'tier': 3},
    'IXS': {'city': 'Silchar', 'tier': 3},
    'AJL': {'city': 'Aizawl', 'tier': 3},
    'IMF': {'city': 'Imphal', 'tier': 3},
    'IXA': {'city': 'Agartala', 'tier': 3}
}

# Airlines with cost factors
AIRLINES = {
    'AI': {'name': 'Air India', 'cost_factor': 1.1},
    '6E': {'name': 'IndiGo', 'cost_factor': 0.9},
    'UK': {'name': 'Vistara', 'cost_factor': 1.2},
    'SG': {'name': 'SpiceJet', 'cost_factor': 0.85},
    'G8': {'name': 'GoFirst', 'cost_factor': 0.8}
}

def connect_db():
    """Connect to PostgreSQL database"""
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        return conn
    except Exception as e:
        print(f"❌ Error connecting to database: {e}")
        return None

def create_table(conn):
    """Create flights table if it doesn't exist"""
    create_table_sql = """
    CREATE TABLE IF NOT EXISTS flights (
        flight_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        flight_number VARCHAR(20) NOT NULL,
        source VARCHAR(10) NOT NULL,
        destination VARCHAR(10) NOT NULL,
        cost DECIMAL(10,2) NOT NULL,
        duration INTEGER NOT NULL,
        available_seats INTEGER NOT NULL,
        booked_seats INTEGER NOT NULL,
        flight_status VARCHAR(20) NOT NULL,
        departure_time TIME NOT NULL,
        arrival_time TIME NOT NULL,
        created_at TIMESTAMP DEFAULT NOW(),
        updated_at TIMESTAMP DEFAULT NOW()
    );
    """
    
    try:
        cursor = conn.cursor()
        cursor.execute(create_table_sql)
        conn.commit()
        print("✅ Table 'flights' created/verified")
        cursor.close()
    except Exception as e:
        print(f"❌ Error creating table: {e}")

def calculate_cost(source, dest, airline_code):
    """Calculate realistic flight cost"""
    src_tier = AIRPORTS[source]['tier']
    dest_tier = AIRPORTS[dest]['tier']
    airline_factor = AIRLINES[airline_code]['cost_factor']
    
    base_cost = 2000
    if src_tier == 1 and dest_tier == 1:
        distance_cost = random.randint(3000, 6000)
    elif src_tier <= 2 and dest_tier <= 2:
        distance_cost = random.randint(2000, 4000)
    else:
        distance_cost = random.randint(1500, 3000)
    
    total_cost = (base_cost + distance_cost) * airline_factor
    return round(total_cost, 2)

def calculate_duration(source, dest):
    """Calculate flight duration"""
    src_tier = AIRPORTS[source]['tier']
    dest_tier = AIRPORTS[dest]['tier']
    
    if src_tier == 1 and dest_tier == 1:
        return random.randint(120, 180)
    elif src_tier <= 2 and dest_tier <= 2:
        return random.randint(90, 150)
    else:
        return random.randint(60, 120)

def should_connect(source, dest):
    """Determine if airports should have direct flights"""
    if source == dest:
        return False
    
    src_tier = AIRPORTS[source]['tier']
    dest_tier = AIRPORTS[dest]['tier']
    
    # Tier 1 to tier 1/2 - high probability
    if src_tier == 1 and dest_tier <= 2:
        return random.random() < 0.7
    # Tier 2 to tier 1 - high probability
    if src_tier == 2 and dest_tier == 1:
        return random.random() < 0.7
    # Tier 2 to tier 2 - medium probability
    if src_tier == 2 and dest_tier == 2:
        return random.random() < 0.3
    # Regional connections
    if src_tier == 3 and dest_tier <= 2:
        return random.random() < 0.2
    # Regional to regional - very low
    if src_tier == 3 and dest_tier == 3:
        return random.random() < 0.05
    
    return False

def generate_guaranteed_demo_routes():
    """Generate guaranteed routes for AMD->BLR demo with 0, 1, and 2 stops"""
    demo_flights = []
    flight_counter = 1000  # Start with higher counter to avoid conflicts

    # Time slots for flights
    time_slots = [
        (6, 0), (7, 30), (8, 0), (9, 30), (10, 0), (11, 30),
        (12, 0), (13, 30), (14, 0), (15, 30), (16, 0), (17, 30),
        (18, 0), (19, 30), (20, 0), (21, 30)
    ]

    print("Generating guaranteed AMD->BLR demo routes...")

    # DIRECT FLIGHTS (0 stops): AMD -> BLR
    direct_routes = [
        ('AMD', 'BLR', 'AI', 4500, 120),  # Air India - expensive, medium duration
        ('AMD', 'BLR', '6E', 3200, 115),  # IndiGo - cheap, fast
        ('AMD', 'BLR', 'UK', 5200, 125),  # Vistara - expensive, slow
        ('AMD', 'BLR', 'SG', 2800, 110),  # SpiceJet - very cheap, very fast
    ]

    # ONE-STOP ROUTES (1 stop): AMD -> X -> BLR
    one_stop_hubs = ['BOM', 'DEL', 'HYD', 'MAA']  # Major hub cities

    # TWO-STOP ROUTES (2 stops): AMD -> X -> Y -> BLR
    two_stop_combinations = [
        ('BOM', 'DEL'),  # AMD->BOM->DEL->BLR
        ('DEL', 'MAA'),  # AMD->DEL->MAA->BLR
        ('HYD', 'BOM'),  # AMD->HYD->BOM->BLR
        ('MAA', 'CCU'),  # AMD->MAA->CCU->BLR
    ]

    # Generate direct flights
    for source, dest, airline, base_cost, base_duration in direct_routes:
        for i in range(2):  # 2 flights per airline for variety
            dep_hour, dep_min = random.choice(time_slots)
            dep_time = time(dep_hour, dep_min)

            # Add variation to cost and duration
            cost_variation = random.randint(-300, 500)
            duration_variation = random.randint(-10, 20)

            final_cost = base_cost + cost_variation
            final_duration = base_duration + duration_variation

            # Calculate arrival time
            arr_hour = (dep_hour + final_duration // 60) % 24
            arr_min = (dep_min + final_duration % 60) % 60
            if arr_min >= 60:
                arr_hour = (arr_hour + 1) % 24
                arr_min = arr_min - 60
            arr_time = time(arr_hour, arr_min)

            flight_number = f"{airline}{flight_counter:03d}"
            flight_counter += 1

            total_seats = random.choice([120, 150, 180])
            booked = random.randint(20, total_seats - 10)
            available = total_seats - booked

            flight = (
                flight_number, source, dest, final_cost, final_duration,
                available, booked, "SCHEDULED",
                dep_time.strftime("%H:%M:%S"),
                arr_time.strftime("%H:%M:%S")
            )
            demo_flights.append(flight)

    # Generate one-stop connecting flights
    for hub in one_stop_hubs:
        # AMD -> HUB flights
        for airline in ['6E', 'AI', 'SG']:
            dep_hour, dep_min = random.choice(time_slots)
            dep_time = time(dep_hour, dep_min)
            duration = random.randint(90, 120)
            cost = random.randint(2000, 3500)

            arr_hour = (dep_hour + duration // 60) % 24
            arr_min = (dep_min + duration % 60) % 60
            arr_time = time(arr_hour, arr_min)

            flight_number = f"{airline}{flight_counter:03d}"
            flight_counter += 1

            total_seats = random.choice([120, 150, 180])
            booked = random.randint(20, total_seats - 10)
            available = total_seats - booked

            flight = (
                flight_number, 'AMD', hub, cost, duration,
                available, booked, "SCHEDULED",
                dep_time.strftime("%H:%M:%S"),
                arr_time.strftime("%H:%M:%S")
            )
            demo_flights.append(flight)
            demo_flights.append(flight)

        # HUB -> BLR flights (with connecting times)
        for airline in ['6E', 'AI', 'UK', 'SG']:
            # Schedule 2-3 hours after AMD->HUB flights for connections
            dep_hour = random.randint(10, 20)
            dep_min = random.choice([0, 30])
            dep_time = time(dep_hour, dep_min)
            duration = random.randint(80, 140)
            cost = random.randint(2200, 4000)

            arr_hour = (dep_hour + duration // 60) % 24
            arr_min = (dep_min + duration % 60) % 60
            arr_time = time(arr_hour, arr_min)

            flight_number = f"{airline}{flight_counter:03d}"
            flight_counter += 1

            total_seats = random.choice([120, 150, 180])
            booked = random.randint(20, total_seats - 10)
            available = total_seats - booked

            flight = (
                flight_number, hub, 'BLR', cost, duration,
                available, booked, "SCHEDULED",
                dep_time.strftime("%H:%M:%S"),
                arr_time.strftime("%H:%M:%S")
            )
            demo_flights.append(flight)

    # Generate two-stop connecting flights
    for stop1, stop2 in two_stop_combinations:
        # AMD -> STOP1
        for airline in ['6E', 'SG']:
            dep_time = time(6, 0)  # Early morning
            duration = random.randint(90, 120)
            cost = random.randint(1800, 2800)

            arr_hour = (6 + duration // 60) % 24
            arr_min = duration % 60
            arr_time = time(arr_hour, arr_min)

            flight_number = f"{airline}{flight_counter:03d}"
            flight_counter += 1

            total_seats = random.choice([120, 150, 180])
            booked = random.randint(20, total_seats - 10)
            available = total_seats - booked

            flight = (
                flight_number, 'AMD', stop1, cost, duration,
                available, booked, "SCHEDULED",
                dep_time.strftime("%H:%M:%S"),
                arr_time.strftime("%H:%M:%S")
            )
            demo_flights.append(flight)

        # STOP1 -> STOP2
        dep_time = time(10, 0)  # Mid morning
        duration = random.randint(100, 150)
        cost = random.randint(2500, 3500)

        arr_hour = (10 + duration // 60) % 24
        arr_min = duration % 60
        arr_time = time(arr_hour, arr_min)

        airline = random.choice(['6E', 'AI', 'UK'])
        flight_number = f"{airline}{flight_counter:03d}"
        flight_counter += 1

        total_seats = random.choice([120, 150, 180])
        booked = random.randint(20, total_seats - 10)
        available = total_seats - booked

        flight = (
            flight_number, stop1, stop2, cost, duration,
            available, booked, "SCHEDULED",
            dep_time.strftime("%H:%M:%S"),
            arr_time.strftime("%H:%M:%S")
        )
        demo_flights.append(flight)

        # STOP2 -> BLR
        dep_time = time(14, 0)  # Afternoon
        duration = random.randint(80, 130)
        cost = random.randint(2200, 3200)

        arr_hour = (14 + duration // 60) % 24
        arr_min = duration % 60
        arr_time = time(arr_hour, arr_min)

        airline = random.choice(['6E', 'AI', 'SG'])
        flight_number = f"{airline}{flight_counter:03d}"
        flight_counter += 1

        total_seats = random.choice([120, 150, 180])
        booked = random.randint(20, total_seats - 10)
        available = total_seats - booked

        flight = (
            flight_number, stop2, 'BLR', cost, duration,
            available, booked, "SCHEDULED",
            dep_time.strftime("%H:%M:%S"),
            arr_time.strftime("%H:%M:%S")
        )
        demo_flights.append(flight)

    print(f"✅ Generated {len(demo_flights)} guaranteed demo flights")
    return demo_flights

def generate_flight_data():
    """Generate comprehensive flight dataset with guaranteed demo routes"""
    flights = []
    flight_counter = 1
    airport_codes = list(AIRPORTS.keys())

    # First, generate guaranteed demo routes
    demo_flights = generate_guaranteed_demo_routes()
    flights.extend(demo_flights)
    flight_counter = 2000  # Continue counter after demo flights

    # Time slots for flights
    time_slots = [
        (6, 0), (7, 0), (8, 0), (9, 0), (10, 0), (11, 0),
        (12, 0), (13, 0), (14, 0), (15, 0), (16, 0), (17, 0),
        (18, 0), (19, 0), (20, 0), (21, 0), (22, 0)
    ]

    print(f"🔄 Generating additional flights for {len(airport_codes)} airports...")

    for source in airport_codes:
        for dest in airport_codes:
            if should_connect(source, dest):
                # Number of flights for this route
                src_tier = AIRPORTS[source]['tier']
                dest_tier = AIRPORTS[dest]['tier']

                if src_tier == 1 and dest_tier == 1:
                    num_flights = random.randint(2, 4)
                elif src_tier <= 2 and dest_tier <= 2:
                    num_flights = random.randint(1, 3)
                else:
                    num_flights = random.randint(1, 2)

                for _ in range(num_flights):
                    airline_code = random.choice(list(AIRLINES.keys()))

                    # Generate departure time
                    dep_hour, dep_min = random.choice(time_slots)
                    dep_time = time(dep_hour, dep_min)

                    # Calculate duration and arrival time
                    duration_minutes = calculate_duration(source, dest)
                    arr_hour = (dep_hour + duration_minutes // 60) % 24
                    arr_min = (dep_min + duration_minutes % 60) % 60
                    if arr_min >= 60:
                        arr_hour = (arr_hour + 1) % 24
                        arr_min = arr_min - 60
                    arr_time = time(arr_hour, arr_min)

                    # Calculate cost
                    cost = calculate_cost(source, dest, airline_code)

                    # Flight number
                    flight_number = f"{airline_code}{flight_counter:03d}"
                    flight_counter += 1

                    # Seat availability
                    total_seats = random.choice([120, 150, 180])
                    booked = random.randint(20, total_seats - 10)
                    available = total_seats - booked

                    flight = (
                        flight_number, source, dest, cost, duration_minutes,
                        available, booked, "SCHEDULED",
                        dep_time.strftime("%H:%M:%S"),
                        arr_time.strftime("%H:%M:%S")
                    )
                    flights.append(flight)

    print(f"✅ Generated {len(flights)} total flights (including {len(demo_flights)} demo flights)")
    return flights

def seed_flight_data(conn):
    """Insert comprehensive flight data"""
    flights = generate_flight_data()
    
    insert_sql = """
    INSERT INTO flights (flight_number, source, destination, cost, duration, 
                        available_seats, booked_seats, flight_status,
                        departure_time, arrival_time)
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """
    
    try:
        cursor = conn.cursor()
        
        # Check if data already exists
        cursor.execute("SELECT COUNT(*) FROM flights")
        count = cursor.fetchone()[0]
        
        if count > 0:
            print(f"⚠️  Database already has {count} flights. Skipping seeding.")
            cursor.close()
            return
        
        # Insert flight data
        cursor.executemany(insert_sql, flights)
        conn.commit()
        
        print(f"✅ Successfully seeded {len(flights)} flights!")
        
        # Display statistics
        print(f"\n📊 Dataset Statistics:")
        print(f"   • Total airports: {len(AIRPORTS)}")
        print(f"   • Total flights: {len(flights)}")
        
        # Sample routes for algorithm testing
        amd_blr_direct = [f for f in flights if f[1] == 'AMD' and f[2] == 'BLR']
        amd_bom = [f for f in flights if f[1] == 'AMD' and f[2] == 'BOM']
        bom_blr = [f for f in flights if f[1] == 'BOM' and f[2] == 'BLR']
        
        print(f"\n🎯 Key Routes for K-shortest Path Testing:")
        print(f"   • AMD->BLR direct: {len(amd_blr_direct)} flights")
        print(f"   • AMD->BOM: {len(amd_bom)} flights")
        print(f"   • BOM->BLR: {len(bom_blr)} flights")
        print(f"\n💡 Perfect dataset for K-shortest path algorithm!")
        
        cursor.close()
        
    except Exception as e:
        print(f"❌ Error seeding data: {e}")
        conn.rollback()

def verify_data(conn):
    """Verify the seeded data"""
    try:
        cursor = conn.cursor()
        
        # Get total count
        cursor.execute("SELECT COUNT(*) FROM flights")
        total_flights = cursor.fetchone()[0]
        
        # Get sample flights
        cursor.execute("""
            SELECT flight_number, source, destination, cost, duration, 
                   departure_time, arrival_time
            FROM flights 
            ORDER BY flight_number 
            LIMIT 5
        """)
        
        sample_flights = cursor.fetchall()
        
        print(f"\n📈 Database Verification:")
        print(f"   • Total flights: {total_flights}")
        print(f"   • Sample flight schedules:")
        
        for flight in sample_flights:
            flight_num, src, dest, cost, duration, dep_time, arr_time = flight
            print(f"     - {flight_num}: {src}->{dest} {dep_time}->{arr_time} ₹{cost} ({duration}min)")
        
        cursor.close()
        
    except Exception as e:
        print(f"❌ Error verifying data: {e}")

def main():
    """Main function"""
    print("🚀 Starting Comprehensive Flight Data Generation...")
    print("💡 Optimized for K-shortest path search algorithms")
    
    # Connect to database
    conn = connect_db()
    if not conn:
        return
    
    try:
        # Create table
        create_table(conn)
        
        # Seed data
        seed_flight_data(conn)
        
        # Verify data
        verify_data(conn)
        
        print("\n✅ Comprehensive flight data generation completed!")
        print("🎯 Dataset ready for search algorithms!")
        
    finally:
        conn.close()

if __name__ == "__main__":
    main()