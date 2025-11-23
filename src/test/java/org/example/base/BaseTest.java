package org.example.base;
import org.example.config.Apiconfig;
import org.testng.annotations.BeforeClass;
import static io.restassured.RestAssured.*;

public abstract class BaseTest {
    @BeforeClass
    public void setup(){
        Apiconfig.setup();
        requestSpecification = given()
                .contentType("application/json");
    }

}
