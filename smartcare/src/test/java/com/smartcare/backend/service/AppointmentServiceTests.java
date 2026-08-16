package com.smartcare.backend.service;

import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppointmentServiceTests {
    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        appointmentRepository = mock(AppointmentRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        appointmentService = new AppointmentService(appointmentRepository, doctorRepository);
    }

    @Test
    void updateAppointmentReturnsNotFoundWhenIdDoesNotExist() {
        Appointment appointment = new Appointment();
        appointment.setId(42L);
        when(appointmentRepository.existsById(42L)).thenReturn(false);

        ResponseEntity<Map<String, String>> response = appointmentService.updateAppointment(appointment);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("ko", response.getBody().get("status"));
        verify(appointmentRepository, never()).save(appointment);
    }

    @Test
    void blankPatientNameLoadsAllAppointmentsForTheDay() {
        LocalDate date = LocalDate.of(2026, 8, 16);
        Doctor doctor = new Doctor();
        doctor.setId(7L);
        doctor.setName("Dr Test");
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        when(appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                7L, date.atStartOfDay(), date.plusDays(1).atStartOfDay()))
                .thenReturn(Optional.empty());

        assertEquals(Map.of(), appointmentService.getAppointment(" ", date));

        verify(appointmentRepository).findByDoctorIdAndAppointmentTimeBetween(
                7L, date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }
}
