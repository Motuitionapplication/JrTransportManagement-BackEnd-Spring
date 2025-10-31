
package com.playschool.management.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Schema(
    description = "User entity representing a registered user in the system.",
    example = "{\n  \"id\": 1,\n  \"username\": \"john_doe\",\n  \"email\": \"john.doe@example.com\",\n  \"password\": \"securePassword123\",\n  \"firstName\": \"John\",\n  \"lastName\": \"Doe\",\n  \"phoneNumber\": \"+1234567890\",\n  \"roles\": [\n    {\"id\": 1, \"name\": \"ROLE_OWNER\"}\n  ],\n  \"createdAt\": \"2025-07-21T12:00:00\",\n  \"updatedAt\": \"2025-07-21T12:00:00\",\n  \"isActive\": true\n}"
)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the user", example = "1")
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    @Schema(description = "Unique username of the user", example = "customer", required = true)
    private String username;
    
    @NotBlank
    @Size(max = 100)
    @Email
    @Column(unique = true)
    @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
    private String email;
    
    @NotBlank
    @Size(max = 120)
    @Schema(description = "User's password (hashed)", example = "customer123", required = true)
    private String password;
    
    @NotBlank
    @Size(max = 50)
    @Schema(description = "User's first name", example = "John", required = true)
    private String firstName;
    
    @NotBlank
    @Size(max = 50)
    @Schema(description = "User's last name", example = "Doe", required = true)
    private String lastName;
    
    @Size(max = 15)
    @Schema(description = "User's phone number", example = "+1234567890")
    private String phoneNumber;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Schema(description = "Set of roles assigned to the user")
    private Set<Role> roles = new HashSet<>();
    
    @Column(name = "created_at")
    @Schema(description = "Timestamp when the user was created", example = "2025-07-21T12:00:00")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Schema(description = "Timestamp when the user was last updated", example = "2025-07-21T12:00:00")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    @Schema(description = "Whether the user is active", example = "true")
    private Boolean isActive = true;
    
    // Constructors
    public User() {}
    
    public User(String username, String email, String password, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
