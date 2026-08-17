package com.smartcare.backend.repository;

import com.smartcare.backend.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByNameContainingIgnoreCase(String doctorName);


    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String doctorName, String specialty);


    Doctor findByEmail(String email);

    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}
