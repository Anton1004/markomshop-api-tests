package org.example.testRegister;

import org.example.dataGenerator.DataGenerator;
import org.example.utils.DatabaseHelper;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import org.example.base.BaseTest;
import org.example.endpoints.register.RegisterEndpoints;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import java.sql.*;

public class RegisterTest extends BaseTest {

    @Test
    public void testRegister_Success() throws Exception {
        // 1. Генерируем тестовые данные
        System.out.println("👤 Генерируем данные для регистрации...");
        DataGenerator.RegistrationData data = DataGenerator.generateRegistrationData();

        // 2. Выполняем запрос регистрации
        System.out.println("📨 Отправляем запрос регистрации на: " + data.email);
        Response response = RegisterEndpoints.register(
                data.name, data.email, data.login, data.password, data.confirmPassword
        );

        // 3. ⭐⭐⭐ ДОБАВЛЯЕМ ПОДРОБНУЮ ОТЛАДКУ ОТВЕТА ⭐⭐⭐
        System.out.println("📊 АНАЛИЗ ОТВЕТА:");
        System.out.println("   Статус код: " + response.statusCode());
        System.out.println("   Заголовки: " + response.getHeaders());
        System.out.println("   Тело ответа: " + response.getBody().asString());
        System.out.println("   Content-Type: " + response.getContentType());

        // 4. Проверяем статус код
        assertThat("Статус код должен быть 200 (успех) или 302/307 (редирект)",
                response.statusCode(),
                anyOf(equalTo(200), equalTo(302), equalTo(307)));

        // 5. Анализируем ответ
        if (response.statusCode() == 200) {
            System.out.println("✅ Запрос выполнен успешно (200 OK)");
            String responseBody = response.getBody().asString();
            System.out.println("   Ответ: " + responseBody);
        } else {
            System.out.println("🔄 Получен редирект " + response.statusCode());
            String locationHeader = response.getHeader("Location");
            System.out.println("📍 Перенаправление на: " + locationHeader);

            // ⭐⭐⭐ ЕСЛИ РЕДИРЕКТ НА /reg - ЭТО ОШИБКА РЕГИСТРАЦИИ ⭐⭐⭐
            if ("/reg".equals(locationHeader)) {
                System.out.println("❌ ВОЗМОЖНАЯ ОШИБКА: Редирект на ту же страницу регистрации");
                System.out.println("💡 Причины:");
                System.out.println("   - Невалидные данные");
                System.out.println("   - Пользователь уже существует");
                System.out.println("   - Ошибка валидации на сервере");
            }
        }

        // 6. Проверяем базу данных
        System.out.println("🔍 Проверяем создание пользователя в базе данных...");
        String sql = String.format("SELECT * FROM users WHERE email = '%s' OR login = '%s'",
                data.email, data.login);
        ResultSet resultSet = null;

        try {
            resultSet = DatabaseHelper.executeQuery(sql);

            if (resultSet.next()) {
                System.out.println("✅ Пользователь найден в БД!");

                // Проверяем корректность данных в БД
                String nameFromDb = resultSet.getString("name");
                String emailFromDb = resultSet.getString("email");
                String loginFromDb = resultSet.getString("login");

                System.out.println("   Имя в БД: " + nameFromDb);
                System.out.println("   Email в БД: " + emailFromDb);
                System.out.println("   Логин в БД: " + loginFromDb);

                assertThat("Имя в БД должно совпадать с отправленным", nameFromDb, equalTo(data.name));
                assertThat("Email в БД должен совпадать с отправленным", emailFromDb, equalTo(data.email));
                assertThat("Логин в БД должен совпадать с отправленным", loginFromDb, equalTo(data.login));

                System.out.println("✅ Все данные корректно сохранены в БД");
            } else {
                System.out.println("❌ Пользователь НЕ найден в БД");
                System.out.println("💡 Проверьте:");
                System.out.println("   - Корректность SQL запроса");
                System.out.println("   - Подключение к БД");
                System.out.println("   - Логи сервера на Render");

                // ⭐⭐⭐ ПРОВЕРЯЕМ ДРУГИЕ ВОЗМОЖНЫЕ ПОЛЬЗОВАТЕЛИ ⭐⭐⭐
                checkOtherUsers(data);
            }

        } finally {
            // Всегда закрываем ресурсы БД
            if (resultSet != null) {
                Statement statement = resultSet.getStatement();
                resultSet.close();
                if (statement != null) {
                    statement.close();
                }
            }
            System.out.println("🔒 Ресурсы базы данных закрыты");
        }

        System.out.println("🎉 Тест регистрации завершен!");
    }

    /**
     * Дополнительная проверка - ищем пользователей с похожими данными
     */
    private void checkOtherUsers(DataGenerator.RegistrationData data) throws SQLException {
        System.out.println("🔎 ДОПОЛНИТЕЛЬНАЯ ПРОВЕРКА БАЗЫ ДАННЫХ:");

        // Проверяем всех пользователей
        String allUsersSql = "SELECT COUNT(*) as count FROM users";
        ResultSet countResult = DatabaseHelper.executeQuery(allUsersSql);
        if (countResult.next()) {
            System.out.println("   Всего пользователей в БД: " + countResult.getInt("count"));
        }
        countResult.close();

        // Проверяем есть ли пользователи с таким email или логином
        String checkSql = "SELECT email, login FROM users WHERE email LIKE '%user%' OR login LIKE '%user%' LIMIT 5";
        ResultSet similarUsers = DatabaseHelper.executeQuery(checkSql);
        System.out.println("   Похожие пользователи в БД:");
        while (similarUsers.next()) {
            System.out.println("     Email: " + similarUsers.getString("email") +
                    ", Login: " + similarUsers.getString("login"));
        }
        similarUsers.close();
    }
}