package com.smartcare.backend.service;

import com.smartcare.backend.DTO.DoctorProfileUpdate;
import com.smartcare.backend.DTO.DoctorPageResponse;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

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
        doctor.setApproved(true);
        doctor.setProfileImageUrl("/assets/images/alice_smith.svg");
        doctor.setAvailableTimes(List.of("09:00", "14:00"));
        when(doctorRepository.findByApprovedTrue(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(doctor), PageRequest.of(0, 5), 1));
        DoctorService service = new DoctorService(
                doctorRepository,
                mock(AppointmentRepository.class),
                mock(TokenService.class),
                mock(PasswordEncoder.class));

        DoctorPageResponse directory = service.getDoctors(0, 5, null, false);

        assertEquals(1, directory.totalElements());
        assertEquals(7L, directory.data().getFirst().id());
        assertEquals("/assets/images/alice_smith.svg", directory.data().getFirst().profileImageUrl());
        assertEquals(true, directory.data().getFirst().approved());
        assertEquals(List.of("09:00", "14:00"), directory.data().getFirst().availableTimes());
    }

    @Test
    void availabilityUsesTheRequestedDayBoundaries() {
        DoctorRepository doctorRepository = mock(DoctorRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        TokenService tokenService = mock(TokenService.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        Doctor doctor = new Doctor();
        doctor.setApproved(true);
        doctor.setAvailableTimes(List.of("09:00", "14:00"));
        LocalDate date = LocalDate.of(2026, 8, 16);

        when(doctorRepository.findById(3L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                3L, date.atStartOfDay(), date.plusDays(1).atStartOfDay()))
                .thenReturn(List.of());

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
                .thenReturn(List.of(doctor));
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
                mock(PasswordEncoder.class));

        Map<String, Object> result = service.findDoctorByName("alice");

        assertEquals(doctor, result.get("Dr Alice Smith"));
        verify(doctorRepository).findByNameContainingIgnoreCase("alice");
    }
}
