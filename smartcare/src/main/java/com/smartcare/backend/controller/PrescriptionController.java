package com.smartcare.backend.controller;

import com.smartcare.backend.model.Prescription;
import com.smartcare.backend.service.MyService;
import com.smartcare.backend.service.PrescriptionService;
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

    public PrescriptionController(PrescriptionService prescriptionService, MyService myService) {
        this.prescriptionService = prescriptionService;
        this.myService = myService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> savePrescription(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody Prescription prescription) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(authorization, "doctor");
        if(responseService.getStatusCode() == HttpStatus.OK) {
            return prescriptionService.savePrescription(prescription);
        }
        else {
           return responseService;
        }
    }


    @GetMapping("/{appointmentId}")
    public ResponseEntity<Map<String, Object>> getPrescriptionByAppointmentId(
            @RequestHeader("Authorization") String authorization,
            @PathVariable long appointmentId) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(authorization, "doctor");
        if(responseService.getStatusCode() == HttpStatus.OK) {
            return prescriptionService.getPrescription(appointmentId);
        }
        else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ko");
            response.put("message", "invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }


}
