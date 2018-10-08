package cloud.nativ.javaee;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigSource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("configuration")
public class ConfigurationResource {

    @Inject
    private Config config;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response info() {
        JsonArrayBuilder response = Json.createArrayBuilder();

        Iterable<ConfigSource> configSources = config.getConfigSources();
        for (ConfigSource source : configSources) {
            response.add(Json.createObjectBuilder()
                    .add("name", source.getName())
                    .add("ordinal", source.getOrdinal())
                    .add("propertyNames", Json.createArrayBuilder(source.getPropertyNames()))
            );
        }

        return Response.ok(response.build()).build();
    }

    @GET
    @Path("{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response get(@PathParam("key") String key) {
        return Response.ok(config.getValue(key, String.class)).build();
    }
}
