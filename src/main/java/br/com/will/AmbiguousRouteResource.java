package br.com.will;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import io.quarkus.logging.Log;

@Path("/api-path")
public class AmbiguousRouteResource {

    @GET
    @Path("v1/{parentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DTO> findOwnershipLineageByParentIdentifier(@PathParam("parentId") Long parentId) {
        Log.info("Calling findOwnershipLineageByParentIdentifier");
        return List.of(new DTO(parentId, "findOwnershipLineageByParentIdentifier"));
    }

    @GET
    @Path("v1/{product}")
    @Produces(MediaType.APPLICATION_JSON)
    public DTO getById(@PathParam("product") Long product) {
        Log.info("Calling getById");
        return new DTO(product, "getById");
    }

    public record DTO(Long id, String source) {
    }
}
