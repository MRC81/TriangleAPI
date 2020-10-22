package triangle_api_tests;

import io.qameta.allure.Description;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.testng.Reporter;
import org.testng.annotations.Test;
import triangle_api.SetUp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static triangle_api.Helpers.*;

public class GetArea_Tests extends SetUp {

    @Test(description = "Code 200 and the body verification for the /triangle/{triangleId}/area entry point")
    @Description("This test verifies that the response from /triangle/{triangleId}/area entry point shows Code 200 " +
            "and the proper triangle area value if an existed triangle ID was specified.")
    public void getArea_existedTriangleId_Test() {
        // let's clean up space for a new nine triangles
        deleteAllTriangles(getAllTriangles());

        int bound = 1;
        String pattern = "#.##";
        String id;
        double area;
        HashMap<String, Double> triangles = new HashMap<>();

        // let's create nine triangles where sides will be as ones, tens, and hundreds with a different number
        // of digits after the comma
        for (int i = 0; i < 9; i++) {

            double[] sides = genSides(Strategy.VALID_VALUES,pattern, bound);
            id = createTriangle(sides[0], sides[1], sides[2]);

            double halfPerimeter = (sides[0] + sides[1] + sides[2])/2;

            area = Math.sqrt(halfPerimeter
            * (halfPerimeter - sides[0])
            * (halfPerimeter - sides[1])
            * (halfPerimeter - sides[2]));

            triangles.put(id, area);

            pattern = pattern.substring(0, pattern.length() - 1);

            if (i % 3 == 2) {
                pattern = "#.##";
                bound = bound * 10;
            }
        }
        // verify that each created triangle has a valid area value
        triangles.forEach((key, value) -> {

            given()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
                    .contentType(ContentType.JSON)
                    .pathParam("triangleID", key)
            .when()
                    .get("/{triangleID}/area")
            .then()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
            .assertThat()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("result", equalTo(value));

            Reporter.log("The area value: " +value+ " for the triangle " +
                    "with ID: " +key+ " is valid.", true) ;
        });
    }


    @Test(description = "Code 404 verification for an attempt to get a triangle area with the invalid ID specified.",
            dataProvider = "getInvalidIDs", dataProviderClass = DeleteTriangle_Tests.class)
    @Description("This test specifies an invalid IDs form 'getInvalidIDs' Data Provider to " +
            "the /triangle/{triangleId}/area entry point and verify that the response has the Code 404.")
    public void getArea_invalidId_Test(String id) {

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .pathParam("triangleID", id)
        .when()
                .get("/{triangleID}/area")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(404)
                .body("error", equalTo("Not Found"),
                        "message", equalTo("Not Found"));
    }


    @Test(description = "Code 405 verification for the /triangle/{triangleId}/area entry point")
    @Description("This test verifies that the response has the Code 405 for the /triangle/{triangleId}/area if an " +
            "incorrect HTTP method was used for the request.")
    public void getArea_wrongMethod_Test() {
        // let's check if there are some triangles already exists and if not, create a new one
        List<String> existedTriangles = getAllTriangles();

        String id;

        // get the ID from just created triangle or pick the 1st one from already existed triangles
        if (existedTriangles.isEmpty()) {
            double[] sides = genSides(Strategy.VALID_VALUES,"#", 10);
            id = createTriangle(sides[0], sides[1], sides[2]);
        } else {
            id = existedTriangles.get(0);
        }
        // let's specify a list of incorrect http methods for this EP
        List<String> httpMethods = Arrays.asList("POST", "PUT", "DELETE");

        for (String method : httpMethods) {
            given()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
                    .contentType(ContentType.JSON)
                    .pathParam("triangleID", id)
            .when()
                    .request(method, "/{triangleID}/area")
            .then()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
            .assertThat()
                    .statusCode(405)
                    .body("error", equalTo("Method Not Allowed"),
                            "message", equalTo("Request method '"+method+"' not supported"));
        }
    }

}
