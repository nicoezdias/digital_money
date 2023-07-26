package com.restassured;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.restassured.reports.ExtentFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;


import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAccounts extends Variables {

    private static String token;

    static ExtentSparkReporter spark = new ExtentSparkReporter("target/AccountTestsReport.html");
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

    //**-------------------------------------- GET account by id (/accounts/{id}) -----------------------------------**

    //TC_Cuenta_0001
    @Tag("Smoke")
    @Test
    @Order(1)
    public void ViewAccountSuccess200() {


        test = extent.createTest("TC_Cuenta_0001 - GET account by id - Status Code: 200 - OK");
        test.assignCategory("Cuenta");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de datos de una cuenta. Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}")
                    .pathParams("id", 2).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("alias"))
                    .body("alias", Matchers.equalTo("afectacion.divisa.cambios"))
                    .body("$",hasKey("cvu"))
                    .body("cvu", Matchers.equalTo("1828142364587587493333"))
                    .body("$",hasKey("availableBalance"))
                    .body("availableBalance", Matchers.equalTo(220000f))
                    .body("$",hasKey("account_id"))
                    .body("account_id", Matchers.equalTo(2))
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Cuenta_0002
    @Tag("Smoke")
    @Test
    @Order(2)
    public void ViewAccountFailure404() {

        test = extent.createTest("TC_Cuenta_0002 - GET account by id - Status Code: 404 - Not Found");
        test.assignCategory("Cuenta");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 404 - Not Found");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de los datos de una cuenta. Usuario logueado. ID de cuenta inexistente.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}")
                    .pathParams("id", 99).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(404)
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Not Found"))
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Cuenta_0011
    @Tag("Smoke")
    @Test
    @Order(3)
    public void ViewAccountFailure403() {

        test = extent.createTest("TC_Cuenta_0011 - GET account by id - Status Code: 403 - Forbidden");
        test.assignCategory("Cuenta");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 403 - Forbidden");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de los datos de una cuenta. Usuario logueado. ID de cuenta existente. El ID de cuenta no corresponde al usuario.");

        Response response;

        response = given()
                .header("Authorization", "Bearer " + token)
                .basePath("/accounts/{id}")
                .pathParams("id", 1).
                when().
                get().
                then()
                .assertThat()
                .statusCode(403)
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Forbidden"))
                .log().all()
                .extract()
                .response();

    }

    //TC_Cuenta_0003
    @Tag("Smoke")
    @Test
    @Order(4)
    public void ViewAccountFailure401() {

        test = extent.createTest("TC_Cuenta_0003 - GET account by id - Status Code: 401 - Unauthorized");
        test.assignCategory("Cuenta");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 401 - Unauthorized");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de los datos de una cuenta. Usuario no logueado. ID de cuenta existente.  El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .basePath("/accounts/{id}")
                    .pathParams("id", 2).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(401)
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .log().all()
                    .extract()
                    .response();

    }



    //**------------------------------------ PATCH account by id (/accounts/{id}) ---------------------------------**

    //TC_Cuenta_0004
    @Tag("Smoke")
    @Test
    @Order(5)
    public void UpdateAccountSuccess200() {

        test = extent.createTest("TC_Cuenta_0004 - PATCH account by id - Status Code: 200 - OK");
        test.assignCategory("Cuenta");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Edición exitosa de datos de una cuenta (alias). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        JSONObject request = new JSONObject();
        request.put("word_index_zero", "Ciruela");
        request.put("word_index_one", "Botella");
        request.put("word_index_two", "Merengue");

        RestAssured.registerParser("text/plain", Parser.TEXT);

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(request.toJSONString()).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.TEXT)
                    .body(equalTo("New Alias: ciruela.botella.merengue"))
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Cuenta_0005
    @Tag("Smoke")
    @Test
    @Order(6)
    public void UpdateAccountFailure404() {

        test = extent.createTest("TC_Cuenta_0005 - PATCH account by id - Status Code: 404 - Not Found");
        test.assignCategory("Cuenta");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 404 - Not Found");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Edición fallida de los datos de una cuenta (alias). Usuario logueado. ID de cuenta inexistente.");

        Response response;

        JSONObject request = new JSONObject();
        request.put("word_index_zero", "Margarita");
        request.put("word_index_one", "Arroyo");
        request.put("word_index_two", "Sombrilla");

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}")
                    .pathParams("id", 99)
                    .contentType(ContentType.JSON)
                    .body(request.toJSONString()).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(404)
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .contentType(ContentType.JSON)
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Cuenta_0006
    @Tag("Smoke")
    @Test
    @Order(7)
    public void UpdateAccountFailure401() {

        test = extent.createTest("TC_Cuenta_0006 - PATCH account by id - Status Code: 401 - Unauthorized");
        test.assignCategory("Cuenta");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 401 - Unauthorized");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Edición fallida de los datos de una cuenta (alias). Usuario no logueado. ID de cuenta existente.  El ID de cuenta corresponde al usuario.");

        Response response;

        JSONObject request = new JSONObject();
        request.put("word_index_zero", "Manzana");
        request.put("word_index_one", "Perejil");
        request.put("word_index_two", "Mariposa");

        response = given()
                    .basePath("/accounts/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(request.toJSONString()).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(401)
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Cuenta_0007
    @Tag("Smoke")
    @Test
    @Order(8)
    public void UpdateAccountFailure409() {

        test = extent.createTest("TC_Cuenta_0007 - PATCH account by id - Status Code: 409 - Conflict");
        test.assignCategory("Cuenta");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 409 - Conflict");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Edición fallida de los datos de una cuenta (alias). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. Alias ya registrado.");


        Response response;

        JSONObject request = new JSONObject();
        request.put("word_index_zero", "Riqueza");
        request.put("word_index_one", "Dineros");
        request.put("word_index_two", "Devaluacion");

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(request.toJSONString()).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(409)
                    .statusCode(HttpStatus.SC_CONFLICT)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("The alias is already registered"))
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Cuenta_0009
    @Tag("Smoke")
    @Test
    @Order(9)
    public void UpdateAccountFailure400AliasEmptyWord() {

        test = extent.createTest("TC_Cuenta_0009 - PATCH account by id - Status Code: 400 - Bad Request");
        test.assignCategory("Cuenta");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Edición fallida de los datos de una cuenta (alias). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. Alias en formato incorrecto (una o más palabras vacías.");


        Response response;

        JSONObject request = new JSONObject();
        request.put("word_index_zero", "");
        request.put("word_index_one", "Dineros");
        request.put("word_index_two", "Devaluacion");

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(request.toJSONString()).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("You must choose 3 words. Words cannot be blank."))
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Cuenta_0010
    @Tag("Smoke")
    @Test
    @Order(10)
    public void UpdateAccountFailure400AliasRepeatedWord() {

        test = extent.createTest("TC_Cuenta_0010 - PATCH account by id - Status Code: 400 - Bad Request");
        test.assignCategory("Cuenta");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Edición fallida de los datos de una cuenta (alias). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. Alias en formato incorrecto (dos o tres palabras repetidas.");

        Response response;

        JSONObject request = new JSONObject();
        request.put("word_index_zero", "Dineros");
        request.put("word_index_one", "Dineros");
        request.put("word_index_two", "Devaluacion");

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(request.toJSONString()).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("All the words must be different."))
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Cuenta_0012
    @Tag("Smoke")
    @Test
    @Order(11)
    public void UpdateAccountFailure403() {

        test = extent.createTest("TC_Cuenta_0012 - PATCH account by id - Status Code: 403 - Forbidden");
        test.assignCategory("Cuenta");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 403 - Forbidden");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Edición fallida de los datos de una cuenta (alias). Usuario logueado. ID de cuenta existente. El ID de cuenta no corresponde al usuario.");

        Response response;

        JSONObject request = new JSONObject();
        request.put("word_index_zero", "Ciruela");
        request.put("word_index_one", "Botella");
        request.put("word_index_two", "Merengue");

        response = given()
                .header("Authorization", "Bearer " + token)
                .basePath("/accounts/{id}")
                .pathParams("id", 1)
                .contentType(ContentType.JSON)
                .body(request.toJSONString()).
                when().
                patch().
                then()
                .assertThat()
                .statusCode(403)
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Forbidden"))
                .log().all()
                .extract()
                .response();

    }



}
