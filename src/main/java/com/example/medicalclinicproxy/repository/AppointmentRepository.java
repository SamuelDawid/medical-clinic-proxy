package com.example.medicalclinicproxy.repository;

import com.example.medicalclinicproxy.model.Appointment;
import feign.Param;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> , JpaSpecificationExecutor<Appointment> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(value = {@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")})
    Optional<Appointment> findWithLockById(Long id);
    Set<Appointment> findByDoctorIdAndStartDateTimeLessThanAndEndDateTimeGreaterThan(Long doctorId,
            LocalDateTime newEnd,
            LocalDateTime newStart
    );
}
