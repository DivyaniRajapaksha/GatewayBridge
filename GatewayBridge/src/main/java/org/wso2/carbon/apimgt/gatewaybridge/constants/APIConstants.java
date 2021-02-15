package org.wso2.carbon.apimgt.gatewaybridge.constants;


import javax.xml.namespace.QName;
import java.io.File;
import java.util.*;

/**
 * This class represents the constants that are used for APIManager implementation
 */
public final class APIConstants {




    // Supported Event Types
    public enum EventType {
        API_CREATE,
        API_UPDATE,
        API_DELETE,
        API_LIFECYCLE_CHANGE,
        APPLICATION_CREATE,
        APPLICATION_UPDATE,
        APPLICATION_DELETE,
        APPLICATION_REGISTRATION_CREATE,
        POLICY_CREATE,
        POLICY_UPDATE,
        POLICY_DELETE,
        SUBSCRIPTIONS_CREATE,
        SUBSCRIPTIONS_UPDATE,
        SUBSCRIPTIONS_DELETE,
        DEPLOY_API_IN_GATEWAY,
        REMOVE_API_FROM_GATEWAY,
        REMOVE_APPLICATION_KEYMAPPING,
        SCOPE_CREATE,
        SCOPE_UPDATE,
        SCOPE_DELETE
    }


}
