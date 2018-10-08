package cloud.nativ.javaee;

import org.eclipse.microprofile.config.spi.Converter;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A MicroProfile Config converter for {@link URL}.
 */
public class URLConverter implements Converter<URL> {
    @Override
    public URL convert(String value) {
        try {
            return new URL(value);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
