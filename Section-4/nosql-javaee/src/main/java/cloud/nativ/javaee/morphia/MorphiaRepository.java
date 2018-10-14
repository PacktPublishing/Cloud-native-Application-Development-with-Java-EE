package cloud.nativ.javaee.morphia;

import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.Collection;
import java.util.Optional;

@ApplicationScoped
public class MorphiaRepository {

    @Inject
    private MongoClient client;

    private Morphia morphia;
    private Datastore datastore;

    @PostConstruct
    void initialize() {
        this.morphia = new Morphia();
        this.datastore = morphia.createDatastore(client, "morphia");
        this.datastore.ensureIndexes();
    }

    public Collection<MorphiaPojo> findAll() {
        final Query<MorphiaPojo> query = datastore.createQuery(MorphiaPojo.class);
        return query.asList();
    }

    public String save(MorphiaPojo payload) {
        Key<MorphiaPojo> saved = datastore.save(payload);
        return ((ObjectId) saved.getId()).toHexString();
    }

    public MorphiaPojo findById(String id) {
        Query<MorphiaPojo> query = datastore.createQuery(MorphiaPojo.class).field("id").equal(new ObjectId(id));
        return Optional.ofNullable(query.get()).orElseThrow(NotFoundException::new);
    }

    public void delete(String id) {
        datastore.delete(MorphiaPojo.class, new ObjectId(id));
    }
}
