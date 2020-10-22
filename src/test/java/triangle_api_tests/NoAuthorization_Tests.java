package triangle_api_tests;

import io.qameta.allure.Description;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import java.util.List;

import static triangle_api.Helpers.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class NoAuthorization_Tests {

    @Test(description = "Code 401 verification for POST/triangle")
    @Description("This test verifies that POST/triangle entry point returns code 401 in cases of " +
            "invalid personal token, i.e. 'X-User' value")
    public void addTriangle_code401_Test() {
        // let's set a specification with invalid personal token
        RequestSpecification spec = new RequestSpecBuilder()
                .addHeader("X-User", "invalid_personal_token_value")
                .setBaseUri("https://qa-quiz.natera.com/")
                .setBasePath("/triangle/")
                .build();
        // let's specify a valid sides values
        double firstSide = 3, secondSide = 4, thirdSide = 5;

        String payload = "{\"separator\": \";\", \"input\": \""+firstSide+";"+secondSide+";"+thirdSide+"\"}";
        // send a valid request with invalid token and verify the response
        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .spec(spec)
                .contentType(ContentType.JSON)
                .body(payload)
        .when()
                .post("/")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Unauthorized") );
    }


    @Test(description = "Code 401 verification for GET/triangle/{triangleId}")
    @Description("This test verifies that GET/triangle/{triangleId} entry point returns code 401 in cases of " +
            "invalid personal token, i.e. 'X-User' value")
    public void getTriangle_code401_Test() {
        // let's set a specification with invalid personal token
        RequestSpecification spec = new RequestSpecBuilder()
                .addHeader("X-User", "invalid_personal_token_value")
                .setBaseUri("https://qa-quiz.natera.com/")
                .setBasePath("/triangle/")
                .build();

        String id;

        // let's check if there are no triangles yet, if there's none add a new one and save its id,
        // otherwise, pick some id from existing ones
        List<String> triangles = getAllTriangles();

        if (triangles.isEmpty() ) {
            id = createTriangle(2,4,5);
        } else {
            id = triangles.get(0);
        }
        // send a valid request with invalid token and verify the response
        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .spec(spec)
                .contentType(ContentType.JSON)
                .pathParam("triangleID", id)
        .when()
                .get("/{triangleID}")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Unauthorized") );
    }


    @Test(description = "Code 401 verification for DELETE/triangle/{triangleId}")
    @Description("This test verifies that DELETE/triangle/{triangleId} entry point returns code 401 in cases of " +
            "invalid personal token, i.e. 'X-User' value")
    public void deleteTriangle_code401_Test() {
        // let's set a specification with invalid personal token
        RequestSpecification spec = new RequestSpecBuilder()
                .addHeader("X-User", "invalid_personal_token_value")
                .setBaseUri("https://qa-quiz.natera.com/")
                .setBasePath("/triangle/")
                .build();

        String id;

        // let's check if there are no triangles yet, if there's none add a new one and save its id,
        // otherwise, pick some id from existing ones
        List<String> triangles = getAllTriangles();

        if (triangles.isEmpty() ) {
            id = createTriangle(3,7,9);
        } else {
            id = triangles.get(0);
        }
        // send a valid request with invalid token and verify the response
        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .spec(spec)
                .contentType(ContentType.JSON)
                .pathParam("triangleID", id)
        .when()
                .delete("/{triangleID}")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Unauthorized") );
    }


    @Test(description = "Code 401 verification for GET/triangle/all")
    @Description("This test verifies that GET/triangle/all entry point returns code 401 in cases of " +
            "invalid personal token, i.e. 'X-User' value")
    public void getTriangleAll_code401_Test() {
        // let's set a specification with invalid personal token
        RequestSpecification spec = new RequestSpecBuilder()
                .addHeader("X-User", "invalid_personal_token_value")
                .setBaseUri("https://qa-quiz.natera.com/")
                .setBasePath("/triangle/")
                .build();

        // send a valid request with invalid token and verify the response
        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .spec(spec)
                .contentType(ContentType.JSON)
        .when()
                .get("/all")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Unauthorized") );
    }


    @Test(description = "Code 401 verification for GET/triangle/{triangleId}/perimeter")
    @Description("This test verifies that GET/triangle/{triangleId}/perimeter entry point returns code 401 in cases of " +
            "invalid personal token, i.e. 'X-User' value")
    public void getTrianglePerimeter_code401_Test() {
        // let's set a specification with invalid personal token
        RequestSpecification spec = new RequestSpecBuilder()
                .addHeader("X-User", "invalid_personal_token_value")
                .setBaseUri("https://qa-quiz.natera.com/")
                .setBasePath("/triangle/")
                .build();

        String id;

        // let's check if there are no triangles yet, if there's none add a new one and save its id,
        // otherwise, pick some id from existing ones
        List<String> triangles = getAllTriangles();

        if (triangles.isEmpty() ) {
            id = createTriangle(12,6,8);
        } else {
            id = triangles.get(0);
        }
        // send a valid request with invalid token and verify the response
        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .spec(spec)
                .contentType(ContentType.JSON)
                .pathParam("triangleID", id)
        .when()
                .get("/{triangleID}/perimeter")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Unauthorized") );
    }


    @Test(description = "Code 401 verification for GET/triangle/{triangleId}/area")
    @Description("This test verifies that GET/triangle/{triangleId}/area entry point returns code 401 in cases of " +
            "invalid personal token, i.e. 'X-User' value")
    public void getTriangleArea_code401_Test() {
        // let's set a specification with invalid personal token
        RequestSpecification spec = new RequestSpecBuilder()
                .addHeader("X-User", "invalid_personal_token_value")
                .setBaseUri("https://qa-quiz.natera.com/")
                .setBasePath("/triangle/")
                .build();

        String id;

        // let's check if there are no triangles yet, if there's none add a new one and save its id,
        // otherwise, pick some id from existing ones
        List<String> triangles = getAllTriangles();

        if (triangles.isEmpty() ) {
            id = createTriangle(12,6,8);
        } else {
            id = triangles.get(0);
        }
        // send a valid request with invalid token and verify the response
        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .spec(spec)
                .contentType(ContentType.JSON)
                .pathParam("triangleID", id)
        .when()
                .get("/{triangleID}/area")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(401)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Unauthorized") );
    }
}
