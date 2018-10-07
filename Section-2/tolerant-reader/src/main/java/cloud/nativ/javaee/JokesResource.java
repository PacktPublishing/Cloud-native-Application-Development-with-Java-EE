package cloud.nativ.javaee;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonString;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Path("jokes")
public class JokesResource {

    private Client client;
    private WebTarget target;

    @PostConstruct
    public void initialize() {
        client = ClientBuilder
                .newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS)
                .build();
        target = client.target("https://api.chucknorris.io/jokes/random");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getJoke(@Context UriInfo uriInfo) {
        // obtain the request as JSON object
        JsonObject jsonJoke = target.request().get(JsonObject.class);

        // direct access to value field may be dangerous
        String value = jsonJoke.getString("value", "No Joke!");

        JsonPointer jsonPointer = Json.createPointer("/url");
        String uri;
        if (jsonPointer.containsValue(jsonJoke)) {
            uri = ((JsonString) jsonPointer.getValue(jsonJoke)).getString();
        } else {
            uri = uriInfo.getRequestUri().toString();
        }

        return Response.ok(value).link(uri, "_self").build();
    }
}
