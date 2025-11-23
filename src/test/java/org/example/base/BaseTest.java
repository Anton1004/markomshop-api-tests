package org.example.base;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.config.Apiconfig;
import org.testng.annotations.BeforeSuite;

import static io.restassured.RestAssured.given;

public class BaseTest {

    @BeforeSuite
    public void waitForService() {
        // ⭐⭐⭐ КРИТИЧЕСКИ ВАЖНО: сначала загружаем конфигурацию ⭐⭐⭐
        System.out.println("🔧 Загружаем конфигурацию перед ожиданием сервиса...");
        Apiconfig.setup();

        System.out.println("⏳ Ожидаем полный запуск сервиса на Render...");
        System.out.println("🎯 Базовый URL: " + RestAssured.baseURI);

        int maxAttempts = 36;
        int waitSeconds = 5;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                System.out.println("🔍 Попытка " + attempt + "/" + maxAttempts + " к " + RestAssured.baseURI);

                Response response = given()
                        .relaxedHTTPSValidation()
                        .when()
                        .get("/");

                int statusCode = response.getStatusCode();
                System.out.println("📊 Статус: " + statusCode);

                // Ожидаем 200 или 302 (редирект)
                if (statusCode == 200 || statusCode == 302) {
                    System.out.println("✅ Сервис готов! Статус: " + statusCode);
                    return;
                }

            } catch (Exception e) {
                System.out.println("❌ Ошибка подключения: " + e.getMessage());
            }

            try {
                Thread.sleep(waitSeconds * 1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("⚠️  Сервис не запустился за отведенное время");
    }
}