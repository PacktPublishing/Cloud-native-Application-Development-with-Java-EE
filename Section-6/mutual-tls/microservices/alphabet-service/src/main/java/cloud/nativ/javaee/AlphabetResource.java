package cloud.nativ.javaee;

import lombok.extern.java.Log;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

@Log
@ApplicationScoped
@Path("alphabet")
public class AlphabetResource {

    private Map<String, Buchstabiertafel> buchstabiertafeln = new HashMap<>();

    @PostConstruct
    public void initialize() {
        buchstabiertafeln.put("*", Buchstabiertafel.nato());
        buchstabiertafeln.put("de", Buchstabiertafel.din5009());
        buchstabiertafeln.put("en", Buchstabiertafel.nato());
    }

    @GET
    @Path("/{character}")
    @Produces(MediaType.TEXT_PLAIN)
    @Timed(unit = MetricUnits.MILLISECONDS, absolute = true)
    public Response alphabet(@PathParam("character") @Size(min = 1, max = 1) String character, @Context HttpHeaders headers) {

        Locale locale = headers.getAcceptableLanguages().get(0);
        String language = locale.getLanguage();

        LOGGER.log(Level.INFO, "Get {0} from Buchstabiertafel.", character);
        Buchstabiertafel tafel = buchstabiertafeln.getOrDefault(language, buchstabiertafeln.get("*"));
        return Response.ok(tafel.get(character)).build();
    }


}
