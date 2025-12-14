package testtask.shift.shopapi.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import testtask.shift.shopapi.model.hdd.HardDrive;
import testtask.shift.shopapi.repository.HardDriveRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HardDriveServiceImplTest {

    private final HardDriveRepository repository = mock(HardDriveRepository.class);
    private final HardDriveService service = new HardDriveServiceImpl(repository);

    @Test
    void getAllHardDrives_delegatesToRepository() {
        service.getAllHardDrives();
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getHardDrive_returnsEntityWhenFound() {
        HardDrive hardDrive = new HardDrive("SN-1", "WD", BigDecimal.TEN, 3L, 512);
        when(repository.findById(7L)).thenReturn(Optional.of(hardDrive));

        HardDrive actual = service.getHardDrive(7L);

        assertSame(hardDrive, actual);
        verify(repository).findById(7L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getHardDrive_throwsWhenMissing() {
        when(repository.findById(7L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getHardDrive(7L));
        assertEquals("HardDrive not found", exception.getMessage());

        verify(repository).findById(7L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void save_delegatesToRepository() {
        HardDrive hardDrive = new HardDrive("SN-1", "WD", BigDecimal.TEN, 3L, 512);
        when(repository.save(hardDrive)).thenReturn(hardDrive);

        HardDrive actual = service.save(hardDrive);

        assertSame(hardDrive, actual);
        verify(repository).save(hardDrive);
        verifyNoMoreInteractions(repository);
    }
}

