package testtask.shift.shopapi.analytics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import testtask.shift.shopapi.repository.HardDriveRepository;
import testtask.shift.shopapi.repository.LaptopRepository;
import testtask.shift.shopapi.repository.MonitorRepository;
import testtask.shift.shopapi.repository.PersonalComputerRepository;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final LaptopRepository laptopRepository;
    private final MonitorRepository monitorRepository;
    private final HardDriveRepository hardDriveRepository;
    private final PersonalComputerRepository personalComputerRepository;
    private final RequestMetricsCollector metricsCollector;

    public AnalyticsController(
            LaptopRepository laptopRepository,
            MonitorRepository monitorRepository,
            HardDriveRepository hardDriveRepository,
            PersonalComputerRepository personalComputerRepository,
            RequestMetricsCollector metricsCollector
    ) {
        this.laptopRepository = laptopRepository;
        this.monitorRepository = monitorRepository;
        this.hardDriveRepository = hardDriveRepository;
        this.personalComputerRepository = personalComputerRepository;
        this.metricsCollector = metricsCollector;
    }

    @Operation(summary = "Project analytics: DB entity counts + basic request metrics")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Analytics summary")})
    @GetMapping(value = "/summary", produces = "application/json")
    public AnalyticsSummaryResponse getSummary() {
        DbCounts dbCounts = new DbCounts(
                laptopRepository.count(),
                monitorRepository.count(),
                hardDriveRepository.count(),
                personalComputerRepository.count()
        );
        return new AnalyticsSummaryResponse(dbCounts, metricsCollector.snapshot());
    }

    @Operation(summary = "DB entity counts")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "DB counts")})
    @GetMapping(value = "/db", produces = "application/json")
    public DbCounts getDbCounts() {
        return new DbCounts(
                laptopRepository.count(),
                monitorRepository.count(),
                hardDriveRepository.count(),
                personalComputerRepository.count()
        );
    }

    @Operation(summary = "Request metrics (in-memory, since app start)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Request metrics")})
    @GetMapping(value = "/requests", produces = "application/json")
    public RequestMetricsSnapshot getRequestMetrics() {
        return metricsCollector.snapshot();
    }
}

