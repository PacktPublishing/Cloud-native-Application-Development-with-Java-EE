# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 6.1: Using the Java EE Security APIs with JAX-RS

With Java EE 8 there are some major changes regarding the Security API. It adds easy to use 
default authentication mechanisms (BASIC, Form) as well as the possibility for custom 
authentication mechanisms and pluggable identity store implementations.

### Step 1: Add BASIC authentication to JAX-RS application

First, we add the following annotations to the JAX-RS application class to enable BASIC authentication
as well as declare the used roles.

```java
@ApplicationScoped
@BasicAuthenticationMechanismDefinition(realmName = "security-service")
@DeclareRoles({"admin", "user"})
```

### Step 2: Secure the REST resource using Java EE Security annotations

Now we add the following REST resources to the codebase. We use annotations to specify
the access rights and required roles for the different GET methods. Also we make use of
the the `SecurityContext` to get information about the auth.

```java
@ApplicationScoped
@Path("protected")
public class SecurityResource {

    @Context
    private SecurityContext securityContext;

    @GET
    @PermitAll
    public Response info() {
        if (securityContext.getUserPrincipal() == null) {
            return Response.noContent().build();
        }

        JsonObject jsonObject = getSecurityJsonObject();
        return Response.ok(jsonObject).build();
    }

    @GET
    @Path("all")
    @RolesAllowed({"admin", "user"})
    public Response allRoles() {
        JsonObject jsonObject = getSecurityJsonObject();
        return Response.ok(jsonObject).build();
    }

    @GET
    @Path("admin")
    @RolesAllowed({"admin"})
    public Response adminOnly() {
        JsonObject jsonObject = getSecurityJsonObject();
        return Response.ok(jsonObject).build();
    }

    private JsonObject getSecurityJsonObject() {
        return Json.createObjectBuilder()
                .add("authenticationScheme", securityContext.getAuthenticationScheme())
                .add("secure", securityContext.isSecure())
                .add("userPrincipal", securityContext.getUserPrincipal().getName())
                .add("admin", securityContext.isUserInRole("admin"))
                .build();
    }
}
``` 

### Step 3: Implement custom identity and group store

We want to implement our own simple custom `IdentityStore` implementations to authenticate the
user and obtain the groups for the users. Add the following class to implement each store.

```java
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
```

```java
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
```

### Step 4: Perform authenticated and unauthenticated REST calls

Once you compiled and deployed the service, use a REST client to invoke the REST endpoints with
and without credentials.

```
$ curl http://localhost:8080/api/protected
$ curl --basic --user user:user http://localhost:8080/api/protected

$ curl --basic --user user:user http://localhost:8080/api/protected/all
$ curl --basic --user user:wrongpwd http://localhost:8080/api/protected/all
$ curl --basic --user user:user http://localhost:8080/api/protected/admin

$ curl --basic --user admin:admin http://localhost:8080/api/protected/admin
``` 