package com.smartcare.backend.controller;

import com.smartcare.backend.model.MedicalReport;
import com.smartcare.backend.service.MedicalReportService;
import com.smartcare.backend.service.MyService;
import com.smartcare.backend.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${api.path}reports")
public class MedicalReportController {
    private final MedicalReportService medicalReportService;
    private final MyService myService;
    private final TokenService tokenService;

    public MedicalReportController(MedicalReportService medicalReportService, MyService myService,
                                   TokenService tokenService) {
        this.medicalReportService = medicalReportService;
        this.myService = myService;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<?> save(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody MedicalReport report) {
        ResponseEntity<Map<String, String>> authentication = myService.validateToken(authorization, "doctor");
        if (authentication.getStatusCode() != HttpStatus.OK) {
            return authentication;
        }
        String identifier = tokenService.extractIdentifier(authorization);
        if (!medicalReportService.canAccess(report.getAppointmentId(), identifier, "doctor")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Appointment does not belong to doctor"));
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(medicalReportService.save(report));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", exception.getMessage()));
        }
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> findByAppointment(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long appointmentId) {
        ResponseEntity<Map<String, String>> authentication = myService.validateToken(
                authorization, "doctor", "patient");
        if (authentication.getStatusCode() != HttpStatus.OK) {
            return authentication;
        }
        Map<String, String> token = tokenService.decodeToken(authorization);
        if (!medicalReportService.canAccess(appointmentId, token.get("identifier"), token.get("role"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Appointment access denied"));
        }
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", medicalReportService.findByAppointment(appointmentId)
        ));
    }
}
