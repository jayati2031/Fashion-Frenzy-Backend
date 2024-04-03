package com.example.fashionfrenzy.controller;

import com.example.fashionfrenzy.FrequencyCount;
import com.example.fashionfrenzy.PageRanking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class FrequencyCountController {

    private final FrequencyCount frequencyCount;

    @Autowired
    public FrequencyCountController(FrequencyCount frequencyCount) {
        this.frequencyCount = frequencyCount;
    }

    @GetMapping("/frequency-count")
    public void getFrequencyCount(@RequestParam String gender, @RequestParam String category, @RequestParam String searchQuery) {
        try {
            // Capitalize the category name
            String categoryCapitalized = category.substring(0, 1).toUpperCase() + category.substring(1);
            // List of websites
            List<String> websites = List.of("Amazon", "Boohoo", "Revolve");
            // Generate file paths for each website's Excel file
            List<String> filePaths = websites.stream()
                    .map(website -> String.format("src/main/resources/%s%s%s.xlsx", gender, website, categoryCapitalized))
                    .toList();
            // Run page ranking algorithm
            frequencyCount.run(searchQuery, filePaths);
        } catch (Exception e) {
            // Handle any exceptions and return an empty list if an error occurs
            System.err.println("An error occurred while performing page ranking: " + e.getMessage());
        }
    }
}
