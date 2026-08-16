package com.smartcare.backend.controller;

import com.smartcare.backend.model.Prescription;
import com.smartcare.backend.service.MyService;
import com.smartcare.backend.service.PrescriptionService;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {
    private final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private MyService myService;

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(@PathVariable("token") String token, @RequestBody Prescription prescription) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(token, "doctor");
        if(responseService.getStatusCode() == HttpStatus.OK) {
            return prescriptionService.savePrescription(prescription);
        }
        else {
           return responseService;
        }
    }


    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescriptionByAppointmentId(@PathVariable("token") String token, @PathVariable("appointmentId") long appointmentId) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(token, "doctor");
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
