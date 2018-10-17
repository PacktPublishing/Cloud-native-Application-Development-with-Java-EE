package cloud.nativ.javaee.jnosql;

import org.jnosql.diana.api.Settings;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentCollectionManagerFactory;
import org.jnosql.diana.api.document.DocumentConfiguration;
import org.jnosql.diana.mongodb.document.MongoDBDocumentConfiguration;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Collections;
import java.util.Map;

@ApplicationScoped
public class JNoSqlProducer {

    private static final String COLLECTION = "persons";

    private DocumentConfiguration configuration;

    private DocumentCollectionManagerFactory managerFactory;

    @PostConstruct
    public void init() {
        configuration = new MongoDBDocumentConfiguration();
        Map<String, Object> settings = Collections.singletonMap("mongodb-server-host-1", "mongodb:27017");
        managerFactory = configuration.get(Settings.of(settings));
    }

    @Produces
    public DocumentCollectionManager getManager() {
        return managerFactory.get(COLLECTION);
    }

}