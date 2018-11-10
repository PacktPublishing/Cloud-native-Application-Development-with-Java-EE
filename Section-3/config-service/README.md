# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 3.1: Enabling multi-environment configuration using MicroProfile Config

According to the 12-factor app principles any configuration values should be stored
in the environment and not in the application. Using MicroProfile Config APIs we can
easily access ENV variables, system properties or other config sources and inject
the configuration properties using CDI.

### Step 1: Add MicroProfile Config dependency

In order to use the config API, add the following dependency to your `build.gradle` file.
```groovy
dependencies {
    providedCompile 'org.eclipse.microprofile.config:microprofile-config-api:1.3'
}
```

### Step 2: Simple `@ConfigProperty` injection

All configuration values can be injected, using the `@ConfigProperty` annotation. Create the
following configuration bean class.
```java
@ApplicationScoped
public class ConfigurationBean {

    @Inject
    @ConfigProperty(name = "hostname")
    private String hostname;

    @Inject
    @ConfigProperty(name = "a.optional.string")
    private Optional<String> aOptionalString;

    @Inject
    @ConfigProperty(name = "a.dynamic.string", defaultValue = "A default value.")
    private Provider<String> aDynamicString;

    @Inject
    @ConfigProperty(name = "a.long", defaultValue = "23051977")
    private Long aLong;

    @Inject
    @ConfigProperty(name = "a.string.array")
    private String[] aStringArray;

    // other getters omitted for brevity
    public String getDynamicString() { return aDynamicString.get(); }
}
```

The values are either taken from system properties, ENV variables or the contents
of the `microprofile-config.properties` file.

### Step 3: Add custom MicroProfile Config converters

To convert configuration values to non standard data types, implement a custom converter.
```java
public class JsonObjectConverter implements Converter<JsonObject> {
    @Override
    public JsonObject convert(String value) {
        try {
            return Json.createReader(new StringReader(value)).readObject();
        } catch (JsonException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
```

You need to register all custom converters in the service loader file
`src/main/resources/META-INF/org.eclipse.microprofile.config.spi.Converter`.

```java
    @Inject
    @ConfigProperty(name = "a.json.object")
    private JsonObject aJsonObject;
```

### Step 4: Programmatic configuration

Add the following REST resource implementation class to show the programmatic usage of the `Config` API.

```java
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
```
