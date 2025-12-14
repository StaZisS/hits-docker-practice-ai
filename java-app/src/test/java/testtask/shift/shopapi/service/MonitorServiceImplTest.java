package testtask.shift.shopapi.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import testtask.shift.shopapi.model.monitor.Monitor;
import testtask.shift.shopapi.repository.MonitorRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MonitorServiceImplTest {

    private final MonitorRepository repository = mock(MonitorRepository.class);
    private final MonitorService service = new MonitorServiceImpl(repository);

    @Test
    void getAllMonitors_delegatesToRepository() {
        service.getAllMonitors();
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getMonitor_returnsEntityWhenFound() {
        Monitor monitor = new Monitor("SN-1", "Dell", BigDecimal.TEN, 5L, 24.0);
        when(repository.findById(7L)).thenReturn(Optional.of(monitor));

        Monitor actual = service.getMonitor(7L);

        assertSame(monitor, actual);
        verify(repository).findById(7L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getMonitor_throwsWhenMissing() {
        when(repository.findById(7L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getMonitor(7L));
        assertEquals("Monitor not found", exception.getMessage());

        verify(repository).findById(7L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void save_delegatesToRepository() {
        Monitor monitor = new Monitor("SN-1", "Dell", BigDecimal.TEN, 5L, 24.0);
        when(repository.save(monitor)).thenReturn(monitor);

        Monitor actual = service.save(monitor);

        assertSame(monitor, actual);
        verify(repository).save(monitor);
        verifyNoMoreInteractions(repository);
    }
}

