package org.wso2.carbon.apimgt.gatewaybridge.dto;

import java.util.Objects;

/**
 * This class contains the object of Credentials on endpoint
 */
public class CredentialDto {

    private String alias;
    private String password;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CredentialDto that = (CredentialDto) o;
        return Objects.equals(alias, that.alias) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {

        return Objects.hash(alias, password);
    }
}