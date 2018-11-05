# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 5.2: Adding good, detailed and structured logging

Having good, detailed and structured logging is the cornerstone of good and
easy diagnosability.

### Step 1: Define a suitable logging strategy

- **DEBUG** - Used for detailed and valuable information required for debugging.
- **INFO** - Used for important runtime or business events.
- **WARNING** - Used for non critical errors that can be compensated by the system.
- **ERROR** -  Used for critical errors that need immediate attention (ops team or SRE).

### Step 2: Choose a suitable logging framework

There are many good logging frameworks out there. Usually it is a good idea to have a uniform
logging API available to homogenize all of these. Add the following SLF4J dependencies.

```groovy
    compile 'org.slf4j:slf4j-api:1.7.25'
    runtime 'org.slf4j:slf4j-jdk14:1.7.25'
    runtime 'org.slf4j:log4j-over-slf4j:1.7.25'
    runtime 'org.slf4j:jcl-over-slf4j:1.7.25'
```

### Step 3: Configure logging framework

1. **Reduce noise!** Avoid excessive logging, especially by 3rd party dependencies. Tune the log level.
2. Decide on log format (plain text vs. JSON) and define format.

Open the file called `logging.properties` and add the following content to it.

```
## Handlers
handlers=java.util.logging.ConsoleHandler
java.util.logging.ConsoleHandler.formatter=com.sun.enterprise.server.logging.ODLLogFormatter
com.sun.enterprise.server.logging.ODLLogFormatter.ansiColor=true

# java.util.logging.ConsoleHandler.formatter=fish.payara.enterprise.server.logging.JSONLogFormatter

java.util.logging.ConsoleHandler.level=FINEST

## Global Level
.level=WARNING

## Application Level
cloud.nativ.javaee.level=FINEST

# 3rd Party Level
PayaraMicro.level=INFO
javax.level=INFO
javax.enterprise.system.core.level=INFO
javax.enterprise.system.core.classloading.level=INFO
javax.enterprise.system.tools.deployment.level=INFO
javax.enterprise.system.core.transaction.level=INFO
javax.enterprise.system.tools.admin.level=INFO
org.apache.jasper.level=INFO
org.apache.catalina.level=INFO
org.apache.coyote.level=INFO
```

### Step 4: Add logging

To get hold of a suitable logger instance, we can use CDI by implementing the following producer.

```java
@ApplicationScoped
public class LoggerProducer {

    @Produces
    @Dependent
    public Logger produceLogger(InjectionPoint injectionPoint) {
        Class<?> declaringClass = injectionPoint.getMember().getDeclaringClass();
        return LoggerFactory.getLogger(declaringClass);
    }
}
```

Using the `Logger` in your code is easy, simply `@Inject` the instance where ever needed.

```java
@Slf4j
@ApplicationScoped
@Path("logger")
public class LoggerResource {

    @Inject
    private Logger logger;

    @PUT
    @Logged
    public Response log(@QueryParam("level") @DefaultValue("INFO") String level, @QueryParam("message") String message) {
        switch (level) {
            case "DEBUG":
                logger.debug(message);
                break;
            case "WARN":
                logger.warn(message);
                break;
            case "ERROR":
                logger.error(message);
                break;
            case "INFO":
                logger.info(message);
                break;
            default:
                // this uses the Lombok LOGGER !!!
                LOGGER.info(message);
        }

        return Response.noContent().build();
    }
}
```

### Bonus Step: The Elastic Stack

As a bonus, you may want to follow the instructions found in the elastic stack
repository (https://github.com/elastic/stack-docker) to fire up a current Elastic
Stack locally using Docker Compose. Careful, you need quite some resources.
