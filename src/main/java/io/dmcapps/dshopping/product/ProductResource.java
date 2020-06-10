package io.dmcapps.dshopping.product;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
// end::adocOpenAPIImports[]
// tag::adocResource[]
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/api/products")
@Produces(APPLICATION_JSON)
public class ProductResource {

    private static final Logger LOGGER = Logger.getLogger(ProductResource.class);

    @Inject
    ProductService service;

    @Operation(summary = "Returns a product with given id")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Product.class)))
    @APIResponse(responseCode = "204", description = "The product was not found for a given identifier")
    @GET
    @Path("{id}")
    public Response getProduct(
        @Parameter(description = "Product identifier", required = true)
        @PathParam("id") String id) {
            Product product = service.findProductById(new ObjectId(id));
            if (product != null) {
                LOGGER.debug("Found product " + product);
                return Response.ok(product).build();
            } else {
                LOGGER.debug("No product found with id " + id);
                return Response.noContent().build();
        }
    }

    @Operation(summary = "Returns product search result")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Product.class)))
    @APIResponse(responseCode = "204", description = "No product was not found for given search query")
    @GET
    public Response getProductSearch(
        @Parameter(description = "Search query", required = false)
        @QueryParam("q") String query) {
            List<Product> results = null;
            if (query != null) {
                results = service.searchProducts(query);
            } 
            if (results != null) {
                LOGGER.debug("Found products " + results);
                return Response.ok(results).build();
            } else {
                LOGGER.debug("No product found with query " + query);
                return Response.noContent().build();
        }
    }

    @Operation(summary = "Returns all the products from the database")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Product.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "204", description = "No products")
    @GET
    public Response getAllProducts() {
        List<Product> products = service.returnAllProducts();
        LOGGER.debug("Total number of products " + products);
        return Response.ok(products).build();
    }

    @Operation(summary = "Creates a valid product")
    @APIResponse(responseCode = "201", description = "The URI of the created product", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    @POST
    public Response createProduct(
        @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Product.class)))
        @Valid Product product, @Context UriInfo uriInfo) {
        product = service.persistProduct(product);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(product.id.toString());
        LOGGER.debug("New product created with URI " + builder.build().toString());
        return Response.created(builder.build()).build();
    }

    @Operation(summary = "Updates an exiting  product")
    @APIResponse(responseCode = "200", description = "The updated product", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Product.class)))
    @PUT
    public Response updateProduct(
        @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Product.class)))
        @Valid Product product) {
        product = service.updateProduct(product);
        LOGGER.debug("Product updated with new valued " + product);
        return Response.ok(product).build();
    }
    
    @Operation(summary = "Deletes an exiting product")
    @APIResponse(responseCode = "204")
    @DELETE
    @Path("/{id}")
    public Response deleteProduct(
        @Parameter(description = "Product identifier", required = true)
        @PathParam("id") String id) {
        service.deleteProduct(new ObjectId(id));
        LOGGER.debug("Product deleted with " + id);
        return Response.noContent().build();
    }

    @GET
    @Produces(TEXT_PLAIN)
    @Path("/hello")
    public String hello() {
        return "hello";
    }
}