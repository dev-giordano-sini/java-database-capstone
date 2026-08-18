package com.smartcare.backend.repository;

import com.smartcare.backend.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByNameContainingIgnoreCase(String doctorName);


    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String doctorName, String specialty);


    Doctor findByEmail(String email);

    List<Doctor> findBySpecialtyIgnoreCase(String specialty);

    Page<Doctor> findBySpecialtyIgnoreCase(String specialty, Pageable pageable);

    Page<Doctor> findByApprovedTrue(Pageable pageable);

    Page<Doctor> findBySpecialtyIgnoreCaseAndApprovedTrue(String specialty, Pageable pageable);

    @Query("select distinct d.specialty from Doctor d order by d.specialty")
    List<String> findDistinctSpecialties();

    @Query("select distinct d.specialty from Doctor d where d.approved = true order by d.specialty")
    List<String> findDistinctApprovedSpecialties();
}
