package com.smartcare.backend.controller;

import com.smartcare.backend.DTO.Login;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @PostMapping("/login")
    public String login(@NotNull @Size(min=3) @RequestParam("identifier") String username, @NotNull @Size(min=6) @RequestParam("password") String password){
        Login login = new Login();
        login.setIdentifier(username);
        login.setPassword(password);

        // return a token after validate
        return "";
    }
}
