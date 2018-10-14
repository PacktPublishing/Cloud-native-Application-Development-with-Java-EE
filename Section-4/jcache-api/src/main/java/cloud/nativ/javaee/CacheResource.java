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
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * The REST resource to do stuff with the JCache provider.
 */
@ApplicationScoped
@Path("cache")
@Produces(MediaType.APPLICATION_JSON)
public class CacheResource {

    private static final Logger LOGGER = Logger.getLogger(CacheResource.class.getName());

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
        Cache<String, byte[]> cache = getOrCreateCache(name);

        URI uri = uriInfo.getBaseUriBuilder()
                .path(CacheResource.class).path(CacheResource.class, "createCacheByName")
                .build(cache.getName());

        return Response.created(uri).build();
    }

    @GET
    @Path("/{name}/{key}")
    public Response getCacheEntry(@PathParam("name") String name, @PathParam("key") String key) {
        Cache<String, byte[]> cache = getOrCreateCache(name);
        byte[] value = Optional.ofNullable(cache.get(key)).orElseThrow(NotFoundException::new);

        try (JsonReader reader = Json.createReader(new StringReader(unzip(value)))) {
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

        Cache<String, byte[]> cache = getOrCreateCache(name);
        cache.put(key, zip(json.toString()));

        URI uri = uriInfo.getBaseUriBuilder()
                .path(CacheResource.class).path(CacheResource.class, "putCacheEntry")
                .build(name, key);

        return Response.created(uri).build();
    }

    private Cache<String, byte[]> getOrCreateCache(String name) {
        Cache<String, byte[]> cache = cacheManager.getCache(name, String.class, byte[].class);
        if (cache == null) {
            CompleteConfiguration<String, byte[]> config = new MutableConfiguration<String, byte[]>().setTypes(String.class, byte[].class);
            cache = cacheManager.createCache(name, config);
        }
        return cache;
    }

    private byte[] zip(final String str) {
        if ((str == null) || (str.length() == 0)) {
            throw new IllegalArgumentException("Cannot zip null or empty string");
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
                gzipOutputStream.write(str.getBytes(StandardCharsets.UTF_8));
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new InternalServerErrorException("Failed to zip content", e);
        }
    }

    private String unzip(final byte[] compressed) {
        if ((compressed == null) || (compressed.length == 0)) {
            throw new IllegalArgumentException("Cannot unzip null or empty bytes");
        }
        if (!isZipped(compressed)) {
            return new String(compressed);
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressed)) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
                    try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        StringBuilder output = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            output.append(line);
                        }
                        return output.toString();
                    }
                }
            }
        } catch (IOException e) {
            throw new InternalServerErrorException("Failed to unzip content", e);
        }
    }

    private boolean isZipped(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }
}
