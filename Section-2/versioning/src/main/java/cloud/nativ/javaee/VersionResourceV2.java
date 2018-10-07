package cloud.nativ.javaee;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class VersionResourceV2 {

    @Context
    private HttpHeaders httpHeaders;

    @GET
    public Response getV2() {
        JsonObject response = Json.createObjectBuilder().add("version", "v2").build();
        return Response.ok(response).build();
    }
}