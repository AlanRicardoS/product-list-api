package org.example.productlist.service;

import org.example.productlist.exception.ProductNotFoundException;
import org.example.productlist.model.entity.Product;
import org.example.productlist.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private ProductRepository productRepository;
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public List<Product> getProducts(Integer page, Integer size, String sort, String direction) {
        logger.info("Retrieving products with pagination: {}, size: {}, sort: {}, direction:{}", page, size, sort,
                direction);
        return productRepository.getProducts(page, size, sort, direction);
    }

    public List<Product> getProducts() {
        logger.info("Retrieving all products");
        return productRepository.getProducts();
    }

    public Product getProductById(String productId) {
        logger.info("Retrieving product with id: {}", productId);
        var products = productRepository.getProductById(productId);
        if (products == null) {
            logger.error("Product not found for id: {}", productId);
            throw new ProductNotFoundException(String.format("Product with id %s not found", productId));
        }
        return products;
    }

    public List<Product> searchProductsByParams(String productName, String specifications,
            Double ratingMin, Double ratingMax, Double priceMin, Double priceMax) {
        logger.info("Searching products by params: {}, {}, {}, {}, {}, {}", productName, specifications, ratingMin,
                ratingMax, priceMin, priceMax);
        return productRepository.getProductsByParams(productName, specifications, ratingMin, ratingMax,
                priceMin, priceMax);
    }

}
