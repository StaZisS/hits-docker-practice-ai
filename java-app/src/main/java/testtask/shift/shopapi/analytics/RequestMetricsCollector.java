package testtask.shift.shopapi.analytics;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

@Component
public class RequestMetricsCollector {
    private final Instant startedAt = Instant.now();
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong errorRequests = new AtomicLong(0);
    private final LongAdder totalDurationNanos = new LongAdder();
    private final AtomicLong maxDurationNanos = new AtomicLong(0);
    private final ConcurrentHashMap<String, LongAdder> requestsByPath = new ConcurrentHashMap<>();

    public void record(String pathPattern, int statusCode, long durationNanos) {
        totalRequests.incrementAndGet();
        if (statusCode >= 400) {
            errorRequests.incrementAndGet();
        }
        totalDurationNanos.add(durationNanos);
        maxDurationNanos.accumulateAndGet(durationNanos, Math::max);
        requestsByPath.computeIfAbsent(pathPattern, ignored -> new LongAdder()).increment();
    }

    public RequestMetricsSnapshot snapshot(int maxPaths) {
        long total = totalRequests.get();
        long errors = errorRequests.get();
        double errorRate = total == 0 ? 0.0 : ((double) errors) / total;

        double avgLatencyMs = total == 0 ? 0.0 : nanosToMs(totalDurationNanos.sum()) / total;
        double maxLatencyMs = nanosToMs(maxDurationNanos.get());

        Map<String, Long> topPaths = requestsByPath.entrySet()
                .stream()
                .sorted(Map.Entry.<String, LongAdder>comparingByValue(Comparator.comparingLong(LongAdder::sum)).reversed())
                .limit(Math.max(0, maxPaths))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().sum(), (a, b) -> a, java.util.LinkedHashMap::new));

        long uptimeSeconds = Math.max(0, Duration.between(startedAt, Instant.now()).getSeconds());

        return new RequestMetricsSnapshot(
                startedAt,
                uptimeSeconds,
                total,
                errors,
                errorRate,
                avgLatencyMs,
                maxLatencyMs,
                topPaths
        );
    }

    public RequestMetricsSnapshot snapshot() {
        return snapshot(50);
    }

    private static double nanosToMs(long nanos) {
        return nanos / 1_000_000.0;
    }
}
