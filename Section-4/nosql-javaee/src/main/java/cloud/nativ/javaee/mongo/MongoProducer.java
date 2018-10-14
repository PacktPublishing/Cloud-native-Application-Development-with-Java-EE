package cloud.nativ.javaee.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@ApplicationScoped
public class MongoProducer {

    private MongoClient mongoClient;
    private CodecRegistry pojoCodecRegistry;

    @PostConstruct
    void initialize() {
        // use MicroProfile Config to externalize these
        mongoClient = new MongoClient("mongodb", 27017);
        pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }

    @Produces
    @ApplicationScoped
    public MongoClient client() {
        return mongoClient;
    }

    @Produces
    @ApplicationScoped
    public MongoDatabase database() {
        return mongoClient.getDatabase("javaee").withCodecRegistry(pojoCodecRegistry);
    }
}
