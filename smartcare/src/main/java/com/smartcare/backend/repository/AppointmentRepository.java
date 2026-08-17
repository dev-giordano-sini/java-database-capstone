package com.smartcare.backend.repository;

import com.smartcare.backend.model.Appointment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<List<Appointment>> findByDoctorIdAndAppointmentTimeBetween(long doctorId, LocalDateTime start, LocalDateTime end);

    Optional<List<Appointment>> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            long doctorId, String patientName, LocalDateTime start, LocalDateTime end);

    Optional<List<Appointment>> findByDoctor_NameAndPatient_Id(String doctorName, long patientId);

    Optional<List<Appointment>> findByDoctor_NameAndPatient_IdAndStatus(String doctorName, long patientId, int status);

    @Transactional
    @Modifying
    void deleteAllByDoctorId(Long doctorId);

    /***
     * LOWER, CONCAT, and % for partial, case-insensitive text matches.
     */
    List<Appointment> findByPatientId(Long patientId);

    @Query("""
            select year(appointment.appointmentTime), month(appointment.appointmentTime), count(appointment)
            from Appointment appointment
            group by year(appointment.appointmentTime), month(appointment.appointmentTime)
            order by year(appointment.appointmentTime), month(appointment.appointmentTime)
            """)
    List<Object[]> countAppointmentsByMonth();
}
