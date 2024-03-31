package com.example.fashionfrenzy.controller;

import com.example.fashionfrenzy.ProductSorter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
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
            @RequestParam int sortOrder) {

        List<Map<String, String>> products = fetchProductsController.getProductsByCategory(gender, category);

        Comparator<Map<String, String>> comparator = switch (sortBy) {
            case 1 -> ProductSorter.getBrandComparator();
            case 2 -> ProductSorter.getTitleComparator();
            case 3 -> ProductSorter.getPriceComparator();
            default -> throw new IllegalArgumentException("Invalid sorting criteria");
        };

        boolean ascending = sortOrder == 1;
        ProductSorter.mergeSort(products, comparator, ascending);

        return products;
    }
}
