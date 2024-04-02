package com.example.fashionfrenzy;

import java.util.*;

public class ProductSearch {
    private static class Node {
        String image;
        String title;
        String brand;
        String price;
        String url;
        Node left, right;
        int height;

        Node(String image, String title, String brand, String price, String url) {
            this.image = image;
            this.title = title;
            this.brand = brand;
            this.price = price;
            this.url = url;
            this.height = 1;
        }
    }

    private Node root;

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
    public void insert(String image, String title, String brand, String price, String url) {
        root = insert(root, image, title, brand, price, url);
    }

    // Recursive method to insert a title into the AVL tree
    private Node insert(Node node, String image, String title, String brand, String price, String url) {
        if (node == null) {
            return new Node(image, title, brand, price, url);
        }

        // Perform standard BST insertion
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
    }

    public List<String> spellCheckForProductTitle(List<Map<String, String>> products, ProductSearch productSearch, String searchQuery) {
        Map<String, List<String>> similarWordsMap = new HashMap<>();
        List<String> similarWords = new ArrayList<>();
        for (Map<String, String> product : products) {
            String title = product.get("Title");
            // Split the search query into individual words
            String[] searchWords = searchQuery.split("\\s+");
            String[] titleWords = title.split("\\s+");

            // Calculate the similarity score for each word in the title
            for (String searchWord : searchWords) {
                similarWords = similarWordsMap.computeIfAbsent(searchWord, k -> new ArrayList<>());
                for (String titleWord : titleWords) {
                    double similarityScore = productSearch.similarity(searchWord.toLowerCase(), titleWord.toLowerCase());
                    if (similarityScore > 0.7 && !similarWords.contains(titleWord)) {
                        similarWords.add(titleWord);
                    }
                }
            }
        }
        return similarWords;
    }

    // Calculate the similarity between two strings
    public double similarity(String s1, String s2) {
        int editDistance = editDistance(s1, s2);
        int maxLength = Math.max(s1.length(), s2.length());
        return 1.0 - (double) editDistance / maxLength;
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
}