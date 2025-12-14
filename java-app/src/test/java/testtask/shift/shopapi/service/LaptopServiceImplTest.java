package testtask.shift.shopapi.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import testtask.shift.shopapi.model.laptop.Laptop;
import testtask.shift.shopapi.model.laptop.LaptopSize;
import testtask.shift.shopapi.repository.LaptopRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LaptopServiceImplTest {

    private final LaptopRepository repository = mock(LaptopRepository.class);
    private final LaptopService service = new LaptopServiceImpl(repository);

    @Test
    void getAllLaptops_delegatesToRepository() {
        service.getAllLaptops();
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getLaptop_returnsEntityWhenFound() {
        Laptop laptop = new Laptop("SN-1", "Lenovo", BigDecimal.TEN, 2L, LaptopSize.Inch13);
        when(repository.findById(7L)).thenReturn(Optional.of(laptop));

        Laptop actual = service.getLaptop(7L);

        assertSame(laptop, actual);
        verify(repository).findById(7L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getLaptop_throwsWhenMissing() {
        when(repository.findById(7L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getLaptop(7L));
        assertEquals("Laptop not found", exception.getMessage());

        verify(repository).findById(7L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void save_delegatesToRepository() {
        Laptop laptop = new Laptop("SN-1", "Lenovo", BigDecimal.TEN, 2L, LaptopSize.Inch13);
        when(repository.save(laptop)).thenReturn(laptop);

        Laptop actual = service.save(laptop);

        assertSame(laptop, actual);
        verify(repository).save(laptop);
        verifyNoMoreInteractions(repository);
    }
}
