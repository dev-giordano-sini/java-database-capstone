package com.smartcare.backend.config;

import com.smartcare.backend.model.Admin;
import com.smartcare.backend.repository.AdminRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrapConfig {
    @Bean
    ApplicationRunner bootstrapAdmin(AdminRepository adminRepository,
                                     PasswordEncoder passwordEncoder,
                                     @Value("${app.bootstrap-admin.username}") String username,
                                     @Value("${app.bootstrap-admin.password}") String password) {
        return arguments -> {
            if (adminRepository.findByUsername(username) == null) {
                Admin admin = new Admin();
                admin.setUsername(username);
                admin.setPassword(passwordEncoder.encode(password));
                adminRepository.save(admin);
            }
        };
    }
}
