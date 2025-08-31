package org.example.productlist.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.productlist.model.entity.Product;
import org.example.productlist.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Comparator;
import java.util.List;

@Component
public class LocalStorage implements ProductRepository {

    @Value("${product.list.file}")
    private String productFilePath;
    private List<Product> products;

    private void loadProducts() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            products = mapper.readValue(
                    new File(productFilePath), new TypeReference<List<Product>>() {
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Product> getProducts(Integer page, Integer size, String sort, String direction) {
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
        products = products.subList(fromIndex, toIndex);
        return products;
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

    private Comparator<Product> getComparator(String sort){
        return switch (sort) {
            case "name" -> Comparator.comparing(Product::getName);
            case "price" -> Comparator.comparing(Product::getPrice);
            case "rating" -> Comparator.comparing(Product::getAverageRating);
            default -> Comparator.comparing(Product::getDatCreation);
        };
    }
}
