package cloud.nativ.javaee;

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
@Path("books")
@ApplicationScoped
public class BookResource {

    private Map<String, Book> bookshelf = new HashMap<>();

    @PostConstruct
    @HEAD
    public void initialize() {
        bookshelf.put("978-0345391803",
                new Book("978-0345391803", "The Hitchhiker's Guide to the Galaxy"));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response books() {
        return Response.ok(bookshelf.values()).build();
    }

    @POST
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
    public Response update(@PathParam("isbn") String isbn, @NotNull Book book) {
        if (!Objects.equals(isbn, book.isbn13)) {
            throw new BadRequestException("ISBN must match path parameter.");
        }
        bookshelf.put(isbn, book);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{isbn}")
    public Response delete(@PathParam("isbn") String isbn) {
        Book book = bookshelf.remove(isbn);
        if (book != null) {
            return Response.ok().build();
        } else {
            throw new NotFoundException("No book with ISBN-13 " + isbn);
        }
    }
}