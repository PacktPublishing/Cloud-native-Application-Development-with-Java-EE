package cloud.nativ.javaee;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

@ApplicationScoped
@Path("events")
@Produces(MediaType.APPLICATION_JSON)
public class CloudNativeEventResource {

    @Inject
    private CloudNativeEventStorage storage;

    @GET
    public Response events() {
        JsonArrayBuilder response = Json.createArrayBuilder();
        storage.all().parallelStream().map(CloudNativeEvent::toJson).forEach(response::add);
        return Response.ok(response.build()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response persist(@NotNull JsonObject payload) {
        CloudNativeEvent event = storage.persist(payload);
        URI location = UriBuilder
                .fromResource(CloudNativeEventResource.class)
                .path("/{id}")
                .resolveTemplate("id", event.getId())
                .build();
        return Response.created(location).build();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") Long id) {
        return Response.ok(storage.get(id).toJson()).build();
    }
}
