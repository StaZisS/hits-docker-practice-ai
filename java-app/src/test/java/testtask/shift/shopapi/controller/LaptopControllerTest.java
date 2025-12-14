package testtask.shift.shopapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import testtask.shift.shopapi.error.RestExceptionHandler;
import testtask.shift.shopapi.analytics.RequestMetricsCollector;
import testtask.shift.shopapi.model.laptop.Laptop;
import testtask.shift.shopapi.model.laptop.LaptopSize;
import testtask.shift.shopapi.service.LaptopService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LaptopController.class)
@Import(RestExceptionHandler.class)
class LaptopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LaptopService laptopService;

    @MockBean
    private RequestMetricsCollector requestMetricsCollector;

    @Test
    void getLaptops_returnsJsonArray() throws Exception {
        Laptop laptop = new Laptop();
        laptop.setId(1L);
        laptop.setSeriesNumber("SN-1");
        laptop.setProducer("Lenovo");
        laptop.setPrice(new BigDecimal("1000.00"));
        laptop.setNumberOfProductsInStock(5L);
        laptop.setSize(LaptopSize.Inch13);

        when(laptopService.getAllLaptops()).thenReturn(List.of(laptop));

        mockMvc.perform(get("/api/laptops"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].seriesNumber").value("SN-1"))
                .andExpect(jsonPath("$[0].producer").value("Lenovo"));
    }

    @Test
    void getLaptop_whenMissing_returns404WithMessage() throws Exception {
        when(laptopService.getLaptop(99L)).thenThrow(new ResourceNotFoundException("Laptop not found"));

        mockMvc.perform(get("/api/laptops/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Laptop not found"))
                .andExpect(jsonPath("$.path").value("/api/laptops/99"));
    }

    @Test
    void editLaptop_setsIdFromPath() throws Exception {
        Laptop payload = new Laptop();
        payload.setSeriesNumber("SN-NEW");
        payload.setProducer("Lenovo");
        payload.setPrice(new BigDecimal("999.99"));
        payload.setNumberOfProductsInStock(1L);
        payload.setSize(LaptopSize.Inch14);

        when(laptopService.getLaptop(7L)).thenReturn(new Laptop());
        when(laptopService.save(any(Laptop.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/api/laptops/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.seriesNumber").value("SN-NEW"));

        ArgumentCaptor<Laptop> captor = ArgumentCaptor.forClass(Laptop.class);
        verify(laptopService).getLaptop(7L);
        verify(laptopService).save(captor.capture());
        verifyNoMoreInteractions(laptopService);

        assertEquals(7L, captor.getValue().getId());
    }
}
