package com.playschool.management.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@Profile({"postgresql", "cloud", "dev"})
public class DatabaseConnectionTest implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionTest.class);
    
    private final DataSource dataSource;
    
    public DatabaseConnectionTest(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public void run(String... args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            logger.info("✅ Database connection successful!");
            logger.info("📊 Database URL: {}", connection.getMetaData().getURL());
            logger.info("🔧 Database Product: {}", connection.getMetaData().getDatabaseProductName());
            logger.info("📝 Database Version: {}", connection.getMetaData().getDatabaseProductVersion());
            logger.info("👤 Connected as: {}", connection.getMetaData().getUserName());
        } catch (Exception e) {
            logger.error("❌ Database connection failed: {}", e.getMessage(), e);
        }
    }
}
