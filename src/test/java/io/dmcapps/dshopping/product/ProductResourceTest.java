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

@QuarkusTest
@QuarkusTestResource(DatabaseResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductResourceTest {

    private static final String DEFAULT_NAME = "Wine - White, Chardonnay";
    private static final String UPDATED_NAME = "Wine - Prosecco Valdobiaddene";
    private static final ProductCategory DEFAULT_CATEGORY = new ProductCategory("Vegetales", -1);
    private static final ProductCategory UPDATED_CATEGORY = new ProductCategory("Bebidas", -1);
    private static final Brand DEFAULT_BRAND = new Brand("Ramo", "ramo.png", "Aqui se fabrica el chocorramo");
    private static final Brand UPDATED_BRAND = new Brand("Ramo S.A.", "ramosa.png", "Aqui se fabrica el gansito");
    private static final String DEFAULT_PICTURE = "wine_white_chardonnay.png";
    private static final String UPDATED_PICTURE = "wine_prosecco_valdobiaddene.png";
    private static final HashMap<String, Object> DEFAULT_DESCRIPTION = new HashMap<String, Object>();
    private static final HashMap<String, Object> UPDATED_DESCRIPTION = new HashMap<String, Object>();

    private static final int NB_PRODUCTS= 1000;
    private static final int FOUND_PRODUCTS = 66;
    private static ObjectId productId;


    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/api/products/hello")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }

    @Test
    void shouldPingOpenAPI() {
        given()
            .header(ACCEPT, APPLICATION_JSON)
            .when().get("/openapi")
            .then()
            .statusCode(OK.getStatusCode());
    }

    @Test
    void shouldPingSwaggerUI() {
        given()
            .when().get("/swagger-ui")
            .then()
            .statusCode(OK.getStatusCode());
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
        List<Product> products = given()
            .when().get("/api/products").then()
            .statusCode(OK.getStatusCode())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .extract().body().as(getProductTypeRef());
        assertEquals(NB_PRODUCTS, products.size());
    }

    @Test
    void shouldFindProducts() {
        List<Product> products = given()
            .queryParam("q", "Wine - White, Schroder And Schyl")
            .when().get("/api/search")
            .then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getProductTypeRef());
        assertEquals(FOUND_PRODUCTS, products.size());
    }

    @Test
    @Order(2)
    void shouldAddAnItem() {
        DEFAULT_DESCRIPTION.put("weight", 10.5);
        DEFAULT_DESCRIPTION.put("sku", "1234567");

        Product product = new Product();
        product.name = DEFAULT_NAME;
        product.category = DEFAULT_CATEGORY;
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
        
        String expectedCategory = DEFAULT_CATEGORY.name;
        String category = response.getString("category.name");
        assertEquals(expectedCategory, category);

        String expectedBrandName = DEFAULT_BRAND.id;
        String brandName = response.getString("brand.id");
        assertEquals(expectedBrandName, brandName);

        String expectedBrandPicture = DEFAULT_BRAND.picture;
        String brandPicture = response.getString("brand.picture");
        assertEquals(expectedBrandPicture, brandPicture);

        String expectedBrandDescription = DEFAULT_BRAND.description;
        String brandDescription = response.getString("brand.description");
        assertEquals(expectedBrandDescription, brandDescription);

        String expectedPicture = DEFAULT_PICTURE;
        String picture = response.getString("picture");
        assertEquals(expectedPicture, picture);

        double expectedWeight = (double) DEFAULT_DESCRIPTION.get("weight");
        double weight = response.getDouble("description.weight");
        assertTrue(expectedWeight == weight);

        String expectedSku = (String) DEFAULT_DESCRIPTION.get("sku");
        String sku = response.getString("description.sku");
        assertEquals(expectedSku, sku);

        List<Product> products = given()
            .when().get("/api/products").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getProductTypeRef());
        assertEquals(NB_PRODUCTS + 1, products.size());
    }

    @Test
    @Order(3)
    @Context
    void shouldUpdateAnItem(){
        UPDATED_DESCRIPTION.put("weight", 21.0);
        UPDATED_DESCRIPTION.put("sku", "7654321");
        Product product = new Product();
        product.id = productId;
        product.name = UPDATED_NAME;
        product.category = UPDATED_CATEGORY;
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

        String expectedBrandName = UPDATED_BRAND.id;
        String brandName = response.getString("brand.id");
        assertEquals(expectedBrandName, brandName);

        String expectedBrandPicture = UPDATED_BRAND.picture;
        String brandPicture = response.getString("brand.picture");
        assertEquals(expectedBrandPicture, brandPicture);

        String expectedBrandDescription = UPDATED_BRAND.description;
        String brandDescription = response.getString("brand.description");
        assertEquals(expectedBrandDescription, brandDescription);

        String expectedPicture = UPDATED_PICTURE;
        String picture = response.getString("picture");
        assertEquals(expectedPicture, picture);

        double expectedWeight = (double) UPDATED_DESCRIPTION.get("weight");
        double weight = response.getDouble("description.weight");
        assertTrue(expectedWeight == weight);

        String expectedSku = (String) UPDATED_DESCRIPTION.get("sku");
        String sku = response.getString("description.sku");
        assertEquals(expectedSku, sku);

        List<Product> products = given()
            .when().get("/api/products").then()
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

        List<Product> products = given()
            .when().get("/api/products").then()
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