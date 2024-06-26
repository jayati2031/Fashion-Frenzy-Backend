package com.example.fashionfrenzy.controller;

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
public class PageRankingController {

    private final PageRanking pageRanking;

    @Autowired
    public PageRankingController(PageRanking pageRanking) {
        this.pageRanking = pageRanking;
    }

    @GetMapping("/page-rank")
    public List<Map<String, String>> pageRanking(@RequestParam String gender, @RequestParam String category, @RequestParam String searchQuery) {
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
            return pageRanking.run(searchQuery, filePaths);
        } catch (Exception e) {
            // Handle any exceptions and return an empty list if an error occurs
            System.err.println("An error occurred while performing page ranking: " + e.getMessage());
            return List.of();
        }
    }
}
