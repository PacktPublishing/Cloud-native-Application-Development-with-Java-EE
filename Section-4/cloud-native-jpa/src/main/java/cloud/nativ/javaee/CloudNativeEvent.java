package cloud.nativ.javaee;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "cloud_native_event")
@NoArgsConstructor
@Data
public class CloudNativeEvent {
    @Id
    @SequenceGenerator(name = "cloud_native_event_seq_gen", sequenceName = "cloud_native_event_seq", allocationSize = 5)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cloud_native_event_seq_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "payload", columnDefinition = "jsonb")
    @Convert(converter = JsonObjectConverter.class)
    private JsonObject payload;

    @Column(name = "stored_at")
    private OffsetDateTime storedAt;

    protected CloudNativeEvent(JsonObject payload) {
        this.payload = payload;
    }

    @PrePersist
    protected void onCreate() {
        storedAt = OffsetDateTime.now();
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("id", id)
                .add("stored_at", storedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .add("payload", payload)
                .build();
    }
}
