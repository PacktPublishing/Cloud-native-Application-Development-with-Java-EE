package cloud.nativ.javaee;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Optional;

/**
 * The REST resource to do stuff with the JCache provider.
 */
@ApplicationScoped
@Path("cache")
@Produces(MediaType.APPLICATION_JSON)
public class CacheResource {

    @Inject
    private CacheManager cacheManager;

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("/{name}")
    public Response getCacheByName(@PathParam("name") String name) {
        Cache<Object, Object> cache = cacheManager.getCache(name);
        if (cache == null) {
            throw new NotFoundException("No cache with name " + name);
        } else {
            JsonObject info = Json.createObjectBuilder().add("name", cache.getName()).build();
            return Response.ok(info).build();
        }
    }

    @PUT
    @Path("/{name}")
    public Response createCacheByName(@PathParam("name") String name) {
        Cache<String, String> cache = getOrCreateCache(name);

        URI uri = uriInfo.getBaseUriBuilder()
                .path(CacheResource.class).path(CacheResource.class, "createCacheByName")
                .build(cache.getName());

        return Response.created(uri).build();
    }

    @GET
    @Path("/{name}/{key}")
    public Response getCacheEntry(@PathParam("name") String name, @PathParam("key") String key) {
        Cache<String, String> cache = getOrCreateCache(name);
        String value = Optional.ofNullable(cache.get(key)).orElseThrow(NotFoundException::new);

        try (JsonReader reader = Json.createReader(new StringReader(value))) {
            return Response.ok(reader.readObject()).build();
        }
    }

    @POST
    @Path("/{name}/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putCacheEntry(@PathParam("name") String name, @PathParam("key") String key, @NotNull JsonObject value) {
        StringWriter json = new StringWriter();
        try (JsonWriter writer = Json.createWriter(json)) {
            writer.writeObject(value);
        }

        Cache<String, String> cache = getOrCreateCache(name);
        cache.put(key, json.toString());

        URI uri = uriInfo.getBaseUriBuilder()
                .path(CacheResource.class).path(CacheResource.class, "putCacheEntry")
                .build(name, key);

        return Response.created(uri).build();
    }

    private Cache<String, String> getOrCreateCache(String name) {
        Cache<String, String> cache = cacheManager.getCache(name, String.class, String.class);
        if (cache == null) {
            CompleteConfiguration<String, String> config = new MutableConfiguration<String, String>().setTypes(String.class, String.class);
            cache = cacheManager.createCache(name, config);
        }
        return cache;
    }
}
