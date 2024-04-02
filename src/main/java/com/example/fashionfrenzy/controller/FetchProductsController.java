package com.example.fashionfrenzy.controller;

import com.example.fashionfrenzy.FetchProductsFromExcelBasedOnCategory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/products")
public class FetchProductsController {

    @GetMapping("")
    public List<Map<String, String>> getAllProducts() {
        List<String> genders = List.of("men", "women");
        List<String> categoriesForMen = List.of("shirt", "hoodie", "jeans", "coat", "sweater");
        List<String> categoriesForWomen = List.of("dress", "top", "jeans", "coat", "sweater");
        List<Map<String, String>> allProducts = new ArrayList<>();

        for (String gender : genders) {
            List<String> categories;
            if (gender.equals("men")) {
                categories = categoriesForMen;
            } else {
                categories = categoriesForWomen;
            }
            for (String category : categories) {
                allProducts.addAll(getProductsByCategory(gender, category));
            }
        }
        return allProducts;
    }

    @GetMapping("/category")
    public List<Map<String, String>> getProductsByCategory(@RequestParam String gender, @RequestParam String category) {
        String genderChoice = gender.toLowerCase();
        String categoryChoice = category.toLowerCase();
        if (!isValidGender(genderChoice) || !isValidCategory(categoryChoice)) {
            throw new IllegalArgumentException("Invalid gender or category");
        }
        String categoryCapitalized = categoryChoice.substring(0, 1).toUpperCase() + categoryChoice.substring(1);
        List<String> websites = List.of("Amazon", "Boohoo", "Revolve");
        List<String> filePaths = websites.stream()
                .map(website -> String.format("src/main/resources/%s%s%s.xlsx", genderChoice, website, categoryCapitalized))
                .toList();
        return FetchProductsFromExcelBasedOnCategory.readData(filePaths);
    }

    private boolean isValidGender(String gender) {
        return gender.equals("men") || gender.equals("women");
    }

    private boolean isValidCategory(String category) {
        List<String> validCategoriesMen = List.of("shirt", "hoodie", "jeans", "coat", "sweater");
        List<String> validCategoriesWomen = List.of("dress", "top", "jeans", "coat", "sweater");
        return validCategoriesMen.contains(category) || validCategoriesWomen.contains(category);
    }
}
