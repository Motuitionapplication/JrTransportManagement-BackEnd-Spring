package com.playschool.management.config;

import com.playschool.management.entity.Role;
import com.playschool.management.entity.RoleName;
import com.playschool.management.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(RoleName.ROLE_DRIVER));
            roleRepository.save(new Role(RoleName.ROLE_OWNER));
            roleRepository.save(new Role(RoleName.ROLE_CUSTOMER));
            roleRepository.save(new Role(RoleName.ROLE_ADMIN));
            roleRepository.save(new Role(RoleName.ROLE_SUPER_ADMIN));
            System.out.println("Roles initialized successfully!");
        }
    }
}
