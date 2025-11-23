package org.example.endpoints.register;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class RegisterEndpoints {
    private static final String REGISTER = "/reg";

    public static Response register(String name, String email, String login, String password, String confirm_password) {
        // ⭐⭐⭐ ИСПОЛЬЗУЕМ JSON FORMAT ВМЕСТО TEXT ⭐⭐⭐
        String body = String.format("""
                {
                "name": "%s",
                "email": "%s",
                "login": "%s",
                "password": "%s",
                "confirm_password": "%s"
                }
                """, name, email, login, password, confirm_password);

        System.out.println("📤 Отправляем запрос на: " + RestAssured.baseURI + REGISTER);
        System.out.println("📝 Тело запроса: " + body);

        return given()
                .header("Content-Type", "application/json")  // ⭐⭐⭐ ВАЖНО: Указываем JSON ⭐⭐⭐
                .body(body)
                .log().all()
                .when()
                .post(REGISTER);
    }
}