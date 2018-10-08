package cloud.nativ.javaee;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.json.JsonObject;
import java.net.URL;
import java.util.Optional;

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

    @Inject
    @ConfigProperty(name = "a.url")
    private URL aURL;

    @Inject
    @ConfigProperty(name = "a.json.object")
    private JsonObject aJsonObject;

    public String getHostname() {
        return hostname;
    }

    public Optional<String> getOptionalString() {
        return aOptionalString;
    }

    public String getDynamicString() {
        return aDynamicString.get();
    }

    public Long getLong() {
        return aLong;
    }

    public String[] getStringArray() {
        return aStringArray;
    }

    public URL getURL() {
        return aURL;
    }

    public JsonObject getJsonObject() {
        return aJsonObject;
    }
}
