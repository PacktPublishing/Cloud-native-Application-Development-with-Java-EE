package cloud.nativ.javaee;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.event.ObservesAsync;
import javax.jms.*;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The topic beans listens for {@link JsonObject} CDI events and publishes
 * these via JMS to the jms/MessageEvents topic. We use JSON-P for marshalling
 * the message payload.
 */
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
