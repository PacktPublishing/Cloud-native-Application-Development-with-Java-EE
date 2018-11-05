package cloud.nativ.javaee;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.concurrent.atomic.AtomicInteger;

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
