package cloud.nativ.javaee;

import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

@Log
@ApplicationScoped
public class StaticGroupsStore implements IdentityStore {

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        LOGGER.log(Level.INFO, "Getting caller groups for {0}", validationResult.getCallerPrincipal().getName());

        String callerPrincipalName = validationResult.getCallerPrincipal().getName();
        if ("admin".equalsIgnoreCase(callerPrincipalName)) {
            return new HashSet<>(Arrays.asList("admin", "user"));
        } else {
            return Collections.singleton("user");
        }
    }

    @Override
    public int priority() {
        return 101;
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return Collections.singleton(ValidationType.PROVIDE_GROUPS);
    }
}
