package cloud.nativ.javaee;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.Socket;

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
