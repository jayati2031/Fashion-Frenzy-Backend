package com.example.fashionfrenzy;

// Search Frequency : BST
public class SearchHistory {
    public static class HistoryNode {
        String word;
        int frequency;
        HistoryNode left, right;

        HistoryNode(String word) {
            this.word = word;
            this.frequency = 1;
        }
    }

    public HistoryNode insertHistory(HistoryNode node, String word) {
        if (node == null) {
            return new HistoryNode(word);
        }

        int cmp = word.compareToIgnoreCase(node.word);
        if (cmp < 0) {
            node.left = insertHistory(node.left, word);
        } else if (cmp > 0) {
            node.right = insertHistory(node.right, word);
        } else {
            node.frequency++; // Increment frequency if the word is already present
        }

        // Check if the search history size exceeds 8
        if (getHistorySize(node) > 8) {
            // Remove the oldest entry (the leftmost node)
            node = removeOldestEntry(node);
        }

        return node;
    }

    // Method to get the size of the search history
    private int getHistorySize(HistoryNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + getHistorySize(node.left) + getHistorySize(node.right);
    }

    // Method to remove the oldest entry (the leftmost node)
    private HistoryNode removeOldestEntry(HistoryNode node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = removeOldestEntry(node.left);
        return node;
    }

    // Display all search history words and their frequencies
    public void displayAllSearchHistory(HistoryNode node) {
        if (node == null) {
            return;
        }

        displayAllSearchHistory(node.right);
        System.out.println("Word: " + node.word + ", Frequency: " + node.frequency);
        displayAllSearchHistory(node.left);
    }
}
