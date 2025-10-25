package com.playschool.management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.playschool.management.entity.BackupHistory;
import com.playschool.management.repository.BackupHistoryRepository;

@Service
public class BackupHistoryService {

    @Autowired
    private BackupHistoryRepository repository;

    public List<BackupHistory> getAllBackupHistory() {
        return repository.findAll();
    }

    public BackupHistory createBackup(BackupHistory backup) {
        return repository.save(backup);
    }

    public void deleteBackup(Long id) {
        repository.deleteById(id);
    }
}