package com.smartcare.backend.controller;


import com.smartcare.backend.service.Validator;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {

    @Autowired
    private Validator validator;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@NotNull @PathVariable("token") String token) {
        String template = "";

        if (validator.validateToken(token, "admin")) {
            template = "";
        } else {
            template = "http://localhost:8080";
        }

        return template;
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard (@NotNull @PathVariable("token") String token) {
        String template = "";

        if (validator.validateToken(token, "doctor")) {
            template = "";
        } else {
            template = "http://localhost:8080";
        }

        return template;
    }
}
