package com.example.fashionfrenzy;

import java.util.*;
import org.springframework.stereotype.Component;

// TrieNode class represents a node in the Trie data structure
class TrieNode {
    char character; // The character stored in the node
    boolean isEndOfWord; // Flag to indicate if the node represents the end of a word
    TrieNode[] children; // Array to store references to child nodes

    // Constructor to initialize the TrieNode with a character
    public TrieNode(char character) {
        this.character = character;
        this.isEndOfWord = false;
        this.children = new TrieNode[26]; // Assuming only lowercase alphabetical characters
    }
}

@Component
public class ProductWordCompletion {
    private static TrieNode root = null; // Root node of the Trie

    // Constructor to initialize the ProductWordCompletion class
    public ProductWordCompletion() {
        root = new TrieNode('\0'); // Creating the root node with null character
    }

    // Method to insert a word into the Trie
    public static void insertWord(String word) {
        try {
            TrieNode current = root;

            // Traverse through each character of the word and insert into the Trie
            for (char ch : word.toCharArray()) {
                int index = ch - 'a'; // Mapping character to index
                if (current.children[index] == null) {
                    current.children[index] = new TrieNode(ch); // Create a new node if not already present
                }
                current = current.children[index]; // Move to the next node
            }

            current.isEndOfWord = true; // Mark the last node as end of word
        } catch (Exception e) {
            // Exception Handling
            // Log and handle any exceptions that occur during word insertion
            System.out.println("An error occurred while inserting word into trie: " + e.getMessage());
            throw e;
        }
    }

    // Method to suggest words based on a given prefix
    public static List<String> suggestWords(String prefix) {
        try {
            TrieNode prefixNode = findNode(prefix); // Find the node corresponding to the prefix
            List<String> suggestions = new ArrayList<>(); // List to store suggested words

            if (prefixNode != null) {
                suggestWordsHelper(prefix, prefixNode, suggestions); // Recursively suggest words
            } else {
                // Throw an exception or return an empty list if prefix not found
                System.out.println("\"No suggestions for the given prefix.\"");
            }
            return suggestions;
        } catch (Exception e) {
            // Exception Handling
            // Log and handle any exceptions that occur during word suggestion
            System.out.println("An error occurred while suggesting words: " + e.getMessage());
            throw e;
        }
    }

    // Helper method to search for a node in the Trie corresponding to a given prefix
    private static TrieNode findNode(String prefix) {
        try {
            TrieNode current = root;

            // Traverse through each character of the prefix to find the corresponding node
            for (char ch : prefix.toCharArray()) {
                int index = ch - 'a'; // Mapping character to index
                current = current.children[index]; // Move to the next node
                if (current == null) {
                    return null; // Return null if prefix not found
                }
            }

            return current; // Return the node corresponding to the prefix
        } catch (Exception e) {
            // Exception Handling
            // Log and handle any exceptions that occur during node finding
            System.out.println("An error occurred while finding node in trie: " + e.getMessage());
            throw e;
        }
    }

    // Helper method to recursively suggest words based on a given prefix and node
    private static void suggestWordsHelper(String prefix, TrieNode node, List<String> suggestions) {
        try {
            if (node.isEndOfWord) {
                suggestions.add(prefix); // Add the prefix as a suggestion if it represents a valid word
            }

            // Recursively traverse through each child node to suggest words
            for (int i = 0; i < 26; i++) {
                if (node.children[i] != null) {
                    suggestWordsHelper(prefix + node.children[i].character, node.children[i], suggestions);
                }
            }
        } catch (Exception e) {
            // Exception handling
            // Log and handle any exceptions that occur during word suggestion
            System.out.println("An error occurred while suggesting words: " + e.getMessage());
            throw e;
        }
    }
}
