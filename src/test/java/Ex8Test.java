import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class Ex8Test {

    @Test
    public void testEx8() throws InterruptedException {
        String URL = "https://playground.learnqa.ru/ajax/api/longtime_job";
        Map<String, String> params = new HashMap<>();

        Response response = getRequest(URL, params);
        int seconds = response.jsonPath().get("seconds");

        String token = response.jsonPath().get("token");

        params.put("token", token);

        Response before = getRequest(URL, params);
        assertEquals(before.jsonPath().get("status"), "Job is NOT ready");

        sleep(seconds * 1000L);

        Response after = getRequest(URL, params);
        assertEquals(after.jsonPath().get("status"), "Job is ready");

    }

    public Response getRequest(String url, Map<String, String> params) {
        return RestAssured
                .given()
                .queryParams(params)
                .get(url)
                .andReturn();
    }
}
