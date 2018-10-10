package cloud.nativ.javaee;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("cluster-schedule")
public class ClusterScheduleResource {

    @Inject
    private ClusterScheduleProgBean scheduleProgBean;

    @Inject
    private ClusterScheduleAutoBean scheduleAutoBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject info() {
        return Json.createObjectBuilder()
                .add("programmaticTimeoutCount", scheduleProgBean.getProgrammaticTimeoutCounter())
                .add("automaticTimeoutCount", scheduleAutoBean.getAutomaticTimeoutCounter())
                .build();
    }
}
