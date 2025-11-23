package org.example.config;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.HttpClientConfig;
import java.util.Properties;
import java.io.InputStream;

public class Apiconfig {
    public static final String BASE_URI;

    // Статический блок выполняется при загрузке класса
    static {
        Properties props = new Properties();
        try (InputStream input = Apiconfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            props.load(input);
            BASE_URI = props.getProperty("api.base_uri");
            System.out.println("🔧 Загружен базовый URL: " + BASE_URI);
        } catch (Exception e) {
            throw new RuntimeException("❌ Не удалось загрузить конфигурацию API", e);
        }
    }

    /**
     * НАСТРОЙКА REST ASSURED ДЛЯ РАБОТЫ С RENDER
     *
     * ПРОБЛЕМА:
     * - Render использует HTTPS с самоподписанными сертификатами
     * - Автоматические редиректы мешают анализировать ответы
     * - Таймауты при медленном запуске
     *
     * РЕШЕНИЕ: Настраиваем RestAssured для работы в этих условиях
     */
    public static void setup(){
        // 1. Устанавливаем базовый URL
        RestAssured.baseURI = BASE_URI;
        System.out.println("🎯 Базовый URL установлен: " + BASE_URI);

        // 2. ⭐⭐⭐ ВАЖНО: Отключаем SSL валидацию ⭐⭐⭐
        // Render использует самоподписанные сертификаты, которые вызывают SSL ошибки
        RestAssured.useRelaxedHTTPSValidation();
        System.out.println("🔓 SSL валидация отключена");

        // 3. ⭐⭐⭐ ВАЖНО: Отключаем автоматическое следование редиректам ⭐⭐⭐
        // Это позволяет нам видеть 307 статус и анализировать его
        RestAssured.config = RestAssuredConfig.config()
                .redirect(RedirectConfig.redirectConfig().followRedirects(false));
        System.out.println("🚫 Автоматические редиректы отключены");

        // 4. Включаем подробное логирование при ошибках валидации
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        System.out.println("📝 Логирование запросов/ответов включено");

        // 5. ⭐⭐⭐ УВЕЛИЧИВАЕМ ТАЙМАУТЫ ⭐⭐⭐
        // Render может медленно отвечать во время запуска
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 30000)    // 30 секунд на подключение
                        .setParam("http.socket.timeout", 30000)        // 30 секунд на получение данных
                );
        System.out.println("⏰ Таймауты увеличены до 30 секунд");

        System.out.println("✅ Конфигурация RestAssured завершена успешно");
    }
}