# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 5.5: Adding trace information using MicroProfile OpenTracing

Tracing the requests in a microservice architecture as the 3rd corner stone of good
diagnosability to be able to see and analyse the request flow.

### Step 1: Add the MicroProfile OpenTracing dependency

First, we need to add the following dependencies to the `build.gradle` file.

```groovy
    providedCompile 'org.eclipse.microprofile.opentracing:microprofile-opentracing-api:1.1'
    providedCompile 'io.opentracing:opentracing-api:0.31.0'
```

### Step 2: Configure Payara request tracing

Next we need to configure the request tracing feature of Payara using a post boot command file.
Create a file called `post-boot.asadmin` and add the following content.

```
set-requesttracing-configuration --thresholdValue=25 --enabled=true --target=server-config --thresholdUnit=MICROSECONDS --dynamic=true
requesttracing-log-notifier-configure --dynamic=true --enabled=true --target=server-config
```

We also need to modify the `Dockerfile` to use the post boot command file in the CMD options. 

```dockerfile
FROM qaware/zulu-centos-payara-micro:8u181-5.183

CMD ["--noCluster", "--postbootcommandfile", "/opt/payara/post-boot.asadmin", "--deploymentDir", "/opt/payara/deployments"]

COPY post-boot.asadmin /opt/payara/
COPY build/libs/tracing-service.war /opt/payara/deployments/
```

### Step 3: Use MicroProfile OpenTracing with JAX-RS

To use MicroProfile OpenTracing we simply need to annotate any JAX-RS resource method with the `@Traced` annotation. 
We can also use CDI to `@Inject` a `io.opentracing.Tracer` instance. Create the following class.

```java
@ApplicationScoped
@Path("tracing")
public class TracingResource {

    @Inject
    private Tracer tracer;

    @GET
    @Traced
    public Response trace(@QueryParam("payload") @DefaultValue("None") String payload) {
        tracer.activeSpan().setTag("payload", payload);
        return Response.ok().build();
    }
}
```
 