package com.smartcare.backend.repository;


import com.smartcare.backend.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    public Admin findByUsername(String username);
}
