package com.smartcare.backend.repository;

import com.smartcare.backend.model.Appointment;
import com.smartcare.backend.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Query: Use @Query with LIKE and CONCAT for flexible pattern matching
     * @param doctorName
     * @return
     */
    @Query(
            nativeQuery = true,
            value =
                    "SELECT ea.id, ea.city, ea.state FROM gfgmicroservicesdemo.address ea " +
                            "join gfgmicroservicesdemo.employee e on e.id = ea.employee_id " +
                            "where ea.employee_id=:employeeId")
    Optional<List<Doctor>> findByNameLike(@Param("doctorName") String doctorName);


    /**
     * Query: Use @Query with LOWER, CONCAT, and LIKE for case-insensitive matching
     * @param doctorName
     * @param specialty
     * @return
     */
    @Query(
            nativeQuery = true,
            value =
                    "SELECT ea.id, ea.city, ea.state FROM gfgmicroservicesdemo.address ea " +
                            "join gfgmicroservicesdemo.employee e on e.id = ea.employee_id " +
                            "where ea.employee_id=:employeeId")
    Optional<List<Doctor>> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(@Param("doctorName") String doctorName, @Param("specialty") String specialty);


    public Doctor findByEmail(String email);

    public List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}
