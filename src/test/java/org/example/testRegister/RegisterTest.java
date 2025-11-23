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

    /**
     * ТЕСТ РЕГИСТРАЦИИ ПОЛЬЗОВАТЕЛЯ
     *
     * ИЗМЕНЕНИЯ ПОСЛЕ ДОБАВЛЕНИЯ BaseTest:
     * - Сервис УЖЕ ГОТОВ когда выполняется этот тест (мы ждали в @BeforeSuite)
     * - Мы можем ожидать 200 OK вместо 307
     * - Но оставляем обработку 307 на случай если что-то пошло не так
     */
    @Test
    public void testRegister_Success() throws Exception {
        // 1. Генерируем тестовые данные
        System.out.println("👤 Генерируем данные для регистрации...");
        DataGenerator.RegistrationData data = DataGenerator.generateRegistrationData();

        // 2. Выполняем запрос регистрации
        System.out.println("📨 Отправляем запрос регистрации...");
        Response response = RegisterEndpoints.register(
                data.name, data.email, data.login, data.password, data.confirmPassword
        );

        // 3. ⭐⭐⭐ ИЗМЕНЕННАЯ ПРОВЕРКА СТАТУС КОДА ⭐⭐⭐
        // После ожидания в BaseTest мы ОЖИДАЕМ 200, но принимаем 307 на всякий случай
        assertThat("Статус код должен быть 200 (успех) или 307 (редирект)",
                response.statusCode(),
                anyOf(equalTo(200), equalTo(307)));

        // 4. Анализируем ответ
        if (response.statusCode() == 200) {
            System.out.println("✅ Запрос выполнен успешно (200 OK)");
        } else if (response.statusCode() == 307) {
            // Это маловероятно после BaseTest, но обрабатываем на всякий случай
            String locationHeader = response.getHeader("Location");
            System.out.println("🔄 Получен редирект 307 на: " + locationHeader);
            assertThat("При 307 должен быть заголовок Location",
                    locationHeader, notNullValue());
        }

        // 5. ⭐⭐⭐ ПРОВЕРЯЕМ БАЗУ ДАННЫХ ⭐⭐⭐
        // Главный признак успеха - пользователь создан в БД
        System.out.println("🔍 Проверяем создание пользователя в базе данных...");
        String sql = String.format("SELECT * FROM users WHERE email = '%s'", data.email);
        ResultSet resultSet = null;

        try {
            resultSet = DatabaseHelper.executeQuery(sql);

            // Проверяем что пользователь действительно создан
            assertThat("Пользователь должен быть создан в базе данных",
                    resultSet.next(), is(true));
            System.out.println("✅ Пользователь успешно создан в БД");

            // Проверяем корректность данных в БД
            String nameFromDb = resultSet.getString("name");
            String emailFromDb = resultSet.getString("email");
            String loginFromDb = resultSet.getString("login");

            assertThat("Имя в БД должно совпадать с отправленным", nameFromDb, equalTo(data.name));
            assertThat("Email в БД должен совпадать с отправленным", emailFromDb, equalTo(data.email));
            assertThat("Логин в БД должен совпадать с отправленным", loginFromDb, equalTo(data.login));

            System.out.println("✅ Все данные корректно сохранены в БД");

        } finally {
            // ⭐⭐⭐ ВАЖНО: Всегда закрываем ресурсы БД ⭐⭐⭐
            if (resultSet != null) {
                Statement statement = resultSet.getStatement();
                resultSet.close();
                if (statement != null) {
                    statement.close();
                }
            }
            System.out.println("🔒 Ресурсы базы данных закрыты");
        }

        System.out.println("🎉 Тест регистрации завершен успешно!");
    }
}