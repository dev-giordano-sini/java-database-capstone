package com.smartcare.backend.service;

import org.springframework.stereotype.Service;

@Service
public class Validator {

    //TODO: this must will not return a reponse
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    //private String password;

    public boolean validateToken(String token, String role) {
        return false;
    }
}
