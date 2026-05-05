package com.smartcare.backend.service;

import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.model.Patient;
import com.smartcare.backend.repository.PatientRepository;
import org.apache.commons.logging.LogFactory;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import java.util.Optional;

public class PatientService {
    Log log = (Log) LogFactory.getLog(this.getClass());

    @Autowired
    private PatientRepository patientRepository;
}
