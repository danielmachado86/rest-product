package io.dmcapps.dshopping.product;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/api/brand")
@Produces(APPLICATION_JSON)
public class BrandResource {

    private static final Logger LOGGER = Logger.getLogger(BrandResource.class);

    @Inject
    BrandService service;

    @GET
    public Response getAllBrands() {
        List<Brand> brands = service.findAllBrands();
        LOGGER.debug("Total number of brands " + brands);
        return Response.ok(brands).build();
    }

    @GET
    @Path("/{id}")
    public Response getBrand(
        @PathParam("id") ObjectId id) {
        Brand brand = service.findBrandById(id);
        if (brand != null) {
            LOGGER.debug("Found brand " + brand);
            return Response.ok(brand).build();
        } else {
            LOGGER.debug("No brand found with id " + id);
            return Response.noContent().build();
        }
    }

    @POST
    public Response createBrand(
        @Valid Brand brand, @Context UriInfo uriInfo) {
        brand = service.persistBrand(brand);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(brand.id.toString());
        LOGGER.debug("New brand created with URI " + builder.build().toString());
        return Response.created(builder.build()).build();
    }

    @PUT
    public Response updateBrand(
        @Valid Brand brand) {
        brand = service.updateBrand(brand);
        LOGGER.debug("Brand updated with new valued " + brand);
        return Response.ok(brand).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBrand(
        @PathParam("id") ObjectId id) {
        service.deleteBrand(id);
        LOGGER.debug("Brand deleted with " + id);
        return Response.noContent().build();
    }

    @GET
    @Produces(TEXT_PLAIN)
    @Path("/hello")
    public String hello() {
        return "hello";
    }

}