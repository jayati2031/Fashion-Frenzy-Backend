package com.example.fashionfrenzy.controller;

import com.example.fashionfrenzy.FrequencyCount;
import com.example.fashionfrenzy.PageRanking;
import com.example.fashionfrenzy.ProductSearch;
import com.example.fashionfrenzy.TopSearches;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductSearchController {

    private final ProductSearch productSearch;
    private final FrequencyCount frequencyCount;
    private final PageRanking pageRanking;
    private final TopSearches topSearches;
    private final FetchProductsController fetchProductsController;

    List<Map<String, String>> products;

    public ProductSearchController(ProductSearch productSearch, FrequencyCount frequencyCount, PageRanking pageRanking,
                                   TopSearches topSearches, FetchProductsController fetchProductsController) {
        this.productSearch = productSearch;
        this.frequencyCount = frequencyCount;
        this.pageRanking = pageRanking;
        this.topSearches = topSearches;
        this.fetchProductsController = fetchProductsController;
    }

    @GetMapping("/search")
    public List<Map<String, String>> searchProducts(@RequestParam String gender, @RequestParam String category, @RequestParam String searchQuery) {
        fetchProducts(gender, category);
        searchQuery = searchQuery.trim().toLowerCase();
        List<Map<String, String>> searchResults = productSearch.search(searchQuery);
        List<Map<String, String>> rankedSearchResults = new ArrayList<>();
        if (!searchResults.isEmpty()) {
            String categoryCapitalized = category.substring(0, 1).toUpperCase() + category.substring(1);
            List<String> websites = List.of("Amazon", "Boohoo", "Revolve");
            List<String> filePaths = websites.stream()
                    .map(website -> String.format("src/main/resources/%s%s%s.xlsx", gender, website, categoryCapitalized))
                    .toList();
            // Count the frequency of the searched word
            frequencyCount.run(searchQuery, filePaths);

            // Print the result according to the rank of the page
            rankedSearchResults = pageRanking.run(searchQuery, filePaths);
            try {
                topSearches.insertHistory(searchQuery);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return rankedSearchResults;
    }

    @GetMapping("/spell-check")
    public List<String> performSpellCheck(@RequestParam String gender, @RequestParam String category, @RequestParam String searchQuery) {
        fetchProducts(gender, category);
        return productSearch.spellCheckForProductTitle(products, productSearch, searchQuery);
    }

    private void fetchProducts(String gender, String category) {
        products = fetchProductsController.getProductsByCategory(gender, category);
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
