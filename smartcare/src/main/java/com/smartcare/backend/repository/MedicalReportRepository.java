package com.smartcare.backend.repository;

import com.smartcare.backend.model.MedicalReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalReportRepository extends JpaRepository<MedicalReport, Long> {
    List<MedicalReport> findByAppointmentIdOrderByReportDateDesc(Long appointmentId);
}
