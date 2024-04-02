package com.example.fashionfrenzy.controller;

import com.example.fashionfrenzy.ProductWordCompletion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductWordCompletionController {
    private final ProductWordCompletion productWordCompletion;
    private final FetchProductsController fetchProductsController;

    public ProductWordCompletionController(ProductWordCompletion productWordCompletion, FetchProductsController fetchProductsController) {
        this.productWordCompletion = productWordCompletion;
        this.fetchProductsController = fetchProductsController;
    }

    @GetMapping("/word-completion")
    public List<String> suggestWords(@RequestParam String gender, @RequestParam String category, @RequestParam String prefix) {
        List<Map<String, String>> products = fetchProductsController.getProductsByCategory(gender, category);
        loadProductsIntoTrie(products);
        return productWordCompletion.suggestWords(prefix);
    }

    private void loadProductsIntoTrie(List<Map<String, String>> products) {
        for (Map<String, String> product : products) {
            String title = product.get("Title");
            String[] titleWords = title.split("\\s+");
            for (String titleWord : titleWords) {
                titleWord = titleWord.replaceAll("[^a-zA-Z]", "").toLowerCase();
                if (!titleWord.isEmpty()) {
                    productWordCompletion.wordInsert(titleWord);
                }
            }
        }
    }
}