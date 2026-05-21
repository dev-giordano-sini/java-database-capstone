package com.smartcare.backend.controller;

import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.service.AppointmentService;
import com.smartcare.backend.service.MyService;
import org.apache.commons.logging.LogFactory;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final Log log = (Log) LogFactory.getLog(this.getClass());

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private MyService myService;

    @GetMapping("/{date}/{patientName}/{token}")
    public List<Object> getAppointments(@PathVariable("date") LocalDate date, @PathVariable("patientName") String patientName, @PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> validateTokenResponse = myService.validateToken(token, "doctor");

        if(validateTokenResponse.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> appointments = appointmentService.getAppointment(patientName, date, token);
            return appointments.values().stream().toList();

        }
        else {
            log.error(validateTokenResponse.getStatusCode() + ", " + validateTokenResponse.getBody().toString());
            return new ArrayList<>();
        }
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@PathVariable("token") String token, @RequestBody Appointment appointment) {
        ResponseEntity<Map<String, String>> validateTokenResponse = myService.validateToken(token, "patient");
        Map<String, String> response = new HashMap<>();
        if(validateTokenResponse.getStatusCode() == HttpStatus.OK) {
            int isValid = myService.validateAppointment(appointment);
            HttpStatus httpStatus = null;
            switch (isValid) {
                case -1-> {
                    response.put("status", "error");
                    response.put("message", "doctor not exists");
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
                case 0 -> {
                    response.put("status", "success");
                    response.put("message", "appointment validated");
                    httpStatus = HttpStatus.CREATED;
                }
                case 1 -> {
                    response.put("status", "error");
                    response.put("message", "Invalid date");
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            }

            return new ResponseEntity<>(response, httpStatus);
        }
        else {
            log.error(validateTokenResponse.getStatusCode() + ", " + validateTokenResponse.getBody().toString());
            return validateTokenResponse;
        }
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@PathVariable("token") String token, @RequestBody Appointment appointment) {
        ResponseEntity<Map<String, String>> validateTokenResponse = myService.validateToken(token, "patient");
        if(validateTokenResponse.getStatusCode() == HttpStatus.OK) {
            appointmentService.updateAppointment(appointment);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "appointment updated");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            return validateTokenResponse;
        }
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment (@PathVariable("id") long id, @PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> validateTokenResponse = myService.validateToken(token, "patient");
        if(validateTokenResponse.getStatusCode() == HttpStatus.OK) {
            appointmentService.cancelAppointment(id, token);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "appointment deleted");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            return validateTokenResponse;
        }
    }
}
