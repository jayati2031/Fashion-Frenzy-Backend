package com.example.fashionfrenzy;

import java.util.*;

public class FastSearchUsingBtree {
    private Map<String, List<Node>> invertedIndex;

    public FastSearchUsingBtree() {
        invertedIndex = new HashMap<>();
    }

    // Node class for storing product details
    private static class Node {
        String image;
        String title;
        String brand;
        String price;
        String url;

        Node(String image, String title, String brand, String price, String url) {
            this.image = image;
            this.title = title;
            this.brand = brand;
            this.price = price;
            this.url = url;
        }
    }

    // Insert product details into the inverted index
    public void insert(String image, String title, String brand, String price, String url) {
        Node newNode = new Node(image, title, brand, price, url);
        updateInvertedIndex(newNode);
    }

    // Update inverted index
    private void updateInvertedIndex(Node node) {
        String[] words = node.title.toLowerCase().split("\\s+");
        for (String word : words) {
            invertedIndex.putIfAbsent(word, new ArrayList<>());
            invertedIndex.get(word).add(node);
        }
    }

    // Search for products based on a search query
    public List<Map<String, String>> search(String searchQuery) {
        List<Map<String, String>> results = new ArrayList<>();
        String[] searchWords = searchQuery.toLowerCase().split("\\s+");
        Set<Node> searchSet = new HashSet<>();

        // Intersect the lists of nodes for each search word
        for (String word : searchWords) {
            List<Node> nodeList = invertedIndex.getOrDefault(word, new ArrayList<>());
            if (!nodeList.isEmpty()) {
                if (searchSet.isEmpty()) {
                    searchSet.addAll(nodeList);
                } else {
                    searchSet.retainAll(nodeList);
                }
            }
        }

        // Convert the search set to results
        for (Node node : searchSet) {
            Map<String, String> productDetails = new HashMap<>();
            productDetails.put("Image", node.image);
            productDetails.put("Title", node.title);
            productDetails.put("Brand", node.brand);
            productDetails.put("Price", node.price);
            productDetails.put("URL", node.url);
            results.add(productDetails);
        }

        return results;
    }
}
