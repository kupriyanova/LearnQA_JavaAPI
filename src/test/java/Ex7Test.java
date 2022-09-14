import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Ex7Test {

    @Test
    public void testEx7() {
        int code = 0;
        String URL = "https://playground.learnqa.ru/api/long_redirect";

        while (code != 200) {
            Response response = getRequest(URL);
            System.out.println(response.getHeaders());
            code = response.getStatusCode();
            System.out.println(code);
            URL = response.getHeader("Location");
            System.out.println(URL);
        }


    }

    public Response getRequest(String url) {
        return RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get(url)
                .andReturn();
    }
}
