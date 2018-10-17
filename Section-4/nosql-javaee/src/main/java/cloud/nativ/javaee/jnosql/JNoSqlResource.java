package cloud.nativ.javaee.jnosql;

import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.diana.api.document.DocumentQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.select;

@ApplicationScoped
@Path("jnosql")
@Produces(MediaType.APPLICATION_JSON)
public class JNoSqlResource {
    @Inject
    @Database(DatabaseType.DOCUMENT)
    private PersonRepository repository;

    @Inject
    private DocumentTemplate template;

    @GET
    public Response all() {
        DocumentQuery persons = select().from("Person").build();
        List<Person> results = template.select(persons);
        return Response.ok(results).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(@NotNull Person payload) {
        Person p = repository.save(payload);

        URI location = UriBuilder
                .fromResource(JNoSqlResource.class)
                .path("{id}")
                .resolveTemplate("id", p.getId())
                .build();

        return Response.created(location).build();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") long id) {
        Optional<Person> pojo = repository.findById(id);
        return Response.ok(pojo.orElseThrow(NotFoundException::new)).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id) {
        repository.deleteById(id);
        return Response.noContent().build();
    }
}
