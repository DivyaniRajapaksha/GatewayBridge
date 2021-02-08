package com.gatewayBridge.dto;


import java.io.Serializable;
import java.util.Objects;


public class GatewayContentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String content;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GatewayContentDTO that = (GatewayContentDTO) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, content);
    }
}