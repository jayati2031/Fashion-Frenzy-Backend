package com.example.fashionfrenzy.controller;

import com.example.fashionfrenzy.ProductFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductFilterController {

    private final ProductFilter productFilter;
    private final FetchProductsController fetchProductsController;

    @Autowired
    public ProductFilterController(ProductFilter productFilter, FetchProductsController fetchProductsController) {
        this.productFilter = productFilter;
        this.fetchProductsController = fetchProductsController;
    }

    @GetMapping("/filter/price")
    public List<Map<String, String>> filterProductsByPriceRange(
            @RequestParam String gender,
            @RequestParam String category,
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {
        List<Map<String, String>> products = fetchProductsController.getProductsByCategory(gender, category);
        for (Map<String, String> product : products) {
            productFilter.insert(product); // Insert product into the B-tree
        }
        return productFilter.searchProductsByPriceRange(minPrice, maxPrice);
    }

    @GetMapping("/filter/brands")
    public List<Map<String, String>> filterProductsByBrand(
            @RequestParam String gender,
            @RequestParam String category,
            @RequestParam String[] brands) {
        try {
            List<Map<String, String>> products = fetchProductsController.getProductsByCategory(gender, category);
            for (Map<String, String> product : products) {
                productFilter.insert(product); // Insert product into the B-tree
            }
            return productFilter.filterProductsByBrands(products, brands);
        } catch (IOException e) {
            // Handle IOException appropriately
            handleIOException(e);
        }
        return null;
    }

    @GetMapping("/filter/brands-and-price")
    public List<Map<String, String>> filterProductsByBrandAndPriceRange(
            @RequestParam String gender,
            @RequestParam String category,
            @RequestParam String[] brands,
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {
        try {
            List<Map<String, String>> products = fetchProductsController.getProductsByCategory(gender, category);
            for (Map<String, String> product : products) {
                productFilter.insert(product); // Insert product into the B-tree
            }
            return productFilter.filterProductsByBothBrandAndPriceRange(products, brands, minPrice, maxPrice);
        } catch (IOException e) {
            // Handle IOException appropriately
            handleIOException(e);
        }
        return null;
    }

    // Exception handler for IOException
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        // Return an appropriate error response
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request: " + e.getMessage());
    }
}
