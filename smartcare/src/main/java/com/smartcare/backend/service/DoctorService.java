package com.smartcare.backend.service;

import com.smartcare.backend.DTO.LoginDTO;
import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.DoctorAvailableTimesRepository;
import com.smartcare.backend.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.logging.LogFactory;
import org.apache.juli.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DoctorService {
    private final Log log = (Log) LogFactory.getLog(this.getClass());

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;
    private final DoctorAvailableTimesRepository doctorAvailableTimesRepository;

    public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, TokenService tokenService, DoctorAvailableTimesRepository doctorAvailableTimesRepository) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
        this.doctorAvailableTimesRepository = doctorAvailableTimesRepository;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        List<String> doctorAvailableTimesList = doctorAvailableTimesRepository.findById(doctorId).orElse(new ArrayList<>());
        final List<String> availableTimeList = new ArrayList<>();

        if(!doctorAvailableTimesList.isEmpty()) {
            //iterate trough doctor's slot for appointments
            doctorAvailableTimesList.forEach(availableTime -> {
                String [] intervals = availableTime.split("-");
                String startHour = intervals[0];
                String endHour = intervals[1];
                // "build" date and time from date passed from params and doctor's slot
                LocalDateTime startTime = LocalDateTime.from(date).plusHours(Long.parseLong(startHour));
                LocalDateTime endTime = LocalDateTime.from(date).plusHours(Long.parseLong(endHour));

                List<Appointment> doctorAppointmentList = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, startTime, endTime).orElse(null);
                // if doctor has no appointments then add to daily available slot list
                if (doctorAppointmentList == null || doctorAppointmentList.isEmpty()) {
                    availableTimeList.add(availableTime);
                }
            });
        }

        return availableTimeList;
    }

    /**
     *
     * @param doctor object to save
     * @return 1 when doctor saved
     * 0 doctor when NOT saved
     * -1 doctor already exists or internal error
     */
    @Transactional
    public int saveDoctor(@NotNull Doctor doctor) {
        Doctor doctorSaved = doctorRepository.findByEmail(doctor.getEmail());

        if(doctorSaved != null) {
            return -1;
        }

        try {
            doctorRepository.save(doctor);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }

        doctorSaved = doctorRepository.findByEmail(doctor.getEmail());

        if(doctorSaved != null) {
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     *
     * @param doctor object to save
     * @return 1 update ok
     * 0 update ko
     * -1 error
     */
    @Transactional
    public int updateDoctor(Doctor doctor) {
        Doctor doctorSaved = doctorRepository.findById(doctor.getId()).orElse(null);

        if(doctorSaved == null) {
            return -1;
        }

        try {
            doctorRepository.save(doctor);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }

        doctorSaved = doctorRepository.findById(doctor.getId()).orElse(null);

        if(doctorSaved != null) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public List<Doctor> getDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors == null || doctors.isEmpty() ? new ArrayList<>() : doctors;
    }

    /**
     *
     * @param doctorId of the doctor to delete
     * @return 1 delete ok
     * 0 delete ko
     * -1 not exists or internal error
     */
    @Transactional
    public int deleteDoctor(long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);

        if(doctor == null) {
            return -1;
        }

        try {
            doctorRepository.delete(doctor);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }

        doctor = doctorRepository.findById(doctorId).orElse(null);

        if(doctor == null) {
            return 1;
        }
        else {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> validateDoctor(LoginDTO loginDTO) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "");
        response.put("status", "ko");
        response.put("data", "");
        response.put("token", "");

        if(loginDTO == null || (loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty() || loginDTO.getIdentifier() == null ||
                loginDTO.getIdentifier().isEmpty())) {

            response.put("message", "empty credential");
            return new  ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Doctor doctor = doctorRepository.findByEmail(loginDTO.getIdentifier());

        if(doctor == null) {
            response.put("message", "Doctor's email not found");
            return new  ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String token = tokenService.getToken(loginDTO);

        if(token == null) {
            response.put("message", "Create token error");
            return new  ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else {
            response.put("status", "success");
            response.put("token", token);
        }

        return new  ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public Map<String, Object> findDoctorByName(String doctorName) {
        List<Doctor> doctors = doctorRepository.findByNameLike(doctorName).orElse(null);
        Map<String,  Object> response = new HashMap<>();

        doctors.forEach(doctor -> {
            response.put(doctor.getName(), doctor);
        });

        return response;
    }


    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String doctorName, String specialty, String amOrPm) {
        Map<String,  Object> response = new HashMap<>();

        if((doctorName != null && !doctorName.isEmpty()) &&
                (specialty != null && !specialty.isEmpty()) &&
                (amOrPm != null && !amOrPm.isEmpty() && (amOrPm.equals("AM") || amOrPm.equals("PM")))
        ) {
            List<Doctor> doctors = doctorRepository.findByNameLike(doctorName).orElse(new ArrayList<>());
            response = filterDoctorsByTime(doctors, amOrPm);
        }


        return response;
    }

    private Map<String, Object> filterDoctorsByTime(List<Doctor> doctors, String amOrPm) {
        Map<String,  Object> response = new HashMap<>();

        return response;
    }
}
