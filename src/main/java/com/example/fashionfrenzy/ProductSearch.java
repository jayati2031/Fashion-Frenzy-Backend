package com.example.fashionfrenzy;

import java.io.IOException;
import java.util.*;

public class ProductSearch {
    private FastSearchUsingBtree fastSearchUsingBtree; // Inverted Index

    // Constructor
    public ProductSearch() {
        this.fastSearchUsingBtree = new FastSearchUsingBtree();
    }

    // Insert a title into the AVL tree
    public void insert(String image, String title, String brand, String price, String url) {
        fastSearchUsingBtree.insert(image, title, brand, price, url);
    }

    // Search for a part of the title in the inverted index
    public List<Map<String, String>> search(String searchQuery) {
        return fastSearchUsingBtree.search(searchQuery);
    }

    // Perform spell check
    private List<Map<String, String>> spellCheck(String searchTitle, List<Map<String, String>> products) {
        List<Map<String, String>> spellCheckResults = new ArrayList<>();
        for (Map<String, String> product : products) {
            String title = product.get("Title");
            // Split the search query into individual words
            String[] searchWords = searchTitle.split("\\s+");
            String[] titleWords = title.split("\\s+");
            // Calculate the similarity score for each word in the title
            double totalSimilarity = 0.0;
            for (String searchWord : searchWords) {
                double maxSimilarity = 0.0;
                for (String titleWord : titleWords) {
                    double similarityScore = similarity(searchWord.toLowerCase(), titleWord.toLowerCase());
                    maxSimilarity = Math.max(maxSimilarity, similarityScore);
                }
                totalSimilarity += maxSimilarity;
            }
            double averageSimilarity = totalSimilarity / searchWords.length;
            if (averageSimilarity > 0.7) { // Adjust threshold as needed
                spellCheckResults.add(product);
            }
        }
        return spellCheckResults;
    }

    // Calculate the edit distance between two strings
    private int editDistance(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    // Calculate the similarity between two strings
    public double similarity(String s1, String s2) {
        int editDistance = editDistance(s1, s2);
        int maxLength = Math.max(s1.length(), s2.length());
        return 1.0 - (double) editDistance / maxLength;
    }

    // Print product details
    private static void printProductDetails(Map<String, String> productDetails) {
        System.out.println("Image Src: " + productDetails.get("Image"));
        System.out.println("Title: " + productDetails.get("Title"));
        System.out.println("Brand: " + productDetails.get("Brand"));
        System.out.println("Price: " + productDetails.get("Price"));
        System.out.println("URL: " + productDetails.get("URL"));
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public static void main(String[] args) throws IOException {
        // Assuming you have a list of file paths for the selected category
        List<String> filePaths = new ArrayList<>();
        filePaths.add("src\\main\\resources\\womenAmazonDress.xlsx");
        filePaths.add("src\\main\\resources\\womenBoohooDress.xlsx");
        filePaths.add("src\\main\\resources\\womenRevolveDress.xlsx");

        List<Map<String, String>> products = FetchProductsFromExcelBasedOnCategory.readData(filePaths);
        ProductSearch productSearch = new ProductSearch();
        Scanner scanner = new Scanner(System.in);

        // Create or load search history
        TopSearches topSearches = new TopSearches();
        topSearches.createOrLoadSearchHistory();

        FrequencyCount frequencyCount = new FrequencyCount(); // Instantiate FrequencyCount
        PageRanking pageRanking = new PageRanking(); // Initiate PageRanking

        for (Map<String, String> product : products) {
            String image = product.get("Image");
            String title = product.get("Title");
            String brand = product.get("Brand");
            String price = product.get("Price");
            String url = product.get("URL");
            productSearch.insert(image, title, brand, price, url);
        }

        while (true) {
            // Display search history or message if it is empty;
            topSearches.displayTopSearches();
            System.out.println();

            System.out.print("Enter the title to search for (type 'exit' to quit): ");
            String searchTitle = scanner.nextLine();

            if (searchTitle.equalsIgnoreCase("exit")) {
                break; // Exit loop if user inputs 'exit'
            }

            // Search products
            List<Map<String, String>> searchResults = productSearch.search(searchTitle);

            if (!searchResults.isEmpty()) {
                // Count the frequency of the searched word
                frequencyCount.run(searchTitle, filePaths);

                // Rank each page for the searched word
                pageRanking.run(searchTitle, filePaths);
                System.out.println("\nSearch Results");
                for (Map<String, String> productDetails : searchResults) {
                    printProductDetails(productDetails);
                }
            } else {
                // If no direct matches found, perform spell check
                System.out.println("\nNo matching products found for \"" + searchTitle + "\". Performing spell check...");

                // Display spell check results if any
                List<Map<String, String>> spellCheckResults = productSearch.spellCheck(searchTitle, products);
                if (!spellCheckResults.isEmpty()) {
                    System.out.println("\nDetails related to similar products");
                    for (Map<String, String> productDetails : spellCheckResults) {
                        printProductDetails(productDetails);
                    }
                } else {
                    System.out.println("\nNo similar products found.");
                    System.out.println();
                }
            }
            // Update search history
            topSearches.insertHistory(searchTitle);
        }
        scanner.close();
    }
}