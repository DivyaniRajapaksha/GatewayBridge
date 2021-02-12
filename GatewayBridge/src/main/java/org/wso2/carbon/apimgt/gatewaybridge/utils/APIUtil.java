package org.wso2.carbon.apimgt.gatewayBridge.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.*;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Class for creating HTTP connection
 */
public final class APIUtil {
    private static final Log log = LogFactory.getLog(APIUtil.class);
    private boolean debugEnabled = log.isDebugEnabled();
    private static CloseableHttpResponse httpResponse;

    /**
     * Returns a HttpClient instance
     * This method always returns immediately, whether or not the
     * HttpClient exists.
     *
     * @param port     The port of the client
     * @param protocol Protocol use for communication
     * @return         Executable HttpClient
     */
    public static HttpClient getHttpClient(int port, String protocol) {
        String maxTotal = "100";
        String defaultMaxPerRoute = "50";
        try {
            PoolingHttpClientConnectionManager pool = getPoolingHttpClientConnectionManager(protocol);
            pool.setMaxTotal(Integer.parseInt(maxTotal));
            pool.setDefaultMaxPerRoute(Integer.parseInt(defaultMaxPerRoute));

            RequestConfig params = RequestConfig.custom().build();
            return HttpClients.custom().setConnectionManager(pool).setDefaultRequestConfig(params).build();
        } catch (Exception e) {
            log.debug("Error while getting http client connection manager");
        }
        return null;
    }

    /**
     * Execute an HTTP request
     *
     * @param method        HttpRequest Type
     * @param httpClient    HttpClient
     * @return              HTTPResponse
     * @throws IOException  If there are any errors while executing the http request
     */
    public static CloseableHttpResponse executeHTTPRequest(HttpRequestBase method, HttpClient httpClient)
            throws IOException, Exception {

        int retryCount = 0;
        boolean retry;
        do {
            try {
                httpResponse = (CloseableHttpResponse) httpClient.execute(method);
                retry = false;
            } catch (IOException ex) {
                retryCount++;
                if (retryCount < 2) {
                    retry = true;
                    log.debug("Failed retrieving from remote endpoint: " + ex.getMessage()
                            + ". Retrying after " + 15 +
                            " seconds.");
                    try {
                        Thread.sleep(15 * 1000);
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                } else {
                    throw ex;
                }
            }
        } while (retry);

        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            return httpResponse;
        } else {
            httpResponse.close();
            String errorMessage = EntityUtils.toString(httpResponse.getEntity(),
                    "UTF-8");
            throw new Exception(errorMessage + "Event-Hub status code is : "
                    + httpResponse.getStatusLine().getStatusCode());
        }
    }

    /**
     * Returns a PoolingHttpClientConnectionManager instance
     *
     * @param protocol      Service endpoint protocol. It can be http/https
     * @return              PoolManager instance
     * @throws IOException  If there are any errors while returning the PoolManager
     */
    private static PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager(String protocol)
            throws Exception {

        PoolingHttpClientConnectionManager poolManager;
        if ("https".equals(protocol)) {

            SSLConnectionSocketFactory socketFactory = createSocketFactory();
            org.apache.http.config.Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("https", socketFactory).build();
            poolManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        } else {
            poolManager = new PoolingHttpClientConnectionManager();
        }
        return poolManager;
    }

    /**
     * Returns a SSLConnectionSocketFactory instance
     * Creates a socket and connects it to the specified remote host at the specified remote port.
     * This socket is configured using the socket options established for this factory.
     *
     * @return              SSLConnectionSocketFactory instance
     * @throws Exception    If there are any errors while creating an authenticated socket
     */
    public static SSLConnectionSocketFactory createSocketFactory() throws Exception {
        SSLContext sslContext;

        String keyStorePath = "/home/user/Desktop/New_Test/gateways/gateway-3/repository/resources/security/client-truststore.jks";
        String keyStorePassword = "wso2carbon";
        try {
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
            sslContext = SSLContexts.custom().loadTrustMaterial(trustStore).build();

            log.debug(trustStore);
            X509HostnameVerifier hostnameVerifier;
            String hostnameVerifierOption = "AllowAll";


            if ("AllowAll".equalsIgnoreCase(hostnameVerifierOption)) {
                hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            } else if ("Strict".equalsIgnoreCase(hostnameVerifierOption)) {
                hostnameVerifier = SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;
            } else {
                hostnameVerifier = SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
            }
            log.debug(sslContext + " " + hostnameVerifier);
            return new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        } catch (KeyStoreException e) {
            log.debug("Failed to read from Key Store");
        } catch (IOException e) {
            log.debug("Key Store not found in " + keyStorePath);
        } catch (CertificateException e) {
            log.debug("Failed to read Certificate");
        } catch (NoSuchAlgorithmException e) {
            log.debug("Failed to load Key Store from " + keyStorePath);
        } catch (KeyManagementException e) {
            log.debug("Failed to load key from" + keyStorePath);
        }

        return null;
    }

    /**
     * Returns PoolingHttpClientConnectionManager instance
     * This method creates a pooling connection manager with
     * the provided SSLConnectionSocketFactory.
     *
     * @return                          PoolingHttpClientConnectionManager instance
     * @throws NoSuchAlgorithmException Occurs when cryptographic algorithm is requested but is not available in the environment.
     * @throws KeyStoreException        If an keystore authentication error occurs.
     */
    public static PoolingHttpClientConnectionManager poolingConnectionManager() throws NoSuchAlgorithmException, KeyStoreException {
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        } catch (NoSuchAlgorithmException e) {
            log.debug("Pooling Connection Manager Initialisation failure because of " + e.getMessage());
        }

        try {
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create().register("https", sslsf)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();

            PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            poolingConnectionManager.setMaxTotal(100);
            return poolingConnectionManager;
        } catch (KeyManagementException e) {
            log.debug("Pooling Connection Manager Initialisation failure because of " + e.getMessage());
        }
        return null;
    }

}
