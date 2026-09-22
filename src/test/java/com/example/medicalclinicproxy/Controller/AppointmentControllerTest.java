package com.example.medicalclinicproxy.Controller;

import com.example.medicalclinicproxy.TestDataFactory;
import com.example.medicalclinicproxy.dto.AppointmentDto;
import com.example.medicalclinicproxy.dto.AssignPatientToAppointmentCommand;
import com.example.medicalclinicproxy.dto.CreateAppointmentCommand;
import com.example.medicalclinicproxy.dto.PageDto;
import com.example.medicalclinicproxy.exceptions.AppointmentAlreadyTakenException;
import com.example.medicalclinicproxy.exceptions.AppointmentDoesNotExistsException;
import com.example.medicalclinicproxy.exceptions.DoctorNotFoundException;
import com.example.medicalclinicproxy.exceptions.InvalidDateOfAppointmentException;
import com.example.medicalclinicproxy.exceptions.InvalidTimeOfTheAppointmentException;
import com.example.medicalclinicproxy.exceptions.TimeIsOverlappingWithAnotherAppointmentException;
import com.example.medicalclinicproxy.searchCriteria.AppointmentSearchCriteria;
import com.example.medicalclinicproxy.service.AppointmentService;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.medicalclinicproxy.TestDataFactory.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    AppointmentService appointmentService;
    List<AppointmentDto> appointmentList;

    @BeforeEach
    void setUp() {
        this.appointmentList = TestDataFactory.threeAppointmentDtos();
    }

    @Test
    void search_WhenNoCriteriaGiven_ShouldReturnPageWithAllAppointments() throws Exception {
        //Given
        PageDto<AppointmentDto> page = new PageDto<>(appointmentList,0,20,3,1);
        when(appointmentService.search(any(AppointmentSearchCriteria.class), any(Pageable.class))).thenReturn(page);

        //When + Then
        mockMvc.perform(get("/appointments")
                .param("page", "0")
                .param("size", "20"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(3)),
                        jsonPath("$.pageNumber").value(0),
                        jsonPath("$.pageSize").value(20),
                        jsonPath("$.totalElements").value(3),
                        jsonPath("$.totalPages").value(1)
                );
        verify(appointmentService).search(any(AppointmentSearchCriteria.class), any(Pageable.class));
    }

    @Test
    void search_WhenFilteringByPatient_ShouldReturnOnlyAppointmentsOfThatPatient() throws Exception {
        //Given
        Long patientId = 1L;
        PageDto<AppointmentDto> page =
                new PageDto<>(TestDataFactory.threeAppointmentsForTheSamePatient(PATIENT_NAME), 0, 20, 3, 1);
        when(appointmentService.search(any(AppointmentSearchCriteria.class), any(Pageable.class)))
                .thenReturn(page);
        //When + Then
        mockMvc.perform(get("/appointments")
                        .param("patientId", String.valueOf(patientId))
                        .param("page", "0")
                        .param("size", "20"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content", hasSize(3)),
                        jsonPath("$.pageNumber").value(0),
                        jsonPath("$.pageSize").value(20),
                        jsonPath("$.totalElements").value(3),
                        jsonPath("$.totalPages").value(1),
                        jsonPath("$.content[0].patientName").value(PATIENT_NAME)
                );
        ArgumentCaptor<AppointmentSearchCriteria> criteriaCaptor = ArgumentCaptor.captor();
        verify(appointmentService).search(criteriaCaptor.capture(), any(Pageable.class));
        AppointmentSearchCriteria boundCriteria = criteriaCaptor.getValue();
        assertEquals(patientId, boundCriteria.patientId());
        assertNull(boundCriteria.doctorId());
        assertNull(boundCriteria.specialization());
    }

    @Test
    void search_WhenFilteringByDoctorAndSpecialization_ShouldReturnMatchingAppointments() throws Exception {
        //Given
        PageDto<AppointmentDto> page = new PageDto<>(appointmentList.subList(0,2), 0, 20, 2, 1);
        when(appointmentService.search(any(AppointmentSearchCriteria.class), any(Pageable.class))).thenReturn(page);
        //When + Then
        mockMvc.perform(get("/appointments")
                .param("doctorId", String.valueOf(TestDataFactory.DOCTOR_ID))
                .param("specialization",CARDIOLOGY)
                .param("page", "0")
                .param("size", "20"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.totalElements").value(2),
                        jsonPath("$.totalPages").value(1),
                        jsonPath("$.content[0].doctorName").value(DOCTOR_NAME),
                        jsonPath("$.content[1].doctorName").value(DOCTOR_NAME)
                );
        ArgumentCaptor<AppointmentSearchCriteria> captor = ArgumentCaptor.captor();
        verify(appointmentService).search(captor.capture(),any(Pageable.class));
        assertEquals(DOCTOR_ID,captor.getValue().doctorId());
        assertEquals(CARDIOLOGY,captor.getValue().specialization());
    }

    @Test
    void findById_WhenAppointmentExists_ShouldReturn200WithAppointmentDto() throws Exception {
        //Given
        Long existingId = 1L;
        AppointmentDto appointmentDto = appointmentList.getFirst();
        when(appointmentService.findById(existingId)).thenReturn(appointmentDto);
        //When + Then
        mockMvc.perform(get("/appointments/{id}", existingId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.startDateTime").value("2030-09-15T15:30:00"),
                        jsonPath("$.endDateTime").value("2030-09-15T16:15:00"),
                        jsonPath("$.doctorName").value(DOCTOR_NAME),
                        jsonPath("$.patientName").value(PATIENT_NAME)
                );
    }

    @Test
    void findById_WhenAppointmentDoesNotExists_ShouldReturn404() throws Exception {
        //Given
        Long id = 666L;
        when(appointmentService.findById(id)).thenThrow(new AppointmentDoesNotExistsException());
        //When + Then
        mockMvc.perform(get("/appointments/{id}", id))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("Appointment Does Not Exists"),
                        jsonPath("$.code").value(404)
                );
    }


    @Test
    void create_WhenDoctorExists_ShouldCreateAndReturn201() throws Exception {
        //Given
        CreateAppointmentCommand command = new CreateAppointmentCommand(1L, FIRST_START, FIRST_END);
        AppointmentDto appointmentDto = new AppointmentDto(1L, FIRST_START, FIRST_END, DOCTOR_NAME, null);
        when(appointmentService.create(command)).thenReturn(appointmentDto);
        //When + Then
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.patientName").isEmpty(),
                        jsonPath("$.doctorName").value(DOCTOR_NAME)
                );
    }

    @Test
    void create_WhenAppointmentInThePast_ShouldReturn400() throws Exception {
        //Given
        CreateAppointmentCommand wrongDate = new CreateAppointmentCommand(
                1L,
                LocalDateTime.of(2020, 1, 3, 13, 30),
                LocalDateTime.of(2020, 1, 3, 14, 15)
        );
        when(appointmentService.create(wrongDate)).thenThrow(new InvalidDateOfAppointmentException());
        //When + Then
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongDate)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Date must be in the future"),
                        jsonPath("$.code").value(400)
                );
    }

    @Test
    void create_WhenAppointmentTimeIsNotFullQuarter_ShouldReturn400() throws Exception {
        //Given
        CreateAppointmentCommand wrongTime = new CreateAppointmentCommand(
                1L,
                LocalDateTime.of(2030, 9, 3, 13, 12),
                LocalDateTime.of(2030, 9, 3, 14, 15)
        );
        when(appointmentService.create(wrongTime)).thenThrow(new InvalidTimeOfTheAppointmentException());
        //When + Then
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongTime)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("Invalid Time Of The Appointment"),
                        jsonPath("$.code").value(400)
                );
    }

    @Test
    void create_WhenDoctorNotFound_ShouldReturn404() throws Exception {
        //Given
        Long missingDoctorId = 666L;
        CreateAppointmentCommand command = new CreateAppointmentCommand(missingDoctorId, FIRST_START, FIRST_END);
        when(appointmentService.create(command)).thenThrow(new DoctorNotFoundException(missingDoctorId));
        //When + Then
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("Doctor with id: 666 not found"),
                        jsonPath("$.code").value(404)
                );
    }

    @Test
    void create_WhenAppointmentOverlaps_ShouldReturn409() throws Exception {
        //Given
        CreateAppointmentCommand command = new CreateAppointmentCommand(1L, FIRST_START, FIRST_END);
        when(appointmentService.create(command)).thenThrow(new TimeIsOverlappingWithAnotherAppointmentException());
        //When + Then
        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.message").value("Another appointment at this time already exists"),
                        jsonPath("$.code").value(409)
                );
    }


    @Test
    void assignPatientToAppointment_WhenAppointmentAndPatientExists_ShouldReturn200() throws Exception {
        //Given
        Long existingPatientId = 1L;
        Long existingAppointmentId = 2L;
        AssignPatientToAppointmentCommand command =
                new AssignPatientToAppointmentCommand(existingPatientId, existingAppointmentId);
        AppointmentDto result = new AppointmentDto(1L, FIRST_START, FIRST_END, DOCTOR_NAME, PATIENT_NAME);
        when(appointmentService.assignPatientToAppointment(command)).thenReturn(result);
        //When + Then
        mockMvc.perform(put("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.patientName").value(PATIENT_NAME)
                );
    }

    @Test
    void assignPatientToAppointment_WhenAppointmentAlreadyTaken_ShouldReturn409() throws Exception {
        //Given
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(1L, 2L);
        when(appointmentService.assignPatientToAppointment(command))
                .thenThrow(new AppointmentAlreadyTakenException());
        //When + Then
        mockMvc.perform(put("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.message").value("This Appointment is already taken"),
                        jsonPath("$.code").value(409)
                );
    }

    @Test
    void assignPatientToAppointment_WhenAppointmentDoesNotExists_ShouldReturn404() throws Exception {
        //Given
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(1L, 666L);
        when(appointmentService.assignPatientToAppointment(command))
                .thenThrow(new AppointmentDoesNotExistsException());
        //When + Then
        mockMvc.perform(put("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value("Appointment Does Not Exists"),
                        jsonPath("$.code").value(404)
                );
    }


    @Test
    void patientAppointmentCancel_WhenPatientAndAppointmentExists_ShouldReturn204() throws Exception {
        //Given
        Long appointmentId = 1L;
        //When + Then
        mockMvc.perform(patch("/appointments/cancel/{appointmentId}", appointmentId))
                .andExpect(status().isNoContent());
        verify(appointmentService).removePatientFromVisit(appointmentId);
    }

    @Test
    void patientAppointmentCancel_WhenAppointmentDoesNotExists_ShouldReturn404() throws Exception {
        //Given
        Long appointmentId = 666L;
        doThrow(new AppointmentDoesNotExistsException())
                .when(appointmentService)
                .removePatientFromVisit(appointmentId);
        //When + Then
        mockMvc.perform(patch("/appointments/cancel/{appointmentId}", appointmentId))
                .andExpect(status().isNotFound());
        verify(appointmentService).removePatientFromVisit(appointmentId);
    }


    @Test
    void delete_WhenAppointmentExists_ShouldDeleteAppointmentAndReturn204() throws Exception {
        //Given
        Long appointmentId = 1L;
        //When + Then
        mockMvc.perform(delete("/appointments/{id}", appointmentId))
                .andExpect(status().isNoContent());
        verify(appointmentService).delete(appointmentId);
    }

    @Test
    void delete_WhenAppointmentDoesNotExists_ShouldReturnAppointmentDoesNotExistsAnd404() throws Exception {
        //Given
        Long appointmentId = 999L;
        doThrow(new AppointmentDoesNotExistsException())
                .when(appointmentService)
                .delete(appointmentId);
        //When + Then
        mockMvc.perform(delete("/appointments/{id}", appointmentId))
                .andExpect(status().isNotFound());
        verify(appointmentService).delete(appointmentId);
    }
}
