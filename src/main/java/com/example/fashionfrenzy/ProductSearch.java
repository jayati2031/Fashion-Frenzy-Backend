package com.example.fashionfrenzy;

import java.util.*;

public class ProductSearch {
    private static class Node {
        String title;
        String brand;
        String price;
        String url;
        Node left, right;
        int height;

        Node(String title, String brand, String price, String url) {
            this.title = title;
            this.brand = brand;
            this.price = price;
            this.url = url;
            this.height = 1;
        }
    }

    private Node root;

    private final SearchHistory searchHistory = new SearchHistory(); // Instantiate SearchHistory

    // Get height of a node
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    // Get balance factor of a node
    private int getBalance(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    // Perform right rotation
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T = x.right;

        // Perform rotation
        x.right = y;
        y.left = T;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    // Perform left rotation
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T = y.left;

        // Perform rotation
        y.left = x;
        x.right = T;

        // Update heights
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    // Insert a title into the AVL tree
    public void insert(String title, String brand, String price, String url) {
        root = insert(root, title, brand, price, url);
    }

    // Recursive method to insert a title into the AVL tree
    private Node insert(Node node, String title, String brand, String price, String url) {
        if (node == null) {
            return new Node(title, brand, price, url);
        }

        // Perform standard BST insertion
        int cmp = title.compareToIgnoreCase(node.title);
        if (cmp < 0) {
            node.left = insert(node.left, title, brand, price, url);
        } else if (cmp > 0) {
            node.right = insert(node.right, title, brand, price, url);
        } else { // Duplicate titles are not allowed
            return node;
        }

        // Update height of this ancestor node
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // Get the balance factor of this ancestor node to check if it became unbalanced
        int balance = getBalance(node);

        // If the node is unbalanced, there are four cases
        // Left Left Case
        if (balance > 1 && title.compareToIgnoreCase(node.left.title) < 0) {
            return rotateRight(node);
        }
        // Right Right Case
        if (balance < -1 && title.compareToIgnoreCase(node.right.title) > 0) {
            return rotateLeft(node);
        }
        // Left Right Case
        if (balance > 1 && title.compareToIgnoreCase(node.left.title) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        // Right Left Case
        if (balance < -1 && title.compareToIgnoreCase(node.right.title) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        // Return the unchanged node pointer
        return node;
    }

    // Search for a part of the title in the AVL tree and return all matching product details
    public List<Map<String, String>> search(String searchQuery) {
        List<Map<String, String>> results = new ArrayList<>();
        search(root, searchQuery.toLowerCase(), results);
        return results;
    }

    // Recursive method to search for a part of the title in the AVL tree
    private void search(Node node, String searchQuery, List<Map<String, String>> results) {
        if (node == null) {
            return;
        }

        // Perform search in current node
        if (node.title.toLowerCase().contains(searchQuery)) {
            Map<String, String> productDetails = new HashMap<>();
            productDetails.put("Title", node.title);
            productDetails.put("Brand", node.brand);
            productDetails.put("Price", node.price);
            productDetails.put("URL", node.url);
            results.add(productDetails);
        }

        // Perform search in left and right subtrees
        search(node.left, searchQuery, results);
        search(node.right, searchQuery, results);
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
    private double similarity(String s1, String s2) {
        int editDistance = editDistance(s1, s2);
        int maxLength = Math.max(s1.length(), s2.length());
        return 1.0 - (double) editDistance / maxLength;
    }

    // Method to calculate page rank based on a graph structure
    private Map<String, Double> calculatePageRank(List<Map<String, String>> products) {
        Map<String, Set<String>> graph = new HashMap<>();
        Map<String, Double> pageRanks = new HashMap<>();

        // Build the graph
        for (Map<String, String> product : products) {
            String title = product.get("Title");
            String[] titleWords = title.split("\\s+");
            for (String word : titleWords) {
                if (!graph.containsKey(word)) {
                    graph.put(word, new HashSet<>());
                }
                graph.get(word).add(title);
            }
        }

        // Initialize page ranks
        for (String word : graph.keySet()) {
            pageRanks.put(word, 1.0);
        }

        // Perform page rank iterations
        int iterations = 10;
        double dampingFactor = 0.85;
        for (int i = 0; i < iterations; i++) {
            Map<String, Double> newPageRanks = new HashMap<>();
            double sum = 0;
            for (String word : graph.keySet()) {
                double newPageRank = (1 - dampingFactor);
                for (String incomingWord : graph.keySet()) {
                    if (!incomingWord.equals(word) && graph.get(incomingWord).contains(word)) {
                        newPageRank += dampingFactor * (pageRanks.get(incomingWord) / graph.get(incomingWord).size());
                    }
                }
                newPageRanks.put(word, newPageRank);
                sum += newPageRank;
            }
            for (String word : newPageRanks.keySet()) {
                newPageRanks.put(word, newPageRanks.get(word) / sum);
            }
            pageRanks = newPageRanks;
        }

        return pageRanks;
    }

    public static void main(String[] args) {
        // Assuming you have a list of file paths for the selected category
        List<String> filePaths = new ArrayList<>();
        filePaths.add("src\\main\\resources\\womenAmazonDress.xlsx");
        filePaths.add("src\\main\\resources\\womenBoohooDress.xlsx");
        filePaths.add("src\\main\\resources\\womenRevolveDress.xlsx");

        List<Map<String, String>> products = FetchProductsFromExcelBasedOnCategory.readData(filePaths);
        ProductSearch productSearch = new ProductSearch();

        for (Map<String, String> product : products) {
            String title = product.get("Title");
            String brand = product.get("Brand");
            String price = product.get("Price");
            String url = product.get("URL");
            productSearch.insert(title, brand, price, url);
        }

        // Calculate page rank
        Map<String, Double> pageRanks = productSearch.calculatePageRank(products);

        Scanner scanner = new Scanner(System.in);

        // Search history root initialized to null initially
        SearchHistory.HistoryNode historyRoot = null;

        // Created HashMap to store Top Searches
        Map<String, Integer> searchMap = TrendingSearch.loadDataFromFile();

        while (true) {
            // Call the displayTopSearches Method in the TrendingSearch.java
            TrendingSearch.displayTopSearches(searchMap);

            // Display search history or message if it is empty
            System.out.println("\nSearch History");
            if (historyRoot == null) {
                System.out.println("No search history available.");
            } else {
                productSearch.searchHistory.displayAllSearchHistory(historyRoot);
            }
            System.out.println();

            System.out.print("Enter the title to search for (type 'exit' to quit): ");
            String searchTitle = scanner.nextLine();

            if (searchTitle.equalsIgnoreCase("exit")) {
                break; // Exit loop if user inputs 'exit'
            }

            // The value of the query will be passed to perform the Tending Search feature
            String query = searchTitle.trim().toLowerCase(); // Convert query to lowercase

            // Update search map with new query and frequency, and update Excel sheet
            int frequency = searchMap.getOrDefault(query, 0);
            searchMap.put(query, frequency + 1);
            TrendingSearch.updateExcelSheet(query, frequency + 1);

            // Search products
            List<Map<String, String>> searchResults = productSearch.search(searchTitle);

            if (!searchResults.isEmpty()) {
                // Sort search results based on page rank
                searchResults.sort((p1, p2) -> Double.compare(pageRanks.getOrDefault(p2.get("Title"), 0.0), pageRanks.getOrDefault(p1.get("Title"), 0.0)));

                System.out.println("Search Results:");
                for (Map<String, String> productDetails : searchResults) {
                    System.out.println("Title: " + productDetails.get("Title"));
                    System.out.println("Brand: " + productDetails.get("Brand"));
                    System.out.println("Price: " + productDetails.get("Price"));
                    System.out.println("URL: " + productDetails.get("URL"));
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~");
                }
            } else {
                // If no direct matches found, perform spell check
                System.out.println("No matching products found for \"" + searchTitle + "\". Performing spell check...");

                // Display spell check results if any
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
                            double similarityScore = productSearch.similarity(searchWord.toLowerCase(), titleWord.toLowerCase());
                            maxSimilarity = Math.max(maxSimilarity, similarityScore);
                        }
                        totalSimilarity += maxSimilarity;
                    }
                    double averageSimilarity = totalSimilarity / searchWords.length;
                    if (averageSimilarity > 0.7) { // Adjust threshold as needed
                        spellCheckResults.add(product);
                    }
                }

                if (!spellCheckResults.isEmpty()) {
                    System.out.println("Details related to similar products:");
                    for (Map<String, String> productDetails : spellCheckResults) {
                        System.out.println("Title: " + productDetails.get("Title"));
                        System.out.println("Brand: " + productDetails.get("Brand"));
                        System.out.println("Price: " + productDetails.get("Price"));
                        System.out.println("URL: " + productDetails.get("URL"));
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~");
                    }
                } else {
                    System.out.println("No similar products found.");
                    System.out.println();
                }
            }

            // Insert the searched word into the search history
            historyRoot = productSearch.searchHistory.insertHistory(historyRoot, searchTitle);
        }
        scanner.close();
    }
}
