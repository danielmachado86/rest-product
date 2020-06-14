package io.dmcapps.dshopping.product;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/api/brands")
@Produces(APPLICATION_JSON)
public class BrandResource {

    private static final Logger LOGGER = Logger.getLogger(BrandResource.class);

    @Inject
    BrandService service;

    @Operation(summary = "Returns all the brands from the database")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Brand.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "204", description = "No brands")
    @GET
    public Response getAllBrands() {
        List<Brand> brands = service.findAllBrands();
        LOGGER.debug("Total number of brands " + brands);
        return Response.ok(brands).build();
    }

    @Operation(summary = "Returns a brand for a given identifier")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Brand.class)))
    @APIResponse(responseCode = "204", description = "The brand is not found for a given identifier")
    @GET
    @Path("/{id}")
    public Response getBrand(
        @Parameter(description = "Brand identifier", required = true)
        @PathParam("id") String id) {
        Brand brand = service.findBrandById(id);
        if (brand != null) {
            LOGGER.debug("Found brand " + brand);
            return Response.ok(brand).build();
        } else {
            LOGGER.debug("No brand found with id " + id);
            return Response.noContent().build();
        }
    }

    @Operation(summary = "Creates a valid brand")
    @APIResponse(responseCode = "201", description = "The URI of the created brand", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    @POST
    public Response createBrand(
        @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Brand.class)))
        @Valid Brand brand, @Context UriInfo uriInfo) {
        brand = service.persistBrand(brand);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(brand.id);
        LOGGER.debug("New brand created with URI " + builder.build().toString());
        return Response.created(builder.build()).build();
    }

    @Operation(summary = "Updates an exiting  brand")
    @APIResponse(responseCode = "200", description = "The updated brand", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Brand.class)))
    @PUT
    public Response updateBrand(
        @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Brand.class)))
        @Valid Brand brand) {
        brand = service.updateBrand(brand);
        LOGGER.debug("Brand updated with new valued " + brand);
        return Response.ok(brand).build();
    }

    @Operation(summary = "Deletes an exiting brand")
    @APIResponse(responseCode = "204")
    @DELETE
    @Path("/{id}")
    public Response deleteBrand(
        @Parameter(description = "Brand identifier", required = true)
        @PathParam("id") String id) {
        service.deleteBrand(id);
        LOGGER.debug("Brand deleted with " + id);
        return Response.noContent().build();
    }

}