package com.smartcare.backend.controller;

import com.smartcare.backend.model.Admin;
import com.smartcare.backend.service.MyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "admin")
public class AdminController {
    private final MyService myService;

    public AdminController(MyService myService) {
        this.myService = myService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@Valid @RequestBody Admin admin) {
        return myService.validateAdmin(admin);
    }
}
