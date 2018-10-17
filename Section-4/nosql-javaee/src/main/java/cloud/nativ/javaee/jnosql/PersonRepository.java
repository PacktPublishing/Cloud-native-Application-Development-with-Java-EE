package cloud.nativ.javaee.jnosql;

import org.jnosql.artemis.Repository;

import java.util.List;
import java.util.stream.Stream;

public interface PersonRepository extends Repository<Person, Long> {

    List<Person> findByName(String name);

    Stream<Person> findByPhones(String phone);
}
