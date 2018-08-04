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


## Video 1.5: Containerizing Java EE 8 microservices

### Step 1: Building and running containerized Java EE 8 microservices locally

Create a new file called `Dockerfile` and add the following content:
```
FROM qaware/zulu-alpine-payara-micro:8u181-5.182

COPY build/libs/javaee8-service.war /opt/payara/deployments/
```

Then issue the following commands to build and run the image.
```
docker build -t javaee8-service:1.0 .
docker run -it -p 8080:8080 javaee8-service:1.0
```

### Step 2: Using multi-stage Docker builds for Java EE 8 microservices

You can use Docker to build your service when building the images. This maybe useful in containerized CI environments.
Create a new file called `Builderfile` and add the following content:
```
FROM azul/zulu-openjdk:8u181 as builder

RUN mkdir /codebase
COPY . /codebase/

WORKDIR /codebase
RUN ./gradlew build

FROM qaware/zulu-alpine-payara-micro:8u181-5.182

COPY --from=builder /codebase/build/libs/javaee8-service.war /opt/payara/deployments/
```

Then issue the following command to build and run the image.
```
docker build -t javaee8-service:1.1 -f Builderfile .
docker run -it -p 8080:8080 javaee8-service:1.1
```

### Step 3: Tuning the JVM to run in a containerized environment

Careful when putting the JVM into a container. You may have to tune it for for JVMs prior to JDK10.
The following `ENTRYPOINT` shows some of the parameters.
```
ENTRYPOINT ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:MaxRAMFraction=3", "-XX:ThreadStackSize=256", "-XX:MaxMetaspaceSize=128m", "-XX:+UseG1GC", "-XX:ParallelGCThreads=2", "-XX:CICompilerCount=2", "-XX:+UseStringDeduplication", "-jar", "/opt/payara/payara-micro.jar"]
CMD ["--deploymentDir", "/opt/payara/deployments"]
```

