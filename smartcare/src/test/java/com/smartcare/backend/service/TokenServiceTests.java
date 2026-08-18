package com.smartcare.backend.service;

import com.smartcare.backend.model.Patient;
import com.smartcare.backend.repository.AdminRepository;
import com.smartcare.backend.repository.DoctorRepository;
import com.smartcare.backend.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenServiceTests {
    private static final String SECRET = "5367566859703373367639792F423F452848284D6251655468576D5A71347437";

    private PatientRepository patientRepository;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        AdminRepository adminRepository = mock(AdminRepository.class);
        DoctorRepository doctorRepository = mock(DoctorRepository.class);
        patientRepository = mock(PatientRepository.class);
        tokenService = new TokenService(adminRepository, doctorRepository, patientRepository);
        ReflectionTestUtils.setField(tokenService, "secretKey", SECRET);
        ReflectionTestUtils.setField(tokenService, "expirationMs", 60_000L);
    }

    @Test
    void generatedPatientTokenIsSignedAndContainsIdentityAndRole() {
        Patient patient = new Patient();
        patient.setEmail("patient@example.com");
        when(patientRepository.findByEmail(patient.getEmail())).thenReturn(patient);

        String token = tokenService.generateToken(patient.getEmail());

        assertTrue(tokenService.validateToken(token, "patient"));
        assertTrue(tokenService.validateToken("Bearer " + token, "patient"));
        assertFalse(tokenService.validateToken(token, "doctor"));
        assertEquals(patient.getEmail(), tokenService.extractIdentifier("Bearer " + token));
        assertEquals("patient", tokenService.decodeToken(token).get("role"));
    }

    @Test
    void unknownAccountCannotReceiveToken() {
        assertThrows(IllegalArgumentException.class,
                () -> tokenService.generateToken("missing@example.com"));
    }
}
