package com.smartcare.backend.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DoctorProfileUpdate(
        @Size(min = 3, max = 50) String specialty,
        @Pattern(regexp = "\\d{10}") String phone,
        @NotEmpty List<@Pattern(regexp = "(?:[01]\\d|2[0-3]):[0-5]\\d") String> availableTimes
) {
}
