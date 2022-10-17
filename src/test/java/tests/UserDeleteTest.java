package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static lib.ApiCoreRequests.*;

@Epic("Delete user cases")
@Feature("Delete user")
public class UserDeleteTest extends BaseTestCase {

    @Description("Первый - на попытку удалить пользователя по ID 2.")
    @Test
    public void testTryDeleteSecondUser() {
        Response responseGetAuth = authRequest("vinkotov@example.com", "1234");
        Response responseDelete = deleteUserRequest(
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                "2");

        Assertions.assertResponseTextEquals(responseDelete,
                "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Description("Второй - позитивный. Создать пользователя, авторизоваться из-под него, удалить, " +
            "затем попробовать получить его данные по ID и убедиться, что пользователь действительно удален.")
    @Test
    public void testDeleteCreatedUser() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = generateUserRequest(userData);

        String userId = responseCreateAuth.getString("id");
        String userEmail = userData.get("email");

        //LOGIN
        Response responseGetAuth = authRequest(userEmail,
                userData.get("password"));

        //DELETE
        deleteUserRequest(
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                userId);

        //GET
        // проверяем, что удален
        Response userDataResponse = getUserDataRequest(
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                userId);
        Assertions.assertResponseTextEquals(userDataResponse,
                "User not found");
    }

    @Description("Третий - негативный, попробовать удалить пользователя, будучи авторизованными другим пользователем.")
    @Test
    public void testDeleteAnotherUser() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = generateUserRequest(userData);

        String userId = responseCreateAuth.getString("id");
        String userEmail = userData.get("email");

        //LOGIN SECOND USER
        Response responseSecondGetAuth = authRequest("vinkotov@example.com", "1234");

        //DELETE
        Response responseDelete = deleteUserRequest(
                this.getHeader(responseSecondGetAuth, "x-csrf-token"),
                this.getCookie(responseSecondGetAuth, "auth_sid"),
                userId);

        Assertions.assertResponseTextEquals(responseDelete,
                "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

        //LOGIN FIRST USER
        Response responseGetAuth = authRequest(userEmail,
                userData.get("password"));

        //GET
        // проверяем, что не удален
        Response userDataResponse = getUserDataRequest(
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                userId);
        Assertions.assertJsonByName(userDataResponse, "id", userId);
        Assertions.assertJsonByName(userDataResponse, "username", userData.get("username"));
        Assertions.assertJsonByName(userDataResponse, "email", userEmail);
        Assertions.assertJsonByName(userDataResponse, "firstName", userData.get("firstName"));
        Assertions.assertJsonByName(userDataResponse, "lastName", userData.get("lastName"));
    }
}
