# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 5.3: Adding telemetry data using MicroProfile Metrics

Good telemetry data on technical as well as business metrics is another cornerstone of good
diagnosability in a cloud native environment.

### Step 1: Add MicroProfile Metrics API

Add the following dependency to your `build.gradle` file.

```groovy
    providedCompile 'org.eclipse.microprofile.metrics:microprofile-metrics-api:1.1.1'
```

### Step 2: Add custom metrics to JAX-RS resource

There are several ways to add metrics to your code: annotate methods using the desired MicroProfile Metrics
annotation or inject a suitable metrics instances for programmatic access. Add the following class
to your codebase.

```java
@ApplicationScoped
@Path("metrics-demo")
public class MetricsDemoResource {

    @Inject
    @Metric(name = "globalMetricsDemoCounter", absolute = true)
    private Counter counter;

    private final AtomicInteger concurrentInvocations = new AtomicInteger(0);

    @GET
    @Timed(name = "metricsDemo", unit = MetricUnits.MILLISECONDS)
    public JsonObject metricsDemo() {
        counter.inc();

        try {
            return Json.createObjectBuilder()
                    .add("counter", counter.getCount())
                    .add("currentConcurrentInvocations", concurrentInvocations.incrementAndGet())
                    .build();
        } finally {
            concurrentInvocations.decrementAndGet();
        }
    }

    @Gauge(name = "currentConcurrentMetricsDemoInvocations", unit = MetricUnits.NONE)
    public Integer currentConcurrentInvocations() {
        return concurrentInvocations.get();
    }
}
```

### Step 3: Access metrics endpoints

Once you have compiled everything, built the image and fired up the container, the default
metrics endpoints are exposed already.

```
$ curl http://localhost:8080/api/metrics-demo

$ curl http://localhost:8080/metrics
$ curl http://localhost:8080/metrics/application
$ curl http://localhost:8080/metrics/base
```

### Bonus Step: Try Prometheus

Add the following definitions to your `docker-compose.yml` file to also start Prometheus, the Node Exporter
as well as Grafana.

```yaml
prometheus:
  image: prom/prometheus:v2.4.3
  volumes:
  - ./prometheus.yml:/etc/prometheus/prometheus.yml
  command: "--config.file=/etc/prometheus/pometheus.yml"
  ports:
  - "9090:9090"
  depends_on:
  - node-exporter
  - metrics-service
  networks:
  - jee8net
      
node-exporter:
  image: prom/node-exporter:v0.16.0
  ports:
  - "9100:9100"
  networks:
  - jee8net
  
grafana:
  image: grafana/grafana:5.3.2
  ports:
  - "3000:3000"
  depends_on:
  - prometheus
  networks:
  - jee8net
```

Also, add a `prometheus.yml` file to your codebase with the following content.

```yaml
# global config
global:
  scrape_interval:     5s # By default, scrape targets every 15 seconds.
  evaluation_interval: 5s # By default, scrape targets every 15 seconds.
  # scrape_timeout is set to the global default (10s).

  # Attach these labels to any time series or alerts when communicating with
  # external systems (federation, remote storage, Alertmanager).
  external_labels:
    monitor: 'metrics-monitor'

# Load rules once and periodically evaluate them according to the global 'evaluation_interval'.
rule_files:
# - "first.rules"
# - "second.rules"

# A scrape configuration containing exactly one endpoint to scrape:
# Here it's Prometheus itself.
scrape_configs:
# The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
- job_name: 'prometheus'

  # Override the global default and scrape targets from this job every 5 seconds.
  scrape_interval: 5s

  # metrics_path defaults to '/metrics'
  # scheme defaults to 'http'.

  static_configs:
  - targets: ['localhost:9090']

- job_name: "node-exporter"
  scrape_interval: "15s"
  static_configs:
  - targets: ['node-exporter:9100']

- job_name: "metrics-service"
  scrape_interval: "3s"
  static_configs:
  - targets: ['metrics-service:8080']
```