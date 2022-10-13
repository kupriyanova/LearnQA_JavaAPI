package tests;

import io.qameta.allure.Description;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.ApiCoreRequests.*;

public class UserEditTest extends BaseTestCase {

    @Description("Попытаемся изменить данные пользователя, будучи неавторизованными")
    @Test
    public void testEditWithoutAuth() {
        //EDIT
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newName);

        Response responseEditUser = putEditUserRequest(
                "", "", editData, "2");
        Assertions.assertResponseTextEquals(responseEditUser,
                "Auth token not supplied");

        //GET
        Response userDataResponse = getUserDataRequest("", "", "2");
        Assertions.assertJsonByName(userDataResponse, "username", "Vitaliy");
    }

    @Description("Попытаемся изменить данные пользователя, будучи авторизованными другим пользователем")
    @Test
    public void testEditWithAnotherUser() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        generateUserRequest(userData);

        //LOGIN
        Response responseGetAuthForEdit = authRequest("vinkotov@example.com", "1234");
        Response responseGetAuthAnother = authRequest(userData.get("email"), userData.get("password"));

        //EDIT
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        // пытаемся редактировать другого пользователя
        Response responseEditUser = putEditUserRequest(
                this.getHeader(responseGetAuthAnother,"x-csrf-token"),
                this.getCookie(responseGetAuthAnother, "auth_sid"),
                editData, "2");
        responseEditUser.prettyPrint();

        //GET
        // проверяем, что данные не изменились
        Response userDataResponse = getUserDataRequest(
                this.getHeader(responseGetAuthForEdit,"x-csrf-token"),
                this.getCookie(responseGetAuthForEdit, "auth_sid"),
                "2");
        Assertions.assertJsonByName(userDataResponse, "firstName", "Vitalii");
    }

    @Description("Попытаемся изменить email пользователя, будучи авторизованными тем же пользователем, на новый email без символа @ ")
    @Test
    public void testEditWithWrongEmail() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = generateUserRequest(userData);

        String userId = responseCreateAuth.getString("id");
        String userEmail = userData.get("email");

        //LOGIN
        Response responseGetAuth = authRequest(userEmail, userData.get("password"));

        //EDIT
        Map<String, String> editData = new HashMap<>();
        editData.put("email", userEmail.replace("@", ""));

        // пытаемся редактировать с неправильным email
        Response responseEditUser = putEditUserRequest(
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData, userId);
        Assertions.assertResponseTextEquals(responseEditUser,
                "Invalid email format");

        //GET
        // проверяем, что данные не изменились
        Response userDataResponse = getUserDataRequest(
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                userId);
        Assertions.assertJsonByName(userDataResponse, "email", userEmail);
    }

    @Description("Попытаемся изменить firstName пользователя, будучи авторизованными тем же пользователем, на очень короткое значение в один символ")
    @Test
    public void testEditFirstNameTooShort() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = generateUserRequest(userData);

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Response responseGetAuth = authRequest(userData.get("email"), userData.get("password"));

        //EDIT
        String newName = "1";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = putEditUserRequest(
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData, userId);
        Assertions.assertJsonByName(responseEditUser, "error",
                "Too short value for field firstName");

        //GET
        Response responseUserData = getUserDataRequest(
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                userId);

        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }

    @Test
    public void testEditJustCreated() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = generateUserRequest(userData);

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Response responseGetAuth = authRequest(userData.get("email"), userData.get("password"));

        //EDIT
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        putEditUserRequest(
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData,
                userId);

        //GET
        Response responseUserData = getUserDataRequest(
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                userId);

        Assertions.assertJsonByName(responseUserData, "firstName", newName);

    }
}
