package com.smartcare.backend.service;

import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.model.Prescription;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.PrescriptionRepository;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionService {
    private final Log log = LogFactory.getLog(this.getClass());

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription, String doctorEmail) {
        Map<String, String> response = new HashMap<>();

        try {
            Appointment appointment = appointmentRepository.findById(prescription.getAppointmentId()).orElse(null);
            if (appointment == null) {
                response.put("status", "error");
                response.put("message", "Appointment not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            if (!appointment.getDoctor().getEmail().equalsIgnoreCase(doctorEmail)) {
                response.put("status", "error");
                response.put("message", "This appointment belongs to another doctor");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            prescription.setPatientName(appointment.getPatient().getName());
            prescriptionRepository.save(prescription);
            response.put("status", "success");
            response.put("message", "Prescription saved");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception ex ) {
            log.error(ex.getMessage());
            response.put("error", ex.getMessage());
            response.put("status", "error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId, String identifier, String role) {
        Map<String, Object> response = new HashMap<>();

        try {
            Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
            if (appointment == null) {
                response.put("status", "error");
                response.put("message", "Appointment not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            boolean owner = "doctor".equals(role)
                    ? appointment.getDoctor().getEmail().equalsIgnoreCase(identifier)
                    : "patient".equals(role) && appointment.getPatient().getEmail().equalsIgnoreCase(identifier);
            if (!owner) {
                response.put("status", "error");
                response.put("message", "You cannot access prescriptions for this appointment");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);
            if(prescriptions == null) {
                prescriptions = new ArrayList<>();
            }
            response.put("status", "success");
            response.put("message", "");
            response.put("data", prescriptions);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception ex ) {
            log.error(ex.getMessage());
            response.put("error", ex.getMessage());
            response.put("status", "error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
