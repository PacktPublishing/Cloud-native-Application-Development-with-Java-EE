package cloud.nativ.javaee;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Health
public class EverythingOkCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse
                .named("everythingOk")
                .up()
                .withData("message", "Everything is OK.")
                .withData("version", "1.0.1")
                .withData("currentTimeMillis", System.currentTimeMillis())
                .build();
    }
}
