package triangle_api;

import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.testng.annotations.BeforeClass;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;

public class SetUp {

    @BeforeClass
    public void setup() {

        RestAssured.baseURI = "https://qa-quiz.natera.com/";

        RestAssured.basePath = "/triangle/";

        JsonConfig jsonConfig = JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE);

        RestAssured.config = RestAssured.config()
                .jsonConfig(jsonConfig)
                .encoderConfig(EncoderConfig
                        .encoderConfig()
                        .defaultContentCharset("UTF-8"));

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("X-User", "9ea8c6a6-73f5-4ea1-8ec8-f8a3b00a2564")
                .build()
                .filter(new AllureRestAssured()
                .setRequestTemplate("http-request.ftl")
                .setResponseTemplate("http-response.ftl"));
    }
}
