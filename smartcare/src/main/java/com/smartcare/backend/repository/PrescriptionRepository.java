package com.smartcare.backend.repository;

import com.smartcare.backend.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    public List<Prescription> findByAppointmentId(Long appointmentId);
}
