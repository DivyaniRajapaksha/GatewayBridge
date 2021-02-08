package com.gatewayBridge.listeners;


import com.gatewayBridge.apiRetriever.*;
import com.gatewayBridge.constants.APIConstants;
import com.gatewayBridge.dto.GatewayAPIDTO;
import com.gatewayBridge.models.DeployAPIInGatewayEvent;
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

    public JMSEventListener() {
        if (log.isDebugEnabled()) {
            log.debug("Called JMSEventListener");
        }
    }

    /**
     * Method used to retrieve events and invoke apiRetriever
     *
     * @return
     * @throws NamingException
     * @throws JMSException
     * @throws InterruptedException
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
                        log.debug("GatewayAPIDTO" + gatewayAPIDTO);
                    }

                }

            }
        } catch (Exception e) {
            log.debug("Exception" + e);
        }
    }

    /**
     * Method used to subscribe to a specific topic at the startup and listens to the topic
     *
     * @return
     * @throws NamingException
     * @throws JMSException
     * @throws InterruptedException
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
