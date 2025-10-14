package com.playschool.management.repository;

import com.playschool.management.entity.Setting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SettingRepository extends JpaRepository<Setting, Long> {

    // Find a setting by category and key
    Optional<Setting> findByCategoryAndKey(String category, String key);

    // Find all settings by category
    List<Setting> findByCategory(String category);

    // Delete all settings by category
    void deleteByCategory(String category);

    // Find settings by category with pagination and sorting
    Page<Setting> findByCategory(String category, Pageable pageable);

    // Custom query to find settings by category and key pattern
    @Query("SELECT s FROM Setting s WHERE s.category = :category AND s.key LIKE %:keyPattern%")
    List<Setting> findByCategoryAndKeyPattern(@Param("category") String category, @Param("keyPattern") String keyPattern);

    // New method to check if a setting exists by category and key
    boolean existsByCategoryAndKey(String category, String key);
}