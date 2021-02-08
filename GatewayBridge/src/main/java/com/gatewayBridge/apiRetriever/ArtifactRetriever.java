package com.gatewayBridge.apiRetriever;


public interface ArtifactRetriever {
    /**
     * This method is used to retrieve data from the storage
     *
     * @param APIId              - UUID of the API
     * @param gatewayLabel       - Label subscribed by the gateway
     * @param gatewayInstruction - Whether this is to publish or remove the API from gateway
     * @return A String contains all the information about the API and gateway artifacts
     * @throws Exception if there are any errors when retrieving the Artifacts
     */
    String retrieveArtifact(String APIId, String gatewayLabel, String gatewayInstruction)
            throws Exception;
}
