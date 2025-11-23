package org.example.config;

import io.restassured.RestAssured;
import java.util.Properties;
import java.io.InputStream;

public class Apiconfig {
    public static final String BASE_URI;

    static {
        Properties props = new Properties();
        try (InputStream input = Apiconfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            props.load(input);
            BASE_URI = props.getProperty("api.base_uri");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load API configuration", e);
        }
    }

    public static void setup(){
        RestAssured.baseURI = BASE_URI;

        // ↓ ДОБАВЬ ЭТИ СТРОКИ ДЛЯ ИСПРАВЛЕНИЯ 307 ОШИБКИ ↓
        RestAssured.useRelaxedHTTPSValidation(); // Игнорировать SSL ошибки
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(); // Логировать при ошибках
    }
}