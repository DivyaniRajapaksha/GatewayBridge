package org.wso2.carbon.apimgt.gatewaybridge.apiretriever;

import org.wso2.carbon.apimgt.gatewaybridge.listeners.JMSEventListener;
import org.wso2.carbon.apimgt.gatewaybridge.utils.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Class for retrieving specific api details
 */
public class DBRetriever implements ArtifactRetriever {

    private static final Log log = LogFactory.getLog(JMSEventListener.class);
    private boolean debugEnabled = log.isDebugEnabled();
    private String baseURL = "https://localhost:9443" + "/internal/data/v1";

    /**
     * This method is used to retrieve data from the storage
     *
     * @param APIId                 UUID of the API
     * @param gatewayLabel          Label subscribed by the gateway
     * @param gatewayInstruction    Whether this is to publish or remove the API from gateway
     * @return                       A String contains all the information about the API and gateway artifacts
     * @throws IOException          If there are any errors while executing the http client
     * @throws Exception            If there are any errors when retrieving the Artifacts
     */

    public String retrieveArtifact(String APIId, String gatewayLabel, String gatewayInstruction) throws IOException, Exception {
        try {
            Thread.sleep(1);
            log.debug("Successful while waiting to retrieve artifacts from event hub");
        } catch (InterruptedException e) {
            log.error("Error occurred while waiting to retrieve artifacts from event hub");
        }

        try {
            String endcodedgatewayLabel = URLEncoder.encode(gatewayLabel, "UTF-8");
            String path = "/synapse-artifacts" + "?apiId=" + APIId +
                    "&gatewayInstruction=" + gatewayInstruction + "&gatewayLabel=" + endcodedgatewayLabel;
            String endpoint = baseURL + path;
            CloseableHttpResponse httpResponse = invokeService(endpoint);
            String gatewayRuntimeArtifact = null;
            if (httpResponse.getEntity() != null) {
                gatewayRuntimeArtifact = EntityUtils.toString(httpResponse.getEntity(),
                        "UTF-8");
                httpResponse.close();
            } else {
                //    throw new ArtifactSynchronizerException("HTTP response is empty");
            }
            return gatewayRuntimeArtifact;

        } catch (IOException e) {
            String msg = "Error while executing the http client";
            log.error(msg, e);
            throw new Exception(msg, e);
        }
    }

    /**
     * Return a CloseableHttpResponse instance
     * This method is used to invoke a http service for a given endpoint url
     *<p>
     *This method returns a CloseableHttpResponse instance
     * that implements Closeable interface
     *
     * @param endpoint                   Absolute URL endpoint
     * @return CloseableHttpResponse     CloseableHttpResponse that doesn't have a close method itself
     * @throws IOException               If there are any errors while executing the http request
     * @throws Exception                 If there are any errors when returning a CloseableHttpResponse
     */
    private CloseableHttpResponse invokeService(String endpoint) throws IOException, Exception {
        HttpGet method = new HttpGet(endpoint);
        URL url = new URL(endpoint);
        String username = "admin";
        String password = "admin";
        byte[] credentials = Base64.encodeBase64((username + ":" + password).
                getBytes("UTF-8"));
        int port = url.getPort();
        String protocol = url.getProtocol();
        method.setHeader("Authorization", "Basic "
                + new String(credentials, "UTF-8"));
        HttpClient httpClient = APIUtil.getHttpClient(port, protocol);
       // HttpClient httpClient = HttpUtil.getService();
        try {
            return APIUtil.executeHTTPRequest(method, httpClient);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


}
