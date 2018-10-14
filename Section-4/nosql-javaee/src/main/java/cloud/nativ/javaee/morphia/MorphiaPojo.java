package cloud.nativ.javaee.morphia;

import lombok.Data;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTransient;

@Data
@Entity("pojos")
@JsonbPropertyOrder({"description", "name"})
@JsonbNillable
public class MorphiaPojo {

    @Id
    @JsonbTransient
    private ObjectId id;

    @Property
    @JsonbProperty("the-name")
    private String name;

    @Property
    @JsonbProperty
    private String description;
}
