package cloud.nativ.javaee.jnosql;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jnosql.artemis.Column;
import org.jnosql.artemis.Embeddable;

@Data
@NoArgsConstructor
@Embeddable
public class Address {

    @Column
    private String street;

    @Column
    private String city;

    @Column
    private Integer number;

}