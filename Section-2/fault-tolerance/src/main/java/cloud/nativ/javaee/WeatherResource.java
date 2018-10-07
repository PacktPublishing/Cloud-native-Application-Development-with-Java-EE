package cloud.nativ.javaee;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A REST API to query the weather.
 */
@ApplicationScoped
@Path("weather")
public class WeatherResource {

    @Inject
    private OpenWeatherMapRepository repository;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{city}")
    public Response getWeather(@PathParam("city") String city) {
        String weather = repository.getWeather(city);
        return Response.ok(weather).build();
    }
}
