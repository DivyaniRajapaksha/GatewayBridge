package org.wso2.carbon.apimgt.gatewaybridge.apiretriever;

public interface ArtifactRetriever {

    /**
     * Retrieves data from the storage
     *
     * @param APIId              the UUID of the API
     * @param gatewayLabel       the label subscribed by the gateway
     * @param gatewayInstruction an instruction to check whether this is to publish or remove the API from gateway
     * @return a String contains all the information about the API and gateway artifacts
     * @throws Exception If there are any errors when retrieving the Artifacts
     */
    String retrieveArtifact(String APIId, String gatewayLabel, String gatewayInstruction)
            throws Exception;
}
