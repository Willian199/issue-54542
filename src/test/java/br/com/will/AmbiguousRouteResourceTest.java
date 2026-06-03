package br.com.will;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class AmbiguousRouteResourceTest {
    @Test
    void duplicateNumericPathParameterRouteSilentlySelectsOnlyOneMethod() {
        given()
                .when().get("/api-path/v1/1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("source", is("getById"));

        given()
                .when().get("/api-path/v1/42")
                .then()
                .statusCode(200)
                .body("id", is(42))
                .body("source", is("getById"));
    }
}
