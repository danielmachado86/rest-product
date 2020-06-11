package io.dmcapps.dshopping.product;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
// end::adocOpenAPIImports[]
// tag::adocResource[]
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/api/search")
@Produces(APPLICATION_JSON)
public class ProductSearchResource {

    private static final Logger LOGGER = Logger.getLogger(ProductResource.class);

    @Inject
    ProductService service;

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
}