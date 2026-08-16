package com.smartcare.backend.controller;

import com.smartcare.backend.DTO.Login;
import com.smartcare.backend.model.Patient;
import com.smartcare.backend.repository.PatientRepository;
import com.smartcare.backend.service.MyService;
import com.smartcare.backend.service.PatientService;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private PatientService patientService;

    @Autowired
    private MyService myService;

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable("token") String token){
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(token, "patient");
        if(responseService.getStatusCode() == HttpStatus.OK) {
            return patientService.getPatientDetails(token);
        }
        else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ko");
            response.put("message", "invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping()
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
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
                response.put("message", "Patient with email id or phone already exist");
            }
            case 0->{
                response.put("status", "ko");
                response.put("message", "Internal server error");
            }
            case 1->{
                response.put("status", "ok");
                response.put("message", "Signup successful");
                httpStatus = HttpStatus.OK;
            }
        }

        return new ResponseEntity<>(response, httpStatus);
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(String email, String password) {
        Login login = new Login();
        login.setIdentifier(email);
        login.setPassword(password);
        return myService.validatePatientLogin(login);
    }


    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointments(@PathVariable("id") long patientId, @PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(token, "patient");
        if(responseService.getStatusCode() == HttpStatus.OK) {
            return patientService.getPatientAppointment(patientId, token);
        }
        else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ko");
            response.put("message", "invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> getFilteredPatientAppointments(@PathVariable("condition") String condition, @PathVariable("name") String doctorName, @PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(token, "patient");

        if(responseService.getStatusCode() == HttpStatus.OK) {
            return myService.filterPatient(condition, doctorName, token);
        }
        else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ko");
            response.put("message", "invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}
