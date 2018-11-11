# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 2.3: Resilient service invocation using MicroProfile Fault Tolerance

Everything fails, all the time. When invoking remote endpoints
you must make your logic robust and tolerant to failure by adding timeouts, retries,
circuit breaker or fallback routines. The MicroProfile Fault Tolerance APIs
provide the means to do this.

### Step 1: Add MicroProfile Fault Tolerance dependency

Add the following dependencies to the `build.gradle` file.

```groovy
providedCompile 'org.eclipse.microprofile.fault-tolerance:microprofile-fault-tolerance-api:1.1.2'
providedCompile 'org.eclipse.microprofile.rest.client:microprofile-rest-client-api:1.1'
```

### Step 2: Add typed interface for REST client API

Add the following type REST client interface class.

```java
@RegisterRestClient
@Path("/data/2.5/weather")
public interface OpenWeatherMap {
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    JsonObject getWeather(@QueryParam("q") String city, @QueryParam("appid") String appid);
}
```

### Step 3: Add REST client initialization

```java
    private OpenWeatherMap openWeatherMap;

    @PostConstruct
    void initialize() {
        try {
            openWeatherMap = RestClientBuilder.newBuilder()
                    .baseUri(new URI("https://samples.openweathermap.org"))
                    .property("jersey.config.client.connectTimeout", 500)
                    .property("jersey.config.client.readTimeout", 100)
                    .build(OpenWeatherMap.class);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getWeather(String city) {
        JsonObject response = openWeatherMap.getWeather(city, "b6907d289e10d714a6e88b30761fae22");

        JsonPointer pointer = Json.createPointer("/weather/0/main");
        return ((JsonString) pointer.getValue(response)).getString();
    }
```

### Step 4: Add resiliency annotations and fallback method

Add the following annotations to the repository method
```java
    @CircuitBreaker(delay = 3000L, failureRatio = 0.75, requestVolumeThreshold = 10)
    // @Retry(delay = 100, maxDuration = 2, durationUnit = ChronoUnit.SECONDS, maxRetries = 2)
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "defaultWeather")
```

```java
    public String defaultWeather(String city) {
        return "Unknown";
    }
```
