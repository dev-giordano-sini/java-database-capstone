package com.smartcare.backend.service;

import com.smartcare.backend.model.Admin;
import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;


    public List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end) {
        /**
         * Query: Use @Query with LEFT JOIN FETCH to include doctor and availability info
         */
        return appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end).orElse(new ArrayList<>());
    }

    public List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(Long doctorId, String patientName, LocalDateTime start, LocalDateTime end) {
        /**
         * Query: Use @Query with LEFT JOIN FETCH to include patient and doctor details
         */
        return appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(doctorId, patientName, start, end).orElse(new ArrayList<>());
    }

    @Transactional
    @Modifying
    public void deleteAllByDoctorId(Long doctorId) {

    }

    public List<Appointment> findByPatientId(Long patientId) {
        return null;
    }

    public List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status) {
        return null;
    }

    public List<Appointment> filterByDoctorNameAndPatientId(String doctorName, Long patientId) {
        /***
         * LOWER, CONCAT, and % for partial, case-insensitive text matches.
         */
        return appointmentRepository.filterByDoctorNameAndPatientId(doctorName, patientId).orElse(new ArrayList<>());
    }

    public List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName, Long patientId, int status) {
        /***
         * LOWER, CONCAT, and % for partial, case-insensitive text matches.
         */
        return appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status).orElse(new ArrayList<>());
    }

}
