package org.example.testRegister;
import org.example.dataGenerator.DataGenerator;
import org.example.utils.DatabaseHelper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import org.example.base.BaseTest;
import org.example.endpoints.register.RegisterEndpoints;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import java.sql.*;

public class RegisterTest extends BaseTest{

    @Test
    public void testRegister_Success() throws Exception{
        DataGenerator.RegistrationData data = DataGenerator.generateRegistrationData();
        Response response = RegisterEndpoints.register(data.name, data.email, data.login, data.password, data.confirmPassword);
        assertThat(response.statusCode(), equalTo(307));
        String sql = String.format(
                "SELECT * FROM users WHERE email = '%s'", data.email);
        ResultSet resultSet = DatabaseHelper.executeQuery(sql);
        assertThat("Пользователь существует", resultSet.next(), is(true));

        String nameFromDb = resultSet.getString("name");
        String emailFromDb = resultSet.getString("email");
        String loginFromDb = resultSet.getString("login");

        assertThat("Имя в БД должно совпадать", nameFromDb, equalTo(data.name));
        assertThat("Email в БД должен совпадать", emailFromDb, equalTo(data.email));
        assertThat("Логин в БД должен совпадать", loginFromDb, equalTo(data.login));

        resultSet.close();
        resultSet.getStatement().close();


    }
}
