package com.example.fashionfrenzy.controller;

import com.example.fashionfrenzy.ProductSearch;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductSearchController {

    private final ProductSearch productSearch;

    private final FetchProductsController fetchProductsController;

    List<Map<String, String>> products;

    public ProductSearchController(ProductSearch productSearch, FetchProductsController fetchProductsController) {
        this.productSearch = productSearch;
        this.fetchProductsController = fetchProductsController;
    }

    @GetMapping("/search")
    public List<Map<String, String>> searchProducts(@RequestParam String gender, @RequestParam String category, @RequestParam String searchQuery) {
        fetchProducts(gender, category);
        searchQuery = searchQuery.trim().toLowerCase();
        return productSearch.search(searchQuery);
    }

    @GetMapping("/spell-check")
    public List<Map<String, String>> performSpellCheck(@RequestParam String gender, @RequestParam String category, @RequestParam String searchQuery) {
        fetchProducts(gender, category);
        List<Map<String, String>> spellCheckResults = new ArrayList<>();
        Map<String, List<String>> similarWordsMap = new HashMap<>();
        for (Map<String, String> product : products) {
            String title = product.get("Title");
            // Split the search query into individual words
            String[] searchWords = searchQuery.split("\\s+");
            String[] titleWords = title.split("\\s+");

            // Calculate the similarity score for each word in the title
            for (String searchWord : searchWords) {
                List<String> similarWords = similarWordsMap.computeIfAbsent(searchWord, k -> new ArrayList<>());
                for (String titleWord : titleWords) {
                    double similarityScore = productSearch.similarity(searchWord.toLowerCase(), titleWord.toLowerCase());
                    if (similarityScore > 0.7 && !similarWords.contains(titleWord)) {
                        similarWords.add(titleWord);
                    } else if (similarityScore > 0.7 ) {
                        spellCheckResults.add(product);
                    }
                }
            }
        }
        // Print similar words found
        for (Map.Entry<String, List<String>> entry : similarWordsMap.entrySet()) {
            String searchWord = entry.getKey();
            List<String> similarWords = entry.getValue();
            if (!similarWords.isEmpty()) {
                System.out.println("Similar words found for '" + searchWord + "': " + similarWords);
            }
        }
        return spellCheckResults;
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
