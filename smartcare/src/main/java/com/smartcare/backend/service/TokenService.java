package com.smartcare.backend.service;

import com.smartcare.backend.model.Admin;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.model.Patient;
import com.smartcare.backend.repository.AdminRepository;
import com.smartcare.backend.repository.DoctorRepository;
import com.smartcare.backend.repository.PatientRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public TokenService(AdminRepository adminRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public String generateToken(String identifier) {
        Map<String, Object> claims = new HashMap<>();
        Admin admin = adminRepository.findByUsername(identifier);
        Doctor doctor = doctorRepository.findByEmail(identifier);
        Patient patient = patientRepository.findByEmail(identifier);
        String role = "";
        if(admin != null) {
            role = "admin";
        }
        else if(doctor != null) {
            role = "doctor";
        }
        else if(patient != null) {
            role = "patient";
        }
        if (role.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a token for an unknown account");
        }
        claims.put("role", role);
        return createToken(claims, identifier);
    }

    public String extractIdentifier(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token, String expectedRole) {
        try {
            Claims claims = parseClaims(token);
            return expectedRole.equals(claims.get("role", String.class))
                    && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public Map<String, String> decodeToken(String token) {
        Claims claims = parseClaims(token);
        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("identifier", claims.getSubject());
        tokenData.put("role", claims.get("role", String.class));
        tokenData.put("expirationDate", claims.getExpiration().toInstant().toString());
        return tokenData;
    }

    private String createToken(Map<String, Object> claims, String identifier) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(identifier)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
