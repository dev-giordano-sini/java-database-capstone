package com.smartcare.backend.DTO;

import java.util.List;

public record DoctorPageResponse(
        String status,
        List<DoctorResponse> data,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
