package com.smartcare.backend.service;

import com.smartcare.backend.DTO.DoctorProfileUpdate;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

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

    @Test
    void doctorFilterGroupsDoctorsByMatchingPeriodSlots() {
        DoctorRepository doctorRepository = mock(DoctorRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        Doctor doctor = new Doctor();
        doctor.setName("Dr Test");
        doctor.setAvailableTimes(List.of("09:00", "14:00"));
        when(doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase("Test", "Cardiology"))
                .thenReturn(Optional.of(List.of(doctor)));
        DoctorService service = new DoctorService(
                doctorRepository,
                appointmentRepository,
                mock(TokenService.class),
                mock(PasswordEncoder.class));

        Map<String, Object> result = service.filterDoctorsByNameSpecilityandTime(
                "Test", "Cardiology", "AM");

        assertEquals(List.of(doctor), result.get("09:00"));
        assertNull(result.get("14:00"));
    }

    @Test
    void doctorCanUpdateAndDeduplicateOwnAvailability() {
        DoctorRepository doctorRepository = mock(DoctorRepository.class);
        Doctor doctor = new Doctor();
        when(doctorRepository.findByEmail("doctor@example.com")).thenReturn(doctor);
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        DoctorService service = new DoctorService(
                doctorRepository,
                mock(AppointmentRepository.class),
                mock(TokenService.class),
                mock(PasswordEncoder.class));

        Doctor updated = service.updateOwnProfile("doctor@example.com",
                new DoctorProfileUpdate("Cardiology", "1234567890", List.of("14:00", "09:00", "09:00")));

        assertEquals("Cardiology", updated.getSpecialty());
        assertEquals(List.of("09:00", "14:00"), updated.getAvailableTimes());
        verify(doctorRepository).save(doctor);
    }
}
