package com.smartcare.backend.service;

import com.smartcare.backend.DTO.AppointmentDTO;
import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.model.Patient;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PatientService {
    private final Log log = LogFactory.getLog(this.getClass());

    private final PatientRepository patientRepository;
    private final TokenService tokenService;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;


    private static final String PAST_CONDITION = "PAST";
    private static final String FUTURE_CONDITION = "FUTURE";

    public PatientService(PatientRepository patientRepository, TokenService tokenService,
                          AppointmentRepository appointmentRepository, PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.tokenService = tokenService;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     *
     * @param patient
     * @return 1 ok
     * 0 ko
     */
    public int createPatient(Patient patient) {
        try {
            patient.setPassword(passwordEncoder.encode(patient.getPassword()));
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

        String patientEmail = tokenService.extractIdentifier(token);
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
            return condition.equalsIgnoreCase("past")
                    ? appointment.getAppointmentTime().isBefore(LocalDateTime.now())
                    : appointment.getAppointmentTime().isAfter(LocalDateTime.now());
        }).toList();

        return filteredAppointment.stream().map(AppointmentDTO::to).toList();
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctor(String doctorName, long patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Appointment> appointments = appointmentRepository.findByDoctor_NameAndPatient_Id(doctorName, patientId).orElse(new ArrayList<>());
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
        if (!checkCondition(condition)) {
            response.put("status", "error");
            response.put("message", "Invalid condition passed");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            List<Appointment> appointments = appointmentRepository.findByDoctor_NameAndPatient_Id(doctorName, patientId).orElse(new ArrayList<>());
            LocalDateTime now = LocalDateTime.now();
            List<Appointment> filteredAppointments = appointments.stream()
                    .filter(appointment -> condition.equalsIgnoreCase(PAST_CONDITION)
                            ? appointment.getAppointmentTime().isBefore(now)
                            : appointment.getAppointmentTime().isAfter(now))
                    .toList();
            response.put("status", "ok");
            response.put("data", filteredAppointments.stream().map(AppointmentDTO::to).toList());
            return new ResponseEntity<>(response, HttpStatus.OK);

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
            String patientEmail = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(patientEmail);
            if (patient == null) {
                response.put("status", "error");
                response.put("message", "No patient found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            response.put("status", "ok");
            response.put("data", patient);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
