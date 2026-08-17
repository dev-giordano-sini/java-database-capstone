package com.smartcare.backend.service;

import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.model.MedicalReport;
import com.smartcare.backend.model.Patient;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.MedicalReportRepository;
import com.smartcare.backend.repository.PrescriptionRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MedicalReportServiceTests {
    @Test
    void reportCanReferenceOnlyPrescriptionFromTheSameAppointment() {
        MedicalReportRepository reportRepository = mock(MedicalReportRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        PrescriptionRepository prescriptionRepository = mock(PrescriptionRepository.class);
        MedicalReport report = new MedicalReport();
        report.setAppointmentId(12L);
        report.setPrescriptionId(21L);
        when(appointmentRepository.existsById(12L)).thenReturn(true);
        when(prescriptionRepository.existsByIdAndAppointmentId(21L, 12L)).thenReturn(true);
        MedicalReportService service = new MedicalReportService(
                reportRepository, appointmentRepository, prescriptionRepository);

        service.save(report);

        verify(reportRepository).save(report);
    }

    @Test
    void reportRejectsPrescriptionFromAnotherAppointment() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        PrescriptionRepository prescriptionRepository = mock(PrescriptionRepository.class);
        MedicalReport report = new MedicalReport();
        report.setAppointmentId(12L);
        report.setPrescriptionId(21L);
        when(appointmentRepository.existsById(12L)).thenReturn(true);
        when(prescriptionRepository.existsByIdAndAppointmentId(21L, 12L)).thenReturn(false);
        MedicalReportService service = new MedicalReportService(
                mock(MedicalReportRepository.class), appointmentRepository, prescriptionRepository);

        assertThrows(IllegalArgumentException.class, () -> service.save(report));
    }

    @Test
    void patientCanAccessReportsOnlyForOwnedAppointment() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        Appointment appointment = new Appointment();
        Patient patient = new Patient();
        patient.setEmail("patient@example.com");
        Doctor doctor = new Doctor();
        doctor.setEmail("doctor@example.com");
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        when(appointmentRepository.findById(12L)).thenReturn(Optional.of(appointment));
        MedicalReportService service = new MedicalReportService(
                mock(MedicalReportRepository.class), appointmentRepository,
                mock(PrescriptionRepository.class));

        assertTrue(service.canAccess(12L, "patient@example.com", "patient"));
    }
}
