package cloud.nativ.javaee;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * JPA Converter from JsonObject to jsonb type.
 */
@Converter(autoApply = true)
public class OffsetDateTimeConverter implements AttributeConverter<OffsetDateTime, String> {

    @Override
    public String convertToDatabaseColumn(OffsetDateTime entityValue) {
        return Objects.toString(entityValue, null);
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        return OffsetDateTime.parse(databaseValue);
    }
}