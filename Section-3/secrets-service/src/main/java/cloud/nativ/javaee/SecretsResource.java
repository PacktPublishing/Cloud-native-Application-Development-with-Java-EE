package cloud.nativ.javaee;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("secrets")
public class SecretsResource {

    @Inject
    private SecretsConfiguration configuration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject secrets() {
        return Json.createObjectBuilder()
                .add("env.user.username", configuration.getEnvUsername())
                .add("env.user.password", configuration.getEnvPassword())
                .add("secret.user.username", configuration.getSecretUsername())
                .add("secret.user.password", configuration.getSecretPassword())
                .add("a.secret.decrypted", configuration.getSecret().decrypt())
                .build();
    }
}
