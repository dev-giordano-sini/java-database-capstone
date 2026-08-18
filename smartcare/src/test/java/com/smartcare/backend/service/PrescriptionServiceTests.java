package com.smartcare.backend.service;

import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.model.Patient;
import com.smartcare.backend.model.Prescription;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.PrescriptionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PrescriptionServiceTests {
    @Test
    void patientCanReadPrescriptionsForOwnAppointment() {
        PrescriptionRepository prescriptions = mock(PrescriptionRepository.class);
        AppointmentRepository appointments = mock(AppointmentRepository.class);
        Appointment appointment = appointment("doctor@example.com", "patient@example.com");
        Prescription prescription = new Prescription();
        prescription.setMedication("Example medicine");
        when(appointments.findById(12L)).thenReturn(Optional.of(appointment));
        when(prescriptions.findByAppointmentId(12L)).thenReturn(List.of(prescription));

        var response = new PrescriptionService(prescriptions, appointments)
                .getPrescription(12L, "patient@example.com", "patient");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(prescription), response.getBody().get("data"));
    }

    @Test
    void doctorCanOnlyCreatePrescriptionForOwnAppointment() {
        PrescriptionRepository prescriptions = mock(PrescriptionRepository.class);
        AppointmentRepository appointments = mock(AppointmentRepository.class);
        when(appointments.findById(12L)).thenReturn(Optional.of(appointment("doctor@example.com", "patient@example.com")));
        Prescription prescription = new Prescription();
        prescription.setAppointmentId(12L);
        prescription.setMedication("Example medicine");

        var response = new PrescriptionService(prescriptions, appointments)
                .savePrescription(prescription, "other-doctor@example.com");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void creationUsesPatientNameFromAppointmentRatherThanRequest() {
        PrescriptionRepository prescriptions = mock(PrescriptionRepository.class);
        AppointmentRepository appointments = mock(AppointmentRepository.class);
        when(appointments.findById(12L)).thenReturn(Optional.of(appointment("doctor@example.com", "patient@example.com")));
        Prescription prescription = new Prescription();
        prescription.setAppointmentId(12L);
        prescription.setPatientName("Wrong name");
        prescription.setMedication("Example medicine");

        var response = new PrescriptionService(prescriptions, appointments)
                .savePrescription(prescription, "doctor@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Patient One", prescription.getPatientName());
        verify(prescriptions).save(prescription);
    }

    private Appointment appointment(String doctorEmail, String patientEmail) {
        Doctor doctor = new Doctor();
        doctor.setEmail(doctorEmail);
        Patient patient = new Patient();
        patient.setEmail(patientEmail);
        patient.setName("Patient One");
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        return appointment;
    }
}
