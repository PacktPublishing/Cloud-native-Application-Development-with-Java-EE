# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 2.4: API documentation with MicroProfile Open API

### Step 1: Add MicroProfile Open API dependency

```groovy
dependencies {
    providedCompile 'org.eclipse.microprofile.openapi:microprofile-openapi-api:1.0.1'
}
```

### Step 2: Add OpenAPI definition

Add a `package-info.java` to the root package and add the following `@OpenAPIDefinition` annotation.

```java
@OpenAPIDefinition(
        info = @Info(title = "Cloud-native Applications with Java EE 8",
                contact = @Contact(name = "M.-Leander Reimer", email = "mario-leander.reimer@qaware.de"),
                license = @License(name = "MIT"),
                version = "1.0.0"),
        tags = {
                @Tag(name = "Java EE 8"),
                @Tag(name = "Eclipse MicroProfile")
        },
        servers = {
                @Server(url = "http://localhost:8080/api/")
        },
        externalDocs = @ExternalDocumentation(url = "www.google.com", description = "Use Google for external documentation")
)
```

