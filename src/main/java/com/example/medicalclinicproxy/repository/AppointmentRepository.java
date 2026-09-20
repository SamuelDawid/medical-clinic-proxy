package com.example.medicalclinicproxy.repository;

import com.example.medicalclinicproxy.model.Appointment;
import feign.Param;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findAllByPatientId(Long id, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(value = {@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")})
    Optional<Appointment> findWithLockById(Long id);
    Page<Appointment> findAllByDoctorId(Long id,Pageable pageable);
    Set<Appointment> findByDoctorIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(Long doctorId,
            LocalDateTime newEnd,
            LocalDateTime newStart
    );
    @Query("""
        select a from Appointment a
        where a.doctorSpecialisation = :specialization
          and a.patientId is null
          and a.startDateTime >= :from
          and a.startDateTime <  :to
        order by a.startDateTime
        """)
    Page<Appointment> findAvailableSlotsBySpecialization(
        @Param("specialization") String specialization,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}
