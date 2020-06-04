package io.dmcapps.dshopping.product;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import java.util.List;

@Path("/api/products")
@Produces(APPLICATION_JSON)
public class ProductResource {

    private static final Logger LOGGER = Logger.getLogger(ProductResource.class);

    @Inject
    ProductService service;

    @GET
    @Path("/{id}")
    public Response getProduct(
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

    @GET
    public Response getAllProducts() {
        List<Product> products = service.findAllProducts();
        LOGGER.debug("Total number of heroes " + products);
        return Response.ok(products).build();
    }

    @POST
    public Response createProduct(Product product, @Context UriInfo uriInfo) {
        product = service.persistProduct(product);
        LOGGER.info("product " + product.id);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(product.id.toString());
        LOGGER.debug("New product created with URI " + builder.build().toString());
        return Response.created(builder.build()).build();
    }

    @PUT
    public Response updateProduct(Product product) {
        product = service.updateProduct(product);
        LOGGER.debug("Product updated with new valued " + product);
        return Response.ok(product).build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") String id) {
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