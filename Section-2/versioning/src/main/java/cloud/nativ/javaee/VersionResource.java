package cloud.nativ.javaee;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

/**
 * A version REST resource for path based API versioning.
 */
@RequestScoped
@Path("version")
public class VersionResource {

    @Context
    private ResourceContext context;

    @Path("v1")
    public VersionResourceV1 v1() {
        // alternatively we could return VersionResourceV1.class
        // careful, because injection does not work here
        return new VersionResourceV1();
    }

    @Path("v2")
    public VersionResourceV2 v2() {
        // perform lookup via ResourceContext for @Inject support
        return context.getResource(VersionResourceV2.class);
    }
}
