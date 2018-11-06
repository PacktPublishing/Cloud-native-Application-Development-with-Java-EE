package cloud.nativ.javaee;

import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

@Log
@ApplicationScoped
public class StaticCredentialStore implements IdentityStore {
    @Override
    public CredentialValidationResult validate(Credential credential) {
        try {
            String username = ((UsernamePasswordCredential) credential).getCaller();
            String password = ((UsernamePasswordCredential) credential).getPasswordAsString();

            LOGGER.log(Level.INFO, "Validate credential {0}:{1}", new Object[]{username, password});

            if (Objects.equals(username, password)) {
                return new CredentialValidationResult(username);
            } else {
                return CredentialValidationResult.INVALID_RESULT;
            }
        } catch (SecurityException e) {
            return CredentialValidationResult.NOT_VALIDATED_RESULT;
        }
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return Collections.singleton(ValidationType.VALIDATE);
    }
}
