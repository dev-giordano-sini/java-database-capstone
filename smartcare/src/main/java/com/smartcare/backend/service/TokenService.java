package com.smartcare.backend.service;

import com.smartcare.backend.DTO.Login;
import com.smartcare.backend.repository.AdminRepository;
import com.smartcare.backend.repository.DoctorRepository;
import com.smartcare.backend.repository.PatientRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class TokenService {

    public static final String SECRET = "5367566859703373367639792F423F452848284D6251655468576D5A71347437";

    //TODO: this must will not return a reponse
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    //private String password;

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public TokenService(AdminRepository adminRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public String generateToken(String identifier) {
        Jwts.builder();
        return null ;
    }

    public boolean validateToken(String token, String role) {
        return false;
    }

    public boolean isValidToken(String token) {
        return false;
    }

    public String getToken(Login login) {
        return null;
    }

    public Map<String,String> decodeToken(String token) {return null; }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10080)) // 7 days
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
