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

    private static final String DEFAULT_NAME = "Chocorramo";
    private static final String UPDATED_NAME = "Gansito";
    private static final String DEFAULT_CATEGORY_NAME = "Vegetales";
    private static final String UPDATED_CATEGORY_NAME = "Bebidas";
    private static final String DEFAULT_CATEGORY_PARENT = "Huerta";
    private static final String UPDATED_CATEGORY_PARENT = "Mercado";
    private static final String DEFAULT_BRAND_NAME = "Ramo";
    private static final String UPDATED_BRAND_NAME = "Ramo S.A.";
    private static final String DEFAULT_BRAND_PICTURE = "ramo.png";
    private static final String UPDATED_BRAND_PICTURE = "ramosa.png";
    private static final String DEFAULT_BRAND_DESCRIPTION = "Aqui se fabrica el chocorramo";
    private static final String UPDATED_BRAND_DESCRIPTION = "Aqui se fabrica el gansito";
    private static final String DEFAULT_PICTURE = "chocorramo.png";
    private static final String UPDATED_PICTURE = "gansito.png";
    private static final Double DEFAULT_DESCRIPTION_1_VALUE = 10.5;
    private static final Double UPDATED_DESCRIPTION_1_VALUE = 20.5;
    private static final String DEFAULT_DESCRIPTION_2_VALUE = "1234567";
    private static final String UPDATED_DESCRIPTION_2_VALUE = "7654321";

    private static final int NB_PRODUCTS= 1000;
    private static final int FOUND_PRODUCTS_BY_NAME = 781;
    private static final int FOUND_PRODUCTS_BY_CATEGORY = 52;
    private static ObjectId productId;

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
    void shouldFindProductsByName() {
        List<Product> products = given()
            .queryParam("q", "Wine - White, Schroder And Schyl")
            .when().get("/api/search")
            .then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getProductTypeRef());
        assertEquals(FOUND_PRODUCTS_BY_NAME, products.size());
    }

    @Test
    void shouldFindProductsByCategory() {
        List<Product> products = given()
            .queryParam("q", "Computers")
            .when().get("/api/search")
            .then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getProductTypeRef());
        assertEquals(FOUND_PRODUCTS_BY_CATEGORY, products.size());
    }

    @Test
    @Order(2)
    void shouldAddAnItem() {
        Product product = new Product();
        product.name = DEFAULT_NAME;
        ProductCategory category = new ProductCategory();
        category.id = DEFAULT_CATEGORY_NAME;
        category.parent = DEFAULT_CATEGORY_PARENT;
        product.category = category;
        Brand brand = new Brand();
        brand.id = DEFAULT_BRAND_NAME;
        brand.picture = DEFAULT_BRAND_PICTURE;
        brand.description = DEFAULT_BRAND_DESCRIPTION;
        product.brand = brand;
        product.picture = DEFAULT_PICTURE;
        HashMap<String, Object> description = new HashMap<String, Object>();
        description.put("weight", DEFAULT_DESCRIPTION_1_VALUE);
        description.put("sku", DEFAULT_DESCRIPTION_2_VALUE);
        product.description = description;
        
        System.out.println(product.toString());

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
        
        String expectedCategoryName = DEFAULT_CATEGORY_NAME;
        String categoryName = response.getString("category.id");
        assertEquals(expectedCategoryName, categoryName);
        
        String expectedCategoryParent = DEFAULT_CATEGORY_PARENT;
        String categoryParent = response.getString("category.parent");
        assertEquals(expectedCategoryParent, categoryParent);

        String expectedBrandName = DEFAULT_BRAND_NAME;
        String brandName = response.getString("brand.id");
        assertEquals(expectedBrandName, brandName);

        String expectedBrandPicture = DEFAULT_BRAND_PICTURE;
        String brandPicture = response.getString("brand.picture");
        assertEquals(expectedBrandPicture, brandPicture);

        String expectedBrandDescription = DEFAULT_BRAND_DESCRIPTION;
        String brandDescription = response.getString("brand.description");
        assertEquals(expectedBrandDescription, brandDescription);

        String expectedPicture = DEFAULT_PICTURE;
        String picture = response.getString("picture");
        assertEquals(expectedPicture, picture);

        double expectedWeight = DEFAULT_DESCRIPTION_1_VALUE;
        double weight = response.getDouble("description.weight");
        assertTrue(expectedWeight == weight);

        String expectedSku = DEFAULT_DESCRIPTION_2_VALUE;
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
        Product product = new Product();
        product.id = productId;
        product.name = UPDATED_NAME;
        ProductCategory category = new ProductCategory();
        category.id = UPDATED_CATEGORY_NAME;
        category.parent = UPDATED_CATEGORY_PARENT;
        product.category = category;
        Brand brand = new Brand();
        brand.id = UPDATED_BRAND_NAME;
        brand.picture = UPDATED_BRAND_PICTURE;
        brand.description = UPDATED_BRAND_DESCRIPTION;
        product.brand = brand;
        product.picture = UPDATED_PICTURE;
        HashMap<String, Object> description = new HashMap<String, Object>();
        description.put("weight", UPDATED_DESCRIPTION_1_VALUE);
        description.put("sku", UPDATED_DESCRIPTION_2_VALUE);
        product.description = description;

        System.out.println(product.toString());

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
            
            String expectedCategoryName = UPDATED_CATEGORY_NAME;
            String categoryName = response.getString("category.id");
            assertEquals(expectedCategoryName, categoryName);
            
            String expectedCategoryParent = UPDATED_CATEGORY_PARENT;
            String categoryParent = response.getString("category.parent");
            assertEquals(expectedCategoryParent, categoryParent);
    
            String expectedBrandName = UPDATED_BRAND_NAME;
            String brandName = response.getString("brand.id");
            assertEquals(expectedBrandName, brandName);
    
            String expectedBrandPicture = UPDATED_BRAND_PICTURE;
            String brandPicture = response.getString("brand.picture");
            assertEquals(expectedBrandPicture, brandPicture);
    
            String expectedBrandDescription = UPDATED_BRAND_DESCRIPTION;
            String brandDescription = response.getString("brand.description");
            assertEquals(expectedBrandDescription, brandDescription);
    
            String expectedPicture = UPDATED_PICTURE;
            String picture = response.getString("picture");
            assertEquals(expectedPicture, picture);
    
            double expectedWeight = UPDATED_DESCRIPTION_1_VALUE;
            double weight = response.getDouble("description.weight");
            assertTrue(expectedWeight == weight);
    
            String expectedSku = UPDATED_DESCRIPTION_2_VALUE;
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