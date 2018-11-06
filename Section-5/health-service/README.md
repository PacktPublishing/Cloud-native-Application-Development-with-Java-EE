# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 5.4: Adding readiness probes using MicroProfile Health

When running your microservices using a cluster scheduler like Kubernetes you have to provide endpoints
to check if your service is healthy and ready to serve requests.

### Step 1: Add MicroProfile Health dependency

```groovy
    providedCompile 'org.eclipse.microprofile.health:microprofile-health-api:1.0'
```

### Step 2: Add health check implementations

Add the following `HealthCheck` implementation class to check if a TCP connection can be
established locally.

```java
@ApplicationScoped
@Health
public class TcpConnectHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.builder().name("tcp");
        try (Socket socket = new Socket("localhost", 8080)) {
            if (socket.isConnected()) {
                builder = builder.up();
            } else {
                builder = builder.down();
            }
        } catch (IOException e) {
            builder = builder.down().withData("message", e.getMessage());
        }
        return builder.build();
    }
}
```

Add the following `HealthCheck` implementation to build a more elaborate and modifiable health
checkk response. 

```java
@ApplicationScoped
@Health
public class ModifiableHealthCheck implements HealthCheck {

    private AtomicBoolean state = new AtomicBoolean(true);
    private String message = "Health is OK.";

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.builder()
                .name("modifiable")
                .state(state.get())
                .withData("message", message)
                .withData("currentTimeMillis", System.currentTimeMillis())
                .withData("version", "1.0.1")
                .build();
    }

    public void up(String message) {
        state.compareAndSet(false, true);
        this.message = message;
    }

    public void down(String message) {
        state.compareAndSet(true, false);
        this.message = message;
    }
}
```

To modify the health response, add the following REST resource implementation to your codebase.

```java
@ApplicationScoped
@Path("health-check")
public class HealthCheckResource {

    @Inject
    @Health
    private ModifiableHealthCheck healthCheck;

    @PUT
    @Path("up")
    public Response up(@QueryParam("message") @DefaultValue("Health is OK.") String message) {
        healthCheck.up(message);
        return Response.ok().build();
    }

    @PUT
    @Path("down")
    public Response down(@QueryParam("message") @DefaultValue("Health is NOK.") String message) {
        healthCheck.down(message);
        return Response.ok().build();
    }

}
```

### Step 3: Query /health endpoint manually

Once you compile and run everything, the default `/health` endpoint is exposed by the service.

```
$ curl http://localhost:8080/health

$ curl -X PUT http://localhost:8080/api/health-check/down
$ curl http://localhost:8080/health

$ curl -X PUT http://localhost:8080/api/health-check/up
$ curl http://localhost:8080/health
``` 


### Step 4: Using /health endpoint as Kubernetes readiness probe

Finally, we can now use the `/health` endpoint as a readiness probe in our Kubernetes deployment.
Add the following readiness and liveness probes to the `src/main/kubernetes/health-service-deployment.yaml`. 

```yaml
    # use health endpoint as readiness probe
    readinessProbe:
      httpGet:
        path: /health
        port: 8080
      initialDelaySeconds: 30
      periodSeconds: 5
      
    # use WADL endpoint as liveness probe
    livenessProbe:
      httpGet:
        path: /api/application.wadl
        port: 8080
      initialDelaySeconds: 60
      periodSeconds: 5
```