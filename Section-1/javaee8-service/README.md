# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 1.4: Getting started with Java EE 8 microservices

Depending on the the language and environment, microservice development can be hard.
But getting started with Java EE 8 microservice development is super easy and straightforward.

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

Every microservice is packaged, deployed and run as a container. Docker currently is the predominant technology to achieve this.

### Step 1: Building and running containerized Java EE 8 microservices locally

Create a new file called `Dockerfile` and add the following content:
```
FROM qaware/zulu-alpine-payara-micro:8u181-5.183

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

FROM qaware/zulu-alpine-payara-micro:8u181-5.183

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

## Video 1.6: Infrastructure Composition

Every microservice needs an ecosystem (database, service discovery, edge serve) to run in.
It is crucial that this environment can be setup and run locally for short development cycles.

### Step 1: Writing a `docker-compose.yml` file for Java EE 8 microservice

Create a new file called `docker-compose.yml` and add the following content:
```yaml
version: "3"

services:
  javaee8-service:
    build:
      context: .
    image: javaee8-service:1.0
    ports:
    - "8080:8080"
    networks:
    - jee8net

networks:
  jee8net:
    driver: bridge
```

### Step 2: Building and running with Docker Compose locally

You can use Docker Compose during the local development, using the following commands:
```
docker-compose build
docker-compose up --build

docker-compose up -d --build
docker ps
docker stats
docker-compose logs -f
```

### Additional infrastructure composition

Add the following YAML to your `docker-compose.yml` to add a message queue and a database:
```yaml
  message-queue:
    image: vromero/activemq-artemis:2.6.1
    environment:
    - ENABLE_JMX_EXPORTER=true
    expose:
    - "61616"       # the JMS port
    - "1883"        # the MQTT port
    - "5672"        # the AMQP port
    ports:
    - "8161:8161"   # the admin web UI
    networks:
    - jee8net

  postgres-db:
    image: "postgres:9.6.3"
    environment:
    - POSTGRES_USER=javaee8
    - POSTGRES_PASSWORD=12qwasyx
    ports:
    - "5432:5432"
    networks:
    - jee8net
```

## Video 1.7: Deploying and Running Java EE on Kubernetes

Manually scheduling and orchestrating multiple Docker containers is complex or even
impossible. Kubernetes is a cluster scheduler and cluster orchestrator, it is the
de-facto standard to run cloud native applications.

### Step 1: Use Docker and Docker Compose to deploy to local Kubernetes

Add the following `deploy` section to each service in your `docker-compose.yml`:
```
deploy:
  replicas: 1
  resources:
    limits:
      memory: 640M
    reservations:
      memory: 640M
```

Then enter the following commands in your console to deploy and run everything:
```
docker stack deploy --compose-file docker-compose.yml javaee8

kubectl get deployments
kubectl get pods
kubectl get services

docker stack rm javaee8
```

### Step 2: Go from Docker Compose to Kubernetes with http://kompose.io

Download the latest release of Kompose from Github and put the binary on your `PATH`.
You may want to modify the conversion using labels, like
```yaml
  labels:
    kompose.service.type: nodeport
```

Then issue the following command to convert the `docker-compose.yml` into Kubernetes YAMLs.
```
kompose convert -f docker-compose.yml -o build/
```

### Step 3: Deploy and Run everything on Kubernetes

Use the generated YAML files to deploy and run everything on Kubernetes.
```
kubectl apply -f src/main/kubernetes/

kubectl get deployments
kubectl get pods
kubectl get services

kubectl rm -f src/main/kubernetes/
```
