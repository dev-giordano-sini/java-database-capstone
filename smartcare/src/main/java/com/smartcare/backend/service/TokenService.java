package com.smartcare.backend.service;

import com.smartcare.backend.DTO.Login;
import com.smartcare.backend.model.Admin;
import com.smartcare.backend.model.Doctor;
import com.smartcare.backend.model.Patient;
import com.smartcare.backend.repository.AdminRepository;
import com.smartcare.backend.repository.DoctorRepository;
import com.smartcare.backend.repository.PatientRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

    //TODO: this must will not return a reponse
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    //private String password;

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${JWT.SECRET.KEY}:''")
    private String secretKey;

    public TokenService(AdminRepository adminRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public String generateToken(String identifier) {
        Map<String, Object> claims = new HashMap<String, Object>();
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
        claims.put("role", role);
        return createToken(claims, identifier);
    }

    public String extractIdentifier(String token) {
        return Jwts.parserBuilder().build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, String user) {
        String roleFromToken = Jwts.parserBuilder().build().parseClaimsJws(token).getBody().get("role", String.class);

        Admin admin = adminRepository.findByUsername(user);
        Doctor doctor = doctorRepository.findByEmail(user);
        Patient patient = patientRepository.findByEmail(user);
        String role = null;
        if(admin != null) {
            role = "admin";
        }
        else if(doctor != null) {
            role = "doctor";
        }
        else if(patient != null) {
            role = "patient";
        }

        long expiration = Jwts.parserBuilder().build().parseClaimsJws(token).getBody().getExpiration().getTime();
        long now = new Date().getTime();

        return roleFromToken.equals(role) && expiration >= now;
    }

    public Map<String,String> decodeToken(String token) {return null; }

    private String createToken(Map<String, Object> claims, String identifier) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(identifier)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10080)) // 7 days
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
