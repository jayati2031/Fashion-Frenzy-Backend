package com.example.fashionfrenzy.controller;

import com.example.fashionfrenzy.DSTrie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

public class ProductWordCompletionController {
    private final DSTrie trie;
    private final List<Map<String, String>> products;

    public TrieController() {
        trie = new DSTrie();
        List<String> filePaths = new ArrayList<>();
        filePaths.add("src/main/resources/womenAmazonDresses.xlsx");
        filePaths.add("src/main/resources/womenBoohooDresses.xlsx");
        filePaths.add("src/main/resources/womenRevolveDresses.xlsx");
        products = FetchProductsFromExcelBasedOnCategory.readData(filePaths);
        loadProductsIntoTrie();
    }

    @PostMapping("/insert")
    public void insertWord(@RequestParam String word) {
        trie.wordInsert(word);
    }

    @GetMapping("/suggest")
    public List<String> suggestWords(@RequestParam String prefix) {
        return trie.getSuggestedWords(prefix);
    }

    @GetMapping("/search")
    public List<String> searchForTitle(@RequestParam String title) {
        List<String> suggestions = new ArrayList<>();
        for (Map<String, String> product : products) {
            if (product.get("Title").toLowerCase().contains(title.toLowerCase())) {
                suggestions.add(product.get("Title"));
            }
        }
        return suggestions;
    }

    private void loadProductsIntoTrie() {
        for (Map<String, String> product : products) {
            String title = product.get("Title");
            String[] titleWords = title.split("\\s+");
            for (String titleWord : titleWords) {
                titleWord = titleWord.replaceAll("[^a-zA-Z]", "").toLowerCase();
                if (!titleWord.isEmpty()) {
                    trie.wordInsert(titleWord);
                }
            }
        }
    }
}
