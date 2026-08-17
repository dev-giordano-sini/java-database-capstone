package com.smartcare.backend.service;

import com.smartcare.backend.DTO.Login;
import com.smartcare.backend.DTO.DoctorProfileUpdate;
import com.smartcare.backend.DTO.DoctorResponse;
import com.smartcare.backend.DTO.DoctorPageResponse;
import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.repository.AppointmentRepository;
import com.smartcare.backend.repository.DoctorRepository;
import com.smartcare.backend.repository.PrescriptionRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DoctorService {
    private final Log log = LogFactory.getLog(this.getClass());

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final PrescriptionRepository prescriptionRepository;

    public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository,
                         TokenService tokenService, PasswordEncoder passwordEncoder,
                         PrescriptionRepository prescriptionRepository) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);
        List<String> doctorAvailableTimesList = new ArrayList<>();
        Doctor doctor = null;
        if (optionalDoctor.isPresent()) {
            doctor = optionalDoctor.get();
            doctorAvailableTimesList = doctor.getAvailableTimes();
        }

        if(doctorAvailableTimesList == null || doctorAvailableTimesList.isEmpty()) {
            return new ArrayList<>();
        }

        LocalDateTime startLocalDateTime = date.atStartOfDay();
        LocalDateTime endLocalDateTime = date.plusDays(1).atStartOfDay();
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId,
                startLocalDateTime,
                endLocalDateTime
        );

        Set<LocalTime> timeSet = appointments.stream().map(appointment -> appointment.getAppointmentTime().toLocalTime()).collect(Collectors.toSet());

        return doctorAvailableTimesList.stream().filter(slot -> {
            LocalTime timeSlot = LocalTime.parse(slot);
            return !timeSet.contains(timeSlot);
        }).sorted().toList();
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

        if (doctorSaved != null) {
            return -1;
        }

        try {
            doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
            doctorRepository.save(doctor);
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }

        doctorSaved = doctorRepository.findByEmail(doctor.getEmail());

        if (doctorSaved != null) {
            return 1;
        } else {
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

        if (doctorSaved == null) {
            return -1;
        }

        try {
            doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
            doctorRepository.save(doctor);
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }

        doctorSaved = doctorRepository.findById(doctor.getId()).orElse(null);

        if (doctorSaved != null) {
            return 1;
        } else {
            return 0;
        }
    }

    @Transactional
    public DoctorPageResponse getDoctors(int page, int size, String specialty) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<Doctor> doctors = specialty == null || specialty.isBlank()
                ? doctorRepository.findAll(pageable)
                : doctorRepository.findBySpecialtyIgnoreCase(specialty.trim(), pageable);
        Page<DoctorResponse> responses = doctors.map(DoctorResponse::from);
        return new DoctorPageResponse(
                "success",
                responses.getContent(),
                responses.getNumber(),
                responses.getSize(),
                responses.getTotalElements(),
                responses.getTotalPages()
        );
    }

    public List<String> getSpecialties() {
        return doctorRepository.findDistinctSpecialties();
    }

    @Transactional
    public DoctorResponse getDoctorByEmail(String email) {
        Doctor doctor = doctorRepository.findByEmail(email);
        return doctor == null ? null : DoctorResponse.from(doctor);
    }

    @Transactional
    public Doctor updateOwnProfile(String email, DoctorProfileUpdate update) {
        Doctor doctor = doctorRepository.findByEmail(email);
        if (doctor == null) {
            return null;
        }
        doctor.setSpecialty(update.specialty());
        doctor.setPhone(update.phone());
        doctor.setProfileImageUrl(update.profileImageUrl());
        doctor.setAvailableTimes(update.availableTimes().stream().distinct().sorted().toList());
        return doctorRepository.save(doctor);
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

        if (doctor == null) {
            return -1;
        }

        try {
            appointmentRepository.findByDoctorId(doctorId).stream()
                    .map(Appointment::getId)
                    .forEach(prescriptionRepository::deleteAllByAppointmentId);
            doctorRepository.delete(doctor);
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }

        doctor = doctorRepository.findById(doctorId).orElse(null);

        if (doctor == null) {
            return 1;
        } else {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> validateDoctor(Login loginDTO) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "");
        response.put("status", "ko");
        response.put("data", "");
        response.put("token", "");

        if (loginDTO == null || (loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty() || loginDTO.getIdentifier() == null ||
                loginDTO.getIdentifier().isEmpty())) {

            response.put("message", "empty credential");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Doctor doctor = doctorRepository.findByEmail(loginDTO.getIdentifier());

        if (doctor == null) {
            response.put("message", "Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), doctor.getPassword())) {
            response.put("message", "Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = tokenService.generateToken(loginDTO.getIdentifier());

        if (token == null) {
            response.put("message", "Create token error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            response.put("status", "success");
            response.put("token", token);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public Map<String, Object> findDoctorByName(String doctorName) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCase(doctorName);
        Map<String, Object> response = new HashMap<>();

        doctors.forEach(doctor -> {
            response.put(doctor.getName(), doctor);
        });

        return response;
    }


    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String doctorName, String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();

        if ((doctorName != null && !doctorName.isEmpty()) &&
                (specialty != null && !specialty.isEmpty()) &&
                (amOrPm != null && !amOrPm.isEmpty() && (amOrPm.equals("AM") || amOrPm.equals("PM")))
        ) {
            List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(doctorName, specialty);
            response = filterDoctorsByTime(doctors, amOrPm);
        }


        return response;
    }

    private Map<String, Object> filterDoctorsByTime(List<Doctor> doctors, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        doctors.forEach(doctor -> doctor.getAvailableTimes().stream()
                .filter(timeString -> {
                    LocalTime time = LocalTime.parse(timeString);
                    return amOrPm.equalsIgnoreCase("AM")
                            ? time.isBefore(LocalTime.NOON)
                            : !time.isBefore(LocalTime.NOON);
                })
                .forEach(timeString -> {
                    @SuppressWarnings("unchecked")
                    List<Doctor> doctorsAtTime = (List<Doctor>) response.computeIfAbsent(
                            timeString, ignored -> new ArrayList<Doctor>());
                    doctorsAtTime.add(doctor);
                }));

        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String doctorName, String amOrPm) {
        Map<String, Object> response = new HashMap<>();

        if ((doctorName != null && !doctorName.isEmpty()) &&
                (amOrPm != null && !amOrPm.isEmpty() && (amOrPm.equals("AM") || amOrPm.equals("PM")))
        ) {
            List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCase(doctorName);
            response = filterDoctorsByTime(doctors, amOrPm);
        }

        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecility(String doctorName, String specialty) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors;
        if ((doctorName != null && !doctorName.isEmpty()) && (specialty != null && !specialty.isEmpty())) {
            doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(doctorName, specialty);
        } else {
            doctors = null;
        }

        if(doctors == null) {
            return response;
        }

        Set<String> doctorNames = doctors.stream().map(Doctor::getName).collect(Collectors.toSet());

        doctorNames.forEach(name -> {
            response.put(name, doctors.stream().filter(doctor -> doctor.getName().equals(name)).collect(Collectors.toList()));
        });

        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        Map<String, Object> response = new HashMap<>();

        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);

        Set<String> specialties = doctors.stream().map(Doctor::getSpecialty).collect(Collectors.toSet());

        specialties.forEach(spec -> {
            response.put(spec, doctors.stream().filter(doctor -> doctor.getSpecialty().equals(spec)).collect(Collectors.toList()));
        });

        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();

        return filterDoctorsByTime(doctors, amOrPm);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        Map<String, Object> map = filterDoctorsByTime(doctors, amOrPm);

        return map.values().stream()
                .flatMap(value -> ((List<Doctor>) value).stream())
                .distinct()
                .toList();
    }

}
