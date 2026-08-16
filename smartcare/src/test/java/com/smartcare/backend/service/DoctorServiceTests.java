package com.smartcare.backend.service;

import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DoctorServiceTests {
    @Test
    void availabilityUsesTheRequestedDayBoundaries() {
        DoctorRepository doctorRepository = mock(DoctorRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        TokenService tokenService = mock(TokenService.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        Doctor doctor = new Doctor();
        doctor.setAvailableTimes(List.of("09:00", "14:00"));
        LocalDate date = LocalDate.of(2026, 8, 16);

        when(doctorRepository.findById(3L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                3L, date.atStartOfDay(), date.plusDays(1).atStartOfDay()))
                .thenReturn(Optional.empty());

        DoctorService service = new DoctorService(
                doctorRepository, appointmentRepository, tokenService, passwordEncoder);

        assertEquals(List.of("09:00", "14:00"), service.getDoctorAvailability(3L, date));
    }
}
