package cloud.nativ.javaee;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("media-type")
public class MediaTypeVersionResource {

    /**
     * MediaType implementation for the version resource in v1.
     */
    public static final MediaType V1_MEDIA_TYPE = new MediaType("application", "vnd.version.v1+json");

    /**
     * MediaType implementation for the version resource in v2.
     */
    public static final MediaType V2_MEDIA_TYPE = new MediaType("application", "vnd.version.v2+json");

    @GET
    @Produces({"application/json; qs=0.75", "application/vnd.version.v1+json; qs=1.0"})
    public Response getWithV1MediaType() {
        JsonObject response = Json.createObjectBuilder()
                .add("version", "v1").add("media-type", V1_MEDIA_TYPE.toString())
                .build();
        return Response.ok(response).build();
    }

    @GET
    @Produces("application/vnd.version.v2+json")
    public Response getWithV2MediaType() {
        JsonObject response = Json.createObjectBuilder()
                .add("version", "v2").add("media-type", V2_MEDIA_TYPE.toString())
                .build();
        return Response.ok(response).build();
    }

}
