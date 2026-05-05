package com.smartcare.backend.repository;

import com.smartcare.backend.model.DoctorAvailableTimes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorAvailableTimesRepository extends JpaRepository<List<String>, Long> {

}
