package cloud.nativ.javaee;

import javax.ejb.Stateless;
import javax.json.JsonObject;

/**
 * The topic beans listens for {@link JsonObject} CDI events and publishes
 * these via JMS to the jms/MessageEvents topic. We use JSON-P for marshalling
 * the message payload.
 */
@Stateless
public class MessageEventTopic {

    // TODO implement me

}
