package com.smartcare.backend.controller;

import com.smartcare.backend.DTO.LoginDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @PostMapping("/login")
    public String login(@NotNull @Size(min=3) @RequestParam("identifier") String username, @NotNull @Size(min=6) @RequestParam("password") String password){
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setIdentifier(username);
        loginDTO.setPassword(password);

        // return a token after validate
        return "";
    }
}
