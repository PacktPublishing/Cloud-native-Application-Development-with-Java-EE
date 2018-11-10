# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 3.3: Clustered Scheduling and Coordination with EJBs

Scheduled tasks can easily be implemented using automatic or programmatic timer EJBs.
Depending on the requirements you may want to execute the scheduled tasks as a
cluster singleton only. So you need to coordinate the instances and use a distributed
data structure like a distributed lock.

### Step 1: Multiple deployments with Docker Compose

To simulate a clustered deployment we are going to start the same service as two individual containers.
Add the following snippet to your `docker-compose.yml` file:
```yaml
version: "3"

services:  
  cluster-schedule-1:
    build:
      context: .
    image: cluster-schedule:1.0.1
    ports:
    - "18080:8080"
    networks:
    - jee8net

  cluster-schedule-2:
    image: cluster-schedule:1.0.1
    ports:
    - "28080:8080"
    networks:
    - jee8net

networks:
  jee8net:
    driver: bridge
```

### Step 2: Create a Automatic Timer EJB

In order to create an automatic timer that runs using a Cron like schedule, add the following EJB class.
The schedule will run every 5 seconds.

```java
@Startup
@Singleton
public class ClusterScheduleAutoBean {

    private static final Logger LOGGER = Logger.getLogger(ClusterScheduleAutoBean.class.getName());

    @Inject
    @Metric(name = "automaticTimeoutCounter", absolute = true)
    private Counter automaticTimeoutCounter;

    @Schedule(second = "*/5", minute = "*", hour = "*")
    public void automaticTimeout() {
        automaticTimeoutCounter.inc();
        LOGGER.log(Level.INFO, "Automatic timer execution {0} at {1}.",
                new Object[]{automaticTimeoutCounter.getCount(), LocalDateTime.now()});
    }

    public long getAutomaticTimeoutCounter() {
        return automaticTimeoutCounter.getCount();
    }
}
```

### Step 3: Create a Programmatic Timer EJB

In order to create and run a programmatic interval timer, add the following EJB class. The logic is contained
in the `@Timeout` annotated method. The interval is set to 2 seconds with an initial delay of 10 seconds.

```java
@Startup
@Singleton
public class ClusterScheduleProgBean {

    private static final Logger LOGGER = Logger.getLogger(ClusterScheduleProgBean.class.getName());

    @Inject
    @Metric(name = "programmaticTimeoutCounter", absolute = true)
    private Counter programmaticTimeoutCounter;

    @Resource
    private TimerService timerService;

    @PostConstruct
    public void initialize() {
        timerService.createIntervalTimer(10000, 2000, new TimerConfig(ClusterScheduleProgBean.class.getName(), true));
    }

    @Timeout
    public void programmaticTimeout(Timer timer) {
        programmaticTimeoutCounter.inc();
        LOGGER.log(Level.INFO, "Programmatic timer execution {0} at {1}.",
                new Object[]{programmaticTimeoutCounter.getCount(), LocalDateTime.now()});
    }

    public long getProgrammaticTimeoutCounter() {
        return programmaticTimeoutCounter.getCount();
    }

}
```

### Step 4: Synchronize Clustered Timer with Hazelcast and Distributed Locks

Currently the individual timer EJBs all run concurrently when running multiple instances. This may not be desirable.
To avoid this, we can use a distributed lock from Hazelcast to synchronize the instances.

Make sure you have the following dependency defined in your `build.gradle` file.
```groovy
providedCompile 'com.hazelcast:hazelcast:3.10.5'
```

First, import the Hazelcast instance from Payara bound to the `payara/Hazelcast` JNDI name.
```java
@Resource(name = "payara/Hazelcast")
private HazelcastInstance hazelcast;
```

The, add the following code snipped to the timer beans, e.g. the automatic timer bean.
```java
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
```

```java
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
```
