package org.example.productlist.model.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Specifications {
    private String specificationId;
    private String productId;
    private String key; // e.g. Color, Size, etc.
    private String value; // e.g. Red, Large, etc.

    @JsonCreator
    public Specifications(
            @JsonProperty("specificationId") String specificationId,
            @JsonProperty("productId") String productId,
            @JsonProperty("key") String key,
            @JsonProperty("value") String value
    ) {
        this.specificationId = specificationId;
        this.productId = productId;
        this.key = key;
        this.value = value;
    }

    public Specifications(String key, String value, String productId) {
        this.specificationId = UUID.randomUUID().toString();
        this.key = key;
        this.value = value;
        this.productId = productId;
    }

    public String getSpecificationId() {
        return specificationId;
    }

    public void setSpecificationId(String specificationId) {
        this.specificationId = specificationId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
