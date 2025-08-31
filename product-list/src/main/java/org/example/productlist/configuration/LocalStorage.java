package org.example.productlist.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.productlist.exception.InputStreamObjectException;
import org.example.productlist.model.entity.Product;
import org.example.productlist.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LocalStorage implements ProductRepository {

    @Value("${product.list.file}")
    private String productFilePath;
    private List<Product> products;

    private final Logger logger = LoggerFactory.getLogger(LocalStorage.class);

    private void loadProducts() {
        logger.info("Loading products from file: {}", productFilePath);
        try {
            ObjectMapper mapper = new ObjectMapper();
            products = mapper.readValue(
                    new File(productFilePath), new TypeReference<List<Product>>() {
                    });

        } catch (Exception e) {
            throw new InputStreamObjectException("Failed to load products from file" + e.getMessage() );
        }
    }

    @Override
    public List<Product> getProducts(Integer page, Integer size, String sort, String direction) {
        logger.info("Retrieving products with page: {}, size: {}, sort: {}, direction: {}", page, size, sort, direction);
        if (products == null) {
            loadProducts();
        }
        if(!sort.equals("dateCreation")){
            Comparator<Product> comparator = getComparator(sort);
            if(direction.equalsIgnoreCase("DESC")){
                comparator = comparator.reversed();
            }
            products.sort(comparator);
        }
        int fromIndex = Math.min(page * size, products.size());
        int toIndex = Math.min(fromIndex + size, products.size());
        return  products.subList(fromIndex, toIndex);
    }

    @Override
    public List<Product> getProducts() {
        if (products == null) {
            loadProducts();
        }
        return products;
    }

    @Override
    public Product getProductById(String productId) {
        if (products == null) {
            loadProducts();
        }
        return products.stream()
                .filter(product -> product.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Product> getProductsByParams(String productName, String specifications,
            Double ratingMin, Double ratingMax, Double priceMin, Double priceMax){
        if (products == null) {
            loadProducts();
        }
        logger.info("Searching products by params: {}, {}, {}, {}, {}, {}", productName, specifications, ratingMin, ratingMax, priceMin, priceMax);
        var productList = getProducts();
        if (productName != null && !productName.trim().isEmpty()) {
            productList = filterProductsByName(productList, productName);
        }
        if (specifications != null && !specifications.trim().isEmpty()) {
            productList = filterProductsBySpecifications(productList, specifications);
        }
        productList = filterProductsByPriceMinMax(productList, priceMin, priceMax);
        productList = filterProductsByRatingMinMax(productList, ratingMin, ratingMax);
        logger.info("Found {} products matching the criteria", productList.size());
        return productList;
    }

    private Comparator<Product> getComparator(String sort){
        return switch (sort) {
            case "name" -> Comparator.comparing(Product::getName);
            case "price" -> Comparator.comparing(Product::getPrice);
            case "rating" -> Comparator.comparing(Product::getAverageRating);
            default -> Comparator.comparing(Product::getDatCreation);
        };
    }
    private List<Product> filterProductsByRatingMinMax(List<Product> productList, Double ratingMin, Double ratingMax) {
        logger.info("Filtering products by rating min: {}, max: {}", ratingMin, ratingMax);
        if (ratingMax == 5) {
            return productList.stream()
                    .filter(p -> p.getAverageRating() >= ratingMin)
                    .toList();
        }
        return productList.stream()
                .filter(p -> p.getAverageRating() >= ratingMin && p.getAverageRating() <= ratingMax)
                .toList();
    }

    private List<Product> filterProductsByPriceMinMax(List<Product> productList, Double priceMin, Double priceMax) {
        logger.info("Filtering products by price min: {}, max: {}", priceMin, priceMax);
        if (priceMax == 0) {
            return productList.stream()
                    .filter(p -> p.getPrice() >= priceMin)
                    .toList();
        }
        return productList.stream()
                .filter(p -> p.getPrice() >= priceMin && p.getPrice() <= priceMax)
                .toList();
    }

    private List<Product> filterProductsByName(List<Product> productList, String productName) {
        logger.info("Filtering products by name: {}", productName);
        return productList.stream()
                .filter(p -> p.getName().toLowerCase().contains(productName.toLowerCase()))
                .toList();
    }

    private List<Product> filterProductsBySpecifications(List<Product> productList, String specifications) {
        logger.info("Filtering products by specifications: {}", specifications);
        if (specifications == null || specifications.trim().isEmpty()) {
            return productList;
        }

        Map<String, String> specificationMap = Arrays.stream(specifications.split(";"))
                .map(s -> s.split(":"))
                .filter(spec -> spec.length == 2)
                .collect(Collectors.toMap(spec -> spec[0], spec -> spec[1]));

        return productList.stream()
                .filter(product -> specificationMap.entrySet().stream()
                        .allMatch(entry -> product.getSpecificationsList().stream()
                                .anyMatch(ps -> ps.getKey().equalsIgnoreCase(entry.getKey()) &&
                                        ps.getValue().toLowerCase().contains(entry.getValue().toLowerCase()))))
                .toList();
    }

}
