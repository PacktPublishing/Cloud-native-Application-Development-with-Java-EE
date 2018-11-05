package cloud.nativ.javaee;

import io.opentracing.Tracer;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

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
