package com.smartcare.backend.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DoctorProfileUpdate(
        @Size(min = 3, max = 50) String specialty,
        @Pattern(regexp = "\\d{10}") String phone,
        @Size(max = 2048)
        @Pattern(
                regexp = "^(?:https://.+|/assets/images/[A-Za-z0-9_-]+\\.(?:png|jpg|jpeg|webp|svg))$",
                message = "profileImageUrl must use HTTPS or a supported /assets/images path"
        ) String profileImageUrl,
        @NotEmpty List<@Pattern(regexp = "(?:[01]\\d|2[0-3]):[0-5]\\d") String> availableTimes
) {
}
