package com.smartcare.backend.service;

import com.smartcare.backend.model.Admin;
import com.smartcare.backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    public Admin findByUsername(String username) {
        Admin adminToSearch = new Admin();
        adminToSearch.setUsername(username);
        Example<Admin> example = Example.of(adminToSearch);
        Optional<Admin> optionalAdmin = adminRepository.findOne(example);

        return optionalAdmin.orElse(new Admin());
    }
}
