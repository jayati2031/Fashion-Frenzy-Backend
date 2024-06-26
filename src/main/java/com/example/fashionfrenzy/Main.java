package com.example.fashionfrenzy;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    // Define ANSI color codes for better console output formatting
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // Welcome message
            System.out.println(ANSI_RED + "\n============ WELCOME TO FASHION FRENZY============" + ANSI_RESET);
            System.out.println(ANSI_BLUE + "Find the best fashion products of different brands." + ANSI_RESET);

            boolean continueLoop = true; // Variable to control the outer loop
            while (continueLoop) {
                // Prompt user to select gender
                System.out.println(ANSI_GREEN + "\nSelect gender - men or women (type 'exit' to quit):" + ANSI_RESET);
                String gender = scanner.nextLine().trim();

                // Check if user wants to exit
                if (gender.equalsIgnoreCase("exit")) {
                    break; // Exit the loop if the user inputs 'exit'
                } else if (gender.equalsIgnoreCase("men") || gender.equalsIgnoreCase("women")) {
                    // If valid gender is selected, prompt for category selection
                    String category;
                    if (gender.equalsIgnoreCase("men")) {
                        System.out.println(ANSI_GREEN + "Select one category from the following - (shirt, hoodie, jeans, coat, sweater)" + ANSI_RESET);
                    } else {
                        System.out.println(ANSI_GREEN + "Select one category from the following - (dress, top, jeans, coat, sweater)" + ANSI_RESET);
                    }
                    while (true) {
                        category = scanner.nextLine().trim();
                        // Check if the selected category is valid
                        if (isValidCategory(gender, category)) {
                            // Display user's choices
                            System.out.println("~~~~~~~~~~~~~~~~~~~");
                            System.out.println(ANSI_YELLOW + "Your choices:" + ANSI_RESET);
                            System.out.println(ANSI_YELLOW + "Gender: " + ANSI_RESET + gender);
                            System.out.println(ANSI_YELLOW + "Category: " + ANSI_RESET + category);
                            // Ask user if they want to web scrape for the selected gender and category
                            System.out.println(ANSI_BLUE + "Do you want to Web Scrap for selected gender and category? (Yes/No)" + ANSI_RESET);
                            String answer = scanner.nextLine().trim();
                            if (answer.equalsIgnoreCase("yes")) {
                                Logger logger = Logger.getLogger("org.openqa.selenium.devtools");
                                logger.setLevel(Level.SEVERE);
                                // Call method to scrape product info from fashion websites
                                FashionWebsitesWebScrapper.scrapeProductInfo(gender, category);
                            } else if (answer.equalsIgnoreCase("no")) {
                                // Inform user and exit loop if they choose not to web scrape
                                System.out.println(ANSI_YELLOW + "Then let's checkout our website" + ANSI_RESET);
                                continueLoop = false; // Set the variable too false to exit the outer loop
                            } else {
                                // Handle invalid input
                                System.out.println(ANSI_RED + "Invalid input. Please provide either 'yes' or 'no'." + ANSI_RESET);
                            }
                            break; // Exit the category input loop if a valid category is provided
                        } else {
                            // Prompt user to select a valid category
                            System.out.println(ANSI_RED + "Please select a valid category." + ANSI_RESET);
                        }
                    }
                } else {
                    // Prompt user to write a valid gender
                    System.out.println(ANSI_RED + "Write a valid gender." + ANSI_RESET);
                }
            }
        } catch (Exception e) {
            // Catch any exception that occurs during execution and display error message
            System.out.println(ANSI_RED + "An error occurred: " + e.getMessage() + ANSI_RESET);
        }
        // Ensure the scanner is closed to release resources
    }

    // Method to check if the selected category is valid
    private static boolean isValidCategory(String gender, String category) {
        if (gender.equalsIgnoreCase("men")) {
            return category.matches("shirt|hoodie|jeans|coat|sweater");
        } else if (gender.equalsIgnoreCase("women")) {
            return category.matches("dress|top|jeans|coat|sweater");
        }
        return false;
    }
}
