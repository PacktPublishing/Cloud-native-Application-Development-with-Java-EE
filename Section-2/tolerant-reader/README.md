# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 2.2:  Implementing tolerant reader with JSON-P

In order to build a robust REST client, you should use the tolerant reader
pattern to process the JSON responses: only read and extract the required fields
and data and ignore everything else. The different JSON-P APIs can be used to
implement flexible JSON processing logic.

### Step 1: Tolerant reader on the JAX-RS client side

In this step we are implementing a tolerant reader for the ChuckNorris API.

```java
@ApplicationScoped
@Path("jokes")
public class JokesResource {

    private Client client;
    private WebTarget target;

    @PostConstruct
    public void initialize() {
        client = ClientBuilder
                .newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS)
                .build();
        target = client.target("https://api.chucknorris.io/jokes/random");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getJoke(@Context UriInfo uriInfo) {
        // obtain the request as JSON object
        JsonObject jsonJoke = target.request().get(JsonObject.class);

        // direct access to value field may be dangerous
        String value = jsonJoke.getString("value", "No Joke!");

        JsonPointer jsonPointer = Json.createPointer("/url");
        String uri;
        if (jsonPointer.containsValue(jsonJoke)) {
            uri = ((JsonString) jsonPointer.getValue(jsonJoke)).getString();
        } else {
            uri = uriInfo.getRequestUri().toString();
        }

        return Response.ok(value).link(uri, "_self").build();
    }
}
```

### Step 2: Tolerant processing on the JAX-RS server side

```java
@RequestScoped
@Path("tolerant")
public class TolerantResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postTolerantPayload(@NotNull JsonObject payload) {

        JsonPatch patch = Json.createPatchBuilder()
                .test("/version", "v1")
                .build();

        try {
            JsonObject applied = patch.apply(payload);
            return Response.ok(applied).build();
        } catch (JsonException e) {
            JsonObject error = Json.createObjectBuilder().add("message", e.getMessage()).build();
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }
}
```
