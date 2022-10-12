package tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {

    private final String email = "vinkotov@example.com";

    @Description("Создание пользователя с некорректным email - без символа @")
    @Test
    public void testCreateUserWithIncorrectEmail() {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email.replace("@", ""));
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth,
                "Invalid email format");
    }

    @Description("Создание пользователя с очень коротким именем в один символ")
    @Test
    public void testCreateUserWithShortName() {
        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", "1");
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth,
                "The value of 'firstName' field is too short");
    }

    @Description("Создание пользователя с очень длинным именем - длиннее 250 символов")
    @Test
    public void testCreateUserWithLongName() {
        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", RandomStringUtils.random(251, true, true));
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth,
                "The value of 'firstName' field is too long");
    }

    @Description("Создание пользователя без указания одного из полей - " +
            "с помощью @ParameterizedTest необходимо проверить, " +
            "что отсутствие любого параметра не дает зарегистрировать пользователя")
    @ParameterizedTest
    @ValueSource(strings = {"password", "username", "firstName", "lastName", "email"})
    public void testCreateUserNotEnoughField(String deleteParameter) {
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);
        userData.remove(deleteParameter);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth,
                "The following required params are missed: " + deleteParameter);
    }

    @Test
    public void testCreateUserWithExistingEmail() {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth,
                "Users with email '" + email + "' already exists");
    }

    @Test
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth,
                200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }
}
