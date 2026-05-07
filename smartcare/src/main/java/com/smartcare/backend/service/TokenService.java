package com.smartcare.backend.service;

import com.smartcare.backend.DTO.Login;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    //TODO: this must will not return a reponse
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    //private String password;

    public boolean validateToken(String token, String role) {
        return false;
    }

    public boolean isValidToken(String token) {
        return false;
    }

    public String getToken(Login loginDTO) {
        return null;
    }
}
