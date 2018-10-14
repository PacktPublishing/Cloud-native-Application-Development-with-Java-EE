package cloud.nativ.javaee;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@RequestScoped
@Path("session")
public class SessionResource {

    @Context
    private HttpServletRequest request;

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttribute(@PathParam("name") String name) {
        Object payload = Optional.ofNullable(request.getSession().getAttribute(name)).orElseThrow(NotFoundException::new);
        return Response.ok(payload).build();
    }

    @POST
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setAttribute(@PathParam("name") String name, String payload) {
        request.getSession().setAttribute(name, payload);
        return Response.ok().build();
    }

}
