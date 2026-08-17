package com.smartcare.backend.service;

import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.model.Patient;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.DoctorRepository;
import com.smartcare.backend.repository.PatientRepository;
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
        PatientRepository patientRepository = mock(PatientRepository.class);
        appointmentService = new AppointmentService(
                appointmentRepository, doctorRepository, patientRepository);
    }

    @Test
    void updateAppointmentReturnsNotFoundWhenIdDoesNotExist() {
        Appointment appointment = new Appointment();
        appointment.setId(42L);
        when(appointmentRepository.existsById(42L)).thenReturn(false);

        ResponseEntity<Map<String, String>> response = appointmentService.updateAppointment(
                appointment, "patient@example.com");

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

    @Test
    void patientCannotUpdateAnotherPatientsAppointment() {
        Patient owner = new Patient();
        owner.setEmail("owner@example.com");
        Appointment saved = new Appointment();
        saved.setId(9L);
        saved.setPatient(owner);
        Appointment update = new Appointment();
        update.setId(9L);
        when(appointmentRepository.findById(9L)).thenReturn(Optional.of(saved));

        ResponseEntity<Map<String, String>> response = appointmentService.updateAppointment(
                update, "attacker@example.com");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(appointmentRepository, never()).save(update);
    }

    @Test
    void monthlyStatisticsMapRepositoryAggregation() {
        when(appointmentRepository.countAppointmentsByMonth()).thenReturn(List.of(
                new Object[]{2026, 7, 3L},
                new Object[]{2026, 8, 5L}
        ));

        var statistics = appointmentService.getMonthlyStatistics();

        assertEquals(2, statistics.size());
        assertEquals(2026, statistics.get(0).year());
        assertEquals(7, statistics.get(0).month());
        assertEquals(3L, statistics.get(0).appointments());
        assertEquals(5L, statistics.get(1).appointments());
    }
}
