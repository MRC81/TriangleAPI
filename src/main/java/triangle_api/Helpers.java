package triangle_api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.Reporter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class Helpers {

    /** This method returns a list of IDs of all existed triangles or an empty list if no any IDs were found.
     *
     * @return - list of IDs or empty list
     */
    public static List<String> getAllTriangles() {

        RequestSpecification helpersSpec = new RequestSpecBuilder()
                .addHeader("X-User", "9ea8c6a6-73f5-4ea1-8ec8-f8a3b00a2564")
                .setBaseUri("https://qa-quiz.natera.com/")
                .setBasePath("/triangle/")
                .build();

        Response response =

                given()
                        .log()
                        .ifValidationFails(LogDetail.ALL).spec(helpersSpec)
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

        List<String> listOfIDs = response.jsonPath().getList("id");
        Reporter.log("The following triangle IDs were found: " + listOfIDs, true);

        return listOfIDs;
    }


    /** This method returns an array with side values of the triangle which belongs to the specified ID.
     *
     * @return - array of double with three sides.
     */
    public static double[] getTriangle(String id) {

        RequestSpecification helpersSpec = new RequestSpecBuilder()
                .addHeader("X-User", "9ea8c6a6-73f5-4ea1-8ec8-f8a3b00a2564")
                .setBaseUri("https://qa-quiz.natera.com/")
                .setBasePath("/triangle/")
                .build();

        Response response =

                given()
                        .log()
                        .ifValidationFails(LogDetail.ALL).spec(helpersSpec)
                        .contentType(ContentType.JSON)
                        .pathParam("triangleID", id)
                .when()
                        .get("/{triangleID}")
                .then()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                .assertThat()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .extract()
                        .response();

        Reporter.log("The following triangle was found with this ID: " + response.asString(), true);

        return new double[]{response.path("firstSide"),
                response.path("secondSide"),
                response.path("thirdSide")};
    }


    /** This method deletes each triangle which ID is present in the provided list.
     *
     * @param listOfIDs - a list with triangles IDs
     */
    public static void deleteAllTriangles(List<String> listOfIDs ) {

        RequestSpecification helpersSpec = new RequestSpecBuilder()
                .addHeader("X-User", "9ea8c6a6-73f5-4ea1-8ec8-f8a3b00a2564")
                .setBaseUri("https://qa-quiz.natera.com/")
                .setBasePath("/triangle/")
                .build();

        if (!listOfIDs.isEmpty()) {
            for (String id : listOfIDs) {

                given()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                        .spec(helpersSpec)
                        .contentType(ContentType.JSON)
                        .pathParam("triangleID", id)
                .when()
                        .delete("/{triangleID}")
                .then()
                        .log()
                        .ifValidationFails(LogDetail.ALL)
                .assertThat()
                        .statusCode(200);

                Assert.assertFalse(getAllTriangles().contains(id));

                Reporter.log("The triangle with ID "+id+" was deleted.", true);
            }
        } else {
            Reporter.log("The specified list of IDs is empty, please provide a list with valid IDs", true);
            throw new IllegalArgumentException();
        }
    }


    /** This method deletes the triangle of the specified ID.
     *
     * @param id - ID of the triangles which should be deleted
     */
    public static void deleteOneTriangle(String id) {

        RequestSpecification helpersSpec = new RequestSpecBuilder()
                .addHeader("X-User", "9ea8c6a6-73f5-4ea1-8ec8-f8a3b00a2564")
                .setBaseUri("https://qa-quiz.natera.com/")
                .setBasePath("/triangle/")
                .build();

        given()
                .log()
                .ifValidationFails(LogDetail.ALL)
                .spec(helpersSpec)
                .contentType(ContentType.JSON)
                .pathParam("triangleID", id)
        .when()
                .delete("/{triangleID}")
        .then()
                .log()
                .ifValidationFails(LogDetail.ALL)
        .assertThat()
                .statusCode(200);

        Assert.assertFalse(getAllTriangles().contains(id));

        Reporter.log("The triangle with ID "+id+" was deleted.", true);
    }

    /** This method creates a new triangle if specified sides are valid for a real triangle,
     *  and returns the ID of the created triangle.
     *
     * @param firstSide - a first side of the triangle
     * @param secondSide - a second side of the triangle
     * @param thirdSide - a third side of the triangle
     * @return the ID of created triangle
     */
    public static String createTriangle(double firstSide, double secondSide, double thirdSide) {

        Assert.assertTrue(getAllTriangles().size() < 10,
                "The service allows only 10 triangles and all 10 are already present. " +
                        "Please delete some triangle to add a new one.");

        RequestSpecification helpersSpec = new RequestSpecBuilder()
                .addHeader("X-User", "9ea8c6a6-73f5-4ea1-8ec8-f8a3b00a2564")
                .setBaseUri("https://qa-quiz.natera.com/")
                .setBasePath("/triangle/")
                .build();

        JsonConfig jsonConfig = JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE);

        RestAssuredConfig helperConfig = RestAssured.config()
                .jsonConfig(jsonConfig)
                .encoderConfig(EncoderConfig
                        .encoderConfig()
                        .defaultContentCharset("UTF-8"));

        boolean sidesPositive = firstSide > 0
                && secondSide > 0
                && thirdSide > 0;

        boolean sidesValid = firstSide + secondSide > thirdSide
                && firstSide + thirdSide > secondSide
                && secondSide + thirdSide > firstSide;

        if (sidesPositive && sidesValid) {

            String payload = "{\"separator\": \";\", \"input\": \""+firstSide+";"+secondSide+";"+thirdSide+"\"}";

            Response response =

                    given()
                            .log()
                            .ifValidationFails(LogDetail.ALL)
                            .contentType(ContentType.JSON)
                            .spec(helpersSpec)
                            .config(helperConfig)
                            .body(payload)
                    .when()
                            .post("/")
                    .then()
                            .log()
                            .ifValidationFails(LogDetail.ALL)
                    .assertThat()
                            .statusCode(200)
                            .contentType(ContentType.JSON)
                            .body("firstSide", equalTo(Double.valueOf(firstSide)),
                                    "secondSide", equalTo(Double.valueOf(secondSide)),
                                    "thirdSide", equalTo(Double.valueOf(thirdSide)) )
                            .extract()
                            .response();

            String id = response.path("id");
            Reporter.log("The triangle with sides "+firstSide+", "+secondSide+", and "+thirdSide+" was" +
                    " successfully created, its ID is: " +id, true);

            return id;

        } else if (!sidesPositive) {
            Reporter.log("At least one of the specified sides is < 0, all of them must be > 0 ", true);
        } else {
            Reporter.log("The specified sides lengths are not valid for a triangle, they must follow the criteria: " +
                    "\n(firstSide + secondSide) > thirdSide " +
                    "\nAND \n(firstSide + thirdSide) > secondSide " +
                    "\nAND \n(secondSide + thirdSide) > firstSide.", true);
        }
        throw new IllegalArgumentException();
    }

    /** This enum defines values for the strategy argument of the genSides method*/
    public enum Strategy {
        VALID_VALUES,
        SUM_VALUES,
        INVALID_VALUES,
        EQUILATERAL_VALUES,
        ISOSCELES_VALUES,
        WITH_ZERO_VALUES
    }

    /** This method randomly generates three values according to selected strategy:
     *  INVALID_VALUES - where the sum of some two value is less than the third one;
     *  SUM_VALUES - where the sum of any two values equals to the third one;
     *  VALID_VALUES - where the sum of some two value is greater than the third one;
     *  EQUILATERAL_VALUES - where all the sides are equal;
     *  ISOSCELES_VALUES - where any two of the sides are equal;
     *
     * @param strategy - one of the values described above;
     * @param pattern - # - no digits after comma (will be shown as number.0 since it's double),
     *               #.# - one digit after comma,
     *               #.## two digits after comma - and so on;
     * @param bound - the upper bound for the generated values,
     *             i.e. 10 - values will be up to 10; 100 - values will be up to100, and so on.
     * @return array of double with three values.
     */
    public static double[] genSides(Strategy strategy, String pattern, int bound) {
        double firstSide, secondSide, thirdSide;

        switch (strategy) {
            case INVALID_VALUES:
                do {
                    firstSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01,bound))
                                    .replace(",","."));
                    secondSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01,bound))
                                    .replace(",","."));
                    thirdSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01,bound))
                                    .replace(",","."));

                } while (!(firstSide + secondSide < thirdSide
                        || firstSide + thirdSide < secondSide
                        || secondSide + thirdSide < firstSide));
                break;

            case SUM_VALUES:
                do {
                    firstSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01,bound))
                                    .replace(",","."));
                    secondSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01,bound))
                                    .replace(",","."));
                    thirdSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01,bound))
                                    .replace(",","."));

                } while (!(firstSide + secondSide == thirdSide
                        || firstSide + thirdSide == secondSide
                        || secondSide + thirdSide == firstSide));
                break;

            case VALID_VALUES:
                do {
                    firstSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01,bound))
                                    .replace(",","."));
                    secondSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01,bound))
                                    .replace(",","."));
                    thirdSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01,bound))
                                    .replace(",","."));

                } while (!(firstSide + secondSide > thirdSide
                        && firstSide + thirdSide > secondSide
                        && secondSide + thirdSide > firstSide));
                break;

            case EQUILATERAL_VALUES:
                firstSide = Double
                        .parseDouble(new DecimalFormat(pattern)
                                .format(ThreadLocalRandom.current().nextDouble(0.01,bound))
                                .replace(",","."));
                secondSide = firstSide;
                thirdSide = firstSide;
                break;
            case ISOSCELES_VALUES:
                do {
                    firstSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01,bound))
                                    .replace(",","."));
                    secondSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01,bound))
                                    .replace(",","."));
                    thirdSide = Double
                            .parseDouble(new DecimalFormat(pattern)
                                    .format(ThreadLocalRandom.current().nextDouble(0.01,bound))
                                    .replace(",","."));

                } while (!(firstSide == secondSide && firstSide *2 > thirdSide
                        || firstSide == thirdSide && thirdSide *2 > secondSide
                        || secondSide == thirdSide && secondSide *2 > firstSide));
                break;

            default:
                throw new IllegalStateException("Unexpected strategy value: " + strategy);
        }
        return new double[]{firstSide, secondSide, thirdSide};
    }



}
