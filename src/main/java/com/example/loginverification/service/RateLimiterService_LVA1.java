package com.example.loginverification.service;

import com.example.loginverification.exception.RateLimitException_LVA1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A mock service to handle rate limiting for OTP verification attempts.
 * In production, use a more robust solution like a distributed cache (Redis) with atomic operations.
 */
@Service
public class RateLimiterService_LVA1 {
    private final int maxAttempts;
    private final long blockMinutes;
    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public RateLimiterService_LVA1(@Value("${rate.limit.attempts}") int maxAttempts,
                                @Value("${rate.limit.block.minutes}") long blockMinutes) {
        this.maxAttempts = maxAttempts;
        this.blockMinutes = blockMinutes;
    }
    
    /**
     * Records a failed attempt for a phone number and checks if the rate limit has been exceeded.
     * @param key The phone number to track.
     * @throws RateLimitException_LVA1 if the number of attempts exceeds the configured limit.
     */
    public synchronized void recordFailedAttempt(String key) {
        Attempt attempt = attempts.computeIfAbsent(key, k -> new Attempt());
        
        if (LocalDateTime.now().isAfter(attempt.getBlockedUntil())) {
            attempt.reset();
        }

        if (attempt.getCount() >= maxAttempts) {
            throw new RateLimitException_LVA1();
        }
        
        attempt.increment();
        
        if (attempt.getCount() >= maxAttempts) {
            attempt.setBlockedUntil(LocalDateTime.now().plusMinutes(blockMinutes));
        }
    }

    /**
     * Resets the attempt counter for a given key, typically on successful login.
     * @param key The phone number to reset.
     */
    public void resetAttempts(String key) {
        attempts.remove(key);
    }

    private static class Attempt {
        private final AtomicInteger count = new AtomicInteger(0);
        private LocalDateTime blockedUntil = LocalDateTime.now().minusSeconds(1);

        public int getCount() {
            return count.get();
        }

        public void increment() {
            count.incrementAndGet();
        }

        public void reset() {
            count.set(0);
            blockedUntil = LocalDateTime.now().minusSeconds(1);
        }

        public LocalDateTime getBlockedUntil() {
            return blockedUntil;
        }

        public void setBlockedUntil(LocalDateTime blockedUntil) {
            this.blockedUntil = blockedUntil;
        }
    }
}
```
src/main/java/com/example/loginverification/service/LoginVerificationService_LVA1.java
```java