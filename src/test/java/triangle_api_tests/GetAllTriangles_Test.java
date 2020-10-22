package triangle_api_tests;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import triangle_api.Helpers;
import triangle_api.SetUp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static triangle_api.Helpers.*;

public class GetAllTriangles_Test extends SetUp {

    @Test(description = "Code 200 and the body verification for the /triangle/all entry point")
    @Description("This test creates a new 10 triangles and verifies that the response from /triangle/all entry point " +
            "shows Code 200 and contains all 10 just created triangles.")
    public void getAllTriangles_Test() {
        // let's clear up all existed triangles
        deleteAllTriangles(getAllTriangles());

        String id;
        HashMap<String, double[]> triangles = new LinkedHashMap<>();
        // let's create a new 10 triangles and save their IDs and sides to the Map
        for (int i = 0; i < 10; i++) {
            double[] sides = genSides(Helpers.Strategy.VALID_VALUES,"#", 10);
            id = createTriangle(sides[0], sides[1], sides[2]);

            triangles.put(id, sides);
        }
        // get the list of existed triangles again
        Response response =

                given()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                        .contentType(ContentType.JSON)
                .when()
                        .get("/all")
                .then()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                .assertThat()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .extract()
                        .response();
        // verify that all IDs and sides from the Map are present in the response
        for (int i = 0; i < 10; i++) {
            id = response.path("id["+i+"]").toString();
            double firstSide = response.path("firstSide["+i+"]");
            double secondSide = response.path("secondSide["+i+"]");
            double thirdSide = response.path("thirdSide["+i+"]");

            Assert.assertTrue(triangles.containsKey(id),
                    "The saved ID and the ID from response are different.");
            Assert.assertEquals(firstSide, triangles.get(id)[0],
                    "The saved 'firstSide' value and the one from the response are different.");
            Assert.assertEquals(secondSide, triangles.get(id)[1],
                    "The saved 'secondSide' value and the one from the response are different.");
            Assert.assertEquals(thirdSide, triangles.get(id)[2],
                    "The saved 'thirdSide' value and the one from the response are different.");
        }
    }


    @Test(description = "Code 200 and the body verification for the /triangle/all entry point where there's no " +
            "any triangles")
    @Description("This test verifies that the response from /triangle/all entry point shows Code 200 and has an " +
            "empty body in the response in case there are no triangles exists.")
    public void getAllTriangles_noTriangles_Test() {
        // let's clear up all existed triangles if there are some
        List<String> existedTriangles = getAllTriangles();
        if (!existedTriangles.isEmpty()) {
            deleteAllTriangles(existedTriangles);
        }

        // get the list of existed triangles again
        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .contentType(ContentType.JSON)
        .when()
                .get("/all")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(0));
    }


    @Issue("The entry point returns the Code 200 if the 'DELETE' HTTP method was used where the Code 405 is expected.")
    @Test(description = "Code 405 verification for the /triangle/all entry point")
    @Description("This test verifies that the response has the Code 405 for the /triangle/all if an " +
            "incorrect HTTP method was used for the request.")
    public void getAllTriangles_wrongMethod_Test() {
        // let's check if there are some triangles already exists and if not, create a new one
        List<String> existedTriangles = getAllTriangles();

        if (existedTriangles.isEmpty()) {
            double[] sides = genSides(Strategy.VALID_VALUES,"#", 10);
            createTriangle(sides[0], sides[1], sides[2]);
        }
        // let's specify a list of incorrect http methods for this EP
        List<String> httpMethods = Arrays.asList("POST", "PUT", "DELETE");

        for (String method : httpMethods) {
            given()
                    .log()
                    .ifValidationFails(LogDetail.ALL)
                    .contentType(ContentType.JSON)
            .when()
                    .request(method, "/all")
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
