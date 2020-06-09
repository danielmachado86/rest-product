package io.dmcapps.dshopping.product;

import io.quarkus.test.junit.NativeImageTest;

import io.quarkus.test.common.QuarkusTestResource;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@NativeImageTest
@QuarkusTestResource(DatabaseResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NativeProductResourceIT extends ProductResourceTest {


    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/api/products/hello")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }

}