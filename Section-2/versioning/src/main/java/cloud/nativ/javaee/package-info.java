@OpenAPIDefinition(
        info = @Info(title = "Java EE 8 Microservice API",
                contact = @Contact(name = "M.-Leander Reimer", email = "mario-leander.reimer@qaware.de"),
                license = @License(name = "MIT"),
                version = "1.0.0"),
        tags = {
                @Tag(name = "Java EE 8"),
                @Tag(name = "Eclipse MicroProfile")
        },
        servers = {
                @Server(url = "localhost:8080/api/")
        },
        externalDocs = @ExternalDocumentation(url = "www.google.com", description = "Use Google for external documentation")
)
package cloud.nativ.javaee;

