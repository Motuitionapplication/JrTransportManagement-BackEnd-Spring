package com.playschool.management.controller;

import com.playschool.management.entity.User;
import com.playschool.management.repository.UserRepository;
import com.playschool.management.entity.Role;
import com.playschool.management.entity.RoleName;
import com.playschool.management.repository.RoleRepository;
import com.playschool.management.dto.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    // Add new admin
    @PostMapping("/add")
    public ResponseEntity<?> addAdmin(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username already exists!"));
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email already exists!"));
        }
        Set<Role> roles = new HashSet<>();
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found."));
        roles.add(adminRole);
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Admin added successfully!"));
    }

    // Fetch all admins
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllAdmins() {
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found."));
        List<User> admins = userRepository.findByRoles(adminRole);
        return ResponseEntity.ok(admins);
    }

    // Fetch admin by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getAdminById(@PathVariable Long id) {
        Optional<User> adminOpt = userRepository.findById(id);
        return adminOpt.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Edit admin
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editAdmin(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> adminOpt = userRepository.findById(id);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User admin = adminOpt.get();
        admin.setFirstName(updatedUser.getFirstName());
        admin.setLastName(updatedUser.getLastName());
        admin.setEmail(updatedUser.getEmail());
        admin.setPhoneNumber(updatedUser.getPhoneNumber());
        userRepository.save(admin);
        return ResponseEntity.ok(new MessageResponse("Admin updated successfully!"));
    }
    
 // Delete admin
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        Optional<User> adminOpt = userRepository.findById(id);

        if (adminOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User admin = adminOpt.get();
        // Ensure this user is actually an admin before deleting
        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);

        if (!isAdmin) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("The specified user is not an admin."));
        }

        userRepository.delete(admin);
        return ResponseEntity.ok(new MessageResponse("Admin deleted successfully!"));
    }
 // Fetch current password for admin (for testing only, not recommended for production)
 @GetMapping("/password/{id}")
 public ResponseEntity<?> getAdminPassword(@PathVariable Long id) {
     Optional<User> adminOpt = userRepository.findById(id);
     if (adminOpt.isEmpty()) {
         return ResponseEntity.notFound().build();
     }
     User admin = adminOpt.get();
     boolean isAdmin = admin.getRoles().stream()
             .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);

     if (!isAdmin) {
         return ResponseEntity.badRequest()
                 .body(new MessageResponse("The specified user is not an admin."));
     }

     // WARNING: Never expose plain passwords in production!
     return ResponseEntity.ok(new MessageResponse(admin.getPassword()));
 }
//Change admin password
@PutMapping("/change-password/{id}")
public ResponseEntity<?> changeAdminPassword(@PathVariable Long id, @RequestBody User user) {
  Optional<User> adminOpt = userRepository.findById(id);
  if (adminOpt.isEmpty()) {
      return ResponseEntity.notFound().build();
  }
  User admin = adminOpt.get();
  boolean isAdmin = admin.getRoles().stream()
          .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);

  if (!isAdmin) {
      return ResponseEntity.badRequest()
              .body(new MessageResponse("The specified user is not an admin."));
  }

  admin.setPassword(user.getPassword()); // You may want to hash the password here!
  userRepository.save(admin);
  return ResponseEntity.ok(new MessageResponse("Password updated successfully!"));
}

    
}
