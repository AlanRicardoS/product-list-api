package org.example.productlist.repository;

import org.example.productlist.model.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository {
    List<Product> getProducts(Integer page, Integer size, String sort, String direction);
    List<Product> getProducts();
    Product getProductById(String productId);
    List<Product> getProductsByParams(String productName, String specifications,
            Double ratingMin, Double ratingMax, Double priceMin, Double priceMax);
}
