package cloud.nativ.javaee;

import javax.enterprise.context.ApplicationScoped;

/**
 * Simple REST repository implementation.
 */
@ApplicationScoped
public class OpenWeatherMapRepository {

    public String getWeather(String city) {
        return "Implement me!";
    }
}
