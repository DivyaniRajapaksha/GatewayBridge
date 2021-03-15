package org.wso2.carbon.apimgt.gatewaybridge.apiretriever;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.wso2.carbon.apimgt.gatewaybridge.constants.APIConstants;
import org.wso2.carbon.apimgt.gatewaybridge.listeners.JMSEventListener;
import org.wso2.carbon.apimgt.gatewaybridge.utils.APIUtil;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Class for retrieving specific api details
 */
public class DBRetriever implements ArtifactRetriever {

    private static final Log log = LogFactory.getLog(JMSEventListener.class);

    /**
     * Retrieve data from the storage
     *
     * @param APIId              the UUID of the API
     * @param gatewayLabel       the label subscribed by the gateway
     * @param gatewayInstruction an instruction to check whether this is to publish or remove the API from gateway
     * @return  a String contains all the information about the API and gateway artifacts
     * @throws Exception  If there are any errors when retrieving the Artifacts
     */

    public String retrieveArtifact(String APIId, String gatewayLabel, String gatewayInstruction) throws  Exception {

        try {
            String endcodedGatewayLabel = URLEncoder.encode(gatewayLabel, APIConstants.CHARSET);
            String path = APIConstants.SYNAPSE_ARTIFACTS + "?apiId=" + APIId +
                    "&gatewayInstruction=" + gatewayInstruction + "&gatewayLabel=" + endcodedGatewayLabel;
            String baseURL = "https://localhost:9443" + APIConstants.INTERNAL_WEB_APP_EP;
            String endpoint = baseURL + path;
            CloseableHttpResponse httpResponse = invokeService(endpoint);
            String gatewayRuntimeArtifact = null;
            if (httpResponse.getEntity() != null) {
                gatewayRuntimeArtifact = EntityUtils.toString(httpResponse.getEntity(),
                        APIConstants.CHARSET);
                httpResponse.close();
            }  //    throw new ArtifactSynchronizerException("HTTP response is empty");

            return gatewayRuntimeArtifact;

        } catch (IOException e) {
            String msg = "Error while executing the http client";
            log.error(msg, e);
            throw new Exception(msg, e);
        }
    }

    /**
     * Returns a CloseableHttpResponse instance
     * This method is used to invoke a http service for a given endpoint url
     * <p>
     * This method returns a CloseableHttpResponse instance
     * that implements Closeable interface
     *
     * @param endpoint                   the absolute URL endpoint
     * @return CloseableHttpResponse     the closeableHttpResponse that doesn't have a close method itself
     * @throws IOException If there are any errors while executing the http request
     * @throws Exception   If there are any errors when returning a CloseableHttpResponse
     */
    private CloseableHttpResponse invokeService(String endpoint) throws IOException, Exception {

        HttpGet method = new HttpGet(endpoint);
        URL url = new URL(endpoint);
        String username = "admin";
        String password = "admin";
        byte[] credentials = Base64.encodeBase64((username + APIConstants.DELEM_COLON+ password).
                getBytes(StandardCharsets.UTF_8));
        int port = url.getPort();
        String protocol = url.getProtocol();
        method.setHeader(APIConstants.AUTHORIZATION_HEADER_DEFAULT, APIConstants.AUTHORIZATION_BASIC
                + new String(credentials, StandardCharsets.UTF_8));
        HttpClient httpClient;
        httpClient = APIUtil.getHttpClient(port, protocol);
        //httpClient = HttpUtil.getService();
        try {
            assert httpClient != null;
            return APIUtil.executeHTTPRequest(method, httpClient);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

}


