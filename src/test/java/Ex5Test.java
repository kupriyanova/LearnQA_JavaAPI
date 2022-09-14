import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

public class Ex5Test {

    @Test
    public void testEx5() {
        JsonPath jsonPath = RestAssured.given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
//        jsonPath.prettyPrint();
        String s = jsonPath.get("messages.message[1]");
        System.out.println(s);
    }
}
