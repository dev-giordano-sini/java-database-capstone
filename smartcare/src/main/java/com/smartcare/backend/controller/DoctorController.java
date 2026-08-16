package com.smartcare.backend.controller;

import com.smartcare.backend.DTO.Login;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.service.DoctorService;
import com.smartcare.backend.service.MyService;
import jakarta.websocket.server.PathParam;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {
    private final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private MyService myService;

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, String>> getDoctorAvailability(@PathVariable("user") String user, @PathVariable("doctorId") long doctorId, @PathVariable("date") LocalDate date, @PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(token, user);
        if(responseService.getStatusCode() == HttpStatus.OK) {
            List<String> availability =  doctorService.getDoctorAvailability(doctorId, date);
            Map<String, String> response = new HashMap<>();
            response.put("data", availability.toString());
            response.put("status", "success");

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            return responseService;
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        Map<String, String> response = new HashMap<>();
        response.put("data", doctors.toString());
        response.put("status", "success");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> addDoctor(@PathVariable("token") String token, @RequestBody Doctor doctor) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(token, "admin");

        if(responseService.getStatusCode() == HttpStatus.OK) {
            int status = doctorService.saveDoctor(doctor);
            Map<String, String> response = new HashMap<>();
            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            switch (status) {
                case 0-> {
                    response.put("status", "ko");
                    response.put("message", "Doctor not saved");
                }
                case -1-> {
                    response.put("status", "ko");
                    response.put("message", "Doctor already exists");
                }
                case 1 -> {
                    response.put("status", "success");
                    response.put("message", "Doctor added");
                    httpStatus = HttpStatus.OK;
                }
            }

            return new ResponseEntity<>(response, httpStatus);
        }
        else {
            return responseService;
        }
    }

    @PostMapping("/login")
    public String loginDoctor(@PathParam("email") String email, @PathParam("password") String password) {
        Login login = new  Login();
        login.setIdentifier(email);
        login.setPassword(password);
        ResponseEntity<Map<String, String>> response = doctorService.validateDoctor(login);

        if(response.getStatusCode() == HttpStatus.OK) {
            return response.getBody().get("token");
        }

        return "";
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>>updateDoctorDetail(@PathVariable("token")String token, @RequestBody Doctor doctor) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(token, "admin");

        if(responseService.getStatusCode() == HttpStatus.OK) {
            int status = doctorService.updateDoctor(doctor);
            HttpStatus  httpStatus = HttpStatus.NON_AUTHORITATIVE_INFORMATION;
            Map<String, String> response = new HashMap<>();
            switch (status) {
                case -1 -> {
                    response.put("status", "ko");
                    response.put("message", "Doctor update error");
                }
                case 0 -> {
                    response.put("status", "ko");
                    response.put("message", "Doctor not updated");
                }
                case 1 -> {
                    response.put("status", "ok");
                    response.put("message", "Doctor updated");
                    httpStatus = HttpStatus.OK;
                }
            }

            return new ResponseEntity<>(response, httpStatus);
        }
        else {
            return responseService;
        }
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable("id") long id, @PathVariable("token") String token) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(token, "admin");

        if(responseService.getStatusCode() == HttpStatus.OK) {
            int status = doctorService.deleteDoctor(id);
            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            Map<String, String> response = new  HashMap<>();
            switch (status) {
                case -1 -> {
                    response.put("status", "ko");
                    response.put("message", "Some internal error occurred");
                }
                case 0 -> {
                    response.put("status", "ko");
                    response.put("message", "Doctor not found with id");
                }
                case 1 -> {
                    response.put("status", "ok");
                    response.put("message", "Doctor deleted successfully");
                    httpStatus = HttpStatus.OK;
                }
            }

            return new ResponseEntity<>(response, httpStatus);
        }
        else {
            return responseService;
        }
    }


    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, String>> filterDoctors(@PathVariable("name") String name, @PathVariable("time") String amOrPm, @PathVariable("speciality") String speciality) {
        Map<String,Object> filteredDoctors = myService.filterDoctor(name, speciality, amOrPm);
        Map<String, String> response = new  HashMap<>();
        response.put("status", "ok");
        response.put("message", "filter all doctors");
        response.put("data", filteredDoctors.toString());

        return new  ResponseEntity<>(response, HttpStatus.OK);
    }

}
