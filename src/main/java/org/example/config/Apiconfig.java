package org.example.config;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import java.util.Properties;
import java.io.InputStream;

public class Apiconfig {
    public static final String BASE_URI;

    static {
        Properties props = new Properties();
        try (InputStream input = Apiconfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("❌ config.properties не найден в classpath!");
            }

            props.load(input);

            // ⭐⭐⭐ ПРОСТОЙ ВАРИАНТ: используем только API_BASE_URI ⭐⭐⭐
            BASE_URI = props.getProperty("API_BASE_URI");

            if (BASE_URI == null || BASE_URI.trim().isEmpty()) {
                throw new RuntimeException("❌ API_BASE_URI не найден в config.properties");
            }

            System.out.println("🔧 Загружен базовый URL: " + BASE_URI);

        } catch (Exception e) {
            throw new RuntimeException("❌ Не удалось загрузить конфигурацию API", e);
        }
    }

    public static void setup(){
        System.out.println("🎯 Устанавливаем базовый URL: " + BASE_URI);
        RestAssured.baseURI = BASE_URI;

        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.config = RestAssuredConfig.config()
                .redirect(RedirectConfig.redirectConfig().followRedirects(false));
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        System.out.println("✅ Конфигурация RestAssured завершена");
    }
}