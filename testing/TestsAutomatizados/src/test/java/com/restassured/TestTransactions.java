package com.restassured;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.google.common.collect.Ordering;
import com.restassured.model.Card;
import com.restassured.model.TransactionDeposit;
import com.restassured.model.Transference;
import com.restassured.reports.ExtentFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.testng.annotations.BeforeTest;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestTransactions extends Variables {

    private static String token;
    private static String token_id_4;

    static ExtentSparkReporter spark = new ExtentSparkReporter("target/TransactionsTestsReport.html");
    static ExtentReports extent;
    ExtentTest test;

    @BeforeAll
    public static void Setup() {

        RestAssured.baseURI = base_uri;

        extent = ExtentFactory.getInstance();
        extent.attachReporter(spark);

    }

    @BeforeAll
    public static void Login() {
        token = given()
                    .auth().preemptive()
                    .basic(client_id, client_secret)
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("grant_type", "password")
                    //account_id:2
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

    //**-------------------------- GET last 5 transactions (/accounts/{id}/transactions) ------------------------**

    //TC_Transacciones_0001
    @Tag("Smoke")
    @Test
    @Order(1)
    public void ViewLastFiveTransactionsSuccess200() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0001 - GET last 5 transactions by account id - Status Code: 200 - OK");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de ultimas 5 transacciones de la cuenta. Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        List<String> transactionDateList = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/transactions")
                    .pathParams("id", 2).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$", hasKey("account"))
                    .body("$", hasKey("transactions"))
                    .body("transactions[0]", hasKey("amount"))
                    .body("transactions[0]", hasKey("realizationDate"))
                    .body("transactions[0]", hasKey("description"))
                    .body("transactions[0]", hasKey("fromCvu"))
                    .body("transactions[0]", hasKey("toCvu"))
                    .body("transactions[0]", hasKey("type"))
                    .body("transactions[0]", hasKey("transaction_id"))
                    .body("transactions.size()", Matchers.lessThanOrEqualTo(5))
                    .body("transactions.size()", Matchers.greaterThanOrEqualTo(1))
                    .log().all()
                    .extract()
                    .jsonPath().getList("transactions.realizationDate");


        List<LocalDateTime> localDateTimeList = new ArrayList<>();
        for (String dateT:transactionDateList) {
            String date = dateT.replace("T", " ");
            LocalDateTime newDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            localDateTimeList.add(newDate);
        }

        LocalDateTime date1 = localDateTimeList.get(0);
        LocalDateTime date2 = localDateTimeList.get(1);
        Assertions.assertTrue(date1.isAfter(date2));
        Assertions.assertFalse(date1.isBefore(date2));
        Assertions.assertTrue(Ordering.natural().reverse().isOrdered(localDateTimeList));

        localDateTimeList.add(0, LocalDateTime.now());
        Assertions.assertTrue(Ordering.natural().reverse().isOrdered(localDateTimeList));

        localDateTimeList.add(2, LocalDateTime.now());
        Assertions.assertFalse(Ordering.natural().reverse().isOrdered(localDateTimeList));

}

    //TC_Transacciones_0002
    @Tag("Smoke")
    @Test
    @Order(2)
    public void ViewLastFiveTransactionsFailure404() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0002 - GET last 5 transactions by account id - Status Code: 404 - Not Found (Account ID)");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 404 - Not Found");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de ultimas 5 transacciones de la cuenta. Usuario logueado. ID de cuenta inexistente.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/transactions")
                    .pathParams("id", 99).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(404)
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Not Found"))
                    .log().all()
                    .extract()
                    .response();

    }


    //TC_Transacciones_0003
    @Tag("Smoke")
    @Test
    @Order(3)
    public void ViewLastFiveTransactionsFailure401() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0003 - GET last 5 transactions by account id - Status Code: 401 - Unauthorized");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 401 - Unauthorized");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de ultimas 5 transacciones de la cuenta. Usuario no logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .basePath("/accounts/{id}/transactions")
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

    //TC_Transacciones_0004
    @Tag("Smoke")
    @Test
    @Order(4)
    public void ViewLastFiveTransactionsSuccess204() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0004 - GET last 5 transactions by account id - Status Code: 204 - No Content");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 204 - No Content");
        test.assignCategory("Sprint: 2");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta Exitosa de ultimas 5 transacciones de la cuenta. ID de cuenta existente. Usuario logueado. El usuario no posee ninguna transacción. El ID de cuenta corresponde al usuario.");

        Login_Id_4();

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token_id_4)
                    .basePath("/accounts/{id}/transactions")
                    .pathParams("id", 4).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(204)
                    .statusCode(HttpStatus.SC_NO_CONTENT)
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Transacciones_0005
    @Tag("Smoke")
    @Test
    @Order(5)
    public void ViewLastFiveTransactionsFailure403() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0005 - GET last 5 transactions by account id - Status Code: 403 - Forbidden");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 403 - Forbidden");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de ultimas 5 transacciones de la cuenta. ID de cuenta existente. Usuario logueado. El ID de cuenta no corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/transactions")
                    .pathParams("id", 1).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(403)
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Forbidden"))
                    .body("message", Matchers.equalTo("You don't have access to that account"))
                    .log().all()
                    .extract()
                    .response();
    }

    //**---------------------------- GET all transactions (/accounts/{id}/activity) --------------------------**


    //TC_Transacciones_0006
    @Tag("Smoke")
    @Test
    @Order(6)
    public void ViewAllTransactionsSuccess200() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0006 - GET all transactions by account id - Status Code: 200 - OK");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de todas las transacciones de la cuenta. Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        List<String> transactionDateList = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity")
                    .pathParams("id", 2).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$", hasKey("account"))
                    .body("$", hasKey("transactions"))
                    .body("transactions[0]", hasKey("amount"))
                    .body("transactions[0]", hasKey("realizationDate"))
                    .body("transactions[0]", hasKey("description"))
                    .body("transactions[0]", hasKey("fromCvu"))
                    .body("transactions[0]", hasKey("toCvu"))
                    .body("transactions[0]", hasKey("type"))
                    .body("transactions[0]", hasKey("transaction_id"))
                    .body("transactions.size()", Matchers.greaterThanOrEqualTo(1))
                    .log().all()
                    .extract()
                    .jsonPath().getList("transactions.realizationDate");


        List<LocalDateTime> localDateTimeList = new ArrayList<>();
        for (String dateT:transactionDateList) {
            String date = dateT.replace("T", " ");
            LocalDateTime newDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            localDateTimeList.add(newDate);
        }

        LocalDateTime date1 = localDateTimeList.get(0);
        LocalDateTime date2 = localDateTimeList.get(1);
        Assertions.assertTrue(date1.isAfter(date2));
        Assertions.assertFalse(date1.isBefore(date2));
        Assertions.assertTrue(Ordering.natural().reverse().isOrdered(localDateTimeList));

        localDateTimeList.add(0, LocalDateTime.now());
        Assertions.assertTrue(Ordering.natural().reverse().isOrdered(localDateTimeList));

        localDateTimeList.add(2, LocalDateTime.now());
        Assertions.assertFalse(Ordering.natural().reverse().isOrdered(localDateTimeList));

    }

    //TC_Transacciones_0007
    @Tag("Smoke")
    @Test
    @Order(7)
    public void ViewAllTransactionsFailure404() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0007 - GET al transactions by account id - Status Code: 404 - Not Found (Account ID)");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 404 - Not Found");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de todas las transacciones de la cuenta. Usuario logueado. ID de cuenta inexistente");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity")
                    .pathParams("id", 99).
                when().
                    get().
                then()
                .assertThat()
                    .statusCode(404)
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Not Found"))
                    .contentType(ContentType.JSON)
                    .log().all()
                    .extract()
                    .response();

    }


    //TC_Transacciones_0008
    @Tag("Smoke")
    @Test
    @Order(8)
    public void ViewAllTransactionsFailure401() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0008 - GET all transactions by account id - Status Code: 401 - Unauthorized");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 401 - Unauthorized");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de todas las transacciones de la cuenta. Usuario no logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .basePath("/accounts/{id}/activity")
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

    //TC_Transacciones_0009
    @Tag("Smoke")
    @Test
    @Order(9)
    public void ViewAllTransactionsSuccess204() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0009 - GET all transactions by account id - Status Code: 204 - No Content");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 204 - No Content");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta Exitosa de todas las transacciones de la cuenta. ID de cuenta existente. Usuario logueado. El ID de cuenta corresponde al usuario. El usuario no posee ninguna transacción");

        Login_Id_4();

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token_id_4)
                    .basePath("/accounts/{id}/activity")
                    .pathParams("id", 4).
                when().
                    get().
                    then()
                    .assertThat()
                    .statusCode(204)
                    .statusCode(HttpStatus.SC_NO_CONTENT)
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Transacciones_0010
    @Tag("Smoke")
    @Test
    @Order(10)
    public void ViewAllTransactionsFailure403() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0010 - GET all transactions by account id - Status Code: 403 - Forbidden");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 403 - Forbidden");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de todas las transacciones de la cuenta. ID de cuenta existente. Usuario logueado. El ID de cuenta no corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity")
                    .pathParams("id", 1).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(403)
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Forbidden"))
                    .body("message", Matchers.equalTo("You don't have access to that account"))
                    .log().all()
                    .extract()
                    .response();
    }

    //**--------------- GET a transaction by id (/accounts/{id}/activity/activity/{transferencesId}) -------------**

    //TC_Transacciones_0011
    @Tag("Smoke")
    @Test
    @Order(11)
    public void ViewTransactionByIdSuccess200() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0011 - GET a an account's transaction by id - Status Code: 200 - OK");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de una transacción en particular (por su id). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/{transferencesId}")
                    .pathParams("id", 2)
                    .pathParams("transferencesId", 2).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$", hasKey("amount"))
                    .body("$", hasKey("realizationDate"))
                    .body("$", hasKey("description"))
                    .body("$", hasKey("fromCvu"))
                    .body("$", hasKey("toCvu"))
                    .body("$", hasKey("type"))
                    .body("$", hasKey("transaction_id"))
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Transacciones_0012
    @Tag("Smoke")
    @Test
    @Order(12)
    public void ViewTransactionByIdFailure404AccountIdNotFound() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0012 - GET a an account's transaction by id - Status Code: 404 - Not Found (Account ID)");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 404 - Not Found");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de una transacción en particular (por su id) asociada a un id de cuenta. Usuario logueado. ID de cuenta inexistente.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/{transferencesId}")
                    .pathParams("id", 99)
                    .pathParams("transferencesId", 2).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(404)
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Not Found"))
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Transacciones_0019
    @Tag("Smoke")
    @Test
    @Order(13)
    public void ViewTransactionByIdFailure404TransactionIdNotFound() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0012 - GET a an account's transaction by id - Status Code: 404 - Not Found (Transaction ID)");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 404 - Not Found");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de una transacción en particular (por su id) asociada a un id de cuenta. Usuario logueado. ID de cuenta inexistente.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/{transferencesId}")
                    .pathParams("id", 2)
                    .pathParams("transferencesId", 99).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(404)
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Not Found"))
                    .log().all()
                    .extract()
                    .response();

    }


    //TC_Transacciones_0013
    @Tag("Smoke")
    @Test
    @Order(14)
    public void ViewATransactionByIdFailure401() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0013 - GET a an account's transaction by id - Status Code: 401 - Unauthorized");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 401 - Unauthorized");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de una transacción en particular (por su id) asociada a un id de cuenta. Usuario no logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .basePath("/accounts/{id}/activity/{transferencesId}")
                    .pathParams("id", 2)
                    .pathParams("transferencesId", 2).
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

    //TC_Transacciones_0014
    @Tag("Smoke")
    @Test
    @Order(15)
    public void ViewATransactionByIdFailure403() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0014 - GET a an account's transaction by id - Status Code: 403 - Forbidden");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 403 - Forbidden");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de una transacción en particular (por su id) asociada a un id de cuenta. Usuario logueado. ID de cuenta existente. El ID de cuenta no corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/{transferencesId}")
                    .pathParams("id", 1)
                    .pathParams("transferencesId", 1).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(403)
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Forbidden"))
                    .body("message", Matchers.equalTo("You don't have access to that account"))
                    .log().all()
                    .extract()
                    .response();
    }

    //**------------------ POST a transaction (add money from card) (/accounts/{id}/deposit) -----------------**


    //TC_Transactions_0015
    @Tag("Smoke")
    @Test
    @Order(16)
    public void AddTransactionDepositMoneySuccess201() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0015 - POST a transaction (deposit money) - Status Code: 201 - Created ");
        test.assignCategory("Tarjetas");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 201 - Created");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta exitosa de nueva transacción (deposito de dinero a billetera desde tarjeta). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. Tarjeta no asociada a otro id de cuenta.");

        Response response;

        TransactionDeposit transaction = new TransactionDeposit(50.00, 3L);

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-type", "application/json")
                    .contentType(ContentType.JSON)
                    .basePath("/accounts/{id}/deposit")
                    .pathParams("id", 2)
                    .body(transaction).
                when().
                    post().
                then()
                    .assertThat()
                    .statusCode(201)
                    .statusCode(HttpStatus.SC_CREATED)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$", hasKey("transactionId"))
                    .body("$", hasKey("amount"))
                    .body("amount", Matchers.equalTo(50.00F))
                    .body("$", hasKey("realizationDate"))
                    .body("$", hasKey("description"))
                    .body("description", Matchers.equalTo("You deposited $50.0 from Brubank S.A.U. Débito"))
                    .body("cardNumber", Matchers.equalTo("**** 6269"))
                    .body("$", hasKey("toCvu"))
                    .body("toCvu", Matchers.equalTo("1828142364587587493333"))
                    .body("$", hasKey("type"))
                    .body("type", Matchers.equalTo("INCOMING"))
                    .log().all()
                    .extract()
                    .response();
    }


//TC_Transactions_0016
        @Tag("Smoke")
        @Test
        @Order(17)
        public void AddTransactionDepositMoneyFailure400AmountZero() throws InterruptedException {

            test = extent.createTest("TC_Transactions_0016 - POST a transaction (deposit money) - Status Code: 400 - Bad Request ");
            test.assignCategory("Tarjetas");
            test.assignCategory("Suite: Smoke");
            test.assignCategory("Request Method: POST");
            test.assignCategory("Status Code: 400 - Bad Request");
            test.assignCategory("Sprint: 3");
            test.assignAuthor("Ana Laura Fidalgo");
            test.info("Alta fallida de nueva transacción (deposito de dinero a billetera desde tarjeta). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. Tarjeta no asociada a otro id de cuenta. El monto de la transacción es igual a 0 (cero).");

            Response response;

            TransactionDeposit transaction = new TransactionDeposit(0.00, 3L);

            response = given()
                        .header("Authorization", "Bearer " + token)
                        .header("Content-type", "application/json")
                        .contentType(ContentType.JSON)
                        .basePath("/accounts/{id}/deposit")
                        .pathParams("id", 2)
                        .body(transaction).
                    when().
                        post().
                    then()
                        .assertThat()
                        .statusCode(400)
                        .statusCode(HttpStatus.SC_BAD_REQUEST)
                        .contentType(ContentType.JSON)
                        .body("$", Matchers.instanceOf(Map.class))
                        .body("$",hasKey("error"))
                        .body("error", Matchers.equalTo("Bad Request"))
                        .body("$",hasKey("message"))
                        .body("message", Matchers.equalTo("The amount can't be 0. Please enter a valid amount"))
                        .log().all()
                        .extract()
                        .response();
        }

    //TC_Transactions_0017
    @Tag("Smoke")
    @Test
    @Order(18)
    public void AddTransactionDepositMoneyFailure400AmountNegative() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0017 - POST a transaction (deposit money) - Status Code: 400 - Bad Request ");
        test.assignCategory("Tarjetas");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transacción (deposito de dinero a billetera desde tarjeta). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. Tarjeta no asociada a otro id de cuenta. El monto de la transacción es un número negativo.");

        Response response;

        TransactionDeposit transaction = new TransactionDeposit(-45.00, 3L);

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-type", "application/json")
                    .contentType(ContentType.JSON)
                    .basePath("/accounts/{id}/deposit")
                    .pathParams("id", 2)
                    .body(transaction).
                when().
                    post().
                then()
                .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Bad Request"))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("The amount can't be negative. Please enter a valid amount"))
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Transactions_0018
    @Tag("Smoke")
    @Test
    @Order(19)
    public void AddTransactionDepositMoneyFailure400CardExpired() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0018 - POST a transaction (deposit money) - Status Code: 400 - Bad Request ");
        test.assignCategory("Tarjetas");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transacción (deposito de dinero a billetera desde tarjeta). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. Tarjeta no asociada a otro id de cuenta. La tarjeta está expirada.");

        Response response;

        TransactionDeposit transaction = new TransactionDeposit(100.00, 5L);

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-type", "application/json")
                    .contentType(ContentType.JSON)
                    .basePath("/accounts/{id}/deposit")
                    .pathParams("id", 2)
                    .body(transaction).
                when().
                    post().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Bad Request"))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("The card you are trying to use is expired. "))
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Transactions_0020
    @Tag("Smoke")
    @Test
    @Order(20)
    public void AddTransactionDepositMoneyFailure401Unauthorized() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0020 - POST a transaction (deposit money) - Status Code: 401 - Unauthorized");
        test.assignCategory("Tarjetas");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("401 - Unauthorized");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transacción (deposito de dinero a billetera desde tarjeta). Usuario no logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. Tarjeta no asociada a otro id de cuenta.");

        Response response;

        TransactionDeposit transaction = new TransactionDeposit(100.00, 3L);

        response = given()
                    .header("Content-type", "application/json")
                    .contentType(ContentType.JSON)
                    .basePath("/accounts/{id}/deposit")
                    .pathParams("id", 2)
                    .body(transaction).
                when().
                    post().
                then()
                    .assertThat()
                    .statusCode(401)
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Transactions_0021
    @Tag("Smoke")
    @Test
    @Order(21)
    public void AddTransactionDepositMoneyFailure403ForbiddenAccountId() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0021 - POST a transaction (deposit money) - Status Code: 403 - Forbidden");
        test.assignCategory("Tarjetas");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("403 - Forbidden");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transacción (deposito de dinero a billetera desde tarjeta). Usuario logueado. ID de cuenta existente. El ID de cuenta no corresponde al usuario. Tarjeta no asociada a otro id de cuenta.");

        Response response;

        TransactionDeposit transaction = new TransactionDeposit(100.00, 3L);

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-type", "application/json")
                    .contentType(ContentType.JSON)
                    .basePath("/accounts/{id}/deposit")
                    .pathParams("id", 1)
                    .body(transaction).
                when().
                    post().
                then()
                    .assertThat()
                    .statusCode(403)
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Forbidden"))
                    .body("message", Matchers.equalTo("You don't have access to that account"))
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Transactions_0022
    @Tag("Smoke")
    @Test
    @Order(22)
    public void AddTransactionDepositMoneyFailure403ForbiddenCardId() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0022 - POST a transaction (deposit money) - Status Code: 403 - Forbidden");
        test.assignCategory("Tarjetas");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("403 - Forbidden");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transacción (deposito de dinero a billetera desde tarjeta). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. Tarjeta asociada a otro id de cuenta.");

        Response response;

        TransactionDeposit transaction = new TransactionDeposit(100.00, 1L);

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-type", "application/json")
                    .contentType(ContentType.JSON)
                    .basePath("/accounts/{id}/deposit")
                    .pathParams("id", 2)
                    .body(transaction).
                when().
                    post().
                then()
                    .assertThat()
                    .statusCode(403)
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Forbidden"))
                    .body("message", Matchers.equalTo("The card doesn't belong to the account"))
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Transactions_0023
    @Tag("Smoke")
    @Test
    @Order(23)
    public void AddTransactionDepositMoneyFailure404AccountId() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0023 - POST a transaction (deposit money) - Status Code: 404 - Not Found (Account Id)");
        test.assignCategory("Tarjetas");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("404 - Not Found");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transacción (deposito de dinero a billetera desde tarjeta). Usuario logueado. ID de cuenta inexistente. Tarjeta no asociada a otro id de cuenta.");

        Response response;

        TransactionDeposit transaction = new TransactionDeposit(100.00, 3L);

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-type", "application/json")
                    .contentType(ContentType.JSON)
                    .basePath("/accounts/{id}/deposit")
                    .pathParams("id", 99)
                    .body(transaction).
                when().
                    post().
                then()
                    .assertThat()
                    .statusCode(404)
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Not Found"))
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Transactions_0024
    @Tag("Smoke")
    @Test
    @Order(24)
    public void AddTransactionDepositMoneyFailure404CardId() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0024 - POST a transaction (deposit money) - Status Code: 404 - Not Found (Card Id)");
        test.assignCategory("Tarjetas");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("404 - Not Found");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transacción (deposito de dinero a billetera desde tarjeta). Usuario logueado. ID de cuenta existente. ID de tarjeta inexistente.");

        Response response;

        TransactionDeposit transaction = new TransactionDeposit(100.00, 99L);

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-type", "application/json")
                    .contentType(ContentType.JSON)
                    .basePath("/accounts/{id}/deposit")
                    .pathParams("id", 2)
                    .body(transaction).
                when().
                    post().
                then()
                    .assertThat()
                    .statusCode(404)
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Not Found"))
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Transactions_0025
    @Tag("Regression")
    @Test
    @Order(25)
    public void AddTransactionDepositMoneyFailure400AmountMoreThan2Decimals() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0025 - POST a transaction (deposit money) - Status Code: 400 - Bad Request (Amount has more than 2 decimals)");
        test.assignCategory("Tarjetas");
        test.assignCategory("Suite: Regression");
        test.assignCategory("Request Method: POST");
        test.assignCategory("400 - Bad Request");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transacción (deposito de dinero a billetera desde tarjeta). Usuario logueado. ID de cuenta existente. Tarjeta no asociada a otro id de cuenta. El monto de la transacción posee más de dos decimales.");

        Response response;

        TransactionDeposit transaction = new TransactionDeposit(100.012, 3L);

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-type", "application/json")
                    .contentType(ContentType.JSON)
                    .basePath("/accounts/{id}/deposit")
                    .pathParams("id", 2)
                    .body(transaction).
                when().
                    post().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Bad Request"))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("The amount must be a number with two decimal places"))
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Transactions_0026
    @Tag("Regression")
    @Test
    @Order(26)
    public void AddTransactionDepositMoneyFailure400AmountMoreThan2DecimalsAllZero() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0026 - POST a transaction (deposit money) - Status Code: 201 - Created (Amount has more than 2 decimals ALL zero)");
        test.assignCategory("Tarjetas");
        test.assignCategory("Suite: Regression");
        test.assignCategory("Request Method: POST");
        test.assignCategory("201 - Created");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transacción (deposito de dinero a billetera desde tarjeta). Usuario logueado. ID de cuenta existente. Tarjeta no asociada a otro id de cuenta. El monto de la transacción posee más de dos decimales que son todos 0.");

        Response response;

        TransactionDeposit transaction = new TransactionDeposit(100.000, 3L);

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-type", "application/json")
                    .contentType(ContentType.JSON)
                    .basePath("/accounts/{id}/deposit")
                    .pathParams("id", 2)
                    .body(transaction).
                when().
                    post().
                then()
                    .assertThat()
                    .statusCode(201)
                    .statusCode(HttpStatus.SC_CREATED)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$", hasKey("transactionId"))
                    .body("$", hasKey("amount"))
                    .body("amount", Matchers.equalTo(100.00F))
                    .body("$", hasKey("realizationDate"))
                    .body("$", hasKey("description"))
                    .body("description", Matchers.equalTo("You deposited $100.0 from Brubank S.A.U. Débito"))
                    .body("cardNumber", Matchers.equalTo("**** 6269"))
                    .body("$", hasKey("toCvu"))
                    .body("toCvu", Matchers.equalTo("1828142364587587493333"))
                    .body("$", hasKey("type"))
                    .body("type", Matchers.equalTo("INCOMING"))
                    .log().all()
                    .extract()
                    .response();
    }

    //**--------- GET all transactions filtered by amount (/accounts/{id}/activity/amount/{amountRange}) ---------**

    //TC_Transacciones_0027
    @Tag("Smoke")
    @Test
    @Order(27)
    public void ViewAllTransactionsFilteredByAmountRangeSuccess200() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0027 - GET all transactions filtered by amount - Status Code: 200 - OK");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de todas las transacciones de la cuenta (actividad) filtrada por rango de monto 3 (amount entre 5000 y 20000). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/amount/{amountRange}")
                    .pathParams("id", 2)
                    .pathParams("amountRange", 3).
                when().
                    get().
                    then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$", hasKey("account"))
                    .body("$", hasKey("transactions"))
                    .body("transactions[0]", hasKey("amount"))
                    .body("transactions[0]", hasKey("realizationDate"))
                    .body("transactions[0]", hasKey("description"))
                    .body("transactions[0]", hasKey("fromCvu"))
                    .body("transactions[0]", hasKey("toCvu"))
                    .body("transactions[0]", hasKey("type"))
                    .body("transactions[0]", hasKey("transaction_id"))
                    .body("transactions.size()", Matchers.greaterThanOrEqualTo(1))
                    .body("transactions.amount", Matchers.everyItem(greaterThanOrEqualTo(5000F)))
                    .body("transactions.amount", Matchers.everyItem(lessThanOrEqualTo(20000F)))
                    .log().all()
                    .extract()
                    .response();

    }

    //TC_Transacciones_0028
    @Tag("Smoke")
    @Test
    @Order(28)
    public void ViewAllTransactionsFilteredByAmountRangeSuccess204() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0028 - GET all transactions filtered by amount - Status Code: 204 - No Content");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 204 - No Content");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de todas las transacciones de la cuenta (actividad) filtrada por rango de monto 2 (amount entre 1000 y 5000). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/amount/{amountRange}")
                    .pathParams("id", 2)
                    .pathParams("amountRange", 2).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(204)
                    .statusCode(HttpStatus.SC_NO_CONTENT)
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Transacciones_0029
    @Tag("Regression")
    @Test
    @Order(29)
    public void ViewAllTransactionsFilteredByAmountRangeFailure400RangeLessThan1() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0029 - GET all transactions filtered by amount - Status Code: 400 - Bad Request (Range < 1)");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Regression");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de todas las transacciones de la cuenta (actividad) filtrada por rango de monto < 1 (los rangos posibles son entre 1 y 5). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/amount/{amountRange}")
                    .pathParams("id", 2)
                    .pathParams("amountRange", 0).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Select index out of range"))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("Please select a option within the range"))
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Transacciones_0030
    @Tag("Regression")
    @Test
    @Order(30)
    public void ViewAllTransactionsFilteredByAmountRangeFailure400RangeGreaterThan5() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0030 - GET all transactions filtered by amount - Status Code: 400 - Bad Request (Range > 5)");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Regression");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de todas las transacciones de la cuenta (actividad) filtrada por rango de monto > 5 (los rangos posibles son entre 1 y 5). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/amount/{amountRange}")
                    .pathParams("id", 2)
                    .pathParams("amountRange", 6).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Select index out of range"))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("Please select a option within the range"))
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Transacciones_0031
    @Tag("Smoke")
    @Test
    @Order(31)
    public void ViewAllTransactionsFilteredByAmountRangeFailure401() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0031 - GET all transactions filtered by amount - Status Code: 401 - Unauthorized");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 401 - Unauthorized");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de todas las transacciones de la cuenta (actividad) filtrada por rango de monto. Usuario no logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .basePath("/accounts/{id}/activity/amount/{amountRange}")
                    .pathParams("id", 2)
                    .pathParams("amountRange", 2).
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

    //TC_Transacciones_0032
    @Tag("Smoke")
    @Test
    @Order(32)
    public void ViewAllTransactionsFilteredByAmountRangeFailure403() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0032 - GET all transactions filtered by amount - Status Code: 403 - Forbidden");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 403 - Forbidden");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de todas las transacciones de la cuenta (actividad) filtrada por rango de monto. Usuario logueado. ID de cuenta existente. El ID de cuenta no corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/amount/{amountRange}")
                    .pathParams("id", 1)
                    .pathParams("amountRange", 2).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(403)
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Forbidden"))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("You don't have access to that account"))
                    .log().all()
                    .extract()
                    .response();
    }

    //TC_Transacciones_0033
    @Tag("Smoke")
    @Test
    @Order(33)
    public void ViewAllTransactionsFilteredByAmountRangeFailure404AccountId() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0033 - GET all transactions filtered by amount - Status Code: 404 - Not Found");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 404 - Not Found");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de todas las transacciones de la cuenta (actividad) filtrada por rango de monto. Usuario logueado. ID de cuenta inexistente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/amount/{amountRange}")
                    .pathParams("id", 99)
                    .pathParams("amountRange", 2).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(404)
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Not Found"))
                    .log().all()
                    .extract()
                    .response();
        }


    //TC_Transacciones_0034
    @Tag("Smoke")
    @Test
    @Order(34)
    public void ViewAllTransactionsFilteredByDynamicFilterAmountPeriodTypeSuccess200() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0034 - GET all transactions filtered (Dynamic Filter: amount, period, type) - Status Code: 200 - OK");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (monto, tipo transaccion y periodo). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("startDate", "2022-01-01")
                    .queryParams("endDate", "2023-05-16")
                    .queryParams("amountRange", 3)
                    .queryParams("type", "INCOMING").
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$", hasKey("account"))
                    .body("$", hasKey("transactions"))
                    .body("transactions[0]", hasKey("amount"))
                    .body("transactions[0]", hasKey("realizationDate"))
                    .body("transactions[0]", hasKey("description"))
                    .body("transactions[0]", hasKey("fromCvu"))
                    .body("transactions[0]", hasKey("toCvu"))
                    .body("transactions[0]", hasKey("type"))
                    .body("transactions[0]", hasKey("transaction_id"))
                    .body("transactions.size()", Matchers.greaterThanOrEqualTo(1))
                    .body("transactions.type", Matchers.everyItem(equalTo("INCOMING")))
                    .body("transactions.amount", Matchers.everyItem(greaterThanOrEqualTo(5000F)))
                    .body("transactions.amount", Matchers.everyItem(lessThanOrEqualTo(20000F)))
                    .body("transactions.realizationDate", Matchers.everyItem(greaterThanOrEqualTo("2022-01-01")))
                    .body("transactions.realizationDate", Matchers.everyItem(lessThanOrEqualTo("2023-05-16")))
                    .log().all()
                    .extract().response();

    }

    //TC_Transacciones_0035
    @Tag("Smoke")
    @Test
    @Order(35)
    public void ViewAllTransactionsFilteredByDynamicFilterAmountPeriodTypeSuccess204() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0035 - GET all transactions filtered (Dynamic Filter: amount, period, type) - Status Code: 204 - No Content");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 204 - No Content");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (monto, tipo transaccion y periodo). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. No se encuentran transacciones acordes a los filtros ingresados por el usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("startDate", "2022-01-01")
                    .queryParams("endDate", "2023-05-16")
                    .queryParams("amountRange", 2)
                    .queryParams("type", "INCOMING").
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(204)
                    .statusCode(HttpStatus.SC_NO_CONTENT)
                    .log().all()
                    .extract().response();

    }

    //TC_Transacciones_0036
    @Tag("Smoke")
    @Test
    @Order(36)
    public void ViewAllTransactionsFilteredByDynamicFilterAmountPeriodSuccess200() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0036 - GET all transactions filtered (Dynamic Filter: amount, period) - Status Code: 200 - OK");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (monto y periodo). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("startDate", "2022-01-01")
                    .queryParams("endDate", "2023-05-16")
                    .queryParams("amountRange", 3).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$", hasKey("account"))
                    .body("$", hasKey("transactions"))
                    .body("transactions[0]", hasKey("amount"))
                    .body("transactions[0]", hasKey("realizationDate"))
                    .body("transactions[0]", hasKey("description"))
                    .body("transactions[0]", hasKey("fromCvu"))
                    .body("transactions[0]", hasKey("toCvu"))
                    .body("transactions[0]", hasKey("type"))
                    .body("transactions[0]", hasKey("transaction_id"))
                    .body("transactions.size()", Matchers.greaterThanOrEqualTo(1))
                    .body("transactions.amount", Matchers.everyItem(greaterThanOrEqualTo(5000F)))
                    .body("transactions.amount", Matchers.everyItem(lessThanOrEqualTo(20000F)))
                    .body("transactions.realizationDate", Matchers.everyItem(greaterThanOrEqualTo("2022-01-01")))
                    .body("transactions.realizationDate", Matchers.everyItem(lessThanOrEqualTo("2023-05-16")))
                    .log().all()
                    .extract().response();

    }

    //TC_Transacciones_0037
    @Tag("Smoke")
    @Test
    @Order(37)
    public void ViewAllTransactionsFilteredByDynamicFilterAmountTypeSuccess200() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0037 - GET all transactions filtered (Dynamic Filter: amount, type) - Status Code: 200 - OK");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (monto y tipo). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("type", "OUTGOING")
                    .queryParams("amountRange", 3).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$", hasKey("account"))
                    .body("$", hasKey("transactions"))
                    .body("transactions[0]", hasKey("amount"))
                    .body("transactions[0]", hasKey("realizationDate"))
                    .body("transactions[0]", hasKey("description"))
                    .body("transactions[0]", hasKey("fromCvu"))
                    .body("transactions[0]", hasKey("toCvu"))
                    .body("transactions[0]", hasKey("type"))
                    .body("transactions[0]", hasKey("transaction_id"))
                    .body("transactions.size()", Matchers.greaterThanOrEqualTo(1))
                    .body("transactions.amount", Matchers.everyItem(greaterThanOrEqualTo(5000F)))
                    .body("transactions.amount", Matchers.everyItem(lessThanOrEqualTo(20000F)))
                    .body("transactions.type", Matchers.everyItem(equalTo("OUTGOING")))
                    .log().all()
                    .extract().response();

    }

    //TC_Transacciones_0038
    @Tag("Smoke")
    @Test
    @Order(38)
    public void ViewAllTransactionsFilteredByDynamicFilterAmountSuccess200() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0038 - GET all transactions filtered (Dynamic Filter: amount) - Status Code: 200 - OK");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (monto). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("amountRange", 3).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$", hasKey("account"))
                    .body("$", hasKey("transactions"))
                    .body("transactions[0]", hasKey("amount"))
                    .body("transactions[0]", hasKey("realizationDate"))
                    .body("transactions[0]", hasKey("description"))
                    .body("transactions[0]", hasKey("fromCvu"))
                    .body("transactions[0]", hasKey("toCvu"))
                    .body("transactions[0]", hasKey("type"))
                    .body("transactions[0]", hasKey("transaction_id"))
                    .body("transactions.size()", Matchers.greaterThanOrEqualTo(1))
                    .body("transactions.amount", Matchers.everyItem(greaterThanOrEqualTo(5000F)))
                    .body("transactions.amount", Matchers.everyItem(lessThanOrEqualTo(20000F)))
                    .log().all()
                    .extract().response();
    }

    //TC_Transacciones_0039
    @Tag("Smoke")
    @Test
    @Order(39)
    public void ViewAllTransactionsFilteredByDynamicFilterAmountFailure400AmountRangeGreaterThan5() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0039 - GET all transactions filtered (Dynamic Filter: amount) - Status Code: 400 - Bad Request (AmountRange > 5");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (monto). Monto ingresado fuera de rango (>5) Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("amountRange", 6).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Select index out of range"))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("Please select a option within the range"))
                    .log().all()
                    .extract().response();
    }


    //TC_Transacciones_0040
    @Tag("Smoke")
    @Test
    @Order(40)
    public void ViewAllTransactionsFilteredByDynamicFilterAmountFailure400AmountRangeLessThan1() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0040 - GET all transactions filtered (Dynamic Filter: amount) - Status Code: 400 - Bad Request (AmountRange < 1");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (monto). Monto ingresado fuera de rango (<1) Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("amountRange", 0).
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Select index out of range"))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("Please select a option within the range"))
                    .log().all()
                    .extract().response();
        }

    //TC_Transacciones_0041
    @Tag("Smoke")
    @Test
    @Order(41)
    public void ViewAllTransactionsFilteredByDynamicFilterPeriodTypeSuccess200() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0041 - GET all transactions filtered (Dynamic Filter: period, type) - Status Code: 200 - OK");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (tipo transaccion y periodo). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("startDate", "2022-01-01")
                    .queryParams("endDate", "2023-05-16")
                    .queryParams("type", "INCOMING").
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$", hasKey("account"))
                    .body("$", hasKey("transactions"))
                    .body("transactions[0]", hasKey("amount"))
                    .body("transactions[0]", hasKey("realizationDate"))
                    .body("transactions[0]", hasKey("description"))
                    .body("transactions[0]", hasKey("fromCvu"))
                    .body("transactions[0]", hasKey("toCvu"))
                    .body("transactions[0]", hasKey("type"))
                    .body("transactions[0]", hasKey("transaction_id"))
                    .body("transactions.size()", Matchers.greaterThanOrEqualTo(1))
                    .body("transactions.realizationDate", Matchers.everyItem(greaterThanOrEqualTo("2022-01-01")))
                    .body("transactions.realizationDate", Matchers.everyItem(lessThanOrEqualTo("2023-05-16")))
                    .body("transactions.type", Matchers.everyItem(equalTo("INCOMING")))
                    .log().all()
                    .extract().response();

    }

    //TC_Transacciones_0042
    @Tag("Smoke")
    @Test
    @Order(42)
    public void ViewAllTransactionsFilteredByDynamicFilterPeriodSuccess200() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0042 - GET all transactions filtered (Dynamic Filter: period) - Status Code: 200 - OK");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (periodo). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("startDate", "2023-01-01")
                    .queryParams("endDate", "2023-05-16").
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$", hasKey("account"))
                    .body("$", hasKey("transactions"))
                    .body("transactions[0]", hasKey("amount"))
                    .body("transactions[0]", hasKey("realizationDate"))
                    .body("transactions[0]", hasKey("description"))
                    .body("transactions[0]", hasKey("fromCvu"))
                    .body("transactions[0]", hasKey("toCvu"))
                    .body("transactions[0]", hasKey("type"))
                    .body("transactions[0]", hasKey("transaction_id"))
                    .body("transactions.size()", Matchers.greaterThanOrEqualTo(1))
                    .body("transactions.realizationDate", Matchers.everyItem(greaterThanOrEqualTo("2023-01-01")))
                    .body("transactions.realizationDate", Matchers.everyItem(lessThanOrEqualTo("2023-05-16")))
                    .log().all()
                    .extract().response();

    }

    //TC_Transacciones_0043
    @Tag("Smoke")
    @Test
    @Order(43)
    public void ViewAllTransactionsFilteredByDynamicFilterPeriodFailure400EndDateBeforeStartDate() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0043 - GET all transactions filtered (Dynamic Filter: period) - Status Code: 400 - Bad Request (EndDate before startDate");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (periodo). La endDate es anterior a la startDate. Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("startDate", "2023-01-01")
                    .queryParams("endDate", "2022-05-16").
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Bad Request"))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("The start date must be before the end date"))
                    .log().all()
                    .extract().response();

    }

    //TC_Transacciones_0044
    @Tag("Smoke")
    @Test
    @Order(44)
    public void ViewAllTransactionsFilteredByDynamicFilterPeriodSuccess200OnlyStartDate() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0044 - GET all transactions filtered (Dynamic Filter: period) - Status Code: 200 - OK (only startDate)");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (periodo). El usuario solo ingresa startDate. El sistema devuelve todas las transacciones desde esa fecha hasta hoy. Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("startDate", "2023-01-01").
                when().
                     get().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$", hasKey("account"))
                    .body("$", hasKey("transactions"))
                    .body("transactions[0]", hasKey("amount"))
                    .body("transactions[0]", hasKey("realizationDate"))
                    .body("transactions[0]", hasKey("description"))
                    .body("transactions[0]", hasKey("fromCvu"))
                    .body("transactions[0]", hasKey("toCvu"))
                    .body("transactions[0]", hasKey("type"))
                    .body("transactions[0]", hasKey("transaction_id"))
                    .body("transactions.size()", Matchers.greaterThanOrEqualTo(1))
                    .body("transactions.realizationDate", Matchers.everyItem(greaterThanOrEqualTo("2023-01-01")))
                    .log().all()
                    .extract().response();
    }

    //TC_Transacciones_0045
    @Tag("Smoke")
    @Test
    @Order(45)
    public void ViewAllTransactionsFilteredByDynamicFilterPeriodFailure400OnlyEndDate() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0045 - GET all transactions filtered (Dynamic Filter: period) - Status Code: 400 - Bad Request (only endDate)");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("400 - Bad Request");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (periodo). El usuario solo ingresa endDate. Debe ingresar tanto startDate como endDate o unicamente startDate. Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("endDate", "2022-01-01").
                when().
                     get().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Bad Request"))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("Only an end date was entered. You must enter a start date."))
                    .log().all()
                    .extract().response();
    }

    //TC_Transacciones_0046
    @Tag("Smoke")
    @Test
    @Order(46)
    public void ViewAllTransactionsFilteredByDynamicFilterTypeSuccess200() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0046 - GET all transactions filtered (Dynamic Filter: type) - Status Code: 200 - OK");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (tipo). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("type", "INCOMING").
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$", hasKey("account"))
                    .body("$", hasKey("transactions"))
                    .body("transactions[0]", hasKey("amount"))
                    .body("transactions[0]", hasKey("realizationDate"))
                    .body("transactions[0]", hasKey("description"))
                    .body("transactions[0]", hasKey("fromCvu"))
                    .body("transactions[0]", hasKey("toCvu"))
                    .body("transactions[0]", hasKey("type"))
                    .body("transactions[0]", hasKey("transaction_id"))
                    .body("transactions.size()", Matchers.greaterThanOrEqualTo(1))
                    .body("transactions.type", Matchers.everyItem(equalTo("INCOMING")))
                    .log().all()
                    .extract().response();

    }

    //TC_Transacciones_0047
    @Tag("Smoke")
    @Test
    @Order(47)
    public void ViewAllTransactionsFilteredByDynamicFilterTypeFailure400WrongType() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0047 - GET all transactions filtered (Dynamic Filter: type) - Status Code: 400 - Bad Request (Type is not INCOMING or OUTGOING)");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (tipo). El tipo ingresado (DEPOSIT) es incorrecto: debe ser INCOMING o OUTGOING. Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("type", "DEPOSIT").
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(400)
                    .statusCode(HttpStatus.SC_BAD_REQUEST)
                    .contentType(ContentType.JSON)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Bad Request"))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("Incorrect transaction type. Please choose INCOMING or OUTGOING"))
                    .log().all()
                    .extract().response();
    }

    //TC_Transacciones_0048
    @Tag("Smoke")
    @Test
    @Order(48)
    public void ViewAllTransactionsFilteredByDynamicFilterAmountPeriodTypeFailure401() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0048 - GET all transactions filtered (Dynamic Filter: amount, period, type) - Status Code: 401 - Unauthorized");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 401 - Unauthorized");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (monto, tipo transaccion y periodo). Usuario no logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 2)
                    .queryParams("startDate", "2022-01-01")
                    .queryParams("endDate", "2023-05-16")
                    .queryParams("amountRange", 2)
                    .queryParams("type", "INCOMING").
                when().
                     get().
                then()
                    .assertThat()
                    .statusCode(401)
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .log().all()
                    .extract().response();

    }

    //TC_Transacciones_0049
    @Tag("Smoke")
    @Test
    @Order(49)
    public void ViewAllTransactionsFilteredByDynamicFilterAmountPeriodTypeFailure403() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0049 - GET all transactions filtered (Dynamic Filter: amount, period, type) - Status Code: 403 - Forbidden");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 403 - Forbidden");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (monto, tipo transaccion y periodo). Usuario logueado. ID de cuenta existente. El ID de cuenta no corresponde al usuario.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 1)
                    .queryParams("startDate", "2022-01-01")
                    .queryParams("endDate", "2023-05-16")
                    .queryParams("amountRange", 2)
                    .queryParams("type", "INCOMING").
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(403)
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Forbidden"))
                    .body("$",hasKey("message"))
                    .body("message", Matchers.equalTo("You don't have access to that account"))
                    .log().all()
                    .extract().response();

    }

    //TC_Transacciones_0050
    @Tag("Smoke")
    @Test
    @Order(50)
    public void ViewAllTransactionsFilteredByDynamicFilterAmountPeriodTypeFailure404AccountId() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0050 - GET all transactions filtered (Dynamic Filter: amount, period, type) - Status Code: 404 - Not Found");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 404 - Not Found");
        test.assignCategory("Sprint: 3");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta fallida de todas las transacciones de la cuenta (actividad) filtrada con filtro dinamico (monto, tipo transaccion y periodo). Usuario logueado. ID de cuenta inexistente.");

        Response response;

        response = given()
                    .header("Authorization", "Bearer " + token)
                    .basePath("/accounts/{id}/activity/filters")
                    .pathParams("id", 99)
                    .queryParams("startDate", "2022-01-01")
                    .queryParams("endDate", "2023-05-16")
                    .queryParams("amountRange", 2)
                    .queryParams("type", "DEPOSIT").
                when().
                    get().
                then()
                    .assertThat()
                    .statusCode(404)
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .body("$", Matchers.instanceOf(Map.class))
                    .body("$",hasKey("error"))
                    .body("error", Matchers.equalTo("Not Found"))
                    .log().all()
                    .extract().response();

    }

//**-------------------------- POST transaction (/accounts/{id}/transferences) ------------------------**

    //TC_Transactions_0051
    @Tag("Smoke")
    @Test
    @Order(51)
    public void AddTransferenceSuccess201AliasToExternalAlias() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0051 - POST a transference (account to account) - Status Code: 201 - Created (alias to external alias)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 201 - Created");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta exitosa de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El alias de from_account pertenece al usuario. El alias de to_account no pertenece a la billetera Digital Money");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "asd.asd.asd");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(201)
                .statusCode(HttpStatus.SC_CREATED)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$", hasKey("transactionId"))
                .body("$", hasKey("amount"))
                .body("amount", Matchers.equalTo(100.00F))
                .body("$", hasKey("realizationDate"))
                .body("$", hasKey("description"))
                .body("description", Matchers.equalTo("Prueba de transferencia"))
                .body("$", hasKey("fromCvu"))
                .body("fromCvu", Matchers.equalTo("1828142364587587493333"))
                .body("$", hasKey("toCvu"))
                //.body("toCvu", Matchers.equalTo(""))
                .body("$", hasKey("type"))
                .body("type", Matchers.equalTo("OUTGOING"))
                .body("$", hasKey("account"))
                .body("account",hasKey("alias"))
                .body("account.alias", Matchers.equalTo("afectacion.divisa.cambios"))
                .body("account",hasKey("cvu"))
                .body("account.cvu", Matchers.equalTo("1828142364587587493333"))
                .body("account",hasKey("availableBalance"))
                //.body("account.availableBalance", Matchers.equalTo(f))
                .body("account",hasKey("account_id"))
                .body("account.account_id", Matchers.equalTo(2))
                .body("account",hasKey("user_id"))
                .body("account.user_id", Matchers.equalTo(2))
                .log().all()
                .extract()
                .response();
    }


    //TC_Transactions_0052
    @Tag("Smoke")
    @Test
    @Order(52)
    public void AddTransferenceSuccess201AliasToAlias() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0052 - POST a transference (account to account) - Status Code: 201 - Created (alias to alias)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 201 - Created");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta exitosa de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El alias de from_account pertenece al usuario. El alias de to_account pertenece a la billetera Digital Money.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "accion.adeudar.afianzamiento");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(201)
                .statusCode(HttpStatus.SC_CREATED)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$", hasKey("transactionId"))
                .body("$", hasKey("amount"))
                .body("amount", Matchers.equalTo(100.00F))
                .body("$", hasKey("realizationDate"))
                .body("$", hasKey("description"))
                .body("description", Matchers.equalTo("Prueba de transferencia"))
                .body("$", hasKey("fromCvu"))
                .body("fromCvu", Matchers.equalTo("1828142364587587493333"))
                .body("$", hasKey("toCvu"))
                .body("toCvu", Matchers.equalTo("1828142364587587491111"))
                .body("$", hasKey("type"))
                .body("type", Matchers.equalTo("OUTGOING"))
                .body("$", hasKey("account"))
                .body("account",hasKey("alias"))
                .body("account.alias", Matchers.equalTo("afectacion.divisa.cambios"))
                .body("account",hasKey("cvu"))
                .body("account.cvu", Matchers.equalTo("1828142364587587493333"))
                .body("account",hasKey("availableBalance"))
                //.body("account.availableBalance", Matchers.equalTo(f))
                .body("account",hasKey("account_id"))
                .body("account.account_id", Matchers.equalTo(2))
                .body("account",hasKey("user_id"))
                .body("account.user_id", Matchers.equalTo(2))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0053
    @Tag("Regression")
    @Test
    @Order(53)
    public void AddTransferenceSuccess201AliasToExternalAliasOneWord() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0053 - POST a transference (account to account) - Status Code: 201 - Created (alias to external alias - external alias format: one word)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Regression");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 201 - Created");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta exitosa de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El alias de from_account pertenece al usuario. El alias de to_account no pertenece a la billetera Digital Money. El alias de destino es una sola palabra.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "cazador");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(201)
                .statusCode(HttpStatus.SC_CREATED)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$", hasKey("transactionId"))
                .body("$", hasKey("amount"))
                .body("amount", Matchers.equalTo(100.00F))
                .body("$", hasKey("realizationDate"))
                .body("$", hasKey("description"))
                .body("description", Matchers.equalTo("Prueba de transferencia"))
                .body("$", hasKey("fromCvu"))
                .body("fromCvu", Matchers.equalTo("1828142364587587493333"))
                .body("$", hasKey("toCvu"))
                //.body("toCvu", Matchers.equalTo(""))
                .body("$", hasKey("type"))
                .body("type", Matchers.equalTo("OUTGOING"))
                .body("$", hasKey("account"))
                .body("account",hasKey("alias"))
                .body("account.alias", Matchers.equalTo("afectacion.divisa.cambios"))
                .body("account",hasKey("cvu"))
                .body("account.cvu", Matchers.equalTo("1828142364587587493333"))
                .body("account",hasKey("availableBalance"))
                //.body("account.availableBalance", Matchers.equalTo(f))
                .body("account",hasKey("account_id"))
                .body("account.account_id", Matchers.equalTo(2))
                .body("account",hasKey("user_id"))
                .body("account.user_id", Matchers.equalTo(2))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0055
    @Tag("Regression")
    @Test
    @Order(55)
    public void AddTransferenceSuccess201AliasToExternalAliasStartsWithNumber() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0055 - POST a transference (account to account) - Status Code: 201 - Created (alias to external alias - external alias format: starts with number)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Regression");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 201 - Created");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta exitosa de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El alias de from_account pertenece al usuario. El alias de to_account no pertenece a la billetera Digital Money. El alias de destino es una sola palabra + un solo punto.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "2arbol.sol");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(201)
                .statusCode(HttpStatus.SC_CREATED)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$", hasKey("transactionId"))
                .body("$", hasKey("amount"))
                .body("amount", Matchers.equalTo(100.00F))
                .body("$", hasKey("realizationDate"))
                .body("$", hasKey("description"))
                .body("description", Matchers.equalTo("Prueba de transferencia"))
                .body("$", hasKey("fromCvu"))
                .body("fromCvu", Matchers.equalTo("1828142364587587493333"))
                .body("$", hasKey("toCvu"))
                //.body("toCvu", Matchers.equalTo(""))
                .body("$", hasKey("type"))
                .body("type", Matchers.equalTo("OUTGOING"))
                .body("$", hasKey("account"))
                .body("account",hasKey("alias"))
                .body("account.alias", Matchers.equalTo("afectacion.divisa.cambios"))
                .body("account",hasKey("cvu"))
                .body("account.cvu", Matchers.equalTo("1828142364587587493333"))
                .body("account",hasKey("availableBalance"))
                //.body("account.availableBalance", Matchers.equalTo(f))
                .body("account",hasKey("account_id"))
                .body("account.account_id", Matchers.equalTo(2))
                .body("account",hasKey("user_id"))
                .body("account.user_id", Matchers.equalTo(2))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0054
    @Tag("Regression")
    @Test
    @Order(54)
    public void AddTransferenceSuccess201AliasToExternalAliasOneWordOneDot() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0054 - POST a transference (account to account) - Status Code: 201 - Created (alias to external alias - external alias format: one word + one dot)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Regression");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 201 - Created");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta exitosa de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El alias de from_account pertenece al usuario. El alias de to_account no pertenece a la billetera Digital Money. El alias de destino es una sola palabra + un solo punto.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "cazador.");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(201)
                .statusCode(HttpStatus.SC_CREATED)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$", hasKey("transactionId"))
                .body("$", hasKey("amount"))
                .body("amount", Matchers.equalTo(100.00F))
                .body("$", hasKey("realizationDate"))
                .body("$", hasKey("description"))
                .body("description", Matchers.equalTo("Prueba de transferencia"))
                .body("$", hasKey("fromCvu"))
                .body("fromCvu", Matchers.equalTo("1828142364587587493333"))
                .body("$", hasKey("toCvu"))
                //.body("toCvu", Matchers.equalTo(""))
                .body("$", hasKey("type"))
                .body("type", Matchers.equalTo("OUTGOING"))
                .body("$", hasKey("account"))
                .body("account",hasKey("alias"))
                .body("account.alias", Matchers.equalTo("afectacion.divisa.cambios"))
                .body("account",hasKey("cvu"))
                .body("account.cvu", Matchers.equalTo("1828142364587587493333"))
                .body("account",hasKey("availableBalance"))
                //.body("account.availableBalance", Matchers.equalTo(f))
                .body("account",hasKey("account_id"))
                .body("account.account_id", Matchers.equalTo(2))
                .body("account",hasKey("user_id"))
                .body("account.user_id", Matchers.equalTo(2))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0056
    @Tag("Smoke")
    @Test
    @Order(56)
    public void AddTransferenceFailure400FromAccountEmpty() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0056 - POST a transference (account to account) - Status Code: 400 - Bad Request (from_account empty)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El campo from_account esta vacío.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "", "asd.asd.asd");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(400)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Bad Request"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0057
    @Tag("Smoke")
    @Test
    @Order(57)
    public void AddTransferenceFailure404FromAccountAliasNotFound() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0057 - POST a transference (account to account) - Status Code: 404 - Not Found (from_account alias not found)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 404 - Not Found");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El alias de from_account no está registrado en Digital Money.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "cordero.lapiz.alfombra", "asd.asd.asd");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(404)
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Not Found"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("The account from which you are sending money does not exist"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0058
    @Tag("Regression")
    @Test
    @Order(58)
    public void AddTransferenceFailure404FromAccountAliasWrongFormat() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0058 - POST a transference (account to account) - Status Code: 400 - Bad Request (from_account alias wrong format)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Regression");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El alias de from_account no está registrado en Digital Money.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.dinero", "asd.asd.asd");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(400)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Bad Request"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("The account from which you want to send that you have entered does not comply with the alias rules"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0059
    @Tag("Smoke")
    @Test
    @Order(59)
    public void AddTransferenceFailure400ToAccountEmpty() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0059 - POST a transference (account to account) - Status Code: 400 - Bad Request (to_account empty)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El campo from_account pertenece al usuario. El campo to_account esta vacío.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(400)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Bad Request"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0060
    @Tag("Smoke")
    @Test
    @Order(60)
    public void AddTransferenceSuccess201AliasToExternalCVU() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0060 - POST a transference (account to account) - Status Code: 201 - Created (alias to External CVU)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 201 - Created");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta exitosa de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El alias de from_account pertenece al usuario. El CVU de to_account no pertenece a la billetera Digital Money.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "0123456789012345678901");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(201)
                .statusCode(HttpStatus.SC_CREATED)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$", hasKey("transactionId"))
                .body("$", hasKey("amount"))
                .body("amount", Matchers.equalTo(100.00F))
                .body("$", hasKey("realizationDate"))
                .body("$", hasKey("description"))
                .body("description", Matchers.equalTo("Prueba de transferencia"))
                .body("$", hasKey("fromCvu"))
                .body("fromCvu", Matchers.equalTo("1828142364587587493333"))
                .body("$", hasKey("toCvu"))
                .body("toCvu", Matchers.equalTo("0123456789012345678901"))
                .body("$", hasKey("type"))
                .body("type", Matchers.equalTo("OUTGOING"))
                .body("$", hasKey("account"))
                .body("account",hasKey("alias"))
                .body("account.alias", Matchers.equalTo("afectacion.divisa.cambios"))
                .body("account",hasKey("cvu"))
                .body("account.cvu", Matchers.equalTo("1828142364587587493333"))
                .body("account",hasKey("availableBalance"))
                //.body("account.availableBalance", Matchers.equalTo(f))
                .body("account",hasKey("account_id"))
                .body("account.account_id", Matchers.equalTo(2))
                .body("account",hasKey("user_id"))
                .body("account.user_id", Matchers.equalTo(2))
                .log().all()
                .extract()
                .response();
    }


    //TC_Transactions_0061
    @Tag("Smoke")
    @Test
    @Order(61)
    public void AddTransferenceSuccess201AliasToCVU() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0061 - POST a transference (account to account) - Status Code: 201 - Created (alias to CVU)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 201 - Created");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta exitosa de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El alias de from_account pertenece al usuario. El CVU de to_account pertenece a la billetera Digital Money.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "1828142364587587491111");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(201)
                .statusCode(HttpStatus.SC_CREATED)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$", hasKey("transactionId"))
                .body("$", hasKey("amount"))
                .body("amount", Matchers.equalTo(100.00F))
                .body("$", hasKey("realizationDate"))
                .body("$", hasKey("description"))
                .body("description", Matchers.equalTo("Prueba de transferencia"))
                .body("$", hasKey("fromCvu"))
                .body("fromCvu", Matchers.equalTo("1828142364587587493333"))
                .body("$", hasKey("toCvu"))
                .body("toCvu", Matchers.equalTo("1828142364587587491111"))
                .body("$", hasKey("type"))
                .body("type", Matchers.equalTo("OUTGOING"))
                .body("$", hasKey("account"))
                .body("account",hasKey("alias"))
                .body("account.alias", Matchers.equalTo("afectacion.divisa.cambios"))
                .body("account",hasKey("cvu"))
                .body("account.cvu", Matchers.equalTo("1828142364587587493333"))
                .body("account",hasKey("availableBalance"))
                //.body("account.availableBalance", Matchers.equalTo(f))
                .body("account",hasKey("account_id"))
                .body("account.account_id", Matchers.equalTo(2))
                .body("account",hasKey("user_id"))
                .body("account.user_id", Matchers.equalTo(2))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0062
    @Tag("Smoke")
    @Test
    @Order(62)
    public void AddTransferenceFailure400ToAccountCVULessThan22Digits() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0062 - POST a transference (account to account) - Status Code: 400 - Bad Request (alias to External CVU - CVU less than 22 digits)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El alias de from_account pertenece al usuario. El CVU de to_account no pertenece a la billetera Digital Money y posee menos de 22 dígitos.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "12345");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(400)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Bad Request"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("The account you are trying to send to does not meet the CVU/CBU rules. Please enter a 22 digit number"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0063
    @Tag("Smoke")
    @Test
    @Order(63)
    public void AddTransferenceFailure400ToAccountCVUMoreThan22Digits() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0063 - POST a transference (account to account) - Status Code: 400 - Bad Request (alias to External CVU - CVU more than 22 digits)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El alias de from_account pertenece al usuario. El CVU de to_account no pertenece a la billetera Digital Money y posee mas de 22 dígitos.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "01234567890123456789012");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(400)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Bad Request"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("The account you are trying to send to does not meet the CVU/CBU rules. Please enter a 22 digit number"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0064
    @Tag("Smoke")
    @Test
    @Order(64)
    public void AddTransferenceSuccess201CvuToCVU() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0064 - POST a transference (account to account) - Status Code: 201 - Created (CVU to CVU)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 201 - Created");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta exitosa de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El CVU de from_account pertenece al usuario. El CVU de to_account pertenece a la billetera Digital Money.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "1828142364587587493333", "1828142364587587491111");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(201)
                .statusCode(HttpStatus.SC_CREATED)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$", hasKey("transactionId"))
                .body("$", hasKey("amount"))
                .body("amount", Matchers.equalTo(100.00F))
                .body("$", hasKey("realizationDate"))
                .body("$", hasKey("description"))
                .body("description", Matchers.equalTo("Prueba de transferencia"))
                .body("$", hasKey("fromCvu"))
                .body("fromCvu", Matchers.equalTo("1828142364587587493333"))
                .body("$", hasKey("toCvu"))
                .body("toCvu", Matchers.equalTo("1828142364587587491111"))
                .body("$", hasKey("type"))
                .body("type", Matchers.equalTo("OUTGOING"))
                .body("$", hasKey("account"))
                .body("account",hasKey("alias"))
                .body("account.alias", Matchers.equalTo("afectacion.divisa.cambios"))
                .body("account",hasKey("cvu"))
                .body("account.cvu", Matchers.equalTo("1828142364587587493333"))
                .body("account",hasKey("availableBalance"))
                //.body("account.availableBalance", Matchers.equalTo(f))
                .body("account",hasKey("account_id"))
                .body("account.account_id", Matchers.equalTo(2))
                .body("account",hasKey("user_id"))
                .body("account.user_id", Matchers.equalTo(2))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0065
    @Tag("Smoke")
    @Test
    @Order(65)
    public void AddTransferenceSuccess201CvuToExternalCVU() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0065 - POST a transference (account to account) - Status Code: 201 - Created (CVU to External CVU)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 201 - Created");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta exitosa de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El CVU de from_account pertenece al usuario. El CVU de to_account no pertenece a la billetera Digital Money.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "1828142364587587493333", "1111111111111111111111");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(201)
                .statusCode(HttpStatus.SC_CREATED)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$", hasKey("transactionId"))
                .body("$", hasKey("amount"))
                .body("amount", Matchers.equalTo(100.00F))
                .body("$", hasKey("realizationDate"))
                .body("$", hasKey("description"))
                .body("description", Matchers.equalTo("Prueba de transferencia"))
                .body("$", hasKey("fromCvu"))
                .body("fromCvu", Matchers.equalTo("1828142364587587493333"))
                .body("$", hasKey("toCvu"))
                .body("toCvu", Matchers.equalTo("1111111111111111111111"))
                .body("$", hasKey("type"))
                .body("type", Matchers.equalTo("OUTGOING"))
                .body("$", hasKey("account"))
                .body("account",hasKey("alias"))
                .body("account.alias", Matchers.equalTo("afectacion.divisa.cambios"))
                .body("account",hasKey("cvu"))
                .body("account.cvu", Matchers.equalTo("1828142364587587493333"))
                .body("account",hasKey("availableBalance"))
                //.body("account.availableBalance", Matchers.equalTo(f))
                .body("account",hasKey("account_id"))
                .body("account.account_id", Matchers.equalTo(2))
                .body("account",hasKey("user_id"))
                .body("account.user_id", Matchers.equalTo(2))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0066
    @Tag("Smoke")
    @Test
    @Order(66)
    public void AddTransferenceFailure400FromAccountCVULessThan22Digits() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0066 - POST a transference (account to account) - Status Code: 400 - Bad Request (CVU to External CVU - from_account CVU less than 22 digits)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El CVU de from_account y posee menos de 22 dígitos. El CVU de to_account no pertenece a la billetera Digital Money.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "12345", "1828142364587587491111");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(400)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Bad Request"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("The account from which you want to send that you have entered does not comply with the alias, cvu or cbu rules"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0067
    @Tag("Smoke")
    @Test
    @Order(67)
    public void AddTransferenceFailure400FromAccountCVUMoreThan22Digits() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0067 - POST a transference (account to account) - Status Code: 400 - Bad Request (CVU to External CVU - from_account CVU more than 22 digits)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El CVU de from_account y posee mas de 22 dígitos. El CVU de to_account no pertenece a la billetera Digital Money.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "01234567890123456789012", "1828142364587587491111");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(400)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Bad Request"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("The account from which you want to send that you have entered does not comply with CVU/CBU rules. Please enter a 22 digit number"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0068
    @Tag("Smoke")
    @Test
    @Order(68)
    public void AddTransferenceSuccess201CvuToAlias() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0068 - POST a transference (account to account) - Status Code: 201 - Created (CVU to Alias)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 201 - Created");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta exitosa de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El CVU de from_account pertenece al usuario. El alias de to_account pertenece a la billetera Digital Money.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "1828142364587587493333", "accion.adeudar.afianzamiento");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(201)
                .statusCode(HttpStatus.SC_CREATED)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$", hasKey("transactionId"))
                .body("$", hasKey("amount"))
                .body("amount", Matchers.equalTo(100.00F))
                .body("$", hasKey("realizationDate"))
                .body("$", hasKey("description"))
                .body("description", Matchers.equalTo("Prueba de transferencia"))
                .body("$", hasKey("fromCvu"))
                .body("fromCvu", Matchers.equalTo("1828142364587587493333"))
                .body("$", hasKey("toCvu"))
                .body("toCvu", Matchers.equalTo("1828142364587587491111"))
                .body("$", hasKey("type"))
                .body("type", Matchers.equalTo("OUTGOING"))
                .body("$", hasKey("account"))
                .body("account",hasKey("alias"))
                .body("account.alias", Matchers.equalTo("afectacion.divisa.cambios"))
                .body("account",hasKey("cvu"))
                .body("account.cvu", Matchers.equalTo("1828142364587587493333"))
                .body("account",hasKey("availableBalance"))
                //.body("account.availableBalance", Matchers.equalTo(f))
                .body("account",hasKey("account_id"))
                .body("account.account_id", Matchers.equalTo(2))
                .body("account",hasKey("user_id"))
                .body("account.user_id", Matchers.equalTo(2))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0069
    @Tag("Smoke")
    @Test
    @Order(69)
    public void AddTransferenceSuccess201CvuToExternalAlias() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0069 - POST a transference (account to account) - Status Code: 201 - Created (CVU to External Alias)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 201 - Created");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta exitosa de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El CVU de from_account pertenece al usuario. El alias de to_account no pertenece a la billetera Digital Money.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "1828142364587587493333", "asd.asd.asd");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(201)
                .statusCode(HttpStatus.SC_CREATED)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$", hasKey("transactionId"))
                .body("$", hasKey("amount"))
                .body("amount", Matchers.equalTo(100.00F))
                .body("$", hasKey("realizationDate"))
                .body("$", hasKey("description"))
                .body("description", Matchers.equalTo("Prueba de transferencia"))
                .body("$", hasKey("fromCvu"))
                .body("fromCvu", Matchers.equalTo("1828142364587587493333"))
                .body("$", hasKey("toCvu"))
                .body("$", hasKey("type"))
                .body("type", Matchers.equalTo("OUTGOING"))
                .body("$", hasKey("account"))
                .body("account",hasKey("alias"))
                .body("account.alias", Matchers.equalTo("afectacion.divisa.cambios"))
                .body("account",hasKey("cvu"))
                .body("account.cvu", Matchers.equalTo("1828142364587587493333"))
                .body("account",hasKey("availableBalance"))
                //.body("account.availableBalance", Matchers.equalTo(f))
                .body("account",hasKey("account_id"))
                .body("account.account_id", Matchers.equalTo(2))
                .body("account",hasKey("user_id"))
                .body("account.user_id", Matchers.equalTo(2))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0070
    @Tag("Smoke")
    @Test
    @Order(70)
    public void AddTransferenceFailure400DescriptionEmpty() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0070 - POST a transference (account to account) - Status Code: 400 - Bad Request (description empty)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El CVU de from_account pertenece al usuario. El alias de to_account no pertenece a la billetera Digital Money. La descripcion de la transferencia esta vacía.");

        Response response;

        Transference transference = new Transference(100.0, "", "1828142364587587493333", "asd.asd.asd");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(400)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Bad Request"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("The description cannot be empty"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0071
    @Tag("Smoke")
    @Test
    @Order(71)
    public void AddTransferenceFailure400AmountZero() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0071 - POST a transference (account to account) - Status Code: 400 - Bad Request (amount = 0)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El CVU de from_account pertenece al usuario. El alias de to_account no pertenece a la billetera Digital Money. El amount de la transferencia es igual a 0 (cero).");

        Response response;

        Transference transference = new Transference(0.0, "Prueba de transferencia", "1828142364587587493333", "asd.asd.asd");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(400)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Bad Request"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("Amount cannot be less than 1"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0072
    @Tag("Smoke")
    @Test
    @Order(72)
    public void AddTransferenceFailure400AmountLessThanZero() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0072 - POST a transference (account to account) - Status Code: 400 - Bad Request (amount < 0)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El CVU de from_account pertenece al usuario. El alias de to_account no pertenece a la billetera Digital Money. El amount de la transferencia es menor a 0 (cero).");

        Response response;

        Transference transference = new Transference(-100.0, "Prueba de transferencia", "1828142364587587493333", "asd.asd.asd");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(400)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Bad Request"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("Amount cannot be less than 1"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0073
    @Tag("Smoke")
    @Test
    @Order(73)
    public void AddTransferenceFailure400TransferToSameAccountCvuToCvu() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0073 - POST a transference (account to account) - Status Code: 400 - Bad Request (transfer to same account - CVU to CVU)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.  El CVU de from_account pertenece a la cuenta del usuario logueado. El CVU de to_account es el mismo y pertenece a la misma cuenta.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "1828142364587587493333", "1828142364587587493333");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(400)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Bad Request"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("You can't transfer money to the same account"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0074
    @Tag("Smoke")
    @Test
    @Order(74)
    public void AddTransferenceFailure400TransferToSameAccountCvuToAlias() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0074 - POST a transference (account to account) - Status Code: 400 - Bad Request (transfer to same account - CVU to alias)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El CVU de from_account pertenece a la cuenta del usuario logueado. El alias de to_account también pertenece a la misma cuenta.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "1828142364587587493333", "afectacion.divisa.cambios");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(400)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Bad Request"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("You can't transfer money to the same account"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0075
    @Tag("Smoke")
    @Test
    @Order(75)
    public void AddTransferenceFailure400TransferToSameAccountAliasToAlias() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0075 - POST a transference (account to account) - Status Code: 400 - Bad Request (transfer to same account - alias to alias)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El alias de from_account pertenece a la cuenta del usuario logueado. El alias de to_account es el mismo y pertenece a la misma cuenta.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "afectacion.divisa.cambios");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(400)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Bad Request"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("You can't transfer money to the same account"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0076
    @Tag("Smoke")
    @Test
    @Order(76)
    public void AddTransferenceFailure400TransferToSameAccountAliasToCvu() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0076 - POST a transference (account to account) - Status Code: 400 - Bad Request (transfer to same account - alias to CVU)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 400 - Bad Request");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El alias de from_account pertenece a la cuenta del usuario logueado. El CVU de to_account es el mismo y también pertenece a la misma cuenta.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "1828142364587587493333");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(400)
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Bad Request"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("You can't transfer money to the same account"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0077
    @Tag("Smoke")
    @Test
    @Order(77)
    public void AddTransferenceFailure400InsufficientFunds() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0077 - POST a transference (account to account) - Status Code: 410 (insufficient funds)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 410 - Insufficient Funds");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. La cuenta de from_account pertenece a la cuenta del usuario logueado. La cuenta de to_account no pertenece a la billetera Digital Money. La cuenta de origen no cuenta con fondos suficientes para realizar la transferencia por el monto ingresado.");

        Response response;

        Transference transference = new Transference(1000000.0, "Prueba de transferencia", "afectacion.divisa.cambios", "asd.asd.asd");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(410)
                .statusCode(HttpStatus.SC_GONE)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Insufficient Funds"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("Account balance less than the chosen amount"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0078
    @Tag("Smoke")
    @Test
    @Order(78)
    public void AddTransferenceFailure403AccountId() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0078 - POST a transference (account to account) - Status Code: 403 - Forbidden (account id doesn't belong to user)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 403 - Forbidden");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta no corresponde al usuario.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "asd.asd.asd");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 1)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(403)
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Forbidden"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("You don't have access to that account"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0079
    @Tag("Smoke")
    @Test
    @Order(79)
    public void AddTransferenceFailure403FromAccountAlias() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0079 - POST a transference (account to account) - Status Code: 403 - Forbidden (from_account alias doesn't belong to user)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 403 - Forbidden");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El alias de from_account no pertenece al usuario.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "accion.adeudar.afianzamiento", "asd.asd.asd");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(403)
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Forbidden"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("The account from which you are sending money from does not belong to you"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0080
    @Tag("Smoke")
    @Test
    @Order(80)
    public void AddTransferenceFailure403FromAccountCvu() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0080 - POST a transference (account to account) - Status Code: 403 - Forbidden (from_account CVU doesn't belong to user)");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 403 - Forbidden");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. El CVU de from_account no pertenece al usuario.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "1828142364587587491111", "asd.asd.asd");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(403)
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Forbidden"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("The account from which you are sending money from does not belong to you"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0081
    @Tag("Smoke")
    @Test
    @Order(81)
    public void AddTransferenceFailure404AccountIdNotFound() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0081 - POST a transference (account to account) - Status Code: 404 - Not Found");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 404 - Not Found");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario logueado. ID de cuenta inexistente.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "asd.asd.asd");

        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 99)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(404)
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Not Found"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("The search returned no results with id: 99"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transactions_0082
    @Tag("Smoke")
    @Test
    @Order(82)
    public void AddTransferenceFailure401() throws InterruptedException {

        test = extent.createTest("TC_Transactions_0082 - POST a transference (account to account) - Status Code: 401 - Unauthorized");
        test.assignCategory("Transferencias");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: POST");
        test.assignCategory("Status Code: 401 - Unauthorized");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Alta fallida de nueva transferencia (envío de dinero de una cuenta a otra). Usuario no logueado. ID de cuenta existente.");

        Response response;

        Transference transference = new Transference(100.0, "Prueba de transferencia", "afectacion.divisa.cambios", "asd.asd.asd");

        response = given()
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2)
                .body(transference).
                when().
                post().
                then()
                .assertThat()
                .statusCode(401)
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .log().all()
                .extract()
                .response();
    }


    //**------------------ GET last 5 transference recipients (/accounts/{id}/transferences) ---------------**


    //TC_Transacciones_0083
    @Tag("Smoke")
    @Test
    @Order(83)
    public void ViewLastFiveTransferencesSuccess200() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0083 - GET last 5 transference recipients by CVU - Status Code: 200 - OK");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 200 - OK");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de los últimos 5 CVUs destinatarios de transferencias. Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario.");

        Response response;

        response = given()
                .header("Authorization", "Bearer " + token)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 2).
                when().
                get().
                then()
                .assertThat()
                .statusCode(200)
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .body("$", Matchers.instanceOf(List.class))
                .body("[0]",hasKey("cvu"))
                .body("[0].cvu",equalTo("1828142364587587495555"))
                .body("$.size()", Matchers.greaterThanOrEqualTo(1))
                .body("$.size()", Matchers.lessThanOrEqualTo(5))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transacciones_0084
    @Tag("Smoke")
    @Test
    @Order(84)
    public void ViewLastFiveTransferencesSuccess204() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0084 - GET last 5 transference recipients by CVU - (account without transferences) Status Code: 204 - No Content");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 204 - No Content");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de los últimos 5 CVUs destinatarios de transferencias. Usuario logueado. ID de cuenta existente. El ID de cuenta corresponde al usuario. La cuenta no posee transferencias.");

        Login_Id_4();

        Response response;

        response = given()
                .header("Authorization", "Bearer " + token_id_4)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 4).
                when().
                get().
                then()
                .assertThat()
                .statusCode(204)
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .log().all()
                .extract()
                .response();
    }

    //TC_Transacciones_0085
    @Tag("Smoke")
    @Test
    @Order(85)
    public void ViewLastFiveTransferencesSuccess403() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0085 - GET last 5 transference recipients by CVU - Status Code: 403 - Forbidden");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 403 - Forbidden");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de los últimos 5 CVUs destinatarios de transferencias. Usuario logueado. ID de cuenta existente. El ID de cuenta no corresponde al usuario.");

        Response response;

        response = given()
                .header("Authorization", "Bearer " + token)
                .basePath("/accounts/{id}/transferences")
                .pathParams("id", 1).
                when().
                get().
                then()
                .assertThat()
                .statusCode(403)
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("$", Matchers.instanceOf(Map.class))
                .body("$",hasKey("error"))
                .body("error", Matchers.equalTo("Forbidden"))
                .body("$",hasKey("message"))
                .body("message", Matchers.equalTo("You don't have access to that account"))
                .log().all()
                .extract()
                .response();
    }

    //TC_Transacciones_0086
    @Tag("Smoke")
    @Test
    @Order(86)
    public void ViewLastFiveTransferencesSuccess401() throws InterruptedException {

        test = extent.createTest("TC_Transacciones_0086 - GET last 5 transference recipients by CVU - Status Code: 401 - Unauthorized");
        test.assignCategory("Transacciones");
        test.assignCategory("Suite: Smoke");
        test.assignCategory("Request Method: GET");
        test.assignCategory("Status Code: 401 - Unauthorized");
        test.assignCategory("Sprint: 4");
        test.assignAuthor("Ana Laura Fidalgo");
        test.info("Consulta exitosa de los últimos 5 CVUs destinatarios de transferencias. Usuario no logueado. ID de cuenta existente.");

        Response response;

        response = given()
                .basePath("/accounts/{id}/transferences")
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

    //**--------------------------------------------------- AUX -------------------------------------------------**

    public static void Login_Id_4() {
        token_id_4 = given()
                    .auth().preemptive()
                    .basic(client_id, client_secret)
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("grant_type", "password")
                    //account_id: 4
                    .formParam("username", "amaria@mail.com")
                    .formParam("password", password)
                    .basePath("/security/oauth/token")
                .when()
                    .post()
                .then()
                    .log().all()
                    .extract()
                    .jsonPath().get("access_token");
    }

}
