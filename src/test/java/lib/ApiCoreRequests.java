package lib;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class ApiCoreRequests extends BaseTestCase {

    @Step("Отправляет запрос на авторизацию")
    public static Response authRequest(String email, String password) {
        Map<String, String> authDate = new HashMap<>();
        authDate.put("email", email);
        authDate.put("password", password);

        return RestAssured
                .given()
                .body(authDate)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
    }

    @Step("Отправляет запрос на получение данных пользователя")
    public static Response getUserDataRequest(String header, String cookie, String id) {
        return RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/" + id)
                .andReturn();
    }
}
