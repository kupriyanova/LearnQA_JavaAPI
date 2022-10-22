import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class Ex9Test extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final List<String> passwords = List.of(
            "123456", "123456789", "qwerty", "password", "1111111", "12345678",
            "abc123", "1234567", "12345",
            "1234567890", "123123", "000000", "Iloveyou", "1234", "1q2w3e4r5t",
            "Qwertyuiop", "123","Monkey", "Dragon", "football",
            "monkey", "monkey", "111111",
            "1234", "football", "1234567890", "letmein",
            "letmein", "baseball", "1234", "sunshine", "iloveyou",
            "trustno1", "princess", "football",
            "baseball", "adobe123", "baseball", "1234", "iloveyou", "123123",
            "baseball", "123123", "welcome", "login", "admin", "princess",
            "trustno1", "admin", "monkey", "1234567890", "welcome", "welcome", "admin", "qwerty123",
            "1234567890", "letmein", "solo", "monkey", "welcome", "1q2w3e4r",
            "master", "sunshine", "letmein", "111111", "login", "666666", "admin",
            "sunshine", "master", "photoshop", "111111", "1qaz2wsx", "admin", "qwertyuiop",
            "ashley", "123123", "1234", "mustang", "121212", "starwars", "football", "654321",
            "bailey", "welcome", "monkey", "access", "master", "flower", "123123", "123123", "555555",
            "passw0rd", "shadow", "monkey", "passw0rd", "monkey", "lovely",
            "admin", "654321", "555555", "12345678", "lovely",
            "7777777", "123123", "welcome",
            "1234567890", "888888","princess", "qwerty123", "1q2w3e", "123qwe",
            "aa12345678", "shadow", "ashley", "sunshine", "master", "letmein", "dragon", "passw0rd", "654321", "7777777",
            "123123", "football", "michael", "login", "sunshine", "master", "!@#$%^&*", "welcome",
            "654321", "jesus", "superman", "princess", "master", "hello", "charlie", "888888",
            "1234", "superman", "michael", "princess", "696969", "qwertyuiop", "hottie", "freedom", "aa123456", "princess",
            "qwertyuiop", "iloveyou", "qazwsx", "ninja", "azerty", "123123", "solo", "loveme", "whatever", "donald",
            "123321", "michael", "mustang", "trustno1", "batman", "passw0rd", "zaq1zaq1", "qazwsx",
            "password123", "Football", "password1", "trustno1", "starwars", "trustno1", "qwerty123", "123qwe");
    String cookie;

    @Test
    public void testEx9() {
        String URL = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
        String checkURL = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";
        Map<String, String> authData = new HashMap<>();
        authData.put("login", "super_admin");

        for (String pass : passwords) {
            authData.put("password", pass);
            Response responseGetCookie = apiCoreRequests
                    .makePostRequest(URL, authData);
            assertEquals(responseGetCookie.statusCode(), 200, "Указан не верный логин");
            this.cookie = this.getCookie(responseGetCookie, "auth_cookie");

//            Response responseCheckCookie = apiCoreRequests
//                    .makeGetRequestWithCookie(checkURL, cookie);
            Map<String, String> cookieData = new HashMap<>();
            authData.put("auth_cookie", cookie);
            Response responseCheckCookie = apiCoreRequests.makePostRequest(checkURL,cookieData);

            assertEquals(responseCheckCookie.asString(),
                    "You are NOT authorized",
            "Правильный пароль " + pass);
        }
        fail("Правильный пароль не найден");

    }
}
