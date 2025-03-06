package lib;

import io.restassured.response.Response;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Assertions {
    public static void assertJsonByName(Response Response, String name, int expectedValue) {
        Response.then().assertThat().body("$", hasKey(name));

        int value = Response.jsonPath().getInt(name);

        assertEquals(expectedValue, value, "JSON value is not equal to expected value");


    }


    public static void assertResponseTextEquals(Response response, String expectedAnswer) {
        assertEquals(expectedAnswer, response.asString(), "Response text is not as expected");

    }

    public static void assertResponseStatusCodeEquals(Response response, int expectedStatusCode) {
        assertEquals(expectedStatusCode, response.statusCode(), "Response status code is not as expected");

    }

    public static void assertJsonHasField(Response Response, String expectedField) {
        Response.then().assertThat().body("$", hasKey(expectedField));

    }

    public static void assertJsonHasNotField(Response Response, String unexpectedField) {
        Response.then().assertThat().body("$", not(hasKey(unexpectedField)));
    }

    public static void assertJsonHasFields(Response Response, String[] expectedFields) {

        for (String fieldName : expectedFields) {
            Response.then().assertThat().body("$", hasKey(fieldName));
        }

    }

    public static void assertJsonHasNotFields(Response Response, String[] unexpectedFields) {
        for (String fieldName : unexpectedFields) {
            Response.then().assertThat().body("$", not(hasKey(fieldName)));
        }
    }

    public static void assertJsonByName(Response Response, String name, String expectedValue) {
        Response.then().assertThat().body("$", hasKey(name));

        String value = Response.jsonPath().getString(name);

        assertEquals(expectedValue, value, "JSON value is not equal to expected value");


    }


    public static void assertInvalidLoginResponse(Response response, String expectedAnswer) {
        response.then().assertThat()
                .body(containsString(expectedAnswer));
    }


}
