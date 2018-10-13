package cloud.nativ.javaee;

import javax.ejb.Stateless;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collection;

@Stateless
@Transactional(Transactional.TxType.REQUIRED)
public class CloudNativeEventStorage {

    @PersistenceContext
    private EntityManager entityManager;

    public CloudNativeEvent get(Long id) {
        return entityManager.find(CloudNativeEvent.class, id);
    }

    public Collection<CloudNativeEvent> all() {
        return entityManager.createQuery("SELECT e FROM CloudNativeEvent e", CloudNativeEvent.class).getResultList();
    }

    public CloudNativeEvent persist(JsonObject payload) {
        return entityManager.merge(new CloudNativeEvent(payload));
    }

}
