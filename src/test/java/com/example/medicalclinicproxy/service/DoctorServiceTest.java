package com.example.medicalclinicproxy.service;

import com.example.medicalclinicproxy.TestDataFactory;
import com.example.medicalclinicproxy.dto.DoctorSummaryDto;
import com.example.medicalclinicproxy.dto.PageDto;
import com.example.medicalclinicproxy.facade.MedicalClinicFacade;
import com.example.medicalclinicproxy.searchCriteria.DoctorSearchCriteria;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.example.medicalclinicproxy.TestDataFactory.CARDIOLOGY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DoctorServiceTest {

    private MedicalClinicFacade facade;
    private DoctorService service;

    @BeforeEach
    void setUp() {
        this.facade = Mockito.mock(MedicalClinicFacade.class);
        this.service = new DoctorService(facade);
    }

    @Test
    void search_WhenCriteriaHasSpeciality_ShouldPassItToFacadeAndReturnItsPage() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        DoctorSearchCriteria criteria = new DoctorSearchCriteria(CARDIOLOGY);
        PageDto<DoctorSummaryDto> expected =
                new PageDto<>(List.of(TestDataFactory.doctorSummary()), 0, 20, 1, 1);
        when(facade.getDoctorsFromSpecificSpeciality(CARDIOLOGY, pageable)).thenReturn(expected);
        //When
        PageDto<DoctorSummaryDto> result = service.search(criteria, pageable);
        //Then
        verify(facade).getDoctorsFromSpecificSpeciality(CARDIOLOGY, pageable);
        Assertions.assertAll(
                () -> assertEquals(1, result.content().size()),
                () -> assertEquals(CARDIOLOGY, result.content().getFirst().medicalSpecialty()),
                () -> assertEquals("Anna", result.content().getFirst().firstName())
        );
    }

}
