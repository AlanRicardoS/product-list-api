package org.example.productlist.model.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class Product {
    private String productId;
    private String name;
    private Double price;
    private Date datCreation;
    private Date datUpdate;
    private String description;
    private String imageUrl;
    private List<Specifications> specificationsList;
    private List<Rating> ratingList;

    @JsonCreator
    public Product(@JsonProperty("productId") String productId,
            @JsonProperty("name") String name,
            @JsonProperty("price") Double price,
            @JsonProperty("datCreation") Date datCreation,
            @JsonProperty("datUpdate") Date datUpdate,
            @JsonProperty("description") String description,
            @JsonProperty("imageUrl") String imageUrl,
            @JsonProperty("specificationsList") List<Specifications> specificationsList,
            @JsonProperty("ratingList") List<Rating> ratingList) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.datCreation = datCreation;
        this.datUpdate = datUpdate;
        this.description = description;
        this.imageUrl = imageUrl;
        this.specificationsList = specificationsList;
        this.ratingList = ratingList;
    }

    public Double getAverageRating() {
        if (ratingList == null || ratingList.isEmpty()) {
            return 0.0;
        }
        // Average score of all ratings
        var rating = ratingList.stream()
                .mapToDouble(Rating::getScore)
                .average()
                .orElse(0.0);
        return Math.round(rating * 100.0) / 100.0;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDatCreation() {
        return datCreation;
    }

    public void setDatCreation(Date datCreation) {
        this.datCreation = datCreation;
    }

    public Date getDatUpdate() {
        return datUpdate;
    }

    public void setDatUpdate(Date datUpdate) {
        this.datUpdate = datUpdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Specifications> getSpecificationsList() {
        return specificationsList;
    }

    public void setSpecificationsList(List<Specifications> specificationsList) {
        this.specificationsList = specificationsList;
    }

    public List<Rating> getRatingList() {
        return ratingList;
    }

    public void setRatingList(List<Rating> ratingList) {
        this.ratingList = ratingList;
    }
}
