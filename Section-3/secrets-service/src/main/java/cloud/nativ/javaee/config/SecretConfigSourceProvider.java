package cloud.nativ.javaee.config;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

/**
 * The {@link ConfigSourceProvider} for META-INF/secret.properties
 */
public class SecretConfigSourceProvider implements ConfigSourceProvider {
    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
        Enumeration<URL> secrets;
        try {
            secrets = forClassLoader.getResources("META-INF/secret.properties");
        } catch (IOException e) {
            return Collections.emptyList();
        }

        Collection<ConfigSource> configSources = new ArrayList<>();
        while (secrets.hasMoreElements()) {
            URL url = secrets.nextElement();
            configSources.add(new SecretConfigSource(url));
        }
        return configSources;
    }
}
