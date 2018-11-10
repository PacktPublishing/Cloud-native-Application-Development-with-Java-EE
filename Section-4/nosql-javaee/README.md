# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 4.3: Using NoSQL databases with Java EE

Sometimes relational object mapping using JPA is not a good fit for your data model
and you may want to use a NoSQL data store. While there is no dedicated API, you
can easily retrofit the functionality by using CDI, JSON-P or JSON-B in combination
with the native driver like the Mongo DB Java client.

### Step 1: Infrastructure Setup for MongoDB

Add the following definition to your `docker-compose.yml` file.

```yaml
  mongodb:
    image: mongo:3.6
    ports:
    - "27017:27017"
    networks:
    - jee8net
```

### Step 2: CDI Integration of MongoDB Java client

Add the following dependencies for the MongoDB Java Client and the
Morphia object mapper (optional) to the `build.gradle` file.

```groovy
    compile 'org.mongodb:mongo-java-driver:3.6.4'
    compile 'org.mongodb.morphia:morphia:1.3.2'
```

Add the following CDI producer bean to create the MongoDB client and database beans.

```java
@ApplicationScoped
public class MongoProducer {

    private MongoClient mongoClient;
    private CodecRegistry pojoCodecRegistry;

    @PostConstruct
    void initialize() {
        // use MicroProfile Config to externalize these
        mongoClient = new MongoClient("mongodb", 27017);
        pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }

    @Produces
    @ApplicationScoped
    public MongoClient client() {
        return mongoClient;
    }

    @Produces
    @ApplicationScoped
    public MongoDatabase database() {
        return mongoClient.getDatabase("javaee").withCodecRegistry(pojoCodecRegistry);
    }
}
```

### Step 3: Using MongoDB with JSON-P

Since MongoDB stores documents as JSON, the natural fit is to combine it with JSON-P and JAX-RS.
Add the following resource class to query and store documents in Mongo collections.

```java
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
```

### Step 4a: Using MongoDB with Morphia and JSON-B

If you like it more typed using POJOs, you can use the Morphia library (https://github.com/MorphiaOrg/morphia) which gives
you a more JPA like feeling. In combination with JSON-B we can take care of the marshalling in JAX-RS.

We create the following POJO class and annotate it using the Morphia annotations (NOT the JPA equivalient!) and also
JSON-B annotations for correct marshalling.

```java
@Data
@Entity("pojos")
@JsonbPropertyOrder({"description", "name"})
@JsonbNillable
public class MorphiaPojo {

    @Id
    @JsonbTransient
    private ObjectId id;

    @Property
    @JsonbProperty("the-name")
    private String name;

    @Property
    @JsonbProperty
    private String description;
}
```

We create a simple CDI repository bean to handle all the persistence logic.

```java
@ApplicationScoped
public class MorphiaRepository {

    @Inject
    private MongoClient client;

    private Morphia morphia;
    private Datastore datastore;

    @PostConstruct
    void initialize() {
        this.morphia = new Morphia();
        this.datastore = morphia.createDatastore(client, "morphia");
        this.datastore.ensureIndexes();
    }

    public Collection<MorphiaPojo> findAll() {
        final Query<MorphiaPojo> query = datastore.createQuery(MorphiaPojo.class);
        return query.asList();
    }

    public String save(MorphiaPojo payload) {
        Key<MorphiaPojo> saved = datastore.save(payload);
        return ((ObjectId) saved.getId()).toHexString();
    }

    public MorphiaPojo findById(String id) {
        Query<MorphiaPojo> query = datastore.createQuery(MorphiaPojo.class).field("id").equal(new ObjectId(id));
        return Optional.ofNullable(query.get()).orElseThrow(NotFoundException::new);
    }

    public void delete(String id) {
        datastore.delete(MorphiaPojo.class, new ObjectId(id));
    }
}
```

And we create a JAX-RS resource class to provide a REST API for the Morphia repository.

```java
@ApplicationScoped
@Path("morphia")
@Produces(MediaType.APPLICATION_JSON)
public class MorphiaResource {

    @Inject
    private MorphiaRepository repository;

    @GET
    public Response all() {
        Collection<MorphiaPojo> results = repository.findAll();
        return Response.ok(results).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(@NotNull MorphiaPojo payload) {
        String id = repository.save(payload);

        URI location = UriBuilder
                .fromResource(MorphiaResource.class)
                .path("{id}")
                .resolveTemplate("id", id)
                .build();

        return Response.created(location).build();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") String id) {
        MorphiaPojo pojo = repository.findById(id);
        return Response.ok(pojo).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") String id) {
        repository.delete(id);
        return Response.noContent().build();
    }
}
```

### Step 4b: Using MongoDB with JNoSQL and JSON-B

Alternatively, you can use the JNoSql library to mimic the JPA APIs to access any
NoSQL database.
