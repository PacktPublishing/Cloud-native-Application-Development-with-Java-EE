package cloud.nativ.javaee.morphia;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Collection;

@ApplicationScoped
@Path("morphia")
@Produces(MediaType.APPLICATION_JSON)
public class MorphiaResource {

    @Inject
    private MorphiaRepository repository;

    @GET
    public Response all() {
        Collection<MorphiaPojo> results = repository.findAll();
        return Response.ok(results).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(@NotNull MorphiaPojo payload) {
        String id = repository.save(payload);

        URI location = UriBuilder
                .fromResource(MorphiaResource.class)
                .path("{id}")
                .resolveTemplate("id", id)
                .build();

        return Response.created(location).build();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") String id) {
        MorphiaPojo pojo = repository.findById(id);
        return Response.ok(pojo).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") String id) {
        repository.delete(id);
        return Response.noContent().build();
    }
}
