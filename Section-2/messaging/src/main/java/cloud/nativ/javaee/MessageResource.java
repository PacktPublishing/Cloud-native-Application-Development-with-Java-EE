package cloud.nativ.javaee;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Path("message")
public class MessageResource {

    private static final Logger LOGGER = Logger.getLogger(MessageResource.class.getName());

    @Inject
    private Event<JsonObject> messageEvents;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(@NotNull JsonObject requestBody) {
        LOGGER.log(Level.INFO, "Received message request {0}", requestBody);
        messageEvents.fireAsync(requestBody);
        return Response.accepted().build();
    }
}
