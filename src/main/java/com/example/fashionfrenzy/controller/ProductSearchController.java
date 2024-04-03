package com.example.fashionfrenzy.controller;

import java.io.IOException;
import java.util.*;

import org.springframework.web.bind.annotation.*;
import com.example.fashionfrenzy.TopSearches;
import com.example.fashionfrenzy.PageRanking;
import com.example.fashionfrenzy.ProductSearch;
import com.example.fashionfrenzy.FrequencyCount;

@RestController
@RequestMapping("/api/products")
public class ProductSearchController {

    // Injecting dependencies
    private final ProductSearch productSearch;
    private final FrequencyCount frequencyCount;
    private final PageRanking pageRanking;
    private final TopSearches topSearches;
    private final FetchProductsController fetchProductsController;

    // List to store fetched products
    List<Map<String, String>> products;

    // Constructor to inject dependencies
    public ProductSearchController(ProductSearch productSearch, FrequencyCount frequencyCount, PageRanking pageRanking,
                                   TopSearches topSearches, FetchProductsController fetchProductsController) {
        this.productSearch = productSearch;
        this.frequencyCount = frequencyCount;
        this.pageRanking = pageRanking;
        this.topSearches = topSearches;
        this.fetchProductsController = fetchProductsController;
    }

    // Endpoint to search products
    @GetMapping("/search")
    public List<Map<String, String>> searchProducts(@RequestParam String gender, @RequestParam String category, @RequestParam String searchQuery) {
        // Fetch products based on gender and category
        fetchProducts(gender, category);
        // Trim and convert search query to lowercase
        searchQuery = searchQuery.trim().toLowerCase();
        // List to store search results
        List<Map<String, String>> searchResults = productSearch.search(searchQuery);
        List<Map<String, String>> rankedSearchResults = new ArrayList<>();
        // Check if search results are not empty
        if (!searchResults.isEmpty()) {
            // Capitalize category name for file path
            String categoryCapitalized = category.substring(0, 1).toUpperCase() + category.substring(1);
            // Websites where products are stored
            List<String> websites = List.of("Amazon", "Boohoo", "Revolve");
            // File paths for each website
            List<String> filePaths = websites.stream()
                    .map(website -> String.format("src/main/resources/%s%s%s.xlsx", gender, website, categoryCapitalized))
                    .toList();
            // Run frequency count for search query
            try {
                frequencyCount.run(searchQuery, filePaths);
                // Print the result according to the rank of the page
                rankedSearchResults = pageRanking.run(searchQuery, filePaths);
                // Insert search query into top searches
                topSearches.insertHistory(searchQuery);
            } catch (IOException e) {
                // Handle IOException when running frequency count or inserting into top searches
                System.err.println("An error occurred during search: " + e.getMessage());
                // You might want to log the exception or perform any necessary cleanup
                // Rethrow or return an appropriate response to the client
                throw new RuntimeException("Failed to complete the search operation", e);
            }
        }
        // Return ranked search results
        return rankedSearchResults;
    }

    // Endpoint to perform spell check
    @GetMapping("/spell-check")
    public List<String> performSpellCheck(@RequestParam String gender, @RequestParam String category, @RequestParam String searchQuery) {
        // Fetch products based on gender and category
        fetchProducts(gender, category);
        // Perform spell check for product title
        return productSearch.spellCheckForProductTitle(products, productSearch, searchQuery);
    }

    // Method to fetch products
    private void fetchProducts(String gender, String category) {
        // Fetch products from fetchProductsController
        try {
            products = fetchProductsController.getProductsByCategory(gender, category);
        } catch (Exception e) {
            // Handle exceptions when fetching products
            System.err.println("An error occurred while fetching products: " + e.getMessage());
            // You might want to log the exception or perform any necessary cleanup
            // Rethrow or return an appropriate response to the client
            throw new RuntimeException("Failed to fetch products", e);
        }
        // Insert fetched products into product search index
        for (Map<String, String> product : products) {
            String image = product.get("Image");
            String title = product.get("Title");
            String brand = product.get("Brand");
            String price = product.get("Price");
            String url = product.get("URL");
            productSearch.insert(image, title, brand, price, url);
        }
    }
}