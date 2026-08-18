package com.smartcare.backend.service;

import com.smartcare.backend.model.MedicalReport;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.MedicalReportRepository;
import com.smartcare.backend.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class MedicalReportService {
    private final MedicalReportRepository medicalReportRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;

    public MedicalReportService(MedicalReportRepository medicalReportRepository,
                                AppointmentRepository appointmentRepository,
                                PrescriptionRepository prescriptionRepository) {
        this.medicalReportRepository = medicalReportRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public MedicalReport save(MedicalReport report) {
        if (!appointmentRepository.existsById(report.getAppointmentId())) {
            throw new IllegalArgumentException("Appointment not found");
        }
        if (report.getPrescriptionId() != null
                && !prescriptionRepository.existsByIdAndAppointmentId(
                        report.getPrescriptionId(), report.getAppointmentId())) {
            throw new IllegalArgumentException("Prescription does not belong to appointment");
        }
        return medicalReportRepository.save(report);
    }

    public List<MedicalReport> findByAppointment(Long appointmentId) {
        return medicalReportRepository.findByAppointmentIdOrderByReportDateDesc(appointmentId);
    }

    @Transactional
    public boolean canAccess(Long appointmentId, String identifier, String role) {
        return appointmentRepository.findById(appointmentId)
                .map(appointment -> switch (role) {
                    case "doctor" -> appointment.getDoctor().getEmail().equalsIgnoreCase(identifier);
                    case "patient" -> appointment.getPatient().getEmail().equalsIgnoreCase(identifier);
                    default -> false;
                })
                .orElse(false);
    }
}
