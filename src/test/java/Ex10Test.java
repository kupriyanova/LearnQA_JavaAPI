import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Ex10Test {

    @Test
    public void testEx10() {
        Response response = RestAssured
            .get("https://playground.learnqa.ru/api/map")
            .andReturn();

        assertEquals(response.getStatusCode(), 200,
                "Status code should be 200");

        char[] chars = response.getBody().toString().toCharArray();
        assertTrue(chars.length > 15,
                "Length of response less then 15 symbols");

    }
}
//https://playground.learnqa.ru/api/map