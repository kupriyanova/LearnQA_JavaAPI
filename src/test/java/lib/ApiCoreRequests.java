package lib;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class ApiCoreRequests extends BaseTestCase {

    @Step("Отправляет запрос на регистрацию нового пользователя")
    public static JsonPath generateUserRequest(Map<String, String> userData) {
        return RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();
    }

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

    @Step("Отправляет запрос на изменение данных пользователя")
    public static Response putEditUserRequest(String header, String cookie,Map<String, String> editData, String userId) {
        return  RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
    }
    @Step("Удаляет пользователя")
    public static Response deleteUserRequest(String header, String cookie, String userId) {
        return  RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .delete("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
    }
}
