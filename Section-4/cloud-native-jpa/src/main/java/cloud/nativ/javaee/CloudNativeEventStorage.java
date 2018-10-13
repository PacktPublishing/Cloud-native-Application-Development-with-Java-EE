package cloud.nativ.javaee;

import javax.ejb.Stateless;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.util.Collection;
import java.util.Optional;

@Stateless
@Transactional(Transactional.TxType.REQUIRED)
public class CloudNativeEventStorage {

    @PersistenceContext
    private EntityManager entityManager;

    public CloudNativeEvent get(Long id) {
        return Optional.ofNullable(entityManager.find(CloudNativeEvent.class, id)).orElseThrow(NotFoundException::new);
    }

    public Collection<CloudNativeEvent> all() {
        return entityManager.createQuery("SELECT e FROM CloudNativeEvent e", CloudNativeEvent.class).getResultList();
    }

    public CloudNativeEvent persist(JsonObject payload) {
        return entityManager.merge(new CloudNativeEvent(payload));
    }

}
