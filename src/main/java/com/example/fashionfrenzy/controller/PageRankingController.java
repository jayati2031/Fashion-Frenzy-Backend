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
        String categoryCapitalized = category.substring(0, 1).toUpperCase() + category.substring(1);
        List<String> websites = List.of("Amazon", "Boohoo", "Revolve");
        List<String> filePaths = websites.stream()
                .map(website -> String.format("src/main/resources/%s%s%s.xlsx", gender, website, categoryCapitalized))
                .toList();
        return pageRanking.run(searchQuery, filePaths);
    }
}