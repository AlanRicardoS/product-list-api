package org.example.productlist.model.records;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@Schema(description = "Product")
public record ProductRecord(
        @Schema(description = "Product ID")
        String productId,
        @Schema(description = "Product name")
        String name,
        @Schema(description = "Product price")
        Double price,
        @Schema(description = "Product creation date")
        Date datCreation,
        @Schema(description = "Product update date")
        Date datUpdate,
        @Schema(description = "Product description")
        String description,
        @Schema(description = "Product image URL")
        String imageUrl,
        @Schema(description = "Product specifications")
        List<SpecificationRecord> specificationsList,
        @Schema(description = "Product ratings")
        List<RatingRecord> ratingList
) {
}
