package com.gatewayBridge.dto;

import java.util.Objects;

/**
 * This class contains the object of Credentials on endpoint
 */
public class CredentialDto {

    private String alias;
    private String password;

    public String getAlias() {

        return alias;
    }

    public void setAlias(String alias) {

        this.alias = alias;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

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
