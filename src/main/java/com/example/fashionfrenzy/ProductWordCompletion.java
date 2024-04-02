package com.example.fashionfrenzy;

import org.springframework.stereotype.Component;

import java.util.*;

//TrieNode1 class represents a node in the Trie data structure
class NodeOfTrie1 {
    char data;
    boolean isEndOfWord;
    NodeOfTrie1[] children;

    public NodeOfTrie1(char data) {
        this.data = data;
        this.isEndOfWord = false;
        this.children = new NodeOfTrie1[26]; // Assuming only lowercase alphabetical characters
    }
}

@Component
public class ProductWordCompletion {
    private static NodeOfTrie1 rt = null;

    public ProductWordCompletion() {
        rt = new NodeOfTrie1('\0');
    }

    // Insert a word into the Trie
    public static void wordInsert(String word) {
        NodeOfTrie1 current = rt;

        for (char ch : word.toCharArray()) {
            int idx = ch - 'a';
            if (current.children[idx] == null) {
                current.children[idx] = new NodeOfTrie1(ch);
            }
            current = current.children[idx];
        }

        current.isEndOfWord = true;
    }

    // Suggest words based on a given prefix
    public static List<String> suggestWords(String prefix) {
        NodeOfTrie1 prefixNode = nodeFinding(prefix);
        List<String> suggestions = new ArrayList<>();

        if (prefixNode != null) {
            suggestWordsHelper(prefix, prefixNode, suggestions);
        } else {
            // Instead of printing, throw an exception or return an empty list.
            throw new IllegalArgumentException("No suggestions for the given prefix.");
        }

        return suggestions;
    }

    // Helper method to search for a node in the Trie
    private static NodeOfTrie1 nodeFinding(String wd) {
        NodeOfTrie1 current = rt;

        for (char ch : wd.toCharArray()) {
            int index = ch - 'a';
            current = current.children[index];
            if (current == null) {
                return null; // Prefix not found
            }
        }

        return current;
    }

    // Helper method to recursively suggest words
    private static void suggestWordsHelper(String prefix, NodeOfTrie1 node, List<String> suggestions) {
        if (node.isEndOfWord) {
            suggestions.add(prefix);
        }

        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                suggestWordsHelper(prefix + node.children[i].data, node.children[i], suggestions);
            }
        }
    }
}
