package com.smartcare.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class DoctorAvailableTimes {

    @ManyToOne
    @NotNull
    private Long doctorId;

    @NotNull
    private String availableTimes;

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(String availableTimes) {
        this.availableTimes = availableTimes;
    }
}
