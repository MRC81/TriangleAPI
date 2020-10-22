package triangle_api_tests;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import triangle_api.SetUp;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static triangle_api.Helpers.*;

public class DeleteTriangle_Tests extends SetUp {

    @Test(description = "Code 200 verification for an attempt to delete an existed triangle.")
    @Description("This test tries to delete an existed triangle by its ID value and verify that the response has " +
            "the Code 200 and the deleted triangle is no longer present in the list of existed ones.")
    public void deleteTriangle_validId_Test() {
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

        deleteOneTriangle(id);
    }


    /** This is Data Provider for the addTriangle_Payload_validSeparatorValues_Test */
    @DataProvider(name = "getInvalidIDs")
    public static Object[][] getInvalidIDs() {
        return new Object[][] {
                {"qwerty12-3456-7asd-f890-zxcvbn123456"},
                {"qWeRty"},
                {"1234567890"},
                {"~!@#$%^&"},
                {" "},
                {"#"},
                {"Qwerty_123~11$@@"},
        };
    }


    @Issue("The entry point accepts just any value as the ID and returns Code 200. I assume it should verify that the " +
            "specified ID exists and if it doesn't, return the error code 4XX (e.g. 404) and proper error message.")
    @Test(description = "Code 404 verification for an attempt to use an invalid ID with /triangle/{triangleId} entry " +
            "point (DELETE).",
            dataProvider = "getInvalidIDs")
    @Description("This test specifies an invalid IDs form 'getInvalidIDs' Data Provider to /triangle/{triangleId} " +
            "entry point and verify that the response has the Code 404.")
    public void deleteTriangle_invalidId_Test(String id) {

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
                .pathParam("triangleID", id)
        .when()
                .delete("/{triangleID}")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(404);
    }


    @Test(description = "Code 405 verification for the /triangle/{triangleId} entry point")
    @Description("This test verifies that the response has the Code 405 for the /triangle/{triangleId} if an " +
            "incorrect HTTP method was used for the request.")
    public void deleteTriangle_wrongMethod_Test() {
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
        List<String> httpMethods = Arrays.asList("POST", "PUT");

        for (String method : httpMethods) {
            given()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
                    .contentType(ContentType.JSON)
                    .pathParam("triangleID", id)
            .when()
                    .request(method, "/{triangleID}")
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
