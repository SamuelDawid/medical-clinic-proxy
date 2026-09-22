package com.example.medicalclinicproxy.Controller;

import com.example.medicalclinicproxy.TestDataFactory;
import com.example.medicalclinicproxy.dto.DoctorSummaryDto;
import com.example.medicalclinicproxy.dto.PageDto;
import com.example.medicalclinicproxy.searchCriteria.DoctorSearchCriteria;
import com.example.medicalclinicproxy.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.medicalclinicproxy.TestDataFactory.CARDIOLOGY;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
class DoctorControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    DoctorService doctorService;

    @Test
    void search_WhenDoctorsWithThatSpecialityExists_ShouldReturn200WithPageOfDoctors() throws Exception {
        //Given
        PageDto<DoctorSummaryDto> page = new PageDto<>(List.of(TestDataFactory.doctorSummary()), 0, 20, 1, 1);
        when(doctorService.search(any(DoctorSearchCriteria.class), any(Pageable.class))).thenReturn(page);
        //When + Then
        mockMvc.perform(get("/doctors").param("speciality", CARDIOLOGY))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(1)),
                        jsonPath("$.content[0].firstName").value("Anna"),
                        jsonPath("$.content[0].lastName").value("Kowalska"),
                        jsonPath("$.content[0].medicalSpecialty").value(CARDIOLOGY),
                        jsonPath("$.totalElements").value(1)
                );
        ArgumentCaptor<DoctorSearchCriteria> criteriaCaptor = ArgumentCaptor.captor();
        verify(doctorService).search(criteriaCaptor.capture(), any(Pageable.class));
        assertEquals(CARDIOLOGY, criteriaCaptor.getValue().speciality());
    }

}
