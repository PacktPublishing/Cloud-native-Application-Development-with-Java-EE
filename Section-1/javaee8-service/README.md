# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 1.4: Getting started with Java EE 8 microservices

### Step 1a: Build and dependency setup for Gradle

Create a `build.gradle` file, apply the WAR plugin and add the Java EE 8
dependency (see https://mvnrepository.com/artifact/javax/javaee-api/8.0).
This is what the final result should look like:
```groovy
plugins {
    id 'war'
}

repositories { jcenter() }

dependencies {
    providedCompile 'javax:javaee-api:8.0'
}
```

### Step 1b: Build and dependency setup for Maven

In case you prefer Maven as build tool, create a simple project with
packaging WAR. Go to https://mvnrepository.com/artifact/javax/javaee-api/8.0
and insert the dependency definition into Maven `pom.xml` file.
```xml
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-api</artifactId>
    <version>8.0</version>
    <scope>provided</scope>
</dependency>
```

### Step 2: Implement simple JAX-RS application and REST resource

First, create the JAX-RS application class and add the `@ApplicationPath` annotation.
```java
@ApplicationPath("api")
public class JAXRSConfiguration extends Application {
}
```

Next, create a resource class for your Hello REST endpoint.
```java
@Path("hello")
public class HelloWorldResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject helloWorld() {
        String hostname = ofNullable(getenv("HOSTNAME")).orElse("localhost");
        return Json.createObjectBuilder()
                .add("message", "Cloud Native Application Development with Java EE.")
                .add("hostname", hostname)
                .build();
    }
}
```

### Step 3: Local deployment with Payara 5

Build everything using `gradlew assemble` and use your IDE or the app server management console to deploy the WAR file.
The REST API should be accessible under http://localhost:8080/javaee8-service/api/hello
