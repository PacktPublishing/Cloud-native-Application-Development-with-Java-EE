package cloud.nativ.javaee;

import javax.annotation.security.DeclareRoles;
import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Configures a JAX-RS endpoint.
 */
@ApplicationScoped
@ApplicationPath("api")
@BasicAuthenticationMechanismDefinition(realmName = "security-service")
@DeclareRoles({"admin", "user"})
public class JAXRSConfiguration extends Application {
}
