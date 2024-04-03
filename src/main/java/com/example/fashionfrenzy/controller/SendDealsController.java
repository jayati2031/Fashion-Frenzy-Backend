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

/**
 * Controller class responsible for handling requests related to sending deals via email.
 */
@Controller
@RequestMapping("/api/products")
public class SendDealsController {

    private final SendDeals sendDeals; // Instance of SendDeals class for sending deals via email
    private final FetchProductsController fetchProductsController; // Instance of FetchProductsController for fetching product information

    /**
     * Constructor to inject dependencies.
     *
     * @param sendDeals              Instance of SendDeals class
     * @param fetchProductsController Instance of FetchProductsController class
     */
    @Autowired
    public SendDealsController(SendDeals sendDeals, FetchProductsController fetchProductsController) {
        this.sendDeals = sendDeals;
        this.fetchProductsController = fetchProductsController;
    }

    /**
     * Handler method for sending deals via email.
     *
     * @return ResponseEntity indicating the status of the operation
     */
    @GetMapping("/send-deals")
    public ResponseEntity<String> sendDealsByEmail() {
        try {
            // Fetch all products
            List<Map<String, String>> products = fetchProductsController.getAllProducts(1);
            // Send deals via email
            sendDeals.sendDeals(products);
            return ResponseEntity.ok("Deals sent successfully.");
        } catch (Exception e) {
            // Return error response if sending deals fails
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send deals. Error: " + e.getMessage());
        }
    }
}
