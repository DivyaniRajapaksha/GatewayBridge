package com.gatewayBridge.apiRetriever;

import com.gatewayBridge.listeners.JMSEventListener;
import com.gatewayBridge.utils.APIUtil;
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
     * Method used to call traffic manager and retrieve artificats from DB
     *
     * @param APIId              -api id
     * @param gatewayLabel
     * @param gatewayInstruction
     * @throws IOException
     * @throws Exception
     */

    public String retrieveArtifact(String APIId, String gatewayLabel, String gatewayInstruction) throws IOException, Exception {
        CloseableHttpResponse httpResponse = null;
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
            httpResponse = invokeService(endpoint);
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
     * Method used to invoke the HTTP service and return a closeableHTttpResponse
     *
     * @param endpoint
     * @return CloseableHttpResponse
     * @throws IOException
     * @throws Exception
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
        try {
            return APIUtil.executeHTTPRequest(method, httpClient);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


}
