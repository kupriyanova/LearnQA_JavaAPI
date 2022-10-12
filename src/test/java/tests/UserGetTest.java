package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import static lib.ApiCoreRequests.getUserDataRequest;

public class UserGetTest extends BaseTestCase {

    @Test
    public void testGetUserDataNotAuth() {
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        Assertions.assertJsonHasField(responseUserData, "username");
        String[] expectedFields = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasNotFields(responseUserData, expectedFields);
    }

    @Test
    public void testGetUserDetailsAuthSameUser() {
        Response responseGetAuth = ApiCoreRequests.authRequest("vinkotov@example.com", "1234");

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = getUserDataRequest(header, cookie, "2");
        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }


    @Test
    public void testGetUserDetailsAuthAnotherUser() {
        Response responseGetAuth = ApiCoreRequests.authRequest("vinkotov@example.com", "1234");

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = getUserDataRequest(header, cookie, "1");

        Assertions.assertJsonHasField(responseUserData, "username");
        String[] expectedFields = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasNotFields(responseUserData, expectedFields);
    }
}
