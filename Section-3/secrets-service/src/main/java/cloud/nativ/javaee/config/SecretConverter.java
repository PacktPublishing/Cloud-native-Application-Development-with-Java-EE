package cloud.nativ.javaee.config;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * Convert String to Secret instance.
 */
public class SecretConverter implements Converter<Secret> {
    @Override
    public Secret convert(String value) {
        return new Secret(value);
    }

}
