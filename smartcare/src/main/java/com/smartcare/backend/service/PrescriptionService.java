package com.smartcare.backend.service;

import com.smartcare.backend.model.Prescription;
import com.smartcare.backend.repository.PrescriptionRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.logging.LogFactory;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionService {
    private final Log log = (Log) LogFactory.getLog(this.getClass());

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();

        try {
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

    @Transactional
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();

        try {
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
