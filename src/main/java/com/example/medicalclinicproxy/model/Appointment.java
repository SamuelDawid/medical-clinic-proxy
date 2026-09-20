package com.example.medicalclinicproxy.model;

import com.example.medicalclinicproxy.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Long patientId;
    @Column(nullable = false)
    private Long doctorId;
    private String doctorName;
    private String patientName;
    private String doctorSpecialisation;

    public String fullName(UserDto userDto) {
        return userDto.firstName() + " " + userDto.lastName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Appointment other)) {
            return false;
        }
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
