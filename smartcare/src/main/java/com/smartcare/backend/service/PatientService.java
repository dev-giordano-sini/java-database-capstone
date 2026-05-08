package com.smartcare.backend.service;

import com.smartcare.backend.DTO.AppointmentDTO;
import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.model.Patient;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.DoctorRepository;
import com.smartcare.backend.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.logging.LogFactory;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {
    Log log = (Log) LogFactory.getLog(this.getClass());

    private final PatientRepository patientRepository;
    private final TokenService tokenService;
    private final AppointmentRepository appointmentRepository;


    private static final String PAST_CONDITION = "PAST";
    private static final String FUTURE_CONDITION = "FUTURE";

    public PatientService(PatientRepository patientRepository, TokenService tokenService, AppointmentRepository appointmentRepository) {
        this.patientRepository = patientRepository;
        this.tokenService = tokenService;
        this.appointmentRepository = appointmentRepository;
    }


    /**
     *
     * @param patient
     * @return 1 ok
     * 0 ko
     */
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage());
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long patientId, String token) {
        Map<String, Object> response = new HashMap<>();

        // retreve mail from token;
        String patientEmail = "";//tokenService.decode(token)
        Patient patient = patientRepository.findByEmail(patientEmail);

        if(patient != null && patient.getId().compareTo(patientId) == 0) {
            List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
            response.put("status", "ok");
            response.put("data", appointments.stream().map(AppointmentDTO::to).toList());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            response.put("status", "error");
        }

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long patientId) {
        Map<String, Object> response = new HashMap<>();

        if(checkCondition(condition)) {
            List<AppointmentDTO> filterAppointments = getAppointmentsByCondition(condition, patientId);

            response.put("status", "ok");
            response.put("data", filterAppointments);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            response.put("status", "error");
            response.put("message", "Invalid condition passed");
        }


        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private boolean checkCondition(String condition) {
        return condition != null && (condition.equalsIgnoreCase("past") || condition.equalsIgnoreCase("future"));
    }

    private List<AppointmentDTO> getAppointmentsByCondition(String condition, Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        List<Appointment> filteredAppointment = appointments.stream().filter(appointment -> {
            return condition.equalsIgnoreCase("past") ?
                    appointment.getAppointmentTime().isBefore(LocalDateTime.now().minusDays(1)) :
                    appointment.getAppointmentTime().isAfter(LocalDateTime.now().plusDays(1));
        }).toList();

        return filteredAppointment.stream().map(AppointmentDTO::to).toList();
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctor(String doctorName, long patientId) {
        Map<String, Object> response = new HashMap<>();

        Patient patient = patientRepository.findById(patientId).orElse(null);

        try {
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(doctorName, patientId).orElse(new ArrayList<>());
            response.put("status", "ok");
            response.put("data", appointments.stream().map(AppointmentDTO::to).toList());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String doctorName, long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(doctorName, patientId).orElse(new ArrayList<>());
            List<Appointment> filteredAppointments = appointments.stream().filter(appointment -> appointment.getDoctor().getName().equalsIgnoreCase(doctorName)).collect(Collectors.toList());
            response.put("status", "ok");
            response.put("data", filteredAppointments.stream().map(AppointmentDTO::to).toList());

        } catch (Exception e) {
            log.error(e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
        }


        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            // retreve mail from token;
            String patientEmail = "";//tokenService.decode(token)
            Patient patient = patientRepository.findByEmail(patientEmail);
            response.put("status", "ok");
            response.put("data", patient);
        } catch (Exception e) {
            log.error(e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
