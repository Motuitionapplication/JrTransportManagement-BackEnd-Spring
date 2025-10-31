package com.playschool.management.service;

import com.playschool.management.entity.Setting;
import com.playschool.management.repository.SettingRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SettingService {

    private static final Logger logger = LoggerFactory.getLogger(SettingService.class);

    @Autowired
    private SettingRepository settingRepository;

    public List<Setting> getAllSettings() {
        try {
            return settingRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching all settings", e);
            throw e;
        }
    }

    public List<Setting> getSettingsByCategory(String category) {
        try {
            return settingRepository.findByCategory(category);
        } catch (Exception e) {
            logger.error("Error fetching settings by category: " + category, e);
            throw e;
        }
    }

    @Transactional
    public List<Setting> saveOrUpdateSettings(List<Setting> settings) {
        if (settings == null || settings.isEmpty()) {
            throw new IllegalArgumentException("Settings list cannot be null or empty");
        }

        try {
            List<Setting> updatedSettings = settings.stream().map(setting -> {
                Optional<Setting> existingSetting = settingRepository.findByCategoryAndKey(
                        setting.getCategory(), setting.getKey());
                if (existingSetting.isPresent()) {
                    // Update existing setting
                    Setting existing = existingSetting.get();
                    existing.setValue(setting.getValue());
                    existing.setUpdatedAt(setting.getUpdatedAt());
                    return existing;
                } else {
                    // Create new setting
                    return setting;
                }
            }).collect(Collectors.toList());

            // Save all settings (both new and updated)
            List<Setting> savedSettings = settingRepository.saveAll(updatedSettings);
            logger.info("Settings saved successfully: " + savedSettings.size() + " records");
            return savedSettings;
        } catch (Exception e) {
            logger.error("Error saving or updating settings", e);
            throw e;
        }
    }

    public Setting saveSetting(String category, String key, String value) {
        if (category == null || category.isEmpty() || key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Category and key cannot be null or empty");
        }

        try {
            Setting setting = settingRepository.findByCategoryAndKey(category, key)
                    .orElse(new Setting());
            setting.setCategory(category);
            setting.setKey(key);
            setting.setValue(value);
            Setting savedSetting = settingRepository.save(setting);
            logger.info("Setting saved successfully: " + savedSetting);
            return savedSetting;
        } catch (Exception e) {
            logger.error("Error saving setting: category=" + category + ", key=" + key, e);
            throw e;
        }
    }
}