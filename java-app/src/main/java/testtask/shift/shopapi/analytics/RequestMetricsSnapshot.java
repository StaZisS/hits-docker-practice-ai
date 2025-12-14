package testtask.shift.shopapi.analytics;

import java.time.Instant;
import java.util.Map;

public class RequestMetricsSnapshot {
    private final Instant startedAt;
    private final long uptimeSeconds;
    private final long totalRequests;
    private final long errorRequests;
    private final double errorRate;
    private final double avgLatencyMs;
    private final double maxLatencyMs;
    private final Map<String, Long> requestsByPath;

    public RequestMetricsSnapshot(
            Instant startedAt,
            long uptimeSeconds,
            long totalRequests,
            long errorRequests,
            double errorRate,
            double avgLatencyMs,
            double maxLatencyMs,
            Map<String, Long> requestsByPath
    ) {
        this.startedAt = startedAt;
        this.uptimeSeconds = uptimeSeconds;
        this.totalRequests = totalRequests;
        this.errorRequests = errorRequests;
        this.errorRate = errorRate;
        this.avgLatencyMs = avgLatencyMs;
        this.maxLatencyMs = maxLatencyMs;
        this.requestsByPath = requestsByPath;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public long getUptimeSeconds() {
        return uptimeSeconds;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public long getErrorRequests() {
        return errorRequests;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public double getAvgLatencyMs() {
        return avgLatencyMs;
    }

    public double getMaxLatencyMs() {
        return maxLatencyMs;
    }

    public Map<String, Long> getRequestsByPath() {
        return requestsByPath;
    }
}

