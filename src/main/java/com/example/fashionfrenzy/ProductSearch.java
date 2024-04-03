package com.example.fashionfrenzy;

import java.util.*;

/**
 * Class for searching products and performing spell-checking on product titles.
 */
public class ProductSearch {

    /**
     * Nested class representing a node in the AVL tree.
     */
    private static class ProductNode {
        String image;   // Image URL of the product
        String title;   // Title of the product
        String brand;   // Brand of the product
        String price;   // Price of the product
        String url;     // URL of the product
        ProductNode left, right;    // References to left and right child nodes
        int height;     // Height of the node in the AVL tree

        /**
         * Constructor to create a new ProductNode.
         *
         * @param image URL of the product image
         * @param title title of the product
         * @param brand brand of the product
         * @param price price of the product
         * @param url URL of the product
         */
        ProductNode(String image, String title, String brand, String price, String url) {
            this.image = image;
            this.title = title;
            this.brand = brand;
            this.price = price;
            this.url = url;
            this.height = 1;    // New node is initially at height 1
        }
    }

    private ProductNode root;   // Root node of the AVL tree

    /**
     * Get the height of a node in the AVL tree.
     *
     * @param node the node to get the height of
     * @return the height of the node, or 0 if the node is null
     */
    private int height(ProductNode node) {
        // if node is null then it will return 0, otherwise return the hight of the tree
        return node == null ? 0 : node.height;
    }

    /**
     * Get the balance factor of a node in the AVL tree.
     *
     * @param node the node to get the balance factor of
     * @return the balance factor of the node
     */
    private int getBalance(ProductNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    /**
     * Perform a right rotation on the given node in the AVL tree.
     *
     * @param y the node to rotate
     * @return the new root node after rotation
     */
    private ProductNode rotateRight(ProductNode y) {
        ProductNode x = y.left;
        ProductNode T = x.right;

        // Perform rotation
        x.right = y;
        y.left = T;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    /**
     * Perform a left rotation on the given node in the AVL tree.
     *
     * @param x the node to rotate
     * @return the new root node after rotation
     */
    private ProductNode rotateLeft(ProductNode x) {
        ProductNode y = x.right;
        ProductNode T = y.left;

        // Perform rotation
        y.left = x;
        x.right = T;

        // Update heights
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    /**
     * Insert a product into the AVL tree.
     *
     * @param image URL of the product image
     * @param title title of the product
     * @param brand brand of the product
     * @param price price of the product
     * @param url URL of the product
     */
    public void insert(String image, String title, String brand, String price, String url) {
        root = insert(root, image, title, brand, price, url);
    }

    /**
     * Recursive method to insert a product into the AVL tree.
     *
     * @param node the current node in the AVL tree
     * @param image URL of the product image
     * @param title title of the product
     * @param brand brand of the product
     * @param price price of the product
     * @param url URL of the product
     * @return the updated node after insertion
     */
    private ProductNode insert(ProductNode node, String image, String title, String brand, String price, String url) {
        if (node == null) {
            return new ProductNode(image, title, brand, price, url);
        }

        try {
            // Perform standard BST insertion
            // Whole insertion operation of BST will be performed over here
            int cmp = title.compareToIgnoreCase(node.title);
            if (cmp < 0) {
                node.left = insert(node.left, image, title, brand, price, url);
            } else if (cmp > 0) {
                node.right = insert(node.right, image, title, brand, price, url);
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
        } catch (Exception e) {
            // Handle any exceptions that may occur during insertion
            // Exception handling will be done here for this method
            System.err.println("An error occurred during insertion: " + e.getMessage());
        }

        // Return the unchanged node pointer
        return node;
    }

    /**
     * Search for products containing a part of the title in the AVL tree.
     *
     * @param searchQuery the search query to match against product titles
     * @return a list of products matching the search query
     */
    public List<Map<String, String>> search(String searchQuery) {
        List<Map<String, String>> results = new ArrayList<>();
        try {
            search(root, searchQuery.toLowerCase(), results);
        } catch (Exception e) {
            // Handle any exceptions that may occur during search
            // Exception handling will be done here for this method
            System.err.println("An error occurred during search: " + e.getMessage());
        }
        return results;
    }

    /**
     * Recursive method to search for products containing a part of the title in the AVL tree.
     *
     * @param node the current node in the AVL tree
     * @param searchQuery the search query to match against product titles
     * @param results the list to store matching products
     */
    private void search(ProductNode node, String searchQuery, List<Map<String, String>> results) {
        if (node == null) {
            return;
        }

        try {
            // Perform search in current node
            if (node.title.toLowerCase().contains(searchQuery)) {
                Map<String, String> productDetails = new HashMap<>();
                productDetails.put("Image", node.image);
                productDetails.put("Title", node.title);
                productDetails.put("Brand", node.brand);
                productDetails.put("Price", node.price);
                productDetails.put("URL", node.url);
                results.add(productDetails);
            }

            // Perform search in left and right subtrees
            search(node.left, searchQuery, results);
            search(node.right, searchQuery, results);
        } catch (Exception e) {
            // Handle any exceptions that may occur during search
            System.err.println("An error occurred during search: " + e.getMessage());
        }
    }

    /**
     * Method to perform spell-check for product titles.
     *
     * @param products the list of products to perform spell-checking on
     * @param productSearch an instance of ProductSearch for similarity calculation
     * @param searchQuery the search query to match against product titles
     * @return a list of similar words found in the product titles
     */
    public List<String> spellCheckForProductTitle(List<Map<String, String>> products, ProductSearch productSearch, String searchQuery) {
        List<String> similarWords = new ArrayList<>();
        try {
            // Map to store similar words for each search word
            Map<String, List<String>> similarWordsMap = new HashMap<>();
            for (Map<String, String> product : products) {
                String title = product.get("Title");
                // Split the search query into individual words
                String[] searchWords = searchQuery.split("\\s+");
                String[] titleWords = title.split("\\s+");

                // Calculate the similarity score for each word in the title
                for (String searchWord : searchWords) {
                    List<String> currentSimilarWords = similarWordsMap.computeIfAbsent(searchWord, k -> new ArrayList<>());
                    for (String titleWord : titleWords) {
                        double similarityScore = productSearch.calculateSimilarity(searchWord.toLowerCase(), titleWord.toLowerCase());
                        if (similarityScore > 0.7 && !currentSimilarWords.contains(titleWord)) {
                            currentSimilarWords.add(titleWord);
                        }
                    }
                }
            }

            // Combine similar words from the map
            for (List<String> words : similarWordsMap.values()) {
                similarWords.addAll(words);
            }
        } catch (Exception e) {
            // Handle any exceptions that may occur during spell-checking
            // Exception handling will be done here for this method
            System.err.println("An error occurred during spell-checking: " + e.getMessage());
        }
        return similarWords;
    }

    /**
     * Method to calculate the similarity between two strings.
     *
     * @param s1 the first string
     * @param s2 the second string
     * @return the similarity score between the two strings
     */
    public double calculateSimilarity(String s1, String s2) {
        try {
            int editDistance = calculateEditDistance(s1, s2);
            int maxLength = Math.max(s1.length(), s2.length());
            return 1.0 - (double) editDistance / maxLength;
        } catch (Exception e) {
            // Handle any exceptions that may occur during similarity calculation
            System.err.println("An error occurred during similarity calculation: " + e.getMessage());
            return 0.0; // Default similarity value in case of error
        }
    }

    /**
     * Method to calculate the edit distance between two strings.
     *
     * @param s1 the first string
     * @param s2 the second string
     * @return the edit distance between the two strings
     */
    private int calculateEditDistance(String s1, String s2) {
        try {
            int m = s1.length(), n = s2.length();
            // Initialize two dimentional array
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
        } catch (Exception e) {
            // Exception handling will be done here for this method
            // Handle any exceptions that may occur during edit distance calculation
            System.err.println("An error occurred during edit distance calculation: " + e.getMessage());
            return -1; // Default value in case of error
        }
    }
}
