package com.playschool.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "settings", uniqueConstraints = @UniqueConstraint(columnNames = {"category", "key"}))
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Category cannot be blank")
    private String category;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Key cannot be blank")
    private String key;

    @Column(nullable = false, length = 500)
    @NotBlank(message = "Value cannot be blank")
    private String value;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Setting setting = (Setting) o;
        return Objects.equals(id, setting.id) &&
               Objects.equals(category, setting.category) &&
               Objects.equals(key, setting.key) &&
               Objects.equals(value, setting.value) &&
               Objects.equals(createdAt, setting.createdAt) &&
               Objects.equals(updatedAt, setting.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category, key, value, createdAt, updatedAt);
    }

    // toString
    @Override
    public String toString() {
        return "Setting{" +
               "id=" + id +
               ", category='" + category + '\'' +
               ", key='" + key + '\'' +
               ", value='" + value + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}