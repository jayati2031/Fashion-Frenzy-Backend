package com.example.fashionfrenzy.controller;

import com.example.fashionfrenzy.ProductSorter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductSortController {

    private final FetchProductsController fetchProductsController;

    public ProductSortController(FetchProductsController fetchProductsController) {
        this.fetchProductsController = fetchProductsController;
    }

    @GetMapping("/sort")
    public List<Map<String, String>> getSortedProducts(
            @RequestParam String gender,
            @RequestParam String category,
            @RequestParam int sortBy,
            @RequestParam boolean sortOrder) {
        List<Map<String, String>> products = fetchProductsController.getProductsByCategory(gender, category);
        ProductSorter.sortAndDisplayProducts(products, sortBy, sortOrder);
        return products;
    }
}
