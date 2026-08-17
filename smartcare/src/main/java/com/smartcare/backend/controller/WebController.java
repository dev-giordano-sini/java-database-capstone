package com.smartcare.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping("/")
    String home() {
        return "index";
    }

    @GetMapping("/patient-dashboard")
    String patientDashboard() {
        return "patient/dashboard";
    }

    @GetMapping("/doctor-dashboard")
    String doctorDashboard() {
        return "doctor/doctorDashboard";
    }

    @GetMapping("/admin-dashboard")
    String adminDashboard() {
        return "admin/adminDashboard";
    }
}
