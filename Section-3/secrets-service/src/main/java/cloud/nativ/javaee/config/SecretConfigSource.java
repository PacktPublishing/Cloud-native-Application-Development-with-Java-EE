package cloud.nativ.javaee.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Reads the properties from a custom property file.
 */
public class SecretConfigSource implements ConfigSource {

    private final Properties secretProperties;

    public SecretConfigSource(URL url) {
        this.secretProperties = new Properties();
        try {
            this.secretProperties.load(new FileInputStream(new File(url.toURI())));
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Set<String> getPropertyNames() {
        return secretProperties.stringPropertyNames();
    }

    @Override
    public int getOrdinal() {
        return 101;
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> props = new HashMap<>();
        for (String name : getPropertyNames()) {
            props.put(name, secretProperties.getProperty(name));
        }
        return props;
    }

    @Override
    public String getValue(String propertyName) {
        return secretProperties.getProperty(propertyName);
    }

    @Override
    public String getName() {
        return "SECRETS";
    }
}
