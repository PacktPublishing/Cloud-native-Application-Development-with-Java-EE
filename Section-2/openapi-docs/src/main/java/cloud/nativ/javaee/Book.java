package cloud.nativ.javaee;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class Book {

    @JsonbProperty("isbn-13")
    public String isbn13;

    public String title;

    @JsonbCreator
    public Book(@JsonbProperty("isbn-13") String isbn13, @JsonbProperty("title") String title) {
        this.isbn13 = isbn13;
        this.title = title;
    }
}
