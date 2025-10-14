package com.playschool.management.controller;

import com.playschool.management.entity.Setting;
import com.playschool.management.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/settings")
public class SettingController {

    private static final Logger logger = LoggerFactory.getLogger(SettingController.class);

    @Autowired
    private SettingService settingService;

    @GetMapping
    public ResponseEntity<List<Setting>> getAllSettings() {
        try {
            List<Setting> settings = settingService.getAllSettings();
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            logger.error("Error fetching all settings", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<Setting>> getSettingsByCategory(@PathVariable String category) {
        try {
            List<Setting> settings = settingService.getSettingsByCategory(category);
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            logger.error("Error fetching settings by category: " + category, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<List<Setting>> saveOrUpdateSettings(@RequestBody List<Setting> settings) {
        try {
            // Save or update settings
            List<Setting> updatedSettings = settingService.saveOrUpdateSettings(settings);
            return ResponseEntity.ok(updatedSettings);
        } catch (Exception e) {
            logger.error("Error saving or updating settings", e);
            return ResponseEntity.badRequest().build();
        }
    }
}