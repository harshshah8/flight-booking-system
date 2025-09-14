# Timeout Handling Trade-offs Analysis

## Problem Statement
When users reserve seats but abandon the booking process (close browser, network issues, etc.), the system needs to automatically release reserved seats to prevent inventory being stuck indefinitely.

## Requirement
- **Timeout Duration**: 5 minutes after seat reservation
- **Action**: Automatically release reserved seats and mark booking as EXPIRED
- **Constraint**: Keep implementation simple for take-home assignment scope

## Evaluated Approaches

### 1. Background Scheduled Job
```java
@Scheduled(fixedRate = 60000) // Every 1 minute
public void cleanupExpiredBookings() {
    // Find and cleanup expired bookings
}
```

**Pros:**
- ✅ Proactive cleanup - expired bookings cleaned immediately
- ✅ Consistent cleanup regardless of user activity
- ✅ Production-ready approach used in enterprise systems

**Cons:**
- ❌ **Over-engineered for assignment**: Adds Spring Scheduler complexity
- ❌ **Infrastructure dependency**: Needs job monitoring in production
- ❌ **Testing complexity**: Hard to test scheduled behavior in unit tests
- ❌ **Additional configuration**: Cron expressions, thread pools
- ❌ **Scope creep**: Beyond core booking logic requirements

**Verdict**: Too complex for take-home assignment scope.

---

### 2. Timer per Booking (Async Thread Sleep)
```java
@Async
public CompletableFuture<Void> startBookingTimer(UUID bookingId) {
    return CompletableFuture.runAsync(() -> {
        Thread.sleep(5 * 60 * 1000L); // 5 minutes
        cleanupBooking(bookingId);
    });
}
```

**Pros:**
- ✅ Simple logic - one timer per booking
- ✅ Self-contained - no external schedulers
- ✅ Demonstrates async programming knowledge

**Cons:**
- ❌ **Resource wasteful**: One thread per booking sleeping for 5 minutes
- ❌ **Poor scalability**: 1000 concurrent bookings = 1000 sleeping threads
- ❌ **Thread pool exhaustion**: Can consume all available threads
- ❌ **Memory inefficient**: Sleeping threads still consume memory

**Verdict**: Not suitable for production due to resource waste.

---

### 3. Eager Cleanup (Chosen Approach)
```java
@PostMapping("/bookings")
public BookingResponse createBooking(BookingRequest request) {
    cleanupExpiredBookings(); // Clean first
    return processNewBooking(request);
}

private void cleanupExpiredBookings() {
    List<Booking> expired = bookingRepository
        .findByStatusAndCreatedAtBefore(SEATS_RESERVED,
                                      LocalDateTime.now().minusMinutes(5));
    expired.forEach(this::expireBooking);
}
```

**Pros:**
- ✅ **Resource efficient**: No background threads or sleeping
- ✅ **Simple implementation**: Single method call
- ✅ **Self-healing system**: Cleans itself on each request
- ✅ **Easy testing**: Straightforward unit tests
- ✅ **Assignment appropriate**: Focuses on core booking logic
- ✅ **Database-driven**: Uses standard SQL queries

**Cons:**
- ⚠️ **Cleanup delay**: Expired bookings only cleaned when new requests arrive
- ⚠️ **Slight latency**: Each booking request does cleanup first
- ⚠️ **Inactive system**: Low-activity periods won't trigger cleanup

**Verdict**: Perfect balance for assignment scope - simple, efficient, testable.

## Final Decision: Eager Cleanup

**Chosen for assignment because:**
1. **Simplicity**: No additional infrastructure or complexity
2. **Resource efficiency**: No thread waste or background processes
3. **Testability**: Easy to write unit and integration tests
4. **Assignment scope**: Focuses on core booking functionality
5. **Self-documenting**: Logic is clear and obvious

**Trade-off acceptance:**
- Slight delay in cleanup is acceptable for assignment demonstration
- Shows understanding of practical system design constraints
- Demonstrates ability to choose appropriate solutions for context

**SQL Query for Cleanup:**
```sql
SELECT * FROM bookings
WHERE status = 'SEATS_RESERVED'
AND created_at < NOW() - INTERVAL '5 minutes'
```

This approach demonstrates clean code principles while maintaining system reliability within assignment constraints.