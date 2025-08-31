package org.example.productlist.model.records;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response message")
public record ResponseMessage(
        String message,
        String statusCode,
        String timestamp
) {
}
