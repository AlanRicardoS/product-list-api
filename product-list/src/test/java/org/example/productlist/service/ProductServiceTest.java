package org.example.productlist.service;

import org.example.productlist.exception.ProductNotFoundException;
import org.example.productlist.model.entity.Product;
import org.example.productlist.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductById_NullProductId() {
        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(null);
        });
    }

    @Test
    void testGetProductById_EmptyProductId() {
        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById("");
        });
    }

    @Test
    void testGetProductById_UnexpectedException() {
        String productId = "123";
        when(productRepository.getProductById(productId)).thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getProductById(productId);
        });

        assertEquals("Unexpected error", exception.getMessage());
        verify(productRepository, times(1)).getProductById(productId);
    }

    @Test
    void testGetProductById_ProductExists() {
        String productId = "123";
        Product mockProduct = new Product("123", "Test Product", 20.0, null, null, "A sample product", null, null,
                null);

        when(productRepository.getProductById(productId)).thenReturn(mockProduct);

        Product returnedProduct = productService.getProductById(productId);

        assertNotNull(returnedProduct);
        assertEquals("123", returnedProduct.getProductId());
        assertEquals("Test Product", returnedProduct.getName());
        verify(productRepository, times(1)).getProductById(productId);
    }

    @Test
    void testGetProductById_ProductDoesNotExist() {
        String productId = "999";

        when(productRepository.getProductById(productId)).thenReturn(null);

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(productId);
        });

        assertEquals("Product with id 999 not found", exception.getMessage());
        verify(productRepository, times(1)).getProductById(productId);
    }

    @Test
    void testGetProducts_EmptyList() {
        when(productRepository.getProducts()).thenReturn(List.of());

        List<Product> products = productService.getProducts();

        assertNotNull(products);
        assertEquals(0, products.size());
        verify(productRepository, times(1)).getProducts();
    }

    @Test
    void testGetProducts_WithProducts() {
        List<Product> mockProducts = List.of(
                new Product("1", "Product A", 10.0, null, null, "Description A", null, null, null),
                new Product("2", "Product B", 20.0, null, null, "Description B", null, null, null)
        );

        when(productRepository.getProducts()).thenReturn(mockProducts);

        List<Product> products = productService.getProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Product A", products.get(0).getName());
        assertEquals("Product B", products.get(1).getName());
        verify(productRepository, times(1)).getProducts();
    }

    @Test
    void testGetProductsWithPaginationAndSorting() {
        Integer page = 0;
        Integer size = 2;
        String sort = "name";
        String direction = "asc";

        List<Product> mockProducts = List.of(
                new Product("1", "Product A", 10.0, null, null, "Description A", null, null, null),
                new Product("2", "Product B", 20.0, null, null, "Description B", null, null, null)
        );

        when(productRepository.getProducts(page, size, sort, direction)).thenReturn(mockProducts);

        List<Product> products = productService.getProducts(page, size, sort, direction);

        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Product A", products.get(0).getName());
        assertEquals("Product B", products.get(1).getName());
        verify(productRepository, times(1)).getProducts(page, size, sort, direction);
    }

    @Test
    void testGetProductById_NullFieldsInProduct() {
        String productId = "test-id";
        Product invalidProduct = new Product(null, null, null, null, null, null, null, null, null);

        when(productRepository.getProductById(productId)).thenReturn(invalidProduct);

        Product result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals(null, result.getProductId());
        assertEquals(null, result.getName());
        verify(productRepository, times(1)).getProductById(productId);
    }

    @Test
    void testGetProductById_PartiallyValidProduct() {
        String productId = "test-id";
        Product partiallyValidProduct = new Product("test-id", "Product Name", null, null, null, null, null, null,
                null);

        when(productRepository.getProductById(productId)).thenReturn(partiallyValidProduct);

        Product result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals("test-id", result.getProductId());
        assertEquals("Product Name", result.getName());
        assertEquals(null, result.getPrice());
        verify(productRepository, times(1)).getProductById(productId);
    }

    @ParameterizedTest
    @CsvSource({
            "Laptop, 2000.0, 2500.0 , 3.8, 5.0, RAM:16GB",
            "Laptop, 2000.0, 2500.0, 3.8, 4.8, RAM:16GB",
            "Laptop, 2000.0, 0.0, 3.8, 4.8, RAM:16GB",
            "Laptop, 2000.0, 0.0, 3.8, 4.8, RAM:16GB",
            "Laptop, 2000.0, 0.0, 3.8, 4.8, ",

    })
    void testSearchProductByParamsSucess(String productName, Double priceMin, Double priceMax, Double ratingMin,
            Double ratingMax, String specifications) {

        List<Product> products = productService.searchProductsByParams(productName, specifications, ratingMin,
                ratingMax, priceMin, priceMax);
        assertNotNull(products);
        assertEquals(0, products.size());

    }

    @ParameterizedTest
    @CsvSource({
            // Null parameters
            ", , , ",
            "0, , , ",
            ", 10, , ",
            ", , name, ",
            ", , , asc",
            // Valid combinations
            "0, 10, name, asc",
            "1, 5, price, desc",
            "0, 20, rating, asc",
            // Edge cases
            "-1, 10, name, asc",
            "0, -5, name, asc",
            "0, 0, name, asc",
            // Invalid sort and direction
            "0, 10, invalidField, asc",
            "0, 10, name, invalidDirection",
            "0, 10, '', ''",
            // Extreme values
            "999999, 1, name, asc",
            "0, 999999, name, asc"
    })
    void testGetProductsWithPagination(Integer page, Integer size, String sort, String direction) {
        when(productRepository.getProducts(page, size, sort, direction)).thenReturn(List.of());

        List<Product> products = productService.getProducts(page, size, sort, direction);

        assertNotNull(products);
        assertEquals(0, products.size());
        verify(productRepository, times(1)).getProducts(page, size, sort, direction);
    }

    @ParameterizedTest
    @CsvSource({
            // Null and empty cases (these should throw exceptions)
            ", ProductNotFoundException",
            "'', ProductNotFoundException",
            // Valid cases
            "validId, Product",
            "123, Product",
            "test-product-id, Product",
            // Special characters
            "product@123, Product",
            "product_with_underscore, Product",
            "product-with-dash, Product"
    })
    void testGetProductByIdParameterized(String productId, String expectedResult) {
        if ("ProductNotFoundException".equals(expectedResult)) {
            when(productRepository.getProductById(productId)).thenReturn(null);

            assertThrows(ProductNotFoundException.class, () -> {
                productService.getProductById(productId);
            });
        } else {
            Product mockProduct = new Product(productId, "Test Product", 20.0, null, null, 
                                            "A sample product", null, null, null);
            when(productRepository.getProductById(productId)).thenReturn(mockProduct);

            Product result = productService.getProductById(productId);

            assertNotNull(result);
            assertEquals(productId, result.getProductId());
        }
    }
}