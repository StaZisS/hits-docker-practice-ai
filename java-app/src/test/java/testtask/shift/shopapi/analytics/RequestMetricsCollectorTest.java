package testtask.shift.shopapi.analytics;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RequestMetricsCollectorTest {

    @Test
    void snapshot_whenEmpty_returnsZeros() {
        RequestMetricsCollector collector = new RequestMetricsCollector();

        RequestMetricsSnapshot snapshot = collector.snapshot();

        assertEquals(0, snapshot.getTotalRequests());
        assertEquals(0, snapshot.getErrorRequests());
        assertEquals(0.0, snapshot.getErrorRate());
        assertTrue(snapshot.getUptimeSeconds() >= 0);
        assertTrue(snapshot.getRequestsByPath().isEmpty());
    }

    @Test
    void record_andSnapshot_aggregatesTotalsAndTopPaths() {
        RequestMetricsCollector collector = new RequestMetricsCollector();

        collector.record("/api/laptops", 200, 10_000_000);
        collector.record("/api/laptops", 200, 20_000_000);
        collector.record("/api/laptops/{id}", 404, 5_000_000);

        RequestMetricsSnapshot snapshot = collector.snapshot(10);

        assertEquals(3, snapshot.getTotalRequests());
        assertEquals(1, snapshot.getErrorRequests());
        assertEquals(1.0 / 3.0, snapshot.getErrorRate());
        assertTrue(snapshot.getAvgLatencyMs() > 0.0);
        assertTrue(snapshot.getMaxLatencyMs() > 0.0);

        List<String> keys = snapshot.getRequestsByPath().keySet().stream().toList();
        assertEquals("/api/laptops", keys.get(0));
        assertEquals(2L, snapshot.getRequestsByPath().get("/api/laptops"));
        assertEquals(1L, snapshot.getRequestsByPath().get("/api/laptops/{id}"));
    }
}

