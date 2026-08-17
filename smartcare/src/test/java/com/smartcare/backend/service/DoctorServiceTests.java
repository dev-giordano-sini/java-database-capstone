package com.smartcare.backend.service;

import com.smartcare.backend.DTO.DoctorProfileUpdate;
import com.smartcare.backend.DTO.DoctorResponse;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.DoctorRepository;
import com.smartcare.backend.repository.PrescriptionRepository;
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
    void doctorDirectoryReturnsSerializableResponsesWithoutPasswords() {
        DoctorRepository doctorRepository = mock(DoctorRepository.class);
        Doctor doctor = new Doctor();
        doctor.setId(7L);
        doctor.setName("Dr Alice Smith");
        doctor.setSpecialty("Cardiology");
        doctor.setEmail("alice@example.com");
        doctor.setPassword("must-not-be-exposed");
        doctor.setProfileImageUrl("https://cdn.example.com/alice.webp");
        doctor.setAvailableTimes(List.of("09:00", "14:00"));
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        DoctorService service = new DoctorService(
                doctorRepository,
                mock(AppointmentRepository.class),
                mock(TokenService.class),
                mock(PasswordEncoder.class),
                mock(PrescriptionRepository.class));

        List<DoctorResponse> directory = service.getDoctors();

        assertEquals(1, directory.size());
        assertEquals(7L, directory.getFirst().id());
        assertEquals("https://cdn.example.com/alice.webp", directory.getFirst().profileImageUrl());
        assertEquals(List.of("09:00", "14:00"), directory.getFirst().availableTimes());
    }

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
                .thenReturn(List.of());

        DoctorService service = new DoctorService(
                doctorRepository, appointmentRepository, tokenService, passwordEncoder,
                mock(PrescriptionRepository.class));

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
                .thenReturn(List.of(doctor));
        DoctorService service = new DoctorService(
                doctorRepository,
                appointmentRepository,
                mock(TokenService.class),
                mock(PasswordEncoder.class),
                mock(PrescriptionRepository.class));

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
                mock(PasswordEncoder.class),
                mock(PrescriptionRepository.class));

        Doctor updated = service.updateOwnProfile("doctor@example.com",
                new DoctorProfileUpdate("Cardiology", "1234567890", "https://cdn.example.com/doctor.webp", List.of("14:00", "09:00", "09:00")));

        assertEquals("Cardiology", updated.getSpecialty());
        assertEquals("https://cdn.example.com/doctor.webp", updated.getProfileImageUrl());
        assertEquals(List.of("09:00", "14:00"), updated.getAvailableTimes());
        verify(doctorRepository).save(doctor);
    }

    @Test
    void doctorNameSearchUsesCaseInsensitiveContainsQuery() {
        DoctorRepository doctorRepository = mock(DoctorRepository.class);
        Doctor doctor = new Doctor();
        doctor.setName("Dr Alice Smith");
        when(doctorRepository.findByNameContainingIgnoreCase("alice")).thenReturn(List.of(doctor));
        DoctorService service = new DoctorService(
                doctorRepository,
                mock(AppointmentRepository.class),
                mock(TokenService.class),
                mock(PasswordEncoder.class),
                mock(PrescriptionRepository.class));

        Map<String, Object> result = service.findDoctorByName("alice");

        assertEquals(doctor, result.get("Dr Alice Smith"));
        verify(doctorRepository).findByNameContainingIgnoreCase("alice");
    }
}
