package org.example.utils;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseHelper {
    // УБИРАЕМ хардкодные настройки
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    // ДОБАВЛЯЕМ блок загрузки из config.properties
    static {
        Properties props = new Properties();
        try (InputStream input = DatabaseHelper.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            props.load(input);
            // БЕРЕМ настройки из конфига
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить конфигурацию базы данных", e);
        }
    }

    // Метод остается без изменений
    public static ResultSet executeQuery(String sql) throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }
}