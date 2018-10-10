package cloud.nativ.javaee;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.annotation.Metric;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
public class ClusterScheduleAutoBean {

    private static final Logger LOGGER = Logger.getLogger(ClusterScheduleAutoBean.class.getName());

    @Inject
    @Metric(name = "automaticTimeoutCounter", absolute = true)
    private Counter automaticTimeoutCounter;

    @Resource(name = "payara/Hazelcast")
    private HazelcastInstance hazelcast;

    @Schedule(second = "*/5", minute = "*", hour = "*")
    public void automaticTimeout() {
        ILock lock = hazelcast.getLock("automaticTimeoutLock");
        if (lock.tryLock()) {
            try {
                automaticTimeoutCounter.inc();
                LOGGER.log(Level.INFO, "Automatic timer execution {0} at {1}.",
                        new Object[]{automaticTimeoutCounter.getCount(), LocalDateTime.now()});

                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        } else {
            LOGGER.log(Level.INFO, "Skip automatic timer execution. Locked by another instance.");
        }
    }

    public long getAutomaticTimeoutCounter() {
        return automaticTimeoutCounter.getCount();
    }
}
