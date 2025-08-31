package org.example.productlist.model.records;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Rating")
public record RatingRecord(
        @Schema(description = "Rating ID")
        String ratingId,
        @Schema(description = "Product ID")
        String productId,
        @Schema(description = "Rating score")
        Double score,
        @Schema(description = "Rating message")
        String message,
        @Schema(description = "Customer ID")
        String customerId
) {
}
