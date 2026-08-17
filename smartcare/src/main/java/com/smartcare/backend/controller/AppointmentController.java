package com.smartcare.backend.controller;

import com.smartcare.backend.DTO.AppointmentDTO;
import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.service.AppointmentService;
import com.smartcare.backend.service.MyService;
import com.smartcare.backend.service.TokenService;
import jakarta.validation.Valid;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}appointments")
public class AppointmentController {
    private final Log log = LogFactory.getLog(this.getClass());

    private final AppointmentService appointmentService;
    private final MyService myService;
    private final TokenService tokenService;

    public AppointmentController(AppointmentService appointmentService, MyService myService,
                                 TokenService tokenService) {
        this.appointmentService = appointmentService;
        this.myService = myService;
        this.tokenService = tokenService;
    }

    @GetMapping
    public ResponseEntity<?> getAppointments(@RequestParam LocalDate date,
                                             @RequestParam(required = false) String patientName,
                                             @RequestHeader("Authorization") String authorization) {
        ResponseEntity<Map<String, String>> validateTokenResponse = myService.validateToken(authorization, "doctor");

        if(validateTokenResponse.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> appointments = appointmentService.getAppointment(patientName, date);
            return ResponseEntity.ok(appointments.values().stream().toList());

        }
        else {
            log.error(validateTokenResponse.getStatusCode() + ", " + validateTokenResponse.getBody().toString());
            return validateTokenResponse;
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> bookAppointment(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody AppointmentDTO appointmentDTO) {
        ResponseEntity<Map<String, String>> validateTokenResponse = myService.validateToken(authorization, "patient");
        Map<String, String> response = new HashMap<>();
        if(validateTokenResponse.getStatusCode() == HttpStatus.OK) {
            Appointment appointment = AppointmentDTO.getAppointment(appointmentDTO);
            int isValid = myService.validateAppointment(appointment);
            HttpStatus httpStatus = null;
            switch (isValid) {
                case -1-> {
                    response.put("status", "error");
                    response.put("message", "doctor not exists");
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
                case 0 -> {
                    response.put("status", "error");
                    response.put("message", "appointment time unavailable");
                    httpStatus = HttpStatus.CONFLICT;
                }
                case 1 -> {
                    String patientEmail = tokenService.extractIdentifier(authorization);
                    int saved = appointmentService.bookAppointment(appointment, patientEmail);
                    response.put("status", saved == 1 ? "success" : "error");
                    response.put("message", saved == 1 ? "appointment booked" : "appointment not saved");
                    httpStatus = saved == 1 ? HttpStatus.CREATED : HttpStatus.INTERNAL_SERVER_ERROR;
                }
            }

            return new ResponseEntity<>(response, httpStatus);
        }
        else {
            log.error(validateTokenResponse.getStatusCode() + ", " + validateTokenResponse.getBody().toString());
            return validateTokenResponse;
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> updateAppointment(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody Appointment appointment) {
        ResponseEntity<Map<String, String>> validateTokenResponse = myService.validateToken(authorization, "patient");
        if(validateTokenResponse.getStatusCode() == HttpStatus.OK) {
            return appointmentService.updateAppointment(appointment, tokenService.extractIdentifier(authorization));
        }
        else {
            return validateTokenResponse;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable long id, @RequestHeader("Authorization") String authorization) {
        ResponseEntity<Map<String, String>> validateTokenResponse = myService.validateToken(authorization, "patient");
        if(validateTokenResponse.getStatusCode() == HttpStatus.OK) {
            return appointmentService.cancelAppointment(id, tokenService.extractIdentifier(authorization));
        }
        else {
            return validateTokenResponse;
        }
    }
}
