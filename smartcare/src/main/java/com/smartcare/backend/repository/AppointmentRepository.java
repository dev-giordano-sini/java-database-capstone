package com.smartcare.backend.repository;

import com.smartcare.backend.model.Appointment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * USE LEFT JOIN FETCH
     * @param doctorId
     * @param start
     * @param end
     * @return
     */
    @Query(
            nativeQuery = true,
            value =
                    "SELECT ea.id, ea.city, ea.state FROM gfgmicroservicesdemo.address ea " +
                            "join gfgmicroservicesdemo.employee e on e.id = ea.employee_id " +
                            "where ea.employee_id=:employeeId")
    Optional<List<Appointment>> findByDoctorIdAndAppointmentTimeBetween(@Param("doctorId") long doctorId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * USE LEFT JOIN FETCH to include patient and doctor details
     *
     * @param doctorId
     * @param patientName
     * @param start
     * @param end
     * @return
     */
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

    @Transactional
    @Modifying
    public void deleteAllByDoctorId(Long doctorId);

    /***
     * LOWER, CONCAT, and % for partial, case-insensitive text matches.
     */
    public List<Appointment> findByPatientId(Long patientId);

    /***
     * LOWER, CONCAT, and % for partial, case-insensitive text matches.
     */
    public List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName, Long patientId, int status);


    public List<Appointment> filterByDoctorNameAndPatientId(String doctorName, Long patientId);
}

