package com.smartcare.backend.controller;

import com.smartcare.backend.DTO.Login;
import com.smartcare.backend.model.Patient;
import com.smartcare.backend.repository.PatientRepository;
import com.smartcare.backend.service.MyService;
import com.smartcare.backend.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}patients")
public class PatientController {

    private final PatientService patientService;
    private final MyService myService;
    private final PatientRepository patientRepository;

    public PatientController(PatientService patientService, MyService myService,
                             PatientRepository patientRepository) {
        this.patientService = patientService;
        this.myService = myService;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getPatient(
            @RequestHeader("Authorization") String authorization) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(authorization, "patient");
        if(responseService.getStatusCode() == HttpStatus.OK) {
            return patientService.getPatientDetails(authorization);
        }
        else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ko");
            response.put("message", "invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@Valid @RequestBody Patient patient) {
        int status = -1;
        Patient patientFromDb = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        //if not exists!
        if(patientFromDb == null) {
            status = patientService.createPatient(patient);
        }
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, String> response = new HashMap<>();
        switch (status) {
            case -1 -> {
                response.put("status", "ko");
                response.put("message", "Patient with email or phone already exists");
                httpStatus = HttpStatus.CONFLICT;
            }
            case 0->{
                response.put("status", "ko");
                response.put("message", "Internal server error");
            }
            case 1->{
                response.put("status", "ok");
                response.put("message", "Signup successful");
                httpStatus = HttpStatus.CREATED;
            }
        }

        return new ResponseEntity<>(response, httpStatus);
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(@Valid @RequestBody Login login) {
        return myService.validatePatientLogin(login);
    }


    @GetMapping("/me/appointments")
    public ResponseEntity<Map<String, Object>> getPatientAppointments(
            @RequestHeader("Authorization") String authorization) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(authorization, "patient");
        if(responseService.getStatusCode() == HttpStatus.OK) {
            return patientService.getPatientAppointments(authorization);
        }
        else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ko");
            response.put("message", "invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/me/appointments/filter")
    public ResponseEntity<Map<String, Object>> getFilteredPatientAppointments(
            @RequestParam String condition,
            @RequestParam String doctorName,
            @RequestHeader("Authorization") String authorization) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(authorization, "patient");

        if(responseService.getStatusCode() == HttpStatus.OK) {
            return myService.filterPatient(condition, doctorName, authorization);
        }
        else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ko");
            response.put("message", "invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}
