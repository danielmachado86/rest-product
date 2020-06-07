package io.dmcapps.dshopping.product;

import io.quarkus.test.junit.NativeImageTest;

import io.quarkus.test.common.QuarkusTestResource;
import io.restassured.common.mapper.TypeRef;
import io.restassured.path.json.JsonPath;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import static javax.ws.rs.core.Response.Status.*;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static org.junit.jupiter.api.Assertions.*;

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