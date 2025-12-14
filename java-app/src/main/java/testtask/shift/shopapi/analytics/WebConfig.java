package testtask.shift.shopapi.analytics;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final RequestMetricsInterceptor requestMetricsInterceptor;

    public WebConfig(RequestMetricsInterceptor requestMetricsInterceptor) {
        this.requestMetricsInterceptor = requestMetricsInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestMetricsInterceptor);
    }
}

