package io.dmcapps.dshopping.product;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.path.json.JsonPath;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import static javax.ws.rs.core.Response.Status.*;

import java.util.List;
import java.util.Random;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(DatabaseResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BrandResourceTest {

    private static final String DEFAULT_NAME = "Ramo";
    private static final String DEFAULT_PICTURE = "ramo.png";
    private static final String UPDATED_PICTURE = "ramo_updated.png";
    private static final String DEFAULT_DESCRIPTION = "Productos Ramo es una compañía Colombiana de alimentos, especializada en productos de panadería​.";
    private static final String UPDATED_DESCRIPTION = "Es conocida por sus Ponqués de diversos sabores.";

    private static final int NB_BRANDS = 64;
    private static String brandId;


    private String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }
    
    @Test
    void shouldNotGetUnknownProduct() {
        String id = getRandomHexString(10);
        given()
            .pathParam("id", id)
            .when().get("/api/brands/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    @Order(1)
    void shouldGetInitialItems() {
        List<Brand> brands = given()
            .when().get("/api/brands").then()
            .statusCode(OK.getStatusCode())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .extract().body().as(getBrandsTypeRef());
        assertEquals(NB_BRANDS, brands.size());
    }

    @Test
    void shouldFindProducts() {
        List<Brand> brands = given()
            .queryParam("q", "")
            .when().get("/api/brands")
            .then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getBrandsTypeRef());
        assertEquals(NB_BRANDS, brands.size());
    }

    @Test
    @Order(2)
    void shouldAddAnItem() {
        Brand brand = new Brand();
        brand.id = DEFAULT_NAME;
        brand.picture = DEFAULT_PICTURE;
        brand.description = DEFAULT_DESCRIPTION;

        String location = given()
            .body(brand.toString())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/brands")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().header("Location");
        assertTrue(location.contains("/api/brands"));

        // Stores the id
        String[] segments = location.split("/");
        brandId = segments[segments.length - 1];
        assertNotNull(segments[segments.length - 1]);

        JsonPath response = given()
        .pathParam("id", brandId)
        .when().get("/api/brands/{id}")
        .then()
        .statusCode(OK.getStatusCode())
        .header(CONTENT_TYPE, APPLICATION_JSON)
        .extract().jsonPath();
        
        String expectedPicture = DEFAULT_PICTURE;
        String picture = response.getString("picture");
        assertEquals(expectedPicture, picture);
        
        String expectedDescription = DEFAULT_DESCRIPTION;
        String description = response.getString("description");
        assertEquals(expectedDescription, description);


        List<Brand> brands = given()
            .when().get("/api/brands").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getBrandsTypeRef());
        assertEquals(NB_BRANDS + 1, brands.size());
    }

    @Test
    @Order(3)
    @Context
    void shouldUpdateAnItem(){
        Brand brand = new Brand();
        brand.id = brandId;
        brand.picture = UPDATED_PICTURE;
        brand.description = UPDATED_DESCRIPTION;

        JsonPath response = given()
            .contentType("application/json")
            .body(brand)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .put("/api/brands")
            .then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().jsonPath();

        String expectedPicture = UPDATED_PICTURE;
        String picture = response.getString("picture");
        assertEquals(expectedPicture, picture);

        String expectedDescription = UPDATED_DESCRIPTION;
        String description = response.getString("description");
        assertEquals(expectedDescription, description);


        List<Brand> brands = given()
            .when().get("/api/brands").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getBrandsTypeRef());
        assertEquals(NB_BRANDS+ 1, brands.size());
    }

    @Test
    @Order(4)
    void shouldRemoveAnItem() {
        given()
            .pathParam("id", brandId)
            .when().delete("/api/brands/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        List<Brand> brands = given()
            .when().get("/api/brands").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getBrandsTypeRef());
        assertEquals(NB_BRANDS, brands.size());
    }

    private TypeRef<List<Brand>> getBrandsTypeRef() {
        return new TypeRef<List<Brand>>() {
            // Kept empty on purpose
        };
    }

}