package cloud.nativ.javaee;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The message driven bean listener for MessageEvent messages. We use JSON-P for unmarshalling
 * the message payload.
 */
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
