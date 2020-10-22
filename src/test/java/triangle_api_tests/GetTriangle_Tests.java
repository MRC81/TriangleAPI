package triangle_api_tests;

import io.qameta.allure.Description;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.testng.Reporter;
import org.testng.annotations.Test;
import triangle_api.SetUp;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static triangle_api.Helpers.*;

public class GetTriangle_Tests extends SetUp {

    @Test(description = "Code 200 and the content verification for an attempt to get an existed triangle.")
    @Description("This test tries to get a certain triangle by its ID and verify that the response has " +
            "the Code 200 and correct triangle id and sides information.")
    public void getTriangle_validId_Test() {
        // let's clean up space for a new nine triangles
        deleteAllTriangles(getAllTriangles());

        int bound = 1;
        String pattern = "#.##";
        String id;

        HashMap<String, double[]> triangles = new HashMap<>();

        // let's create nine triangles where sides will be as ones, tens, and hundreds with a different number
        // of digits after the comma
        for (int i = 0; i < 9; i++) {

            double[] sides = genSides(Strategy.VALID_VALUES,pattern, bound);
            id = createTriangle(sides[0], sides[1], sides[2]);

            triangles.put(id, sides);

            pattern = pattern.substring(0, pattern.length() - 1);

            if (i % 3 == 2) {
                pattern = "#.##";
                bound = bound * 10;
            }
        }
        // verify that each created triangle can be found by its ID and has a valid information in the response
        triangles.forEach((key, value) -> {

            given()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
                    .contentType(ContentType.JSON)
                    .pathParam("triangleID", key)
            .when()
                    .get("/{triangleID}")
            .then()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
                    .assertThat()
            .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("id", equalTo(key),
                            "firstSide", equalTo(value[0]),
                            "secondSide", equalTo(value[1]),
                            "thirdSide", equalTo(value[2]) );

            Reporter.log("The triangle with ID: " +key+ " has a valid information of its sides and ID.",
                    true) ;
        });
    }


    @Test(description = "Code 404 verification for an attempt to use an invalid ID with /triangle/{triangleId} " +
            "entry point(GET).",
            dataProvider = "getInvalidIDs", dataProviderClass = DeleteTriangle_Tests.class)
    @Description("This test specifies an invalid IDs form 'getInvalidIDs' Data Provider to /triangle/{triangleId} " +
            "entry point and verify that the response has the Code 404.")
    public void getTriangle_invalidId_Test(String id) {

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .pathParam("triangleID", id)
        .when()
                .get("/{triangleID}")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(404)
                .body("error", equalTo("Not Found"),
                        "message", equalTo("Not Found"));
    }


}
