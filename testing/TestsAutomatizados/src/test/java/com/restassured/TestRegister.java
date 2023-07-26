package com.restassured;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.restassured.model.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import com.restassured.reports.ExtentFactory;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestRegister extends Variables {

    private static String token;

    static ExtentSparkReporter spark = new ExtentSparkReporter("target/CardsTestsReport.html");
    static ExtentReports extent;
    ExtentTest test;


    @BeforeAll
    public static void  Setup() {

        RestAssured.baseURI = base_uri;

        extent = ExtentFactory.getInstance();
        extent.attachReporter(spark);

    }


    @BeforeAll
    public static void Login(){
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
                .log().all()
                .extract()
                .jsonPath().get("access_token");
    }


    @AfterAll
    public static void quit() {
        extent.flush();
    }

    //**------------------------------------------- POST user (/users) ------------------------------------------**

    //@Tag("Smoke")
    @Test
    public void RegisterSuccess201() {

        Response response;

//        JSONObject request = new JSONObject();
//        request.put("name", "Ana");
//        request.put("last_name", "Fidalgo");
//        request.put("dni", 30744221);
//        request.put("email", "analaurafidalgo@gmail.com");
//        request.put("password", "123456789");
//        request.put("phone", 47828304);

        User user = new User("Ana", "Fidalgo", 30744221, "analaurafidalgo@gmail.com", "123456789", 47828304);

        response = given()
                    .header("Content-type", "application/json")
                    .contentType(ContentType.JSON)
                    .basePath("/users")
                    //.body(request.toJSONString()).
                    .body(user).
                when().
                    post().
                then()
                    .assertThat()
                    .statusCode(201)
                    .statusCode(HttpStatus.SC_CREATED)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("name"))
                    .body("name", Matchers.equalTo("Ana"))
                    .body("$",hasKey("last_name"))
                    .body("last_name", Matchers.equalTo("Fidalgo"))
                    .body("$",hasKey("dni"))
                    .body("dni", Matchers.equalTo(30744221))
                    .body("$",hasKey("email"))
                    .body("email", Matchers.equalTo("analaurafidalgo@gmail.com"))
                    .body("phone", Matchers.equalTo(47828304))
                    .body("$",hasKey("cvu"))
                    .body("$",hasKey("alias"))
                    .body("$",hasKey("user_id"))
                    .body("$", not(hasKey("password")))
                    .log().all()
                    .extract()
                    .response();

    }


}
