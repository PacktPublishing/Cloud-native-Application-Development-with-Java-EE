package cloud.nativ.javaee;

import org.eclipse.microprofile.auth.LoginConfig;

import javax.annotation.security.DeclareRoles;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Configures a JAX-RS endpoint.
 */
@ApplicationScoped
@LoginConfig(authMethod = "MP-JWT", realmName = "MP-JWT")
@DeclareRoles({"admin", "user"})
@ApplicationPath("api")
public class JAXRSConfiguration extends Application {
}
