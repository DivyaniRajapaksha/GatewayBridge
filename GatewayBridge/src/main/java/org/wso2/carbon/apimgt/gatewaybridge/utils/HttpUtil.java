package org.wso2.carbon.apimgt.gatewaybridge.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

/**
 * Class for creating HTTP connection
 */
public final class HttpUtil {
    private static final Log log = LogFactory.getLog(HttpUtil.class);

    public HttpUtil() {
        log.debug("HttpUtilities: ");
    }

    /**
     * Returns a CloseableHttpClient instance
     * This method always returns immediately, whether or not the
     * CloseableHttpClient exists.
     * @return an executable CloseableHttpClient
     */
    public static CloseableHttpClient getService() {
        try {
            APIUtil apiUtil = new APIUtil();
            HttpClientBuilder httpClientBuilder = HttpClients.custom();
            httpClientBuilder.setSSLSocketFactory(apiUtil.createSocketFactory());
            return httpClientBuilder.build();
        } catch (Exception e) {
            log.debug("HttpException");
        }
        return null;
    }
}
