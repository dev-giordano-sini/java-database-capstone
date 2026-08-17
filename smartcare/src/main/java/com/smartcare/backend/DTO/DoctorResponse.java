package com.smartcare.backend.DTO;

import com.smartcare.backend.model.Doctor;

import java.util.List;

public record DoctorResponse(
        Long id,
        String name,
        String specialty,
        String email,
        String phone,
        String profileImageUrl,
        List<String> availableTimes,
        int rating
) {
    public static DoctorResponse from(Doctor doctor) {
        List<String> slots = doctor.getAvailableTimes() == null
                ? List.of()
                : List.copyOf(doctor.getAvailableTimes());

        return new DoctorResponse(
                doctor.getId(),
                doctor.getName(),
                doctor.getSpecialty(),
                doctor.getEmail(),
                doctor.getPhone(),
                doctor.getProfileImageUrl(),
                slots,
                doctor.getRating()
        );
    }
}
