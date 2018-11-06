package cloud.nativ.javaee;

import lombok.extern.java.Log;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

@Provider
@RequestScoped
@Log
public class TracingRequestFilter implements ContainerRequestFilter, ClientRequestFilter {

    private static final Set<String> HEADERS = unmodifiableSet(new HashSet<>(asList("x-request-id", "x-b3-traceid", "x-b3-spanid", "x-b3-parentspanid", "x-b3-sampled", "x-b3-flags", "x-ot-span-context")));

    private MultivaluedMap<String, String> tracingHeaders = new MultivaluedHashMap<>();

    @Override
    public void filter(ClientRequestContext requestContext) {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        tracingHeaders.forEach(headers::addAll);

        LOGGER.log(Level.INFO, "Propagated OpenTracing headers {0} to client request.", tracingHeaders);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        MultivaluedMap<String, String> headers = requestContext.getHeaders();
        headers.forEach((key, values) -> {
            if (HEADERS.contains(key.toLowerCase())) {
                tracingHeaders.addAll(key, values);
            }
        });

        LOGGER.log(Level.INFO, "Extracted OpenTracing headers {0} from server request.", tracingHeaders);
    }
}
