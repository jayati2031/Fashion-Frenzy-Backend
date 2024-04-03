package com.example.fashionfrenzy;

import java.util.*;

public class Main {
    // Paths for source folder and parsed files folder
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println(ANSI_RED + "\n============ WELCOME TO FASHION FRENZY============" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "Find the best fashion products of different brands." + ANSI_RESET);

        boolean continueLoop = true; // Variable to control the outer loop
        while (continueLoop) {
            System.out.println(ANSI_GREEN + "\nSelect gender - men or women (type 'exit' to quit):" + ANSI_RESET);
            String gender = scanner.nextLine().trim();

            String category;
            if (gender.equalsIgnoreCase("exit")) {
                break; // Exit the loop if the user inputs 'exit'
            } else if (gender.equals("men") || gender.equals("women")) {
                if (gender.equals("men")) {
                    System.out.println(ANSI_GREEN + "Select one category from the following - (shirt, hoodie, jeans, coat, sweater)" + ANSI_RESET);
                } else {
                    System.out.println(ANSI_GREEN + "Select one category from the following - (dress, top, jeans, coat, sweater)" + ANSI_RESET);
                }
                while (true) {
                    category = scanner.nextLine().trim();
                    if (isValidCategory(category)) {
                        System.out.println("~~~~~~~~~~~~~~~~~~~");
                        System.out.println(ANSI_YELLOW + "Your choices:" + ANSI_RESET);
                        System.out.println("Gender: " + gender);
                        System.out.println("Category: " + category);
                        System.out.println("Do you want to Web Scrap for selected gender and category?(Yes/No)");
                        String answer = scanner.nextLine().trim();
                        if(answer.equalsIgnoreCase("yes")) {
                            FashionWebsitesWebScrapper.scrapeProductInfo(gender, category);
                        } else if(answer.equalsIgnoreCase("no")) {
                            System.out.println("Then let's checkout our website");
                            continueLoop = false; // Set the variable to false to exit the outer loop
                        } else {
                            System.out.println("Invalid input. Please provide either 'yes' or 'no'.");
                        }
                        break; // Exit the category input loop if a valid category is provided
                    } else {
                        System.out.println(ANSI_RED + "Please select a valid category." + ANSI_RESET);
                    }
                }
            } else {
                System.out.println(ANSI_RED + "Write a valid gender." + ANSI_RESET);
            }
        }
        scanner.close();
    }


    private static boolean isValidCategory(String category) {
        return category.equalsIgnoreCase("shirt") || category.equalsIgnoreCase("hoodie") ||
                category.equalsIgnoreCase("jeans") || category.equalsIgnoreCase("coat") ||
                category.equalsIgnoreCase("sweater") || category.equalsIgnoreCase("dress") ||
                category.equalsIgnoreCase("top");
    }
}
