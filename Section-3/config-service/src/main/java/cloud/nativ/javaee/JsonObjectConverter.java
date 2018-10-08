package cloud.nativ.javaee;

import org.eclipse.microprofile.config.spi.Converter;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import java.io.StringReader;

/**
 * A MicroProfile Config converter for {@link JsonObject}.
 */
public class JsonObjectConverter implements Converter<JsonObject> {
    @Override
    public JsonObject convert(String value) {
        try {
            return Json.createReader(new StringReader(value)).readObject();
        } catch (JsonException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
