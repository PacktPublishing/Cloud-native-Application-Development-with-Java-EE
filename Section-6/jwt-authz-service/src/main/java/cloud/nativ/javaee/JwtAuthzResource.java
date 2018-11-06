package cloud.nativ.javaee;

import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("protected")
public class JwtAuthzResource {

    @Inject
    private JsonWebToken jsonWebToken;

    @GET
    @PermitAll
    public Response info() {
        if (jsonWebToken == null || jsonWebToken.getName() == null) {
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
                .add("name", jsonWebToken.getName())
                .add("issuer", jsonWebToken.getIssuer())
                .add("audience", Json.createArrayBuilder(jsonWebToken.getAudience()))
                .add("groups", Json.createArrayBuilder(jsonWebToken.getGroups()))
                .add("subject", jsonWebToken.getSubject())
                .build();
    }
}
