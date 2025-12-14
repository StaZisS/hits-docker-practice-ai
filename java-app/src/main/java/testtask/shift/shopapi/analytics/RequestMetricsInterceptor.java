package testtask.shift.shopapi.analytics;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RequestMetricsInterceptor implements HandlerInterceptor {
    private static final String START_TIME_NANOS_ATTRIBUTE = RequestMetricsInterceptor.class.getName() + ".startTimeNanos";

    private final RequestMetricsCollector collector;

    public RequestMetricsInterceptor(RequestMetricsCollector collector) {
        this.collector = collector;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_NANOS_ATTRIBUTE, System.nanoTime());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Object start = request.getAttribute(START_TIME_NANOS_ATTRIBUTE);
        if (!(start instanceof Long)) {
            return;
        }

        long duration = System.nanoTime() - (Long) start;
        int status = response.getStatus();

        Object pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String pathPattern = pattern instanceof String ? (String) pattern : request.getRequestURI();

        collector.record(pathPattern, status, duration);
    }
}

