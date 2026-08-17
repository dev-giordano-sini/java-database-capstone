package com.smartcare.backend.controller;

import com.smartcare.backend.DTO.Login;
import com.smartcare.backend.DTO.DoctorProfileUpdate;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.service.DoctorService;
import com.smartcare.backend.service.MyService;
import com.smartcare.backend.service.TokenService;
import jakarta.validation.Valid;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
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

    private final MyService myService;
    private final DoctorService doctorService;
    private final TokenService tokenService;

    public DoctorController(MyService myService, DoctorService doctorService, TokenService tokenService) {
        this.myService = myService;
        this.doctorService = doctorService;
        this.tokenService = tokenService;
    }

    @GetMapping("/{doctorId}/availability")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable long doctorId,
            @RequestParam LocalDate date,
            @RequestHeader("Authorization") String authorization) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(
                authorization, "patient", "doctor");
        if(responseService.getStatusCode() == HttpStatus.OK) {
            List<String> availability =  doctorService.getDoctorAvailability(doctorId, date);
            Map<String, Object> response = new HashMap<>();
            response.put("data", availability);
            response.put("status", "success");

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(new HashMap<>(responseService.getBody()), responseService.getStatusCode());
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        Map<String, Object> response = new HashMap<>();
        response.put("data", doctors);
        response.put("status", "success");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<Map<String, String>> addDoctor(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody Doctor doctor) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(authorization, "admin");

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
    public ResponseEntity<Map<String, String>> loginDoctor(@Valid @RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getOwnProfile(@RequestHeader("Authorization") String authorization) {
        ResponseEntity<Map<String, String>> authentication = myService.validateToken(authorization, "doctor");
        if (authentication.getStatusCode() != HttpStatus.OK) {
            return authentication;
        }
        Doctor doctor = doctorService.getDoctorByEmail(tokenService.extractIdentifier(authorization));
        return doctor == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(doctor);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateOwnProfile(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody DoctorProfileUpdate update) {
        ResponseEntity<Map<String, String>> authentication = myService.validateToken(authorization, "doctor");
        if (authentication.getStatusCode() != HttpStatus.OK) {
            return authentication;
        }
        Doctor doctor = doctorService.updateOwnProfile(tokenService.extractIdentifier(authorization), update);
        return doctor == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(doctor);
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> updateDoctorDetail(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody Doctor doctor) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(authorization, "admin");

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable long id, @RequestHeader("Authorization") String authorization) {
        ResponseEntity<Map<String, String>> responseService = myService.validateToken(authorization, "admin");

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
    public ResponseEntity<Map<String, Object>> filterDoctors(@PathVariable("name") String name, @PathVariable("time") String amOrPm, @PathVariable("speciality") String speciality) {
        Map<String,Object> filteredDoctors = myService.filterDoctor(name, speciality, amOrPm);
        Map<String, Object> response = new  HashMap<>();
        response.put("status", "ok");
        response.put("message", "filter all doctors");
        response.put("data", filteredDoctors);

        return new  ResponseEntity<>(response, HttpStatus.OK);
    }

}
