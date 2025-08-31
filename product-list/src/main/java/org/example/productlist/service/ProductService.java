package org.example.productlist.service;

import org.example.productlist.exception.ProductNotFoundException;
import org.example.productlist.model.entity.Product;
import org.example.productlist.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProducts(Integer page, Integer size, String sort, String direction) {
        return productRepository.getProducts(page, size, sort, direction);
    }

    public List<Product> getProducts() {
        return productRepository.getProducts();
    }

    public Product getProductById(String productId) {
        var products = productRepository.getProductById(productId);
        if (products == null) {
            throw new ProductNotFoundException(String.format("Product with id %s not found", productId));
        }
        return products;
    }

    public List<Product> searchProductsByParams(String productName, String specifications,
            Double ratingMin, Double ratingMax, Double priceMin, Double priceMax) {
        var products = getProducts();
        if (productName != null && !productName.trim().isEmpty()) {
            products = filterProductsByName(products, productName);
        }
        if (specifications != null && !specifications.trim().isEmpty()) {
            products = filterProductsBySpecifications(products, specifications);
        }
        products = filterProductsByPriceMinMax(products, priceMin, priceMax);
        products = filterProductsByRatingMinMax(products, ratingMin, ratingMax);
        return products;
    }

    private List<Product> filterProductsByRatingMinMax(List<Product> products, Double ratingMin, Double ratingMax) {
        if (ratingMax == 5) {
            return products.stream()
                    .filter(p -> p.getAverageRating() >= ratingMin)
                    .toList();
        }
        return products.stream()
                .filter(p -> p.getAverageRating() >= ratingMin && p.getAverageRating() <= ratingMax)
                .toList();
    }

    private List<Product> filterProductsByPriceMinMax(List<Product> products, Double priceMin, Double priceMax) {
        if (priceMax == 0) {
            return products.stream()
                    .filter(p -> p.getPrice() >= priceMin)
                    .toList();
        }
        return products.stream()
                .filter(p -> p.getPrice() >= priceMin && p.getPrice() <= priceMax)
                .toList();
    }

    private List<Product> filterProductsByName(List<Product> products, String productName) {
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(productName.toLowerCase()))
                .toList();
    }

    private List<Product> filterProductsBySpecifications(List<Product> products, String specifications) {
        if (specifications == null || specifications.trim().isEmpty()) {
            return products;
        }

        Map<String, String> specificationMap = Arrays.stream(specifications.split(";"))
                .map(s -> s.split(":"))
                .filter(spec -> spec.length == 2)
                .collect(Collectors.toMap(spec -> spec[0], spec -> spec[1]));

        return products.stream()
                .filter(product -> specificationMap.entrySet().stream()
                        .allMatch(entry -> product.getSpecificationsList().stream()
                                .anyMatch(ps -> ps.getKey().equalsIgnoreCase(entry.getKey()) &&
                                        ps.getValue().toLowerCase().contains(entry.getValue().toLowerCase()))))
                .toList();
    }
}
