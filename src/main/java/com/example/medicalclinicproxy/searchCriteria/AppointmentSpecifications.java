package com.example.medicalclinicproxy.searchCriteria;

import com.example.medicalclinicproxy.enums.Timeframe;
import com.example.medicalclinicproxy.model.Appointment;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class AppointmentSpecifications {
    private AppointmentSpecifications() {
    }

    public static Specification<Appointment> hasPatient(Long patientId) {
        if (patientId == null) {
            return null;
        }
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("patientId"), patientId);
    }

    public static Specification<Appointment> hasDoctor(Long doctorId) {
        if (doctorId == null) {
            return null;
        }
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("doctorId"), doctorId);
    }

    public static Specification<Appointment> hasSpecialization(String specialization) {
        if (specialization == null) {
            return null;
        }
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("doctorSpecialisation"), specialization);
    }

    public static Specification<Appointment> startsBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) {
            return null;
        }
        if (from == null) {
            return (root, query, criteriaBuilder)
                    -> criteriaBuilder.lessThan(root.get("startDateTime"), to);
        }
        if (to == null) {
            return (root, query, criteriaBuilder)
                    -> criteriaBuilder.greaterThanOrEqualTo(root.get("startDateTime"), from);
        }
        return (root, query, cb) -> cb.and(
                cb.greaterThanOrEqualTo(root.get("startDateTime"), from),
                cb.lessThan(root.get("startDateTime"), to));
    }

    public static Specification<Appointment> inTimeframe(Timeframe timeframe) {
        if (timeframe == null || timeframe == Timeframe.ALL) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        if (timeframe == Timeframe.PAST) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("endDateTime"), now);
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("startDateTime"), now);
    }

    public static Specification<Appointment> isFree() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("patientId"));
    }
}
