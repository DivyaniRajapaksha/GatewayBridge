package org.wso2.carbon.apimgt.gatewayBridge;

import org.wso2.carbon.apimgt.gatewayBridge.listeners.JMSEventListener;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.io.IOException;

public class GatewayBridge {
    public static void main(String[] args) throws InterruptedException, IOException, JMSException, NamingException {

        JMSEventListener jmsEventListener = new JMSEventListener();
        jmsEventListener.setSubscriber();
    }

}
