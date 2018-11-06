package cloud.nativ.javaee;

import lombok.extern.java.Log;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

@ApplicationScoped
@Path("spelling")
@Log
public class SpellingResource {

    @Inject
    private AlphabetClient alphabetClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed(unit = MetricUnits.MILLISECONDS, absolute = true)
    public Response spelling(@QueryParam("word") @NotBlank String word, @Context HttpHeaders headers) {
        LOGGER.log(Level.INFO, "Get spelling for {0}.", word);

        Locale locale = headers.getAcceptableLanguages().get(0);
        List<String> spelling = spell(word, locale);

        return Response.ok(spelling).build();
    }

    private List<String> spell(String word, Locale locale) {
        List<String> spelling = new ArrayList<>();
        if (word.length() == 1) {
            return Collections.singletonList(word);
        } else if ("abc".equalsIgnoreCase(word)) {
            spelling.add(alphabetClient.getA(locale));
            spelling.add(alphabetClient.getB(locale));
            spelling.add(alphabetClient.getC(locale));
        } else {
            char[] chars = word.toCharArray();
            for (char c : chars) {
                spelling.add(alphabetClient.getAny(c, locale));
            }
        }

        return spelling;
    }


}
