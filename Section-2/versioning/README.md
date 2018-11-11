# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 2.1: Building and versioning REST APIs with JAX-RS

When evolving a REST interface you need to make sure you do not break existing consumers
by versioning the endpoints. There are two approaches to accomplish this, and both can be
implemented with JAX-RS: URL based versioning and media type versioning.

### Step 1: URL based versioning using sub resources

Add the following resource class as the root of your URL based versioning scheme.
```java
@RequestScoped
@Path("version")
public class VersionResource {

    @Context
    private ResourceContext context;

    @Path("v1")
    public VersionResourceV1 v1() {
        // alternatively we could return VersionResourceV1.class
        // careful, because injection does not work here
        return new VersionResourceV1();
    }

    @Path("v2")
    public VersionResourceV2 v2() {
        // perform lookup via ResourceContext for @Inject support
        return context.getResource(VersionResourceV2.class);
    }
}
```

For each supported version you return the so called nested resources using a sub resource locator.
```java
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class VersionResourceV1 {
    @GET
    public Response getV1() {
        JsonObject response = Json.createObjectBuilder().add("version", "v1").build();
        return Response.ok(response).build();
    }
}

@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class VersionResourceV2 {

    @Context
    private HttpHeaders httpHeaders;

    @GET
    public Response getV2() {
        JsonObject response = Json.createObjectBuilder().add("version", "v2").build();
        return Response.ok(response).build();
    }
}
```

### Step 2: Media-Type versioning

Media type versioning allows for a lot more flexibility. Add the following class to demonstrate its usage.
The V1 resource method acts as the default, it used content negotiation and accepts all requests.

```java
@RequestScoped
@Path("media-type")
public class MediaTypeVersionResource {

    /**
     * MediaType implementation for the version resource in v1.
     */
    public static final MediaType V1_MEDIA_TYPE = new MediaType("application", "vnd.version.v1+json");

    @GET
    @Produces({"application/json; qs=0.75", "application/vnd.version.v1+json; qs=1.0"})
    public Response getWithV1MediaType() {
        JsonObject response = Json.createObjectBuilder()
                .add("version", "v1").add("media-type", V1_MEDIA_TYPE.toString())
                .build();
        return Response.ok(response).build();
    }

}
```

Add a second resource method to return the V2 response when the correct `Accept` header has bee set.
```java
    /**
     * MediaType implementation for the version resource in v2.
     */
    public static final MediaType V2_MEDIA_TYPE = new MediaType("application", "vnd.version.v2+json");

    @GET
    @Produces("application/vnd.version.v2+json")
    public Response getWithV2MediaType() {
        JsonObject response = Json.createObjectBuilder()
                .add("version", "v2").add("media-type", V2_MEDIA_TYPE.toString())
                .build();
        return Response.ok(response).build();
    }
```
