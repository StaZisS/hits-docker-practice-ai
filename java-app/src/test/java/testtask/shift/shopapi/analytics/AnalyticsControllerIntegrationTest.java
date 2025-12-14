package testtask.shift.shopapi.analytics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnalyticsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void analyticsEndpoints_workAndExposeRequestMetrics() throws Exception {
        mockMvc.perform(get("/api/analytics/requests")).andExpect(status().isOk());

        mockMvc.perform(get("/api/analytics/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRequests").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.requestsByPath").exists());

        mockMvc.perform(get("/api/analytics/db"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0));

        mockMvc.perform(get("/api/analytics/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.db.total").value(0))
                .andExpect(jsonPath("$.requests.totalRequests").exists());
    }
}

