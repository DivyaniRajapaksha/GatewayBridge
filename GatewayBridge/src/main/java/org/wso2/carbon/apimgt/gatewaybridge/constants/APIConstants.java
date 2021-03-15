package org.wso2.carbon.apimgt.gatewaybridge.constants;


/**
 * This class represents the constants that are used for APIManager implementation
 */
public final class APIConstants {


    // Supported Event Types
    public enum EventType {

        DEPLOY_API_IN_GATEWAY
    }
    public static final String CHARSET = "UTF-8";
    public static final String SYNAPSE_ARTIFACTS = "/synapse-artifacts";
    public static final String INTERNAL_WEB_APP_EP = "/internal/data/v1";
    public static final String AUTHORIZATION_HEADER_DEFAULT = "Authorization";
    public static final String AUTHORIZATION_BASIC = "Basic ";
    public static final String EVENT = "event";
    public static final String EVENT_TYPE = "eventType";
    public static final String GATEWAY_INSTRUCTION_PUBLISH = "Publish";
    public static final String TOPIC_CONNECTION_FACTORY = "TopicConnectionFactory";

    public static final String DELEM_COLON = ":";


}