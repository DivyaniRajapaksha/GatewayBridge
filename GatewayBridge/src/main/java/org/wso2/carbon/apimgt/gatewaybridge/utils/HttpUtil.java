package org.wso2.carbon.apimgt.gatewaybridge.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import org.wso2.carbon.apimgt.gatewaybridge.listeners.JMSEventListener;
import org.wso2.carbon.apimgt.gatewaybridge.utils.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Class for creating HTTP connection
 */
public final class HttpUtil {
    private static final Log log = LogFactory.getLog(HttpUtil.class);
    private boolean debugEnabled = log.isDebugEnabled();

    public HttpUtil() {
        log.debug("HttpUtilities: ");
    }

    /**
     * Returns a CloseableHttpClient instance
     * This method always returns immediately, whether or not the
     * CloseableHttpClient exists.
     *
     * @return         Executable CloseableHttpClient
     */
    public static CloseableHttpClient getService() {
        try {
            APIUtil apiUtil = new APIUtil();
            HttpClientBuilder httpClientBuilder = HttpClients.custom();
            httpClientBuilder.setSSLSocketFactory(apiUtil.createSocketFactory());
            CloseableHttpClient client = httpClientBuilder.build();
            return client;
        } catch (Exception e) {
            log.debug("HttpException");
        }
        return null;
    }
}
