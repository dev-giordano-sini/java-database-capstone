package com.smartcare.backend.service;

import com.smartcare.backend.DTO.AppointmentDTO;
import com.smartcare.backend.DTO.AppointmentMonthlyStat;
import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.model.Patient;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.DoctorRepository;
import com.smartcare.backend.repository.PatientRepository;
import com.smartcare.backend.repository.PrescriptionRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AppointmentService {
    private final Log log = LogFactory.getLog(this.getClass());

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository,
                              PatientRepository patientRepository, PrescriptionRepository prescriptionRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional
    public int bookAppointment(Appointment appointment, String patientEmail) {
        try {
            Patient patient = patientRepository.findByEmail(patientEmail);
            Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId()).orElse(null);
            if (patient == null || doctor == null) {
                return 0;
            }
            appointment.setPatient(patient);
            appointment.setDoctor(doctor);
            appointmentRepository.save(appointment);
            return 1;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return 0;
        }

    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment, String patientEmail) {
        Map<String, String> response = new HashMap<>();
        Appointment savedAppointment = appointment.getId() == null
                ? null
                : appointmentRepository.findById(appointment.getId()).orElse(null);
        if (savedAppointment == null) {
            response.put("status", "ko");
            response.put("message", "no appointment with id: " + appointment.getId());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        if (!savedAppointment.getPatient().getEmail().equalsIgnoreCase(patientEmail)) {
            response.put("status", "ko");
            response.put("message", "appointment does not belong to authenticated patient");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        appointment.setPatient(savedAppointment.getPatient());
        appointmentRepository.save(appointment);
        response.put("status", "success");
        response.put("message", "appointment updated");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String patientEmail) {
        Map<String,String> map = new HashMap<>();
        map.put("status","success");

        Optional<Appointment> optional = appointmentRepository.findById(id);

        if(optional.isPresent() && optional.get().getPatient().getEmail().equalsIgnoreCase(patientEmail)) {
            map.put("message","appointment cancelled");
            prescriptionRepository.deleteAllByAppointmentId(id);
            appointmentRepository.deleteById(id);
        }
        else if (optional.isEmpty()) {
            map.put("status","ko");
            map.put("message","no appointment found");
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
        else {
            map.put("status", "ko");
            map.put("message", "appointment does not belong to authenticated patient");
            return new ResponseEntity<>(map, HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Transactional
    public Map<String, Object> getAppointment(String patientName, LocalDate date) {
        Map<String,Object> doctorToAppointment = new HashMap<>();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atStartOfDay().plusDays(1);

        List<Doctor> doctors = doctorRepository.findAll();
        doctors.forEach(doctor -> {
            Optional<List<Appointment>> optionalAppointments;
            if(patientName == null || patientName.isBlank()) {
                optionalAppointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctor.getId(), start, end);
            }
            else {
                optionalAppointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(doctor.getId(), patientName, start, end);
            }

            if(optionalAppointments.isPresent() && !optionalAppointments.get().isEmpty()) {
                List<AppointmentDTO> appointmentDTOS = optionalAppointments.get().stream().map(AppointmentDTO::to).toList();
                doctorToAppointment.put(doctor.getName(),appointmentDTOS);
            }
        });

        return doctorToAppointment;
    }

    @Transactional
    public List<AppointmentMonthlyStat> getMonthlyStatistics() {
        return appointmentRepository.countAppointmentsByMonth().stream()
                .map(row -> new AppointmentMonthlyStat(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).longValue()))
                .toList();
    }

}
