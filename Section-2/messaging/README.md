# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 2.5: Building message-driven microservices with Java EE

When building microservice architectures you should strive for loose coupling between
the services by using asynchronous, message driven communication. By using JMS in combination
with JSON-P you can implement flexible messaging logic easily.

### Step 1: Add Message Queue infrastructure

Add the following YAML snippet to your `docker-compose.yml` file to create a message queue.
```yaml
  message-queue:
    image: rmohr/activemq:5.15.6
    expose:
    - "61616"       # the JMS port
    - "1883"        # the MQTT port
    - "5672"        # the AMQP port
    ports:
    - "8161:8161"   # the admin web UI
    networks:
    - jee8net
    labels:
      kompose.service.type: nodeport
    deploy:
      replicas: 1
      resources:
        limits:
          memory: 512M
        reservations:
          memory: 512M
```

### Step 2: Add app server configuration

Next, we need to configure the app server to create the relevant managed objects to connect
to the message queue via JMS. Create a file called `src/main/docker/post-deploy.asadmin`.

```
deploy --type rar --name activemq-rar /opt/payara/deployments/activemq-rar.rar

create-resource-adapter-config --property ServerUrl='tcp://message-queue:61616':UserName='admin':Password='admin' activemq-rar
create-connector-connection-pool --raname activemq-rar --connectiondefinition javax.jms.ConnectionFactory --ping false --isconnectvalidatereq true jms/activeMqConnectionPool
create-connector-resource --poolname jms/activeMqConnectionPool jms/activeMqConnectionFactory
create-admin-object --raname activemq-rar --restype javax.jms.Topic --property PhysicalName=MESSAGE.EVENTS jms/MessageEvents

deploy --type war /opt/payara/deployments/messaging-service.war
```

Modify your `Dockerfile` and copy all relevant files into the image.
```
FROM qaware/zulu-centos-payara-micro:8u181-5.183

CMD ["--postdeploycommandfile", "/opt/payara/post-deploy.asadmin"]

COPY src/main/docker/* /opt/payara/
COPY build/activemq/activemq-rar-5.15.6.rar /opt/payara/deployments/activemq-rar.rar
COPY build/libs/messaging-service.war /opt/payara/deployments/
```

### Step 3: Add JMS topic sender for JSON events

Add the following class to send JSON events to the JMS topic. We use plain TextMessages,
and we set the JMS type as well as contentType message headers.

```java
@Stateless
public class MessageEventTopic {

    private static final Logger LOGGER = Logger.getLogger(MessageEventTopic.class.getName());

    @Resource(lookup = "jms/activeMqConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/MessageEvents")
    private Topic destination;

    public void publish(JsonObject messageEvent) {
        StringWriter payload = new StringWriter();
        Json.createWriter(payload).writeObject(messageEvent);

        try (Connection connection = connectionFactory.createConnection()) {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            producer.setTimeToLive(1000 * 30); // 30 seconds

            TextMessage textMessage = session.createTextMessage(payload.toString());
            textMessage.setJMSType("MessageEvent");
            textMessage.setStringProperty("contentType", "application/vnd.message.v1+json");

            producer.send(textMessage);
            LOGGER.log(Level.INFO, "Sent {0} to MessageEvents destination.", textMessage);
        } catch (JMSException e) {
            LOGGER.log(Level.WARNING, "Could not send JMS message.", e);
        }
    }

    public void observe(@ObservesAsync JsonObject messageEvent) {
        publish(messageEvent);
    }
}
```

### Step 4: Add JMS message driven bean to receive JSON events

Add the following class to implement a simple message driven bean that reads the TextMessage and
uses JSON-P to unmarshall the payload. The `MessageDriven` annotations specify the required
connection properties, and a suitable messageSelector.

```java
@MessageDriven(name = "MessageEventMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/MessageEvents"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "MESSAGE.EVENTS"),
        @ActivationConfigProperty(propertyName = "resourceAdapter", propertyValue = "activemq-rar"),
        @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
        @ActivationConfigProperty(propertyName = "clientId", propertyValue = "messaging-service"),
        @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "MessageEventMDB"),
        @ActivationConfigProperty(propertyName = "messageSelector",
                propertyValue = "(JMSType = 'MessageEvent') AND (contentType = 'application/vnd.message.v1+json')")
})
public class MessageEventMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(MessageEventMDB.class.getName());

    @Override
    public void onMessage(Message message) {
        LOGGER.log(Level.INFO, "Received inbound message {0}.", message);

        String body = getBody(message);
        if (body != null) {
            try (JsonReader reader = Json.createReader(new StringReader(body))) {
                JsonObject jsonObject = reader.readObject();
                LOGGER.log(Level.INFO, "Unmarshalled MessageEvent from {0}.", jsonObject);
            }
        }
    }

    private String getBody(Message message) {
        String body = null;
        try {
            if (message instanceof TextMessage) {
                body = ((TextMessage) message).getText();
            }
        } catch (JMSException e) {
            LOGGER.log(Level.WARNING, "Could not get message body.", e);
        }
        return body;
    }
}
```
