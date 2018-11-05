package cloud.nativ.javaee;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicBoolean;

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
