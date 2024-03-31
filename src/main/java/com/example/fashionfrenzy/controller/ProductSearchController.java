package com.example.fashionfrenzy.controller;

import com.example.fashionfrenzy.ProductSearch;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductSearchController {

    private final ProductSearch productSearch;

    public ProductSearchController(ProductSearch productSearch) {
        this.productSearch = productSearch;
    }

    @PostMapping("/search")
    public List<Map<String, String>> searchProducts(@RequestBody String searchQuery) {
        return productSearch.search(searchQuery);
    }

    @PostMapping("/insert")
    public void insertProduct(@RequestBody Map<String, String> productDetails) {
        String title = productDetails.get("Title");
        String brand = productDetails.get("Brand");
        String price = productDetails.get("Price");
        String url = productDetails.get("URL");
        productSearch.insert(title, brand, price, url);
    }

//    @GetMapping("/history")
//    public List<Map<String, String>> getSearchHistory() {
//        // Implement logic to retrieve search history data
//    }
//
//    @PostMapping("/spellcheck")
//    public List<Map<String, String>> performSpellCheck(@RequestBody String searchQuery) {
//        // Implement logic to perform spell check and return similar products
//    }
}
