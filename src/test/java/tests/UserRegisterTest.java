package tests;

import lib.Constants;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import lib.ApiCoreRequests;
import lib.Assertions;
import io.restassured.response.Response;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import lib.DataGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static lib.Constants.APIDEVUserUrl;
import static lib.Constants.URLtest;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = URLtest;
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Tag("Regress")
    @Description("This test checks unsuccessful registration with existing email")
    @DisplayName("Test negative repeated registration")

    public void testCreateUserWithExistingEmail() {

        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(URLtest + APIDEVUserUrl, userData);

        Assertions.assertResponseStatusCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Tag("Regress")
    @Tag("Smoke")
    @Description("This test checks successful registration with new email")
    @DisplayName("Test positive registration")

    public void testCreateUserWithNewEmail() {

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(URLtest + APIDEVUserUrl, userData);

        Assertions.assertResponseStatusCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Tag("Regress")
    @Description("This test checks unsuccessful registration w/o '@' in url")
    @DisplayName("Test negative registration with invalid email format")

    public void testCreateUserWithInvalidFormatEmail() {

        String email = DataGenerator.getRandomInvalidEmailFormat();

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(URLtest + APIDEVUserUrl, userData);

        Assertions.assertResponseStatusCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");

    }

    @Severity(SeverityLevel.CRITICAL)
    @Tag("Regress")
    @Description("This test checks unsuccessful registration w/o one of required fields")
    @DisplayName("Test negative registration with missing field:")
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithoutParam(String param) {

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.remove(param);

        Response response = apiCoreRequests
                .makePostRequest(URLtest + APIDEVUserUrl, userData);

        Assertions.assertResponseStatusCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The following required params are missed: " + param);

    }

    @Severity(SeverityLevel.MINOR)
    @Tag("Regress")
    @Description("This test checks unsuccessful registration with short name in required fields")
    @DisplayName("Test negative short:")
    @ParameterizedTest
    @ValueSource(strings = {"username", "firstName", "lastName"})
    public void testCreateUserWithInvalidName(String name) {

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put(name, "l");

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(URLtest + APIDEVUserUrl, userData);

        Assertions.assertResponseStatusCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of '" + name + "' field is too short");

    }

    @Severity(SeverityLevel.MINOR)
    @Tag("Regress")
    @Description("This test checks unsuccessful registration with long name in required fields")
    @DisplayName("Test negative long:")
    @ParameterizedTest
    @ValueSource(strings = {"username", "firstName", "lastName"})
    public void testCreateUserWithLongName(String name) {

        String longName = "a".repeat(251);

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put(name, longName);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest(URLtest + APIDEVUserUrl, userData);

        Assertions.assertResponseStatusCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of '" + name + "' field is too long");
    }


}



