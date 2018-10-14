package cloud.nativ.javaee;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonString;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.temporal.ChronoUnit;

/**
 * Simple REST repository implementation.
 */
@ApplicationScoped
public class OpenWeatherMapRepository {

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

    @CircuitBreaker(delay = 3000L, failureRatio = 0.75, requestVolumeThreshold = 10)
    @Timeout(value = 2000, unit = ChronoUnit.MILLIS)
    @Fallback(fallbackMethod = "defaultWeather")
    public String getWeather(String city) {
        JsonObject response = openWeatherMap.getWeather(city, "b6907d289e10d714a6e88b30761fae22");

        JsonPointer pointer = Json.createPointer("/weather/0/main");
        return ((JsonString) pointer.getValue(response)).getString();
    }

    public String defaultWeather(String city) {
        return "Unknown";
    }
}
