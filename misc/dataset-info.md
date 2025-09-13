# Flight Dataset Documentation

## Overview
Comprehensive flight dataset optimized for **K-shortest path algorithms** and flight booking system testing.

## Dataset Structure

### Airports (29 total)
- **Tier 1 (6):** Major hubs - DEL, BOM, BLR, MAA, CCU, HYD
- **Tier 2 (10):** Secondary - AMD, PNQ, GOI, COK, TRV, JAI, LKO, PAT, BHU, GAU  
- **Tier 3 (13):** Regional - IXC, STV, IXR, IXJ, SXR, ATQ, UDR, JDH, VNS, etc.

### Route Generation Logic
```
Tier 1 ↔ Tier 1/2: 70% connectivity (major routes)
Tier 2 ↔ Tier 1:   70% connectivity (secondary to major)
Tier 2 ↔ Tier 2:   30% connectivity (inter-secondary)
Regional routes:   5-20% connectivity (limited)
```

### Pricing Model
```
Cost = (₹2000 base + distance cost) × airline factor

Distance: ₹1500-6000 based on tier combination
Airlines: AI(1.1×), UK(1.2×), 6E(0.9×), SG(0.85×), G8(0.8×)
```

### Flight Characteristics
- **Schedule:** TIME-only (06:00-22:00), no dates
- **Duration:** 60-180 minutes based on route tier
- **Capacity:** 120/150/180 seats with random occupancy
- **Flights per route:** 1-4 based on route importance

## Algorithm Optimization

### K-Shortest Path Ready
- **AMD→BLR example routes:**
  - Direct: AMD→BLR (fastest)
  - Via hub: AMD→BOM→BLR (cost-effective)
  - Alternative: AMD→DEL→BLR (backup)

### Search Features
- **Cost-based:** Cheapest flights first
- **Time-based:** Fastest duration first  
- **Multi-criteria:** Cost vs time trade-offs
- **Hub utilization:** Realistic airline hub model

## Expected Results
- **Total flights:** ~400-600
- **Unique routes:** ~200-300  
- **Average cost:** ₹4000-6000
- **Coverage:** 80%+ flights via major hubs

## Benefits
✅ **Realistic connectivity** (hub-and-spoke model)  
✅ **Algorithm-friendly** (sufficient complexity)  
✅ **Performance testing** (adequate data volume)  
✅ **Feature demonstration** (multiple search criteria)