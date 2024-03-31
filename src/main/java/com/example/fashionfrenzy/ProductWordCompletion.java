package com.example.fashionfrenzy;

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

//Trie class implements the Trie data structure
class DSTrie {
    private final NodeOfTrie1 rt;

    public DSTrie() {
        rt = new NodeOfTrie1('\0');
    }

    // Insert a word into the Trie
    public void wordInsert(String word) {
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
    public void suggestWords(String prefix) {
        NodeOfTrie1 prefixNode = nodeFinding(prefix);

        if (prefixNode != null) {
            suggestWordsHelper(prefix, prefixNode);
        } else {
            System.out.println("No suggestions for the given prefix.");
        }
    }

    // Helper method to search for a node in the Trie
    private NodeOfTrie1 nodeFinding(String wd) {
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
    private void suggestWordsHelper(String prefix, NodeOfTrie1 node) {
        if (node.isEndOfWord) {
            System.out.println(prefix);
        }

        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                suggestWordsHelper(prefix + node.children[i].data, node.children[i]);
            }
        }
    }
}

public class ProductWordCompletion {

    public static void main(String[] args) {
        List<String> filePaths = new ArrayList<>();
        filePaths.add("src\\main\\resources\\womenAmazonDresses.xlsx");
        filePaths.add("src\\main\\resources\\womenBoohooDresses.xlsx");
        filePaths.add("src\\main\\resources\\womenRevolveDresses.xlsx");

        List<Map<String, String>> products = FetchProductsFromExcelBasedOnCategory.readData(filePaths);
        DSTrie trie = new DSTrie();

        for (Map<String, String> product : products) {
            String title = product.get("Title");
            String[] titleWords = title.split("\\s+");
            for (String titleWord : titleWords) {
                // For simplicity, remove non-alphabetic characters
                titleWord = titleWord.replaceAll("[^a-zA-Z]", "").toLowerCase();
                if (!titleWord.isEmpty()) {
                    trie.wordInsert(titleWord);
                }
            }
        }

        Scanner scanner = new Scanner(System.in);

        // Continuous user input
        boolean menu = true;
        while(menu)
        {
            System.out.println("Enter \"exit\" to exit.");
            System.out.print("Enter the title to search for: ");
            String searchTitle = scanner.nextLine();
            if ("exit".equalsIgnoreCase(searchTitle)) {
                System.out.println("Exiting..");
                menu = false;
            } else if (!searchTitle.isEmpty()) {
                trie.suggestWords(searchTitle);
            }
        }
        scanner.close();
    }
}
