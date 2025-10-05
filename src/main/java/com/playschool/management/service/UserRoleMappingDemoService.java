package com.playschool.management.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.playschool.management.entity.Role;
import com.playschool.management.entity.RoleName;
import com.playschool.management.entity.User;
import com.playschool.management.repository.RoleRepository;
import com.playschool.management.repository.UserRepository;

@Service
public class UserRoleMappingDemoService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserRoleMappingDemoService.class);
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    public UserRoleMappingDemoService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    
    /**
     * This method demonstrates exactly how user.id syncs with role.id
     * in the user_roles junction table
     */
    public void demonstrateUserRoleSync() {
        // 1. Get user by ID - this user.id will be used as user_id in junction table
        User user = userRepository.findById(1L).orElse(null);
        if (user != null) {
            logger.info("User ID: {} (this becomes user_id in user_roles table)", user.getId());
            logger.info("Username: {}", user.getUsername());
        }
        
        // 2. Get role by name - this role.id will be used as role_id in junction table  
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElse(null);
        if (adminRole != null) {
            logger.info("Role ID: {} (this becomes role_id in user_roles table)", adminRole.getId());
            logger.info("Role Name: {}", adminRole.getName());
        }
        // You can also demonstrate with other roles, e.g.:
        // Role driverRole = roleRepository.findByName(RoleName.ROLE_DRIVER).orElse(null);
        // ...

        // 3. When we add role to user, JPA creates the mapping
        if (user != null && adminRole != null) {
            user.getRoles().add(adminRole);
            userRepository.save(user);

            logger.info("JPA will execute: INSERT INTO user_roles (user_id, role_id) VALUES ({}, {})", 
                       user.getId(), adminRole.getId());
        }
        
        // 4. Show the actual roles for verification
        if (user != null) {
            User savedUser = userRepository.findById(user.getId()).orElse(null);
            if (savedUser != null) {
                logger.info("User {} now has roles:", savedUser.getUsername());
                savedUser.getRoles().forEach(role -> 
                    logger.info("- Role ID: {}, Name: {}", role.getId(), role.getName())
                );
            }
        }
    }
    
    /**
     * This method shows the database records that would be created
     */
    public void showJunctionTableMapping(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            logger.info("Junction table user_roles records for user {}", user.getUsername());
            logger.info("user_id | role_id | (meaning)");
            logger.info("--------|---------|----------");
            
            user.getRoles().forEach(role -> {
                logger.info("{} | {} | User '{}' has role '{}'",
                        String.format("%7d", user.getId()),
                        String.format("%7d", role.getId()),
                        user.getUsername(),
                        role.getName().toString().replace("ROLE_", "")
                );
            });
        }
    }
}
