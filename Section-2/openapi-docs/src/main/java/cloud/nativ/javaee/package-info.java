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
package cloud.nativ.javaee;

import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;