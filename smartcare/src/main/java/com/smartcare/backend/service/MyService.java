package com.smartcare.backend.service;

import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.repository.AdminRepository;
import com.smartcare.backend.repository.DoctorRepository;
import com.smartcare.backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;


}
