package cloud.nativ.javaee;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@ApplicationScoped
@Path("protected")
public class SecurityResource {

    @Context
    private SecurityContext securityContext;

    @GET
    @PermitAll
    public Response info() {
        if (securityContext.getUserPrincipal() == null) {
            return Response.noContent().build();
        }

        JsonObject jsonObject = getSecurityJsonObject();
        return Response.ok(jsonObject).build();
    }

    @GET
    @Path("all")
    @RolesAllowed({"admin", "user"})
    public Response allRoles() {
        JsonObject jsonObject = getSecurityJsonObject();
        return Response.ok(jsonObject).build();
    }

    @GET
    @Path("admin")
    @RolesAllowed({"admin"})
    public Response adminOnly() {
        JsonObject jsonObject = getSecurityJsonObject();
        return Response.ok(jsonObject).build();
    }

    private JsonObject getSecurityJsonObject() {
        return Json.createObjectBuilder()
                .add("authenticationScheme", securityContext.getAuthenticationScheme())
                .add("secure", securityContext.isSecure())
                .add("userPrincipal", securityContext.getUserPrincipal().getName())
                .add("admin", securityContext.isUserInRole("admin"))
                .build();
    }
}
