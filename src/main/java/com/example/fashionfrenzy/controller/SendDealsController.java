package com.example.fashionfrenzy.controller;

import com.example.fashionfrenzy.SendDeals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/products")
public class SendDealsController {
    private final SendDeals sendDeals;
    private final FetchProductsController fetchProductsController;

    @Autowired
    public SendDealsController(SendDeals sendDeals, FetchProductsController fetchProductsController) {
        this.sendDeals = sendDeals;
        this.fetchProductsController = fetchProductsController;
    }

    @GetMapping("/send-deals")
    public ResponseEntity<String> sendDealsByEmail() {
        try {
            List<Map<String, String>> products = fetchProductsController.getAllProducts(1);
            sendDeals.sendDeals(products);
            return ResponseEntity.ok("Deals sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send deals. Error: " + e.getMessage());
        }
    }
}
