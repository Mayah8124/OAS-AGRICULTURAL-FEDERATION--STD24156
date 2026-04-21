package org.ingredients.agriculturalfederation.config;

import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class DataSourceConfig {
    public Connection getConnection() {
        try {
            String url = System.getenv("URL");
            String username = System.getenv("USERNAME");
            String password = System.getenv("PASSWORD");

            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException("Error while connecting to the database", e);
        }
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error closing database connection", e);
        }
    }
}
