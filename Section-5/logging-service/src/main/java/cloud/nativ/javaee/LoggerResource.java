package cloud.nativ.javaee;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Slf4j
@ApplicationScoped
@Path("logger")
public class LoggerResource {

    @Inject
    private Logger logger;

    @PUT
    @Logged
    public Response log(@QueryParam("level") @DefaultValue("INFO") String level, @QueryParam("message") String message) {
        switch (level) {
            case "DEBUG":
                logger.debug(message);
                break;
            case "WARN":
                logger.warn(message);
                break;
            case "ERROR":
                logger.error(message);
                break;
            case "INFO":
                logger.info(message);
                break;
            default:
                // this uses the Lombok LOGGER !!!
                LOGGER.info(message);
        }

        return Response.noContent().build();
    }
}
