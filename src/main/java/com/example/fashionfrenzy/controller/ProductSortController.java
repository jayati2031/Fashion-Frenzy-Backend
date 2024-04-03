package com.example.fashionfrenzy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.fashionfrenzy.ProductSorter;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/products")
public class ProductSortController {

    private final FetchProductsController fetchProductsController;

    // Constructor injection of FetchProductsController
    public ProductSortController(FetchProductsController fetchProductsController) {
        this.fetchProductsController = fetchProductsController;
    }

    // Endpoint to get sorted products
    @GetMapping("/sort")
    public List<Map<String, String>> getSortedProducts(
            @RequestParam String gender,
            @RequestParam String category,
            @RequestParam int sortBy,
            @RequestParam boolean sortOrder) {
        try {
            // Retrieve products based on gender and category
            List<Map<String, String>> products = fetchProductsController.getProductsByCategory(gender, category);
            // Sort and display products based on sortBy and sortOrder
            ProductSorter.sortAndDisplayProducts(products, sortBy, sortOrder);
            return products; // Return sorted products
        } catch (Exception e) { // Handle any exceptions
            // Log the exception
            System.out.println("An error occurred while fetching and sorting products: " + e.getMessage());
            // Return an empty list or handle the error as per the application's requirement
            return List.of();
        }
    }
}
