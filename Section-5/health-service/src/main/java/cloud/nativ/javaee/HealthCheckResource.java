package cloud.nativ.javaee;

import org.eclipse.microprofile.health.Health;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("health-check")
public class HealthCheckResource {

    @Inject
    @Health
    private ModifiableHealthCheck healthCheck;

    @PUT
    @Path("up")
    public Response up(@QueryParam("message") @DefaultValue("Health is OK.") String message) {
        healthCheck.up(message);
        return Response.ok().build();
    }

    @PUT
    @Path("down")
    public Response down(@QueryParam("message") @DefaultValue("Health is NOK.") String message) {
        healthCheck.down(message);
        return Response.ok().build();
    }

}
