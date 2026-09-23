package com.example.medicalclinicproxy.service;

import com.example.medicalclinicproxy.TestDataFactory;
import com.example.medicalclinicproxy.dto.AppointmentDto;
import com.example.medicalclinicproxy.dto.AssignPatientToAppointmentCommand;
import com.example.medicalclinicproxy.dto.CreateAppointmentCommand;
import com.example.medicalclinicproxy.dto.PageDto;
import com.example.medicalclinicproxy.enums.Timeframe;
import com.example.medicalclinicproxy.exceptions.*;
import com.example.medicalclinicproxy.facade.MedicalClinicFacade;
import com.example.medicalclinicproxy.mapper.AppointmentMapper;
import com.example.medicalclinicproxy.model.Appointment;
import com.example.medicalclinicproxy.repository.AppointmentRepository;
import com.example.medicalclinicproxy.searchCriteria.AppointmentSearchCriteria;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.medicalclinicproxy.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    private MedicalClinicFacade facade;
    private AppointmentMapper mapper;
    private AppointmentRepository repository;
    private AppointmentService service;
    private List<Appointment> appointmentList;

    @BeforeEach
    void setUp() {
        this.facade = Mockito.mock(MedicalClinicFacade.class);
        this.mapper = Mappers.getMapper(AppointmentMapper.class);
        this.repository = Mockito.mock(AppointmentRepository.class);
        this.service = new AppointmentService(facade, mapper, repository);
        this.appointmentList = TestDataFactory.threeAppointments();
    }

    @Test
    void search_WhenNoCriteriaGiven_ShouldReturnPageWithAllAppointments() {
        //given
        Pageable pageable = PageRequest.of(0, 20);
        AppointmentSearchCriteria criteria = new AppointmentSearchCriteria(null, null, null, null, null, null);
        Page<Appointment> page = new PageImpl<>(appointmentList, pageable, 3);
        when(repository.findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable))).thenReturn(page);
        //When
        PageDto<AppointmentDto> result = service.search(criteria, pageable);
        //Then
        assertAll(
                () -> assertEquals(3, result.content().size()),
                () -> assertEquals(3, result.totalElements()),
                () -> assertEquals(0, result.pageNumber()),
                () -> assertEquals(appointmentList.getFirst().getId(), result.content().getFirst().id())
        );
    }

    @Test
    void search_WhenFilteringByPatient_ShouldReturnPageWithPatientSpecification() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        AppointmentSearchCriteria criteria =
                new AppointmentSearchCriteria(PATIENT_ID, null, null, null, null, null);
        Page<Appointment> page = new PageImpl<>(List.of(appointmentList.getFirst()), pageable, 1);
        when(repository.findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable)))
                .thenReturn(page);
        //When
        PageDto<AppointmentDto> result = service.search(criteria, pageable);
        //Then
        verify(repository).findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable));
        AppointmentDto first = result.content().getFirst();
        Assertions.assertAll(
                () -> assertEquals(1, result.content().size()),
                () -> assertEquals(0, result.pageNumber()),
                () -> assertEquals(20, result.pageSize()),
                () -> assertEquals(1L, result.totalElements()),
                () -> assertEquals(1, result.totalPages()),
                () -> assertEquals(PATIENT_NAME, first.patientName()),
                () -> assertEquals(DOCTOR_NAME, first.doctorName()),
                () -> assertEquals(FIRST_START, first.startDateTime())
        );
    }

    @Test
    void search_WhenFilteringByDoctorAndSpecialization_ShouldReturnPageWithBothSpecifications() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        AppointmentSearchCriteria criteria =
                new AppointmentSearchCriteria(null, DOCTOR_ID, CARDIOLOGY, null, null, null);
        Page<Appointment> page = new PageImpl<>(List.of(appointmentList.getFirst(), appointmentList.get(1)), pageable, 2);
        when(repository.findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable)))
                .thenReturn(page);
        //When
        PageDto<AppointmentDto> result = service.search(criteria, pageable);
        //Then
        Assertions.assertAll(
                () -> assertEquals(2, result.content().size()),
                () -> assertEquals(DOCTOR_NAME, result.content().getFirst().doctorName()),
                () -> assertEquals(DOCTOR_NAME, result.content().get(1).doctorName())
        );
    }

    @Test
    void search_WhenFilteringByDateRange_ShouldPassStartsBetweenSpecificationToRepository() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        LocalDateTime from = LocalDateTime.of(2030, 9, 15, 10, 30);
        LocalDateTime to = LocalDateTime.of(2030, 9, 17, 18, 30);
        AppointmentSearchCriteria criteria =
                new AppointmentSearchCriteria(null, null, null, from, to, null);
        Page<Appointment> page = new PageImpl<>(appointmentList, pageable, 3);
        when(repository.findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable)))
                .thenReturn(page);
        ArgumentCaptor<Specification<Appointment>> captor = ArgumentCaptor.captor();
        //When
        PageDto<AppointmentDto> result = service.search(criteria, pageable);
        //Then
        verify(repository).findAll(captor.capture(), eq(pageable));
        Assertions.assertAll(
                () -> assertNotNull(captor.getValue()),
                () -> assertEquals(appointmentList.size(), result.content().size())
        );
    }

    @Test
    void search_WhenFilteringByTimeframe_ShouldReturnPageWithTimeframeSpecification() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        AppointmentSearchCriteria criteria =
                new AppointmentSearchCriteria(null, null, null, null, null, Timeframe.PAST);
        Page<Appointment> page = new PageImpl<>(TestDataFactory.twoAppointmentsInThePast(), pageable, 2);
        when(repository.findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable)))
                .thenReturn(page);
        //When
        PageDto<AppointmentDto> result = service.search(criteria, pageable);
        //Then
        Assertions.assertAll(
                () -> assertEquals(2, result.content().size()),
                () -> assertTrue(result.content().getFirst().endDateTime().isBefore(LocalDateTime.now()))
        );
    }

    @Test
    void search_WhenNothingMatches_ShouldReturnEmptyPage() {
        //Given
        Pageable pageable = PageRequest.of(0, 20);
        AppointmentSearchCriteria searchCriteria = new AppointmentSearchCriteria(3L, null, CARDIOLOGY, null, null, Timeframe.ALL);
        when(repository.findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable))).thenReturn(Page.empty(pageable));
        //When
        PageDto<AppointmentDto> result = service.search(searchCriteria, pageable);
        //Then
        assertAll(
                () -> assertNotNull(result.content()),
                () -> assertTrue(result.content().isEmpty()),
                () -> assertEquals(0, result.totalElements()),
                () -> assertEquals(0, result.totalPages()),
                () -> assertEquals(0, result.pageNumber()),
                () -> assertEquals(20, result.pageSize())
        );
        verify(repository).findAll(ArgumentMatchers.<Specification<Appointment>>any(), eq(pageable));
    }


    @Test
    void findById_WhenAppointmentExists_ShouldReturnMatchingAppointmentDto() {
        //Given
        Long existingId = 1L;
        Appointment existingAppointment = appointmentList.getFirst();
        when(repository.findById(existingId)).thenReturn(Optional.of(existingAppointment));
        //When
        AppointmentDto result = service.findById(existingId);
        //Then
        Assertions.assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(DOCTOR_NAME, result.doctorName()),
                () -> assertEquals(PATIENT_NAME, result.patientName()),
                () -> assertEquals(FIRST_START, result.startDateTime()),
                () -> assertEquals(FIRST_END, result.endDateTime())
        );
        verify(repository).findById(existingId);
    }

    @Test
    void findById_WhenAppointmentDoesNotExists_ShouldThrowAppointmentDoesNotExistsException() {
        //Given
        Long id = 666L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        //When + Then
        AppointmentDoesNotExistsException exception = assertThrows(AppointmentDoesNotExistsException.class,
                () -> service.findById(id));
        assertTrue(exception.getMessage().contains("Appointment Does Not Exists"));
    }

    @Test
    void create_WhenDoctorExistsAndSlotIsFree_ShouldSaveAndReturnAppointmentDto() {
        //Given
        CreateAppointmentCommand command = new CreateAppointmentCommand(DOCTOR_ID, FIRST_START, FIRST_END);
        when(repository.findByDoctorIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(DOCTOR_ID, FIRST_END, FIRST_START))
                .thenReturn(Set.of());
        when(facade.getDoctor(DOCTOR_ID)).thenReturn(TestDataFactory.doctor());
        when(repository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment toSave = invocation.getArgument(0);
            toSave.setId(1L);
            return toSave;
        });
        //When
        AppointmentDto result = service.create(command);
        //Then
        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(repository).save(captor.capture());
        Appointment saved = captor.getValue();
        Assertions.assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(DOCTOR_NAME, result.doctorName()),
                () -> assertNull(result.patientName()),
                () -> assertEquals(FIRST_START, result.startDateTime()),
                () -> assertEquals(FIRST_END, result.endDateTime()),
                () -> assertEquals(DOCTOR_ID, saved.getDoctorId()),
                () -> assertEquals(CARDIOLOGY, saved.getDoctorSpecialisation()),
                () -> assertNull(saved.getPatientId())
        );
    }

    @Test
    void create_WhenDateIsInThePast_ShouldThrowInvalidDateOfAppointmentException() {
        //Given
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                DOCTOR_ID,
                LocalDateTime.now().minusDays(1).withMinute(30),
                LocalDateTime.now().minusDays(1).plusHours(1).withMinute(30)
        );
        //When + Then
        InvalidDateOfAppointmentException exception = assertThrows(InvalidDateOfAppointmentException.class,
                () -> service.create(command));
        assertTrue(exception.getMessage().contains("Date must be in the future"));
        verify(repository, never()).save(any(Appointment.class));
        verify(facade, never()).getDoctor(anyLong());
    }

    @Test
    void create_WhenTimeIsNotAFullQuarter_ShouldThrowInvalidTimeOfTheAppointmentException() {
        //Given
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                DOCTOR_ID,
                LocalDateTime.of(2030, 9, 15, 15, 12),
                LocalDateTime.of(2030, 9, 15, 15, 55)
        );
        //When + Then
        InvalidTimeOfTheAppointmentException exception = assertThrows(InvalidTimeOfTheAppointmentException.class,
                () -> service.create(command));
        assertTrue(exception.getMessage().contains("Invalid Time Of The Appointment"));
        verify(repository, never()).save(any(Appointment.class));
        verify(facade, never()).getDoctor(anyLong());
    }

    @Test
    void create_WhenSlotOverlapsWithAnotherAppointment_ShouldThrowTimeIsOverlappingWithAnotherAppointmentException() {
        //Given
        CreateAppointmentCommand command = new CreateAppointmentCommand(DOCTOR_ID, FIRST_START, FIRST_END);
        when(repository.findByDoctorIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(DOCTOR_ID, FIRST_END, FIRST_START))
                .thenReturn(Set.of(TestDataFactory.bookedAppointment()));
        //When + Then
        TimeIsOverlappingWithAnotherAppointmentException exception =
                assertThrows(TimeIsOverlappingWithAnotherAppointmentException.class,
                        () -> service.create(command));
        assertTrue(exception.getMessage().contains("Another appointment at this time already exists"));
        verify(repository, never()).save(any(Appointment.class));
        verify(facade, never()).getDoctor(anyLong());
    }

    @Test
    void create_WhenDoctorDoesNotExists_ShouldThrowDoctorNotFoundException() {
        //Given
        Long missingDoctorId = 666L;
        CreateAppointmentCommand command = new CreateAppointmentCommand(missingDoctorId, FIRST_START, FIRST_END);
        when(repository.findByDoctorIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(missingDoctorId, FIRST_END, FIRST_START))
                .thenReturn(Set.of());
        when(facade.getDoctor(missingDoctorId)).thenThrow(new DoctorNotFoundException(missingDoctorId));
        //When + Then
        assertThrows(DoctorNotFoundException.class, () -> service.create(command));
        verify(repository, never()).save(any(Appointment.class));
    }

    @Test
    void removePatientFromVisit_WhenAppointmentExists_ShouldClearPatientDataAndKeepDoctor() {
        //Given
        Appointment appointment = appointmentList.getFirst();
        Long appointmentId = 1L;
        when(repository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        //When
        service.removePatientFromVisit(appointmentId);
        //Then
        Assertions.assertAll(
                () -> assertNull(appointment.getPatientId()),
                () -> assertNull(appointment.getPatientName()),
                () -> assertNotNull(appointment.getDoctorId()),
                () -> assertEquals(DOCTOR_NAME, appointment.getDoctorName())
        );
    }

    @Test
    void removePatientFromVisit_WhenAppointmentDoesNotExists_ShouldThrowAppointmentDoesNotExistsException() {
        //Given
        Long id = 666L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        //When + Then
        assertThrows(AppointmentDoesNotExistsException.class,
                () -> service.removePatientFromVisit(id));
    }

    @Test
    void assignPatientToAppointment_WhenAppointmentIsFreeAndPatientExists_ShouldAssignAndReturnAppointmentDto() {
        //Given
        Long appointmentId = 1L;
        Appointment freeAppointment = TestDataFactory.freeAppointment();
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(PATIENT_ID, appointmentId);
        when(repository.findWithLockById(appointmentId)).thenReturn(Optional.of(freeAppointment));
        when(facade.getPatient(PATIENT_ID)).thenReturn(TestDataFactory.patient());
        //When
        AppointmentDto result = service.assignPatientToAppointment(command);
        //Then
        Assertions.assertAll(
                () -> assertEquals(DOCTOR_NAME, result.doctorName()),
                () -> assertEquals(PATIENT_NAME, result.patientName()),
                () -> assertEquals(FIRST_START, result.startDateTime()),
                () -> assertEquals(FIRST_END, result.endDateTime()),
                () -> assertEquals(PATIENT_ID, freeAppointment.getPatientId())
        );
    }

    @Test
    void assignPatientToAppointment_WhenAppointmentAlreadyTaken_ShouldThrowAppointmentAlreadyTakenException() {
        //Given
        Long appointmentId = 1L;
        Appointment takenAppointment = appointmentList.getFirst();
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(PATIENT_ID, appointmentId);
        when(repository.findWithLockById(appointmentId)).thenReturn(Optional.of(takenAppointment));
        //When + Then
        AppointmentAlreadyTakenException exception = assertThrows(AppointmentAlreadyTakenException.class,
                () -> service.assignPatientToAppointment(command));
        assertTrue(exception.getMessage().contains("This Appointment is already taken"));
        verify(facade, never()).getPatient(anyLong());
    }

    @Test
    void assignPatientToAppointment_WhenAppointmentDoesNotExists_ShouldThrowAppointmentDoesNotExistsException() {
        //Given
        Long appointmentId = 666L;
        AssignPatientToAppointmentCommand command = new AssignPatientToAppointmentCommand(PATIENT_ID, appointmentId);
        when(repository.findWithLockById(appointmentId)).thenReturn(Optional.empty());
        //When + Then
        assertThrows(AppointmentDoesNotExistsException.class,
                () -> service.assignPatientToAppointment(command));
        verify(facade, never()).getPatient(anyLong());
    }

    @Test
    void delete_WhenAppointmentExists_ShouldRemoveAppointment() {
        //Given
        Long existingId = 1L;
        Appointment appointment = appointmentList.getFirst();
        when(repository.findById(existingId)).thenReturn(Optional.of(appointment));
        //When
        service.delete(existingId);
        //Then
        verify(repository).delete(appointment);
    }

    @Test
    void delete_WhenAppointmentDoesNotExists_ShouldThrowAppointmentDoesNotExistsException() {
        //Given
        Long id = 666L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        //When + Then
        assertThrows(AppointmentDoesNotExistsException.class,
                () -> service.delete(id));
        verify(repository, never()).delete(any(Appointment.class));
    }
}
