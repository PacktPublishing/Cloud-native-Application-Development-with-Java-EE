# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 6.2: Using JWT based authentication and authorization with JAX-RS

With the Microprofile JWT Authentication API there now is the option to easily secure your REST API
using a JSON Web Token (JWT) in combination with Java EE managed security.

### Step 1: Add MicroProfile JWT Authentication dependency

First, we need to add the MicroProfile JWT Authenication dependency to the `build.gradle` file.

```groovy
    providedCompile 'org.eclipse.microprofile.jwt:microprofile-jwt-auth-api:1.1'
```

### Step 2: Add JWT LoginConfig to JAX-RS application

Next, we add the following annotations to the JAX-RS application class to enable JWT authentication
as well as declare the used roles.

```java
@ApplicationScoped
@LoginConfig(authMethod = "MP-JWT", realmName = "MP-JWT")
@DeclareRoles({"admin", "user"})
```

Also we need to configure the MicroProfile JWT API. This will be dependent on the application
server you are using.

1. Add the Public Key certificate for signature validation (see `src/main/resources/publicKey.pem`)
2. Specify the accepted issuer claim for any JSON web tokens (see `src/main/resources/payara-mp-jwt.properties`)

### Step 3: Secure the REST resource using Java EE Security annotations

Now we add the following REST resources to the codebase. We use annotations to specify
the access rights and required roles for the different GET methods. Also we inject the
`JsonWebToken` principal to get information about the JWT claims.

```java
@ApplicationScoped
@Path("protected")
public class JwtAuthzResource {

    @Inject
    private JsonWebToken jsonWebToken;

    @GET
    @PermitAll
    public Response info() {
        if (jsonWebToken == null || jsonWebToken.getName() == null) {
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
                .add("name", jsonWebToken.getName())
                .add("issuer", jsonWebToken.getIssuer())
                .add("audience", Json.createArrayBuilder(jsonWebToken.getAudience()))
                .add("groups", Json.createArrayBuilder(jsonWebToken.getGroups()))
                .add("subject", jsonWebToken.getSubject())
                .build();
    }
}
```

### Step 4: Perform authenticated and unauthenticated REST calls

Once you compiled and deployed the service, use a REST client to invoke the REST endpoints with
and without JWTs.

For User access use this JWT *(valid for one year)*.
```
eyJraWQiOiJcL3ByaXZhdGVLZXkucGVtIiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJNUC1KV1QgRGVtbyIsImF1ZCI6IlBhY2t0UHVibGlzaGluZyIsInVwbiI6ImFkbWluIiwiYXV0aF90aW1lIjoxNTQxNTA3NjEwLCJpc3MiOiJjbG91ZC5uYXRpdi5qYXZhZWUiLCJncm91cHMiOlsidXNlciJdLCJleHAiOjE1NzMwNDM2MTAsImlhdCI6MTU0MTUwNzYxMCwianRpIjoiYS0xMjMifQ.LAt4rj6B7J28AdUfILrn2P4cxtx9VraXiQDQVquoGJ7zEEpSb8FoBhHTGIob2Zaxo0-JRMk9tP_M0JPYoU-7xekXJp4AvUjjLE-hKDeG8gQ-9ABWK8Csq2oYcfw4CATIlkssYmx93_BSqueo5_pFNLScAQT3mLj5ywK3gqybLeDpLIYlV2oyUg9IsxxBXpnZXU7uxkcsz6qm3PEriIJ8DkBhS6T6pcJ4wfDSYEjPg7FF8gPhYtBBxOLWUXQ7fzqquiO5FASw578yxrY_QIDgRhsuNFZrQ_tKkxHN7Be5FZKXLDg0ZCR3YuIEpBONgSat-clFVTJt0MZfIUHmqxJ9QQ
```

For Admin only access use this JWT *(valid for one year)*.
```
eyJraWQiOiJcL3ByaXZhdGVLZXkucGVtIiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJNUC1KV1QgRGVtbyIsImF1ZCI6IlBhY2t0UHVibGlzaGluZyIsInVwbiI6ImFkbWluIiwiYXV0aF90aW1lIjoxNTQxNTA3Mzk5LCJpc3MiOiJjbG91ZC5uYXRpdi5qYXZhZWUiLCJncm91cHMiOlsiYWRtaW4iLCJ1c2VyIl0sImV4cCI6MTU3MzA0MzM5OSwiaWF0IjoxNTQxNTA3Mzk5LCJqdGkiOiJhLTEyMyJ9.LuTL8ulLyN6ehF6oevhki6ep4piBeW8a4BD9M96aqIwWQTOeq6vLm8LUdcbzL6eVFZ1k2Nixm6bZZQjMSfZe0HonEIgubvrQk4G7u7XiEWdjZbW5_m_eEhnQBjHINlMao9YJ1TT2IqtXnJYCf5vwkOuXNtsVI6Woe0u6mQLIZ0orhfY6j_1qqY18WR1Kl4lFavHh6YEwHLL19YiNrbp7d3Qgu__tdowyHSt63s2wUs7YBsdLuqmcMZi81rMYaUNYetjORN61B0F1SM-0QZQl3uD2NOHkfqiiJYHznW2pdBw9v3KgjauqJKAVFisKk-90DC7_TLWLXBVctCtnqdAE5Q
```

