-- Initial Schema Placeholder for Liquibase/Flyway compatibility inside Spring Boot resources
CREATE TABLE IF NOT EXISTS system_metadata (
    key VARCHAR(50) PRIMARY KEY,
    value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
