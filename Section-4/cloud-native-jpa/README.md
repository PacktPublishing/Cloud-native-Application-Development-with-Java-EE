# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 4.2: Using JPA with Cloud-native databases

In Java EE the API for objection relational mapping is JPA. It can be used to
store relational as well as JSON data in a cloud native databases such as CockroachDB
by simply combining JPA with JSON-P.

### Step 1: Database Infrastructure Setup

Add the following definition to your `docker-compose.yml` file.

```yaml
  cockroach1:
    image: cockroachdb/cockroach:v2.0.5
    hostname: cockroach1
    command: start --insecure
    ports:
    - "26257:26257"
    - "8080:8080"
    volumes:
    - data-volume1:/cockroach/cockroach-data
    networks:
      jee8net:
        aliases:
        - cockroach-db
        - postgres-db

  cockroach2:
    image: cockroachdb/cockroach:v2.0.5
    hostname: cockroach2
    command: start --insecure --join=cockroach1
    volumes:
    - data-volume2:/cockroach/cockroach-data
    depends_on:
    - cockroach1
    links:
    - cockroach1
    networks:
    - jee8net

  cockroach3:
    image: cockroachdb/cockroach:v2.0.5
    hostname: cockroach3
    command: start --insecure --join=cockroach1
    volumes:
    - data-volume3:/cockroach/cockroach-data
    depends_on:
    - cockroach1
    links:
    - cockroach1
    networks:
    - jee8net
```

### Step 2: App Server Container Configuration

Next we will add the Postgres JDBC driver to our Gradle build.
```groovy
configurations {
    postgresql {
        description = "PostgreSQL dependencies"
        transitive = true
    }

    providedCompile.extendsFrom postgresql
}

dependencies {
    providedCompile 'javax:javaee-api:8.0'

    postgresql 'org.postgresql:postgresql:42.2.5'
}

task copyPostgresqlLibs(type: Copy) {
    from configurations.postgresql
    into "$buildDir/postgresql"
}

assemble.dependsOn copyPostgresqlLibs
```

We need to configure our Payara app server to create a JDBC resource. Edit the
file `post-deploy.asadmin` with the following content.
```
create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGConnectionPoolDataSource --restype javax.sql.ConnectionPoolDataSource --property portNumber=26257:password='root':user='root':serverName=cockroach-db:databaseName='cloud_native_db' PostgresPool
create-jdbc-resource --connectionpoolid PostgresPool jdbc/CloudNativeDb

set resources.jdbc-connection-pool.PostgresPool.connection-validation-method=custom-validation
set resources.jdbc-connection-pool.PostgresPool.validation-classname=org.glassfish.api.jdbc.validation.PostgresConnectionValidation
set resources.jdbc-connection-pool.PostgresPool.is-connection-validation-required=true
set resources.jdbc-connection-pool.PostgresPool.fail-all-connections=true

deploy --type war /opt/payara/deployments/cloud-native-jpa.war
```

Now edit the `Dockerfile` to add all artifacts to the image and change the CMD.
```
FROM qaware/zulu-centos-payara-micro:8u181-5.183

COPY post-deploy.asadmin /opt/payara/post-deploy.asadmin
COPY build/postgresql/* /opt/payara/libs/
COPY build/libs/cloud-native-jpa.war /opt/payara/deployments/

CMD ["--noCluster", "--addjars", "/opt/payara/libs/", "--postdeploycommandfile", "/opt/payara/post-deploy.asadmin"]
```

### Step 3: Add JPA persistence for CloudNativeEvents

In this step we want to store simple events with flexible JSON payloads. Create the following JPA entity.  

```java
@Entity
@Table(name = "cloud_native_event")
@NoArgsConstructor
@Data
public class CloudNativeEvent {
    @Id
    @SequenceGenerator(name = "cloud_native_event_seq_gen", sequenceName = "cloud_native_event_seq", allocationSize = 5)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cloud_native_event_seq_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "payload", columnDefinition = "jsonb")
    @Convert(converter = JsonObjectConverter.class)
    private JsonObject payload;

    @Column(name = "stored_at")
    private OffsetDateTime storedAt;

    protected CloudNativeEvent(JsonObject payload) {
        this.payload = payload;
    }

    @PrePersist
    protected void onCreate() {
        storedAt = OffsetDateTime.now();
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("id", id)
                .add("stored_at", storedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .add("payload", payload)
                .build();
    }
}
```

Now create a simple data access object to store and retrieve the `CloudNativeEvent` entities.
```java
@Stateless
@Transactional(Transactional.TxType.REQUIRED)
public class CloudNativeEventStorage {

    @PersistenceContext
    private EntityManager entityManager;

    public CloudNativeEvent get(Long id) {
        return Optional.ofNullable(entityManager.find(CloudNativeEvent.class, id)).orElseThrow(NotFoundException::new);
    }

    public Collection<CloudNativeEvent> all() {
        return entityManager.createQuery("SELECT e FROM CloudNativeEvent e", CloudNativeEvent.class).getResultList();
    }

    public CloudNativeEvent persist(JsonObject payload) {
        return entityManager.merge(new CloudNativeEvent(payload));
    }

}
```

We also need to configure the persistence unit, so add the following content to the `persistence.xml` file.
```xml
<persistence version="2.2"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">

    <persistence-unit name="cloudNativeDb" transaction-type="JTA">
        <jta-data-source>jdbc/CloudNativeDb</jta-data-source>

        <class>cloud.nativ.javaee.CloudNativeEvent</class>
        <exclude-unlisted-classes>false</exclude-unlisted-classes>

        <!-- Disable share cache -->
        <shared-cache-mode>NONE</shared-cache-mode>

        <!--
            Alternatively use Payara Cache coordination
            https://payara.gitbooks.io/payara-server/documentation/payara-server/jpa-cache-coordination.html
        -->

        <properties>
            <property name="javax.persistence.schema-generation.database.action" value="drop-and-create"/>
            <property name="javax.persistence.schema-generation.create-source" value="script-then-metadata"/>
            <property name="javax.persistence.schema-generation.create-script-source" value="META-INF/create.sql"/>

            <property name="javax.persistence.sql-load-script-source" value="META-INF/cloud-native-db.sql"/>

            <property name="eclipselink.logging.level" value="FINE"/>
            <property name="eclipselink.logging.parameters" value="true"/>
        </properties>
    </persistence-unit>

</persistence>
```
