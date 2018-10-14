# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 4.5: Distributed state using the JCache API

### Step 1: Infrastructure setup

We are going to simulate a clustered environment by running the same microservice
twice. Add the following to your `docker-compose.yml`

```yaml
  jcache-api-1:
    build:
      context: .
    image: jcache-api:1.0.1
    ports:
    - "18080:8080"
    networks:
    - jee8net
  
  jcache-api-2:
    image: jcache-api:1.0.1
    ports:
    - "28080:8080"
    networks:
    - jee8net
```

### Step 2: Add JCache API dependency

Add the following dependency to the `build.gradle` file to use the JCache APIs.

```groovy
providedCompile 'javax.cache:cache-api:1.0.0'
```

### Step 3: Configure the Hazelcast JCache provider

To configure the Hazelcast JCache provider, add the following cache definition to the `src/main/docker/hazelcast.xml` file:
```xml
<cache name="replicatedCache">
    <key-type class-name="java.lang.Object"/>
    <value-type class-name="java.lang.String"/>

    <backup-count>1</backup-count>
    <async-backup-count>0</async-backup-count>

    <expiry-policy-factory>
        <timed-expiry-policy-factory expiry-policy-type="CREATED" duration-amount="30" time-unit="SECONDS"/>
    </expiry-policy-factory>
</cache>
```

Add the following to your `Dockerfile` to copy the relevant configuration files and
to start the Payara app server with the relevant CMD parameters.

```
FROM qaware/zulu-centos-payara-micro:8u181-5.183

CMD ["--hzconfigfile", "/opt/payara/hazelcast.xml", "--deploymentDir", "/opt/payara/deployments"]

COPY src/main/docker/* /opt/payara/
COPY build/libs/jcache-api.war /opt/payara/deployments/
```

### Step 4: 