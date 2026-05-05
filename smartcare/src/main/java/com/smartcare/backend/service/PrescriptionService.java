package com.smartcare.backend.service;

import com.smartcare.backend.model.Prescription;
import com.smartcare.backend.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import java.util.List;

public class PrescriptionService {

    @Autowired
    PrescriptionRepository prescriptionRepository;
}
