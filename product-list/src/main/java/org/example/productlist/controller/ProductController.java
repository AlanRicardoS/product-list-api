package org.example.productlist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.productlist.model.records.ResponseMessage;
import org.example.productlist.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static org.example.productlist.utils.StringConstants.ERROR_RESPONSE;
import static org.example.productlist.utils.StringConstants.LIST_OF_PRODUCTS;
import static org.example.productlist.utils.StringConstants.NOT_FOUND_PRODUCT;
import static org.example.productlist.utils.StringConstants.PRODUCT_ID_NOT_NULL;
import static org.example.productlist.utils.StringConstants.PRODUCT_NAME_NOT_NULL;
import static org.example.productlist.utils.StringConstants.PRODUCT_RESPONSE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "List of Products", description = "Retrieve a paginated list of products with optional sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of products",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = org.example.productlist.model.records.ProductRecord.class),
                            examples = @ExampleObject(name = "Products", value = LIST_OF_PRODUCTS))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class),
                            examples = @ExampleObject(name = "Error", value = ERROR_RESPONSE))) })
    public ResponseEntity<?> getProducts(@RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "datCreation") String sort,
            @RequestParam(value = "direction", defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(productService.getProducts(page, size, sort, direction));
    }


    @Operation(summary = "Product by ID", description = "Retrieve a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of product",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = org.example.productlist.model.records.ProductRecord.class),
                            examples = @ExampleObject(name = "Product", value = PRODUCT_RESPONSE))),
            @ApiResponse(responseCode = "404", description = "Product Not Found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class),
                            examples = @ExampleObject(name = "Product Not Found", value = NOT_FOUND_PRODUCT))
            ),
            @ApiResponse(responseCode = "400", description = "Product ID cannot be null",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class),
                            examples = @ExampleObject(name = "Product ID cannot be null", value = PRODUCT_ID_NOT_NULL))
            ),

    })
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable("productId") String productId) {
        if (productId == null) {
            var responseMessage = new ResponseMessage("Product ID cannot be null",
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), LocalDateTime.now().toString());
            return ResponseEntity.badRequest().body(responseMessage);
        }
        var product = productService.getProductById(productId);
        if (product == null) {
            var responseMessage = new ResponseMessage("Product not found for ID: " + productId,
                    String.valueOf(HttpStatus.NOT_FOUND.value()), LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
        }
        return ok(product);
    }


    @Operation(summary = "Product Search by Params", description = "Retrieve a paginated list of products by name, specifications, rating and price")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of product",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = org.example.productlist.model.records.ProductRecord.class),
                            examples = @ExampleObject(name = "Product", value = LIST_OF_PRODUCTS))),
            @ApiResponse(responseCode = "404", description = "Product Not Found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class),
                            examples = @ExampleObject(name = "Product Not Found", value = NOT_FOUND_PRODUCT))
            ),
            @ApiResponse(responseCode = "400", description = "Product ID cannot be null",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class),
                            examples = @ExampleObject(name = "Product Name cannot be null", value = PRODUCT_NAME_NOT_NULL))
            ),
    })
    @GetMapping("/search")
    public ResponseEntity<?> getProductSearchByParams(@RequestParam("name") String name,
            @RequestParam(value = "specifications", defaultValue = "") String specifications,
            @RequestParam(value = "ratingMin", defaultValue = "0") Double ratingMin,
            @RequestParam(value = "ratingMax", defaultValue = "5") Double ratingMax,
            @RequestParam(value = "priceMin", defaultValue = "0") Double priceMin,
            @RequestParam(value = "priceMax", defaultValue = "0") Double priceMax) {
        if (name == null || name.isEmpty()) {
            var responseMessage = new ResponseMessage("Product name cannot be null or empty",
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), LocalDateTime.now().toString());
            return ResponseEntity.badRequest().body(responseMessage);
        }

        var products = productService.searchProductsByParams(name, specifications, ratingMin, ratingMax, priceMin,
                priceMax);
        if (products == null || products.isEmpty()) {
            var responseMessage = new ResponseMessage("No products found matching the criteria",
                    String.valueOf(HttpStatus.NOT_FOUND.value()), LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
        }
        return ok(products);
    }

}
