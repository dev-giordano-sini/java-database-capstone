package com.smartcare.backend.service;

import com.smartcare.backend.DTO.AppointmentDTO;
import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.logging.LogFactory;
import org.apache.juli.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AppointmentService {
    private final Log log = (Log) LogFactory.getLog(this.getClass());

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return 0;
        }

    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        if (appointment.getId() == null || !appointmentRepository.existsById(appointment.getId())) {
            response.put("status", "ko");
            response.put("message", "no appointment with id: " + appointment.getId());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        appointmentRepository.save(appointment);
        response.put("status", "success");
        response.put("message", "appointment updated");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id) {
        Map<String,String> map = new HashMap<>();
        map.put("status","success");

        Optional<Appointment> optional = appointmentRepository.findById(id);

        if(optional.isPresent()) {
            map.put("message","appointment cancelled");
            appointmentRepository.deleteById(id);
        }
        else {
            map.put("status","ko");
            map.put("message","no appointment found");
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

}
