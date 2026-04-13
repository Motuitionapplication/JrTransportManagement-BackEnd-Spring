package com.transport.management.config;

import com.transport.management.entity.Role;
import com.transport.management.entity.RoleName;
import com.transport.management.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final RoleRepository roleRepository;
    
    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Ensure all required roles exist in the database
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(new Role(roleName));
                logger.info("Inserted missing role: {}", roleName);
            }
        }
        logger.info("Role check/insert completed.");
    }
}

