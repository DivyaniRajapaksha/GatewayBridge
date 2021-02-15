package org.wso2.carbon.apimgt.gatewaybridge.listeners;


import org.wso2.carbon.apimgt.gatewayBridge.apiretriever.*;
import org.wso2.carbon.apimgt.gatewayBridge.constants.APIConstants;
import org.wso2.carbon.apimgt.gatewayBridge.dto.GatewayAPIDTO;
import org.wso2.carbon.apimgt.gatewayBridge.models.DeployAPIInGatewayEvent;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Class for listening to the JMS Events for a specific topic
 */
public class JMSEventListener implements MessageListener {
    private static final Log log = LogFactory.getLog(JMSEventListener.class);
    private boolean debugEnabled = log.isDebugEnabled();

    ArtifactRetriever artifactRetriever = new DBRetriever();

    /**
     * This constructs a JMSEventListener instance
     */
    public JMSEventListener() {
        if (log.isDebugEnabled()) {
            log.debug("Called JMSEventListener");
        }
    }

    /**
     * Listens to JMS events
     * This method retrieves the events published to a specifc topic.
     * Retrieved message is decoded and extract
     * the event details.
     * <p>
     * This method in retrieves the gateway run time artifatcs
     *
     * @param message JMS message received from the topic
     */
    public void onMessage(Message message) {

        log.debug("Event Received: " + message);

        try {
            if (message instanceof MapMessage) {
                MapMessage mapMessage = (MapMessage) message;
                Map<String, Object> map = new HashMap<String, Object>();
                Enumeration enumeration = mapMessage.getMapNames();
                while (enumeration.hasMoreElements()) {
                    String key = (String) enumeration.nextElement();
                    map.put(key, mapMessage.getObject(key));
                }
                byte[] eventDecoded = Base64.decodeBase64((String) map.get("event"));
                DeployAPIInGatewayEvent gatewayEvent = new Gson().fromJson(new String(eventDecoded), DeployAPIInGatewayEvent.class);


                if ((APIConstants.EventType.DEPLOY_API_IN_GATEWAY.name().equals((String) map.get("eventType")))) {

                    log.debug("Gatewaylabels" + gatewayEvent.getGatewayLabels());

                    String gatewayLabel = gatewayEvent.getGatewayLabels().iterator().next();
                    String gatewayRuntimeArtifact = artifactRetriever.retrieveArtifact(gatewayEvent.getApiId(), gatewayLabel, "Publish");
                    if (StringUtils.isNotEmpty(gatewayRuntimeArtifact)) {
                        GatewayAPIDTO gatewayAPIDTO = new Gson().fromJson(gatewayRuntimeArtifact, GatewayAPIDTO.class);
                        log.debug("GatewayAPIDTO    :" + gatewayAPIDTO);
                        log.debug("GatewayAPIDTO Name       :" + gatewayAPIDTO.getName());
                    }

                }

            }
        } catch (Exception e) {
            log.debug("Exception" + e);
        }
    }

    /**
     * Subscribe to a topic at the startup
     * Creates a non-durable topic consumer.
     * <p>
     * This method subscribe to a created topic and
     * listens to the topic.
     *
     * @throws NamingException      If an authentication error occurs while accessing the JNDI naming service.
     * @throws JMSException         If a JMS services error occurs while using JMS service.
     * @throws InterruptedException If an error occurs while executing the thread.
     * @throws IOException          If an error occurs while perorming I/O operations.
     */
    public void setSubscriber() throws NamingException, JMSException, InterruptedException, IOException {
        Properties properties = new Properties();
        properties.put("java.naming.factory.initial", "org.wso2.andes.jndi.PropertiesFileInitialContextFactory");
        properties.setProperty("connectionfactory.TopicConnectionFactory", "amqp://admin:admin@clientid/carbon?brokerlist='tcp://localhost:5672?retries='5'%26connectdelay='50';tcp://localhost:5672?retries='5'%26connectdelay='50';'");
        properties.setProperty("topic.notification", "notification");
        Context context = new InitialContext(properties);

        ConnectionFactory connectionFactory
                = (ConnectionFactory) context.lookup("TopicConnectionFactory");
        Connection connection = connectionFactory.createConnection("admin", "admin");
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topicLabel = (Topic) context.lookup("notification");

        MessageConsumer subscriber1 = ((TopicSession) session).createSubscriber(topicLabel);

        subscriber1.setMessageListener(this);

        log.debug("Listening to the Topic" + topicLabel);
        Thread.sleep(10000);


    }
}
