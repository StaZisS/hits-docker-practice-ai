package testtask.shift.shopapi.analytics;

public class AnalyticsSummaryResponse {
    private final DbCounts db;
    private final RequestMetricsSnapshot requests;

    public AnalyticsSummaryResponse(DbCounts db, RequestMetricsSnapshot requests) {
        this.db = db;
        this.requests = requests;
    }

    public DbCounts getDb() {
        return db;
    }

    public RequestMetricsSnapshot getRequests() {
        return requests;
    }
}

