package com.smartcare.backend.controller;

import com.smartcare.backend.model.Admin;
import com.smartcare.backend.service.MyService;
import com.smartcare.backend.service.AppointmentService;
import com.smartcare.backend.DTO.AppointmentMonthlyStat;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("${api.path}" + "admin")
public class AdminController {
    private final MyService myService;
    private final AppointmentService appointmentService;

    public AdminController(MyService myService, AppointmentService appointmentService) {
        this.myService = myService;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@Valid @RequestBody Admin admin) {
        return myService.validateAdmin(admin);
    }

    @GetMapping("/statistics/appointments")
    public ResponseEntity<?> appointmentStatistics(@RequestHeader("Authorization") String authorization) {
        ResponseEntity<Map<String, String>> authentication = myService.validateToken(authorization, "admin");
        if (authentication.getStatusCode() != HttpStatus.OK) {
            return authentication;
        }
        List<AppointmentMonthlyStat> statistics = appointmentService.getMonthlyStatistics();
        return ResponseEntity.ok(statistics);
    }
}
