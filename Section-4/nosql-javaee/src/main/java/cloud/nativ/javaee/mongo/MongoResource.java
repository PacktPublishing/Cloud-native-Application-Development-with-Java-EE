package cloud.nativ.javaee.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@Path("mongo")
@Produces(MediaType.APPLICATION_JSON)
public class MongoResource {

    @Inject
    private MongoDatabase database;

    @GET
    @Path("{collection}")
    public Response all(@PathParam("collection") String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        MongoCursor<Document> cursor = collection.find().iterator();
        JsonArrayBuilder response = Json.createArrayBuilder();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                response.add(Json.createObjectBuilder(document));
            }
        } finally {
            cursor.close();
        }

        return Response.ok(response.build()).build();
    }

    @POST
    @Path("{collection}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertOne(@PathParam("collection") String collectionName, @NotNull String jsonPayload) {
        MongoCollection<Document> collection = database.getCollection(collectionName);

        Document document = Document.parse(jsonPayload).append("_id", UUID.randomUUID().toString());
        collection.insertOne(document);

        URI location = UriBuilder
                .fromResource(MongoResource.class)
                .path("{collection}/{id}")
                .resolveTemplate("collection", collectionName)
                .resolveTemplate("id", document.getString("_id"))
                .build();

        return Response.created(location).build();
    }

    @GET
    @Path("{collection}/{id}")
    public Response get(@PathParam("collection") String collectionName, @PathParam("id") String id) {
        MongoCollection<Document> collection = database.getCollection(collectionName);

        MongoIterable<Document> documents = collection.find(new Document("_id", id));
        Document found = Optional.ofNullable(documents.first()).orElseThrow(NotFoundException::new);

        return Response.ok(found.toJson()).build();
    }
}
