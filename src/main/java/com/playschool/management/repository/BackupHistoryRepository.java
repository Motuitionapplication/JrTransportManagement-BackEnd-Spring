package com.playschool.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.playschool.management.entity.BackupHistory;

@Repository
public interface BackupHistoryRepository extends JpaRepository<BackupHistory, Long> {

    // Find all backup history records
    List<BackupHistory> findAll();

    // Save a new backup history record
    <S extends BackupHistory> S save(S entity);

    // Delete a backup history record by ID
    void deleteById(Long id);

    // Optional: Find a backup history record by ID
    Optional<BackupHistory> findById(Long id);
}