package cloud.nativ.javaee.jnosql;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;
import org.jnosql.artemis.Id;

import java.util.List;

@Data
@NoArgsConstructor
@Entity("Person")
public class Person {
    @Id("id")
    private long id;

    @Column
    private String name;

    @Column
    private Address address;

    @Column
    private List<String> phones;
}
