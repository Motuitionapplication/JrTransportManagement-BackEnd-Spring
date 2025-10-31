package com.playschool.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playschool.management.entity.BackupHistory;
import com.playschool.management.service.BackupHistoryService;



@RestController
@RequestMapping("/api/settings/backup-history")
public class BackupHistoryController {
	 @Autowired
	    private BackupHistoryService service;

	    @GetMapping
	    public ResponseEntity<List<BackupHistory>> getAllBackupHistory() {
	        return ResponseEntity.ok(service.getAllBackupHistory());
	    }

	    @PostMapping
	    public ResponseEntity<BackupHistory> createBackup(@RequestBody BackupHistory backup) {
	        return ResponseEntity.ok(service.createBackup(backup));
	    }

	    @DeleteMapping("/{id}")
	    public ResponseEntity<Void> deleteBackup(@PathVariable Long id) {
	        service.deleteBackup(id);
	        return ResponseEntity.noContent().build();
	    }
	}


