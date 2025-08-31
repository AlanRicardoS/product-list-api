package org.example.productlist.model.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Rating {
    private String ratingId;
    private String productId;
    private Double score; // 1-5
    private String message;
    private String customerId;

    @JsonCreator
    public Rating(
            @JsonProperty("ratingId") String ratingId,
            @JsonProperty("productId") String productId,
            @JsonProperty("evaluation") Double score,
            @JsonProperty("message") String message,
            @JsonProperty("customerId") String customerId
    ) {
        this.ratingId = ratingId;
        this.productId = productId;
        this.score = score;
        this.message = message;
        this.customerId = customerId;
    }

    public Rating(String productId, Double score, String message, String customerId) {
        this.ratingId = UUID.randomUUID().toString();
        this.productId = productId;
        this.score = score;
        this.message = message;
        this.customerId = customerId;
    }

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
