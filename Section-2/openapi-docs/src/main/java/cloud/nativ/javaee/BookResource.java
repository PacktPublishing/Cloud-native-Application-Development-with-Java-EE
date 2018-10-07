package cloud.nativ.javaee;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The Book REST resource implementation.
 */
@ApplicationScoped
@Path("books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    private Map<String, Book> bookshelf = new HashMap<>();

    @PostConstruct
    @HEAD
    @Operation(summary = "Initialize and reset.", description = "Reset the internal bookshelf to default.")
    @APIResponse(responseCode = "204", description = "No content.")
    public void initialize() {
        bookshelf.put("978-0345391803",
                new Book("978-0345391803", "The Hitchhiker's Guide to the Galaxy"));
    }

    @GET
    @Operation(summary = "Get all books.", description = "Retrieves the list of all books.")
    @APIResponse(responseCode = "200", description = "The list of all books.",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = SchemaType.ARRAY, implementation = Book.class)))
    public Response books() {
        return Response.ok(bookshelf.values()).build();
    }

    @POST
    @Operation(summary = "Create a new book.", description = "Creates a new book.")
    @APIResponse(responseCode = "201", description = "The book has been created.")
    @RequestBody(name = "book", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(ref = "Book")))
    public Response create(@NotNull Book book) {
        bookshelf.putIfAbsent(book.isbn13, book);
        URI location = UriBuilder.fromResource(BookResource.class)
                .path("/{isbn}")
                .resolveTemplate("isbn", book.isbn13)
                .build();
        return Response.created(location).build();
    }

    @GET
    @Path("/{isbn}")
    @Operation(summary = "Get book.", description = "Get a book by ISBN-13.")
    @APIResponse(name = "ok", responseCode = "200", description = "The list of all books.",
            content = @Content(mediaType = "application/json", schema = @Schema(ref = "Book")))
    @APIResponse(responseCode = "404", description = "The book was not found.")
    public Response get(@PathParam("isbn") String isbn) {
        Book book = bookshelf.get(isbn);
        if (book != null) {
            return Response.ok(book).build();
        } else {
            throw new NotFoundException("No book with ISBN-13 " + isbn);
        }
    }

    @PUT
    @Path("/{isbn}")
    @Operation(summary = "Update book.", description = "Update book identified by ISBN-13.")
    @APIResponse(responseCode = "200", description = "Update successful.")
    @APIResponse(responseCode = "400", description = "The ISBN did not match or request was invalid.")
    @RequestBody(name = "book", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(ref = "Book")))
    public Response update(@PathParam("isbn") String isbn, @NotNull Book book) {
        if (!Objects.equals(isbn, book.isbn13)) {
            throw new BadRequestException("ISBN must match path parameter.");
        }
        bookshelf.put(isbn, book);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{isbn}")
    @Operation(summary = "Delete book.", description = "Delete a book identified by ISBN-13.")
    @APIResponse(responseCode = "204", description = "No content.")
    @APIResponse(responseCode = "404", description = "The book was not found.")
    public Response delete(@PathParam("isbn") String isbn) {
        Book book = bookshelf.remove(isbn);
        if (book != null) {
            return Response.noContent().build();
        } else {
            throw new NotFoundException("No book with ISBN-13 " + isbn);
        }
    }
}