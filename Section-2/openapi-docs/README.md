# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 2.4: API documentation with MicroProfile Open API

For REST APIs to be easily consumable by clients they should provide good documentation.
The OpenAPI Specification (OAS) defines a standard, language-agnostic interface to
RESTful APIs which allows both humans and computers to discover and understand the
capabilities of the service.

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

### Step 3: Add OpenAPI definitions to REST schema

Add the following annotations to the `Book` schema type.
```java
@Schema(name = "Book", description = "POJO that represents a book.")
public class Book {

    @Schema(required = true, example = "978-0345391803")
    @JsonbProperty("isbn-13")
    public String isbn13;

    @Schema(required = true, example = "The Hitchhiker's Guide to the Galaxy")
    public String title;

    @JsonbCreator
    public Book(@JsonbProperty("isbn-13") String isbn13, @JsonbProperty("title") String title) {
        this.isbn13 = isbn13;
        this.title = title;
    }
}
```

### Step 4: Add OpenAPI definitions to REST operations

Add the following annotations to the `books()` method.
```java
    @Operation(summary = "Get all books.", description = "Retrieves the list of all books.")
    @APIResponse(responseCode = "200", description = "The list of all books.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = SchemaType.ARRAY, implementation = Book.class)))
```

Add the following annotations to the `create()` method.
```java
    @Operation(summary = "Create a new book.", description = "Creates a new book.")
    @APIResponse(responseCode = "201", description = "The book has been created.")
    @RequestBody(name = "book", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(ref = "Book")))
```

Add the following annotations to the `get()` method.
```java
    @Operation(summary = "Get book.", description = "Get a book by ISBN-13.")
    @APIResponse(name = "ok", responseCode = "200", description = "The list of all books.",
            content = @Content(mediaType = "application/json", schema = @Schema(ref = "Book")))
    @APIResponse(responseCode = "404", description = "The book was not found.")
```

Add the following annotations to the `update()` method.
```java
    @Operation(summary = "Update book.", description = "Update book identified by ISBN-13.")
    @APIResponse(responseCode = "200", description = "Update successful.")
    @APIResponse(responseCode = "400", description = "The ISBN did not match or request was invalid.")
    @RequestBody(name = "book", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(ref = "Book")))
```

Add the following annotations to the `delete()` method.
```java
    @Operation(summary = "Delete book.", description = "Delete a book identified by ISBN-13.")
    @APIResponse(responseCode = "204", description = "Delete successful.")
    @APIResponse(responseCode = "404", description = "The book was not found.")
```
