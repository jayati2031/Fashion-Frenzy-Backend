package com.example.fashionfrenzy.controller;

import java.util.*;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.fashionfrenzy.ProductWordCompletion;

@RestController
@RequestMapping("/api/products")
public class ProductWordCompletionController {

    private final FetchProductsController fetchProductsController;

    // Constructor injection of FetchProductsController
    public ProductWordCompletionController(FetchProductsController fetchProductsController) {
        this.fetchProductsController = fetchProductsController;
    }

    // Endpoint for suggesting words based on the given prefix
    @GetMapping("/word-completion")
    public List<String> suggestWords(@RequestParam String gender, @RequestParam String category, @RequestParam String prefix) {
        try {
            // Fetch products based on gender and category
            List<Map<String, String>> products = fetchProductsController.getProductsByCategory(gender, category);

            // Load products into the trie for word completion
            loadProductsIntoTrie(products);

            // Return suggested words based on the prefix
            return ProductWordCompletion.suggestWords(prefix);
        } catch (Exception e) {
            // Log and handle any exceptions that occur during word suggestion
            System.out.println("An error occurred while suggesting words: " + e.getMessage());
            throw e;
        }
    }

    // Load products into the trie data structure for efficient word completion
    private void loadProductsIntoTrie(List<Map<String, String>> products) {
        try {
            // Iterate through each product
            for (Map<String, String> product : products) {
                String title = product.get("Title");
                String[] titleWords = title.split("\\s+");

                // Insert each word from the product title into the trie
                for (String titleWord : titleWords) {
                    titleWord = titleWord.replaceAll("[^a-zA-Z]", "").toLowerCase();
                    if (!titleWord.isEmpty()) {
                        ProductWordCompletion.insertWord(titleWord);
                    }
                }
            }
        } catch (Exception e) {
            // Log and handle any exceptions that occur during loading products into trie
            System.out.println("An error occurred while loading products into trie: " + e.getMessage());
            throw e;
        }
    }
}