package cloud.nativ.javaee;

import cloud.nativ.javaee.config.Secret;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Provider;

@ApplicationScoped
public class SecretsConfiguration {

    @Inject
    @ConfigProperty(name = "env.user.name")
    private Provider<String> envUsername;

    @Inject
    @ConfigProperty(name = "env.user.password")
    private Provider<String> envPassword;

    @Inject
    @ConfigProperty(name = "secret.user.name")
    private Provider<String> secretUsername;

    @Inject
    @ConfigProperty(name = "secret.user.password")
    private Provider<String> secretPassword;

    @Inject
    @ConfigProperty(name = "a.secret")
    private Secret secret;

    public String getEnvUsername() {
        return envUsername.get();
    }

    public String getEnvPassword() {
        return envPassword.get();
    }

    public String getSecretUsername() {
        return secretUsername.get();
    }

    public String getSecretPassword() {
        return secretPassword.get();
    }

    public Secret getSecret() {
        return secret;
    }
}
