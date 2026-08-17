package com.smartcare.backend.service;

import com.smartcare.backend.DTO.Login;
import com.smartcare.backend.model.Admin;
import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.model.Patient;
import com.smartcare.backend.repository.AdminRepository;
import com.smartcare.backend.repository.DoctorRepository;
import com.smartcare.backend.repository.PatientRepository;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class MyService {
    private final Log log = LogFactory.getLog(this.getClass());

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final PasswordEncoder passwordEncoder;


    public MyService(TokenService tokenService, AdminRepository adminRepository, DoctorRepository doctorRepository,
                     PatientRepository patientRepository, DoctorService doctorService, PatientService patientService,
                     PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.passwordEncoder = passwordEncoder;
    }


    public ResponseEntity<Map<String, String>> validateToken(String token, String... allowedRoles) {
        Map<String, String> response = new HashMap<>();

        boolean valid = Arrays.stream(allowedRoles).anyMatch(role -> tokenService.validateToken(token, role));
        if(valid) {
            response.put("status", "success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            response.put("status", "error");
            response.put("message", "Invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        Admin adminFromDb = adminRepository.findByUsername(receivedAdmin.getUsername());

        if(adminFromDb != null && (adminFromDb.getUsername().equals(receivedAdmin.getUsername()) &&
                passwordEncoder.matches(receivedAdmin.getPassword(), adminFromDb.getPassword()))) {
            response.put("status", "success");
            response.put("token", tokenService.generateToken(receivedAdmin.getUsername()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            response.put("status", "error");
            response.put("message", "invalid admin credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        Map<String, Object> response = new HashMap<>();
        return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
    }

    /**
     *
     * @param appointment
     * @return 1 if the appointment time is valid
     * 0 if the time is unavailable
     * -1 if the doctor doesn't exist
     */
    public int validateAppointment(Appointment appointment) {
        Long id = appointment.getDoctor().getId();
        Doctor doctor = doctorRepository.findById(id).orElse(null);
        if(doctor == null) {
            return -1;
        }

        List<String> doctorSlots = doctorService.getDoctorAvailability(id, appointment.getAppointmentTime().toLocalDate());

        String requestedTime = appointment.getAppointmentTime().toLocalTime().toString();
        if(doctorSlots.contains(requestedTime)) {
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     *
     * @param patient
     * @return true if the patient does not exist
     * false if the patient exists already
     */
    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()) == null;
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Patient patient = patientRepository.findByEmail(login.getIdentifier());
        Map<String, String> response = new HashMap<>();
        if(patient != null && patient.getEmail().equals(login.getIdentifier())
                && passwordEncoder.matches(login.getPassword(), patient.getPassword())) {
            response.put("status", "success");
            response.put("token", tokenService.generateToken(login.getIdentifier()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            response.put("status", "error");
            response.put("message", "Invalid username or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

    }

    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String doctorName, String token) {
        Map<String, Object> response = new HashMap<>();
        //patientService methods like filterByCondition(), filterByDoctor(), or filterByDoctorAndCondition()

        Map<String, String> dataFromToken = tokenService.decodeToken(token);
        String patientEmail = dataFromToken.get("identifier");

        Patient patient = patientRepository.findByEmail(patientEmail);
        if(patient != null) {
            return patientService.filterByDoctorAndCondition(condition, doctorName, patient.getId());
        }
        else {
            response.put("status", "error");
            response.put("message", "No patient found");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
