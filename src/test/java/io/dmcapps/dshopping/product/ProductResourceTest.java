package io.dmcapps.dshopping.product;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
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

import java.util.List;
import java.util.Random;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(DatabaseResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductResourceTest {

    private static final String DEFAULT_NAME = "Avena en caja";
    private static final String UPDATED_NAME = "Avena en caja (updated)";
    private static final int DEFAULT_BRAND = 1;
    private static final int UPDATED_BRAND = 2;
    private static final String DEFAULT_PICTURE = "avena_en_caja.png";
    private static final String UPDATED_PICTURE = "avena_en_caja_updated.png";
    private static final ProductDescription DEFAULT_DESCRIPTION = new ProductDescription(2.5, "1234567");
    private static final ProductDescription UPDATED_DESCRIPTION = new ProductDescription(20.5, "7654321");

    private static final int NB_PRODUCTS= 0;
    private static ObjectId productId;

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/api/products/hello")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }

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
        ObjectId id = new ObjectId(getRandomHexString(24));
        given()
            .pathParam("id", id.toString())
            .when().get("/api/products/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    @Order(1)
    void shouldGetInitialItems() {
        List<Product> products = get("/api/products").then()
            .statusCode(OK.getStatusCode())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .extract().body().as(getProductTypeRef());
        assertEquals(NB_PRODUCTS, products.size());
    }

    @Test
    @Order(2)
    void shouldAddAnItem() {
        Product product = new Product();
        product.name = DEFAULT_NAME;
        product.brand = DEFAULT_BRAND;
        product.picture = DEFAULT_PICTURE;
        product.description = DEFAULT_DESCRIPTION;

        String location = given()
            .body(product)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/products")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().header("Location");
        assertTrue(location.contains("/api/products"));

        // Stores the id
        String[] segments = location.split("/");
        productId = new ObjectId(segments[segments.length - 1]);
        assertNotNull(segments[segments.length - 1]);

        JsonPath response = given()
        .pathParam("id", productId.toString())
        .when().get("/api/products/{id}")
        .then()
        .statusCode(OK.getStatusCode())
        .header(CONTENT_TYPE, APPLICATION_JSON)
        .extract().jsonPath();

        String expectedName = DEFAULT_NAME;
        String name = response.getString("name");
        assertEquals(expectedName, name);

        int expectedBrand = DEFAULT_BRAND;
        int brand = response.getInt("brand");
        assertTrue(expectedBrand == brand);

        String expectedPicture = DEFAULT_PICTURE;
        String picture = response.getString("picture");
        assertEquals(expectedPicture, picture);

        double expectedWeight = DEFAULT_DESCRIPTION.weight;
        double weight = response.getDouble("description.weight");
        assertTrue(expectedWeight == weight);

        String expectedSku = DEFAULT_DESCRIPTION.sku;
        String sku = response.getString("description.sku");
        assertEquals(expectedSku, sku);

        List<Product> products = get("/api/products").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getProductTypeRef());
        assertEquals(NB_PRODUCTS + 1, products.size());
    }

    @Test
    @Order(3)
    @Context
    void shouldUpdateAnItem(){
        Product product = new Product();
        product.id = productId;
        product.name = UPDATED_NAME;
        product.brand = UPDATED_BRAND;
        product.picture = UPDATED_PICTURE;
        product.description = UPDATED_DESCRIPTION;

        JsonPath response = given()
            .contentType("application/json")
            .body(product.toString())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .put("/api/products")
            .then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().jsonPath();

        String expectedName = UPDATED_NAME;
        String name = response.getString("name");
        assertEquals(expectedName, name);

        int expectedBrand = UPDATED_BRAND;
        int brand = response.getInt("brand");
        assertTrue(expectedBrand == brand);

        String expectedPicture = UPDATED_PICTURE;
        String picture = response.getString("picture");
        assertEquals(expectedPicture, picture);

        double expectedWeight = UPDATED_DESCRIPTION.weight;
        double weight = response.getDouble("description.weight");
        assertTrue(expectedWeight == weight);

        String expectedSku = UPDATED_DESCRIPTION.sku;
        String sku = response.getString("description.sku");
        assertEquals(expectedSku, sku);

        List<Product> products = get("/api/products").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getProductTypeRef());
        assertEquals(NB_PRODUCTS + 1, products.size());
    }

    @Test
    @Order(4)
    void shouldRemoveAnItem() {
        given()
            .pathParam("id", productId.toString())
            .when().delete("/api/products/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        List<Product> products = get("/api/products").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getProductTypeRef());
        assertEquals(NB_PRODUCTS, products.size());
    }

    private TypeRef<List<Product>> getProductTypeRef() {
        return new TypeRef<List<Product>>() {
            // Kept empty on purpose
        };
    }

}