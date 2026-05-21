package com.smartcare.backend.controller;

import com.smartcare.backend.model.Admin;
import com.smartcare.backend.service.MyService;
import org.apache.commons.logging.LogFactory;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "admin")
public class AdminController {
    private final Log log = (Log) LogFactory.getLog(this.getClass());

    @Autowired
    private MyService myService;


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(Admin admin) {
        return myService.validateAdmin(admin);
    }
}
