package org.example.productlist.model.records;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Specification")
public record SpecificationRecord(
        @Schema(description = "Specification ID")
        String specificationId,
        @Schema(description = "Product ID")
        String productId,
        @Schema(description = "Specification key")
        String key,
        @Schema(description = "Specification value")
        String value
) {
}
