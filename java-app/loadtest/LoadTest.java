import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoadTest {
    record Result(long total, long errors, long totalNanos, long maxNanos, long[] latenciesNanos, Map<Integer, Long> statuses) {}

    public static void main(String[] args) throws Exception {
        String baseUrl = arg(args, "--base-url", "http://localhost:8080");
        int concurrency = Integer.parseInt(arg(args, "--concurrency", "20"));
        int durationSeconds = Integer.parseInt(arg(args, "--duration-seconds", "30"));
        int seed = Integer.parseInt(arg(args, "--seed", "50"));

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();

        List<Long> laptopIds = seedLaptops(client, baseUrl, seed);
        if (laptopIds.isEmpty()) {
            System.err.println("WARNING: no laptops were seeded; GET-by-id will be skipped");
        }

        long startWall = System.nanoTime();
        long deadline = startWall + Duration.ofSeconds(durationSeconds).toNanos();

        ExecutorService pool = Executors.newFixedThreadPool(concurrency);
        List<Future<Result>> futures = new ArrayList<>();
        for (int i = 0; i < concurrency; i++) {
            futures.add(pool.submit(worker(client, baseUrl, laptopIds, deadline, i)));
        }

        Result merged = merge(futures);
        pool.shutdownNow();

        long elapsedNanos = System.nanoTime() - startWall;
        double elapsedSeconds = elapsedNanos / 1_000_000_000.0;

        long[] latencies = merged.latenciesNanos();
        Arrays.sort(latencies);

        double rps = merged.total() / elapsedSeconds;
        double errorRate = merged.total() == 0 ? 0.0 : ((double) merged.errors()) / merged.total();

        System.out.println("=== Load test results ===");
        System.out.printf("Base URL: %s%n", baseUrl);
        System.out.printf("Concurrency: %d%n", concurrency);
        System.out.printf("Elapsed: %.2fs%n", elapsedSeconds);
        System.out.printf("Requests: %d%n", merged.total());
        System.out.printf("RPS: %.2f%n", rps);
        System.out.printf("Errors: %d (rate=%.4f)%n", merged.errors(), errorRate);
        System.out.printf("Avg latency: %.2f ms%n", nanosToMs(merged.totalNanos() / Math.max(1, merged.total())));
        System.out.printf("Max latency: %.2f ms%n", nanosToMs(merged.maxNanos()));
        System.out.printf("p50 latency: %.2f ms%n", nanosToMs(percentile(latencies, 0.50)));
        System.out.printf("p95 latency: %.2f ms%n", nanosToMs(percentile(latencies, 0.95)));
        System.out.printf("p99 latency: %.2f ms%n", nanosToMs(percentile(latencies, 0.99)));
        System.out.println("Status codes: " + merged.statuses());
    }

    private static Callable<Result> worker(HttpClient client, String baseUrl, List<Long> laptopIds, long deadline, int workerId) {
        return () -> {
            SplittableRandom random = new SplittableRandom(1234L + workerId);
            long total = 0;
            long errors = 0;
            long totalNanos = 0;
            long maxNanos = 0;
            long[] latencies = new long[4096];
            int latencySize = 0;
            Map<Integer, Long> statuses = new HashMap<>();

            while (System.nanoTime() < deadline) {
                String path = pickPath(random, laptopIds);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + path))
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();

                long started = System.nanoTime();
                int status = 0;
                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    status = response.statusCode();
                    if (status < 200 || status >= 300) {
                        errors++;
                    }
                } catch (IOException | InterruptedException e) {
                    errors++;
                } finally {
                    long took = System.nanoTime() - started;
                    total++;
                    totalNanos += took;
                    if (took > maxNanos) maxNanos = took;
                    statuses.put(status, statuses.getOrDefault(status, 0L) + 1);
                    if (latencySize == latencies.length) {
                        latencies = Arrays.copyOf(latencies, latencies.length * 2);
                    }
                    latencies[latencySize++] = took;
                }
            }

            return new Result(total, errors, totalNanos, maxNanos, Arrays.copyOf(latencies, latencySize), statuses);
        };
    }

    private static String pickPath(SplittableRandom random, List<Long> laptopIds) {
        double dice = random.nextDouble();
        if (dice < 0.60) return "/api/laptops";
        if (dice < 0.80 && !laptopIds.isEmpty()) {
            long id = laptopIds.get(random.nextInt(laptopIds.size()));
            return "/api/laptops/" + id;
        }
        return "/api/analytics/summary";
    }

    private static Result merge(List<Future<Result>> futures) throws ExecutionException, InterruptedException {
        long total = 0;
        long errors = 0;
        long totalNanos = 0;
        long maxNanos = 0;
        Map<Integer, Long> statuses = new HashMap<>();

        int totalLatencies = 0;
        List<long[]> parts = new ArrayList<>();
        for (Future<Result> f : futures) {
            Result r = f.get();
            total += r.total();
            errors += r.errors();
            totalNanos += r.totalNanos();
            maxNanos = Math.max(maxNanos, r.maxNanos());
            parts.add(r.latenciesNanos());
            totalLatencies += r.latenciesNanos().length;
            for (Map.Entry<Integer, Long> e : r.statuses().entrySet()) {
                statuses.put(e.getKey(), statuses.getOrDefault(e.getKey(), 0L) + e.getValue());
            }
        }

        long[] latencies = new long[totalLatencies];
        int offset = 0;
        for (long[] part : parts) {
            System.arraycopy(part, 0, latencies, offset, part.length);
            offset += part.length;
        }

        return new Result(total, errors, totalNanos, maxNanos, latencies, statuses);
    }

    private static List<Long> seedLaptops(HttpClient client, String baseUrl, int count) {
        List<Long> ids = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String body = """
                    {
                      "seriesNumber": "LT-%d",
                      "producer": "loadtest",
                      "price": 1000.0,
                      "numberOfProductsInStock": 10,
                      "size": "13 inches"
                    }
                    """.formatted(i);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/laptops/add"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    Long id = extractId(response.body());
                    if (id != null) ids.add(id);
                }
            } catch (Exception ignored) {
                // seeding is best-effort; load test still runs
            }
        }
        return ids;
    }

    private static Long extractId(String json) {
        // minimal JSON parsing to avoid extra dependencies
        int idx = json.indexOf("\"id\"");
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx);
        if (colon < 0) return null;
        int start = colon + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) end++;
        if (start == end) return null;
        try {
            return Long.parseLong(json.substring(start, end));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String arg(String[] args, String name, String def) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(name)) return args[i + 1];
        }
        return def;
    }

    private static long percentile(long[] sortedLatencies, double p) {
        if (sortedLatencies.length == 0) return 0;
        int idx = (int) Math.ceil(p * sortedLatencies.length) - 1;
        idx = Math.max(0, Math.min(idx, sortedLatencies.length - 1));
        return sortedLatencies[idx];
    }

    private static double nanosToMs(long nanos) {
        return nanos / 1_000_000.0;
    }
}
