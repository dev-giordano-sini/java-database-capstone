package com.smartcare.backend.controller;

import com.smartcare.backend.model.Prescription;
import com.smartcare.backend.service.MyService;
import com.smartcare.backend.service.PrescriptionService;
import com.smartcare.backend.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;
    private final MyService myService;
    private final TokenService tokenService;

    public PrescriptionController(PrescriptionService prescriptionService, MyService myService,
                                  TokenService tokenService) {
        this.prescriptionService = prescriptionService;
        this.myService = myService;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> savePrescription(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody Prescription prescription) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(authorization, "doctor");
        if(responseService.getStatusCode() == HttpStatus.OK) {
            return prescriptionService.savePrescription(prescription, tokenService.extractIdentifier(authorization));
        }
        else {
           return responseService;
        }
    }


    @GetMapping("/{appointmentId}")
    public ResponseEntity<Map<String, Object>> getPrescriptionByAppointmentId(
            @RequestHeader("Authorization") String authorization,
            @PathVariable long appointmentId) {
        Map<String, String> tokenData;
        try {
            tokenData = tokenService.decodeToken(authorization);
        } catch (RuntimeException exception) {
            tokenData = Map.of();
        }
        String role = tokenData.get("role");
        if (("doctor".equals(role) || "patient".equals(role))
                && myService.validateToken(authorization, role).getStatusCode() == HttpStatus.OK) {
            return prescriptionService.getPrescription(appointmentId, tokenData.get("identifier"), role);
        }
        else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ko");
            response.put("message", "invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }


}
