package com.smartcare.backend.repository;

import com.smartcare.backend.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query(
            nativeQuery = true,
            value =
                    "SELECT ea.id, ea.city, ea.state FROM gfgmicroservicesdemo.address ea " +
                            "join gfgmicroservicesdemo.employee e on e.id = ea.employee_id " +
                            "where ea.employee_id=:employeeId")
    Optional<List<Appointment>> findByDoctorIdAndAppointmentTimeBetween(@Param("doctorId") long doctorId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(
            nativeQuery = true,
            value =
                    "SELECT ea.id, ea.city, ea.state FROM gfgmicroservicesdemo.address ea " +
                            "join gfgmicroservicesdemo.employee e on e.id = ea.employee_id " +
                            "where ea.employee_id=:employeeId")
    Optional<List<Appointment>> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(@Param("doctorId") long doctorId, @Param("patientName") String patientName, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(
            nativeQuery = true,
            value =
                    "SELECT ea.id, ea.city, ea.state FROM gfgmicroservicesdemo.address ea " +
                            "join gfgmicroservicesdemo.employee e on e.id = ea.employee_id " +
                            "where ea.employee_id=:employeeId")
    Optional<List<Appointment>> filterByDoctorNameAndPatientId(@Param("doctorName") String doctorName, @Param("patientId") long patientId);

    @Query(
            nativeQuery = true,
            value =
                    "SELECT ea.id, ea.city, ea.state FROM gfgmicroservicesdemo.address ea " +
                            "join gfgmicroservicesdemo.employee e on e.id = ea.employee_id " +
                            "where ea.employee_id=:employeeId")
    Optional<List<Appointment>> filterByDoctorNameAndPatientIdAndStatus(@Param("doctorName") String doctorName, @Param("patientId") long patientId, @Param("status") int status);
}

