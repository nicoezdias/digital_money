package com.restassured;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
//import com.restassured.reports.ExtentFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;


public class TestLogin extends Variables {

    private static String token;

    @BeforeAll
    public static void  Setup() {
        RestAssured.baseURI = base_uri;
    }

//    static ExtentSparkReporter spark = new ExtentSparkReporter("target/LoginTestReport.html");
//    static ExtentReports extent;
//    ExtentTest test;
//
//    @BeforeEach
//    public void setUp() throws Exception {
//        extent = ExtentFactory.getInstance();
//        extent.attachReporter(spark);
//    }
//
//    @AfterEach
//    public void quit() {
//        extent.flush();
//    }

   // @Tag("Smoke")
    @Test
    public void LoginSuccess200() {
//    test = extent.createTest("Login Exitoso");
//    test.log(Status.INFO, "Inicia el test de login con un usuario registrado y un password válido...");
        token = given()
                    .auth().preemptive()
                    .basic(client_id, client_secret)
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("grant_type", "password")
                    .formParam("username", username)
                    .formParam("password", password)
                    .basePath("/security/oauth/token")
                .when()
                    .post()
                .then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$",hasKey("access_token"))
                    .body("access_token", not(Matchers.blankOrNullString()))
                    .body("$",hasKey("token_type"))
                    .body("token_type", Matchers.equalTo("bearer"))
                    .log().all()
                    .extract()
                    .jsonPath().get("access_token");
                    Assertions.assertNotNull(token);

    }

}
