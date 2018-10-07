package cloud.nativ.javaee;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonPatch;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("tolerant")
public class TolerantResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postTolerantPayload(@NotNull JsonObject payload) {

        JsonPatch patch = Json.createPatchBuilder()
                .test("/version", "v1")
                .build();

        try {
            JsonObject applied = patch.apply(payload);
            return Response.ok(applied).build();
        } catch (JsonException e) {
            JsonObject error = Json.createObjectBuilder().add("message", e.getMessage()).build();
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }
}
