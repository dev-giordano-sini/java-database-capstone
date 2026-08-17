package com.smartcare.backend.repository;

import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.DTO.AppointmentMonthlyStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(long doctorId, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            long doctorId, String patientName, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByDoctor_NameIgnoreCaseAndPatient_Id(String doctorName, long patientId);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    @Query("""
            select new com.smartcare.backend.DTO.AppointmentMonthlyStat(
                year(appointment.appointmentTime),
                month(appointment.appointmentTime),
                count(appointment)
            )
            from Appointment appointment
            group by year(appointment.appointmentTime), month(appointment.appointmentTime)
            order by year(appointment.appointmentTime), month(appointment.appointmentTime)
            """)
    List<AppointmentMonthlyStat> countAppointmentsByMonth();
}
