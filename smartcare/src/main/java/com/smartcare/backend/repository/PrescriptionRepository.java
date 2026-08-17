package com.smartcare.backend.repository;

import com.smartcare.backend.model.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    public List<Prescription> findByAppointmentId(Long appointmentId);

    void deleteAllByAppointmentId(Long appointmentId);
}
