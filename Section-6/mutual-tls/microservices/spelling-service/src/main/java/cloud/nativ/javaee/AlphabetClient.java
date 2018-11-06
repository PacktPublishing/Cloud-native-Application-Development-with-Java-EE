package cloud.nativ.javaee;

import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Log
@ApplicationScoped
public class AlphabetClient {

    @Inject
    @ConfigProperty(name = "a.service.url", defaultValue = "http://a-service:8080/api/alphabet/{a}")
    private String aServiceUrl;

    @Inject
    @ConfigProperty(name = "b.service.url", defaultValue = "http://b-service:8080/api/alphabet/{b}")
    private String bServiceUrl;

    @Inject
    @ConfigProperty(name = "c.service.url", defaultValue = "http://c-service:8080/api/alphabet/{c}")
    private String cServiceUrl;

    @Inject
    @ConfigProperty(name = "alphabet.service.url", defaultValue = "http://alphabet-service:8080/api/alphabet/{character}")
    private String alphabetServiceUrl;

    @Inject
    private TracingRequestFilter tracingRequestFilter;

    private Client client;

    @PostConstruct
    void initialize() {
        client = ClientBuilder.newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    @PreDestroy
    void destroy() {
        client.close();
    }

    @Timeout(2000)
    @Fallback(fallbackMethod = "getFallback")
    @Timed(unit = MetricUnits.MILLISECONDS, absolute = true)
    public String getA(Locale locale) {
        LOGGER.log(Level.INFO, "Getting A for locale {0}.", locale);
        return client.register(tracingRequestFilter).target(aServiceUrl)
                .resolveTemplate("a", "a")
                .request().acceptLanguage(locale)
                .get(String.class);
    }

    @Timeout(2000)
    @Fallback(fallbackMethod = "getFallback")
    @Timed(unit = MetricUnits.MILLISECONDS, absolute = true)
    public String getB(Locale locale) {
        LOGGER.log(Level.INFO, "Getting B for locale {0}.", locale);
        return client.register(tracingRequestFilter).target(bServiceUrl)
                .resolveTemplate("b", "b")
                .request().acceptLanguage(locale)
                .get(String.class);
    }

    @Timeout(2000)
    @Fallback(fallbackMethod = "getFallback")
    @Timed(unit = MetricUnits.MILLISECONDS, absolute = true)
    public String getC(Locale locale) {
        LOGGER.log(Level.INFO, "Getting C for locale {0}.", locale);
        return client.register(tracingRequestFilter).target(cServiceUrl)
                .resolveTemplate("c", "c")
                .request().acceptLanguage(locale)
                .get(String.class);
    }

    @Timeout(2000)
    @Fallback(fallbackMethod = "getAnyFallback")
    @Timed(unit = MetricUnits.MILLISECONDS, absolute = true)
    public String getAny(char character, Locale locale) {
        LOGGER.log(Level.INFO, "Getting character for {0} and locale {1}.", new Object[]{character, locale});
        return client.register(tracingRequestFilter).target(alphabetServiceUrl)
                .resolveTemplate("character", Character.toString(character))
                .request().acceptLanguage(locale)
                .get(String.class);
    }

    public String getFallback(Locale locale) {
        return "?";
    }

    public String getAnyFallback(char character, Locale locale) {
        return getFallback(locale);
    }
}
