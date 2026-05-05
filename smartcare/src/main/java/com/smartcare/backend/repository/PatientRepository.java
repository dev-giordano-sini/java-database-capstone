package com.smartcare.backend.repository;

import com.smartcare.backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    public Patient findByEmail(String email);

    public Patient findByEmailOrPhone(String email, String phone);
}
