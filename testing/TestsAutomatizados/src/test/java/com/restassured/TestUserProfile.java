package com.restassured;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.restassured.model.User;
import com.restassured.reports.ExtentFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestUserProfile extends Variables {

    private static String token;

    static ExtentSparkReporter spark = new ExtentSparkReporter("target/UserProfileTestsReport.html");
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


    //**------------------------------------ GET user profile by id (/users/{id}) -----------------------------------**


    //TC_Perfil_Usuario_0001
    @Tag("Smoke")
    @Test
    @Order(1)
    public void ViewUserProfileSuccess200() throws InterruptedException {

        test = extent.createTest("TC_Perfil_Usuario_0001 - GET user profile by id - Status Code: 200 - OK");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Visualización de datos exitosa. ID de usuario existente. El ID de usuario corresponde al usuario logueado.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/users/{id}")
                    .pathParams("id", 2).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("user"))
                    .body("user", hasKey("name"))
                    .body("user.name", Matchers.equalTo("user1"))
                    .body("user", hasKey("last_name"))
                    .body("user.last_name", Matchers.equalTo("user1"))
                    .body("user", hasKey("dni"))
                    .body("user.dni", Matchers.equalTo(987654321))
                    .body("user", hasKey("email"))
                    .body("user.email", Matchers.equalTo("user1@mail.com"))
                    .body("user", hasKey("user_id"))
                    .body("user.user_id", Matchers.equalTo(2))
                    .body("user", hasKey("phone"))
                    .body("user.phone", Matchers.equalTo(1226489722))
                    .body("user", not(hasKey("password")))
                    .body("user", hasKey("accountId"))
                    .body("account.account_id", Matchers.equalTo(2))
                    .body("account", hasKey("cvu"))
                    .body("account.cvu", Matchers.equalTo("1828142364587587493333"))
                    .body("account.cvu", Matchers.hasLength(22))
                    .body("account", hasKey("alias"))
                    //.body("account.alias", Matchers.equalTo("afectacion.divisa.cambios"))
                    .body("account", hasKey("availableBalance"))
                    .body("account.availableBalance", Matchers.equalTo(220000f))
                    .body("user", not(hasKey("password")))
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Perfil_Usuario_0002
    @Tag("Smoke")
    @Test
    @Order(2)
    public void ViewUserProfileFailure401() throws InterruptedException {

        test = extent.createTest("TC_Perfil_Usuario_0002 - GET user profile by id - Status Code: 401 - Unauthorized");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 401 - Unauthorized");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Visualización de datos fallida. ID de usuario existente. Usuario no logueado.");

        Response response;

        response = given()
                    .basePath("/users/{id}")
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

    //TC_Perfil_Usuario_0003
    @Tag("Smoke")
    @Test
    @Order(3)
    public void ViewUserProfileFailure404() throws InterruptedException {


        test = extent.createTest("TC_Perfil_Usuario_0003 - GET user profile by id - Status Code: 404 - Not Found");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 404 - Not Found");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Visualización de datos fallida. ID de usuario inexistente. Usuario logueado.");


        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/users/{id}")
                    .pathParams("id", 99).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(404)
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .contentType(ContentType.TEXT)
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Perfil_Usuario_0019
    @Tag("Smoke")
    @Test
    @Order(4)
    public void ViewUserProfileFailure403() throws InterruptedException {


        test = extent.createTest("TC_Perfil_Usuario_0019 - GET user profile by id - Status Code: 403 - Forbidden");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 403 - Forbidden");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Visualización de datos fallida. ID de usuario existente. Usuario logueado. El ID de usuario corresponde al usuario logueado.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/users/{id}")
                    .pathParams("id", 1).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(403)
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .contentType(ContentType.TEXT)
                    .body(equalTo("You don't have access to that user"))
                    .log().all()
                    .extract()
                    .response();

    }

    //**---------------------------------- PATCH user profile by id (/users/{id}) --------------------------------**

    //TC_Perfil_Usuario_0004
    @Tag("Smoke")
    @Test
    @Order(5)
    public void UpdateUserProfileSuccess200() throws InterruptedException {

        test = extent.createTest("TC_Perfil_Usuario_0004 - PATCH user profile by id - Status Code: 200 - OK");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Edición de datos exitosa. ID de usuario existente. Usuario logueado. El ID de usuario corresponde al usuario logueado.");

        Response response;

        User user = new User();
        user.setName("Pablo");

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/users/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(user).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("user_id"))
                    .body("$",hasKey("name"))
                    .body("$",hasKey("last_name"))
                    .body("$",hasKey("dni"))
                    .body("$",hasKey("email"))
                    .body("$",hasKey("phone"))
                    .body("$",hasKey("cvu"))
                    .body("$",hasKey("alias"))
                    .body("$",hasKey("accountId"))
                    .body("$", not(hasKey("password")))
                    .body("name", Matchers.equalTo("Pablo"))
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Perfil_Usuario_0005
    @Tag("Smoke")
    @Test
    @Order(6)
    public void UpdateUserProfileFailure401() throws InterruptedException {

        test = extent.createTest("TC_Perfil_Usuario_0005 - PATCH user profile by id - Status Code: 401 - Unauthorized");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 401 - Unauthorized");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Edición de datos fallida. ID de usuario existente. El ID de usuario corresponde al usuario logueado. Usuario no logueado.");

        Response response;

        User user = new User();
        user.setName("Pablo");

        response = given()
                    .basePath("/users/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(user).
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

    //TC_Perfil_Usuario_0006
    @Tag("Smoke")
    @Test
    @Order(7)
    public void UpdateUserProfileFailure404() throws InterruptedException {

        test = extent.createTest("TC_Perfil_Usuario_0006 - PATCH user profile by id - Status Code: 404 - Not Found");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 404 - Not Found");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Edición de datos fallida. ID de usuario inexistente. Usuario logueado.");

        Response response;

        User user = new User();
        user.setName("Carlos");

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/users/{id}")
                    .pathParams("id", 99)
                    .contentType(ContentType.JSON)
                    .body(user).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(404)
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .contentType(ContentType.TEXT)
                    .log().all()
                    .extract()
                    .response();

    }


    //TC_Perfil_Usuario_0007
    @Tag("Smoke")
    @Test
    @Order(8)
    public void UpdateUserProfileFailure400DniInUse() throws InterruptedException {

        test = extent.createTest("TC_Perfil_Usuario_0007 - PATCH user profile by id - Status Code: 400 - Bad Request");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Edición de datos fallida. ID de usuario existente. Usuario logueado. El ID de usuario corresponde al usuario logueado. Dni ya registrado.");

        Response response;

        User user = new User();
        user.setDni(123456789);

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/users/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(user).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.TEXT)
                    .body(equalTo("The dni number is already registered"))
                    .log().all()
                    .extract()
                    .response();

    }


    //TC_Perfil_Usuario_0008
    @Tag("Smoke")
    @Test
    @Order(9)
    public void UpdateUserProfileFailure400EmailInUse() throws InterruptedException {

        test = extent.createTest("TC_Perfil_Usuario_0008 - PATCH user profile by id - Status Code: 400 - Bad Request");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Edición de datos fallida. ID de usuario existente. Usuario logueado. El ID de usuario corresponde al usuario logueado. Email ya registrado.");

        Response response;

        User user = new User();
        user.setEmail("admin@mail.com");

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/users/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(user).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.TEXT)
                    .body(equalTo("The email address is already registered"))
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Perfil_Usuario_0009
    @Tag("Smoke")
    @Test
    @Order(10)
    public void UpdateUserProfileFailure400SamePassword() throws InterruptedException {

        test = extent.createTest("TC_Perfil_Usuario_0009 - PATCH user profile by id - Status Code: 400 - Bad Request");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Edición de datos fallida. ID de usuario existente. Usuario logueado. El ID de usuario corresponde al usuario logueado. El nuevo password es igual al anterior.");


        Response response;

        User user = new User();
        user.setPassword("123456789");

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/users/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(user).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.TEXT)
                    .body(equalTo("The new password must be different than the previous one"))
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Perfil_Usuario_0020
    @Tag("Smoke")
    @Test
    @Order(11)
    public void UpdateUserProfileFailure403() throws InterruptedException {

        test = extent.createTest("TC_Perfil_Usuario_0020 - PATCH user profile by id - Status Code: 403 - Forbidden");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 403 - Forbidden");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Edición de datos fallida. ID de usuario existente. Usuario logueado. El ID de usuario no corresponde al usuario logueado.");

        Response response;

        User user = new User();
        user.setName("Carlos");

        response = given()
                .header("Authorization", "Bearer " + token)
                .basePath("/users/{id}")
                .pathParams("id", 1)
                .contentType(ContentType.JSON)
                .body(user).
                when().
                patch().
                then()
                .assertThat()
                .statusCode(403)
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .contentType(ContentType.TEXT)
                .body(equalTo("You don't have access to that user"))
                .log().all()
                .extract()
                .response();

    }


    //TC_Perfil_Usuario_0010
    @Tag("Regression")
    @Test
    @Order(12)
    public void UpdateUserProfileFailure400NameLength() throws InterruptedException {

        test = extent.createTest("TC_Perfil_Usuario_0010 - PATCH user profile by id - Status Code: 400 - Bad Request");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Edición de datos fallida. ID de usuario existente. Usuario logueado. El ID de usuario corresponde al usuario logueado. Caracteres Excedentes (name supera los 30 caracteres).");

        Response response;

        User user = new User();
        user.setName("Juan Ignacio Javier Sebastian Esteban");

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/users/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(user).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("{name=maximum number of characters 30}"))
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Perfil_Usuario_0011
    @Tag("Regression")
    @Test
    @Order(13)
    public void UpdateUserProfileFailure400LastNameLength() throws InterruptedException {

        test = extent.createTest("TC_Perfil_Usuario_0011 - PATCH user profile by id - Status Code: 400 - Bad Request");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Edición de datos fallida. ID de usuario existente. Usuario logueado. El ID de usuario corresponde al usuario logueado. Caracteres Excedentes (last_name supera los 40 caracteres).");


        Response response;

        User user = new User();
        user.setLast_name("Perez Sanchez Aliendro Brunetti Torres Gomez");

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/users/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(user).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("{lastName=maximum number of characters 40}"))
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Perfil_Usuario_0012
    @Tag("Regression")
    @Test
    @Order(14)
    public void UpdateUserProfileFailure400EmailLength() throws InterruptedException {

        test = extent.createTest("TC_Perfil_Usuario_0012 - PATCH user profile by id - Status Code: 400 - Bad Request");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Edición de datos fallida. ID de usuario existente. Usuario logueado. El ID de usuario corresponde al usuario logueado. Caracteres Excedentes (email supera los 60 caracteres).");


        Response response;

        User user = new User();
        user.setEmail("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@mail.commmmmmmmmmmmmmmmmmmmmmm");

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/users/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(user).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("{email=maximum number of characters 60}"))
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Perfil_Usuario_0013
    @Tag("Smoke")
    @Test
    @Order(15)
    public void UpdateUserProfileFailure400EmailFormat() throws InterruptedException {

        test = extent.createTest("TC_Perfil_Usuario_0013 - PATCH user profile by id - Status Code: 400 - Bad Request");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Edición de datos fallida. ID de usuario existente. Usuario logueado. El ID de usuario corresponde al usuario logueado. Formato email inválido (sin '@').");


        Response response;

        User user = new User();
        user.setEmail("anamariamail.com");

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/users/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(user).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("{email=must be a well-formed email address}"))
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Perfil_Usuario_0014
    @Tag("Regression")
    @Test
    @Order(16)
    public void UpdateUserProfileFailure400PasswordLength() throws InterruptedException {

        test = extent.createTest("TC_Perfil_Usuario_0014 - PATCH user profile by id - Status Code: 400 - Bad Request");
        test.assignCategory("Perfil Usuario");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: PATCH");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Perfil de usuario. Edición de datos fallida. ID de usuario existente. Usuario logueado. El ID de usuario corresponde al usuario logueado. Caracteres Excedentes (password supera los 30 caracteres).");


        Response response;

        User user = new User();
        user.setPassword("123abc123abc123abc123abc123abc123abc");

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/users/{id}")
                    .pathParams("id", 2)
                    .contentType(ContentType.JSON)
                    .body(user).
                when().
                    patch().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("{password=minimum number of characters 8, maximum number of characters 30}"))
                    .log().all()
                    .extract()
                    .response();
    }


}
