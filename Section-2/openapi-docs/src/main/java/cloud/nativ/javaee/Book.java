package cloud.nativ.javaee;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

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
