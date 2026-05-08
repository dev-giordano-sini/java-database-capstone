package com.smartcare.backend.service;

import com.smartcare.backend.DTO.AppointmentDTO;
import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.DoctorRepository;
import com.smartcare.backend.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.logging.LogFactory;
import org.apache.juli.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private final Log log = (Log) LogFactory.getLog(this.getClass());

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService  tokenService;
    private final MyService myService;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, TokenService tokenService, MyService myService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.myService = myService;
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
        Map<String,String> map = new HashMap<>();
        map.put("status","success");
        map.put("message", "");

        Optional<Appointment> optional = appointmentRepository.findById(appointment.getId());

        if(optional.isPresent()) {
            if(optional.get() == appointment) {
                appointmentRepository.save(appointment);
                boolean isValid = validateAppointment(appointment);

                if(isValid) {
                    map.put("message","appointment updated");
                }
                else {
                    map.put("status","ko");
                    map.put("message","appointment not saved");
                }
            }
        }
        else {
            map.put("status","no appointment with id:"+appointment.getId());
        }

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        // TODO: validate token..
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
    public Map<String, Object> getAppointment(String patientName, LocalDate date, String token) {
        // TODO: validate token..
        Map<String,Object> doctorToAppointment = new HashMap<>();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atStartOfDay().plusDays(1);

        List<Doctor> doctors = doctorRepository.findAll();
        doctors.forEach(doctor -> {
            Optional<List<Appointment>> optionalAppointments;
            if(patientName != null && !patientName.isEmpty()) {
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

    public boolean validateAppointment(Appointment oldAppointment) {
        Appointment newAppointment = appointmentRepository.findById(oldAppointment.getId()).orElse(null);
        if(newAppointment  == null) {
            return false;
        }
        boolean isValid = false;

        if(!oldAppointment.getDoctor().equals(newAppointment.getDoctor()))
            isValid = true;
        if(!oldAppointment.getAppointmentTime().equals(newAppointment.getAppointmentTime())
                || !oldAppointment.getEndTime().equals(newAppointment.getEndTime()))
            isValid = true;
        if(!oldAppointment.getNotes().equals(newAppointment.getNotes()))
            isValid = true;

        return true;
    }

}
