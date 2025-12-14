package testtask.shift.shopapi.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import testtask.shift.shopapi.model.pc.FormFactor;
import testtask.shift.shopapi.model.pc.PersonalComputer;
import testtask.shift.shopapi.repository.PersonalComputerRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonalComputerServiceImplTest {

    private final PersonalComputerRepository repository = mock(PersonalComputerRepository.class);
    private final PersonalComputerService service = new PersonalComputerServiceImpl(repository);

    @Test
    void getAllPersonalComputers_delegatesToRepository() {
        service.getAllPersonalComputers();
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getPersonalComputer_returnsEntityWhenFound() {
        PersonalComputer pc = new PersonalComputer("SN-1", "HP", BigDecimal.TEN, 1L, FormFactor.Desktop);
        when(repository.findById(7L)).thenReturn(Optional.of(pc));

        PersonalComputer actual = service.getPersonalComputer(7L);

        assertSame(pc, actual);
        verify(repository).findById(7L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getPersonalComputer_throwsWhenMissing() {
        when(repository.findById(7L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getPersonalComputer(7L));
        assertEquals("PC not found", exception.getMessage());

        verify(repository).findById(7L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void save_delegatesToRepository() {
        PersonalComputer pc = new PersonalComputer("SN-1", "HP", BigDecimal.TEN, 1L, FormFactor.Desktop);
        when(repository.save(pc)).thenReturn(pc);

        PersonalComputer actual = service.save(pc);

        assertSame(pc, actual);
        verify(repository).save(pc);
        verifyNoMoreInteractions(repository);
    }
}
