# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 4.5: Distributed state using the JCache API

Sometime you may want to store and cache temporary data in your clustered microservice
instances. Usually you want that all instances see the same state, so it needs to be
replicated between them. The JCache API provides a HashMap like programming API to access
and use an in-memory datagrid technology like Hazelcast.

### Step 1: Infrastructure setup

We are going to simulate a clustered environment by running the same microservice
twice. Add the following to your `docker-compose.yml`

```yaml
  jcache-api-1:
    build:
      context: .
    image: jcache-api:1.0.1
    ports:
    - "18080:8080"
    networks:
    - jee8net

  jcache-api-2:
    image: jcache-api:1.0.1
    ports:
    - "28080:8080"
    networks:
    - jee8net
```

### Step 2: Add JCache API dependency

Add the following dependency to the `build.gradle` file to use the JCache APIs.

```groovy
providedCompile 'javax.cache:cache-api:1.0.0'
```

### Step 3: Configure the Hazelcast JCache provider

To configure the Hazelcast JCache provider, add the following cache definition to the `src/main/docker/hazelcast.xml` file:
```xml
<cache name="expiry">
    <key-type class-name="java.lang.String"/>
    <value-type class-name="java.lang.String"/>

    <backup-count>1</backup-count>
    <async-backup-count>0</async-backup-count>

    <expiry-policy-factory>
        <timed-expiry-policy-factory expiry-policy-type="MODIFIED" duration-amount="30" time-unit="SECONDS"/>
    </expiry-policy-factory>
</cache>
```

Add the following to your `Dockerfile` to copy the relevant configuration files and
to start the Payara app server with the relevant CMD parameters.

```
FROM qaware/zulu-centos-payara-micro:8u181-5.183

CMD ["--hzconfigfile", "/opt/payara/hazelcast.xml", "--deploymentDir", "/opt/payara/deployments"]

COPY src/main/docker/* /opt/payara/
COPY build/libs/jcache-api.war /opt/payara/deployments/
```

### Step 4: Handle distributed state using the JCache API

Finally, we add a simple JAX-RS resource to access and modify the cache and its values.

```java
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
```
