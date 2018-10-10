package cloud.nativ.javaee;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.annotation.Metric;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
public class ClusterScheduleProgBean {

    private static final Logger LOGGER = Logger.getLogger(ClusterScheduleProgBean.class.getName());

    @Inject
    @Metric(name = "programmaticTimeoutCounter", absolute = true)
    private Counter programmaticTimeoutCounter;

    @Resource
    private TimerService timerService;

    @Resource(name = "payara/Hazelcast")
    private HazelcastInstance hazelcast;

    @PostConstruct
    public void initialize() {
        timerService.createIntervalTimer(10000, 2000,
                new TimerConfig(ClusterScheduleProgBean.class.getName(), true));
    }

    @Timeout
    public void programmaticTimeout(Timer timer) {
        ILock lock = hazelcast.getLock("programmaticTimeoutLock");
        if (lock.tryLock()) {
            try {
                programmaticTimeoutCounter.inc();
                LOGGER.log(Level.INFO, "Programmatic timer execution {0} at {1}.",
                        new Object[]{programmaticTimeoutCounter.getCount(), LocalDateTime.now()});

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        } else {
            LOGGER.log(Level.INFO, "Skip programmatic timer execution. Locked by another instance.");
        }
    }

    public long getProgrammaticTimeoutCounter() {
        return programmaticTimeoutCounter.getCount();
    }

}
