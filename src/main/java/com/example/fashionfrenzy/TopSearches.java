package com.example.fashionfrenzy;

import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;

import org.springframework.stereotype.Component;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;

@Component
public class TopSearches {

    // File name for search history
    private static final String SEARCH_HISTORY_FILE = "SearchHistory.xlsx";
    // Root node of the search history tree
    private HistoryNode root;
    // Map to store top searches
    private Map<String, Integer> topSearches;

    // Inner class representing a node in the search history tree
    public class HistoryNode {
        String word;
        int frequency;
        HistoryNode left, right;

        HistoryNode(String word, int frequency) {
            this.word = word;
            this.frequency = frequency;
        }
    }

    // Constructor
    public TopSearches() {
        topSearches = new LinkedHashMap<>();
    }

    // Method to insert a search term into the search history
    public void insertHistory(String word) throws IOException {
        try {
            root = insertHistory(root, word); // Insert into search history tree
            updateSearchHistory(SEARCH_HISTORY_FILE, word); // Update search history file
        } catch (IOException e) {
            // Handle IOException
            System.err.println("IOException occurred while inserting history: " + e.getMessage());
            throw e; // Re-throw the exception
        }
    }

    // Recursive method to insert a search term into the search history tree
    private HistoryNode insertHistory(HistoryNode node, String word) {
        if (node == null) {
            return new HistoryNode(word, 1); // Initialize frequency to 1 for new word
        }

        int cmp = word.compareToIgnoreCase(node.word);
        if (cmp < 0) {
            node.left = insertHistory(node.left, word);
        } else if (cmp > 0) {
            node.right = insertHistory(node.right, word);
        } else {
            node.frequency++; // Increment frequency if word already exists
        }

        return node;
    }

    // Method to display the top searched terms
    public void displayTopSearches() {
        try {
            topSearches.clear(); // Clear existing top searches
            updateTopSearches(root); // Update top searches map
            System.out.println("\tTrending Searches");
            // Sort the top searches by frequency and print the top 5
            List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(topSearches.entrySet());
            Collections.sort(sortedList, (o1, o2) -> o2.getValue().compareTo(o1.getValue())); // Sort by value in descending order
            int count = 0;
            for (Map.Entry<String, Integer> entry : sortedList) {
                if (count >= 5) break;
                System.out.println(entry.getKey() + " : " + entry.getValue());
                count++;
            }
        } catch (Exception e) {
            // Handle any other exceptions
            System.err.println("An error occurred while displaying top searches: " + e.getMessage());
        }
    }

    // Method to update the top searched terms
    private void updateTopSearches(HistoryNode node) {
        if (node == null) {
            return;
        }

        updateTopSearches(node.right);

        // Update the top searches map
        if (topSearches.size() < 5) {
            topSearches.put(node.word, node.frequency);
        } else {
            // Replace the least frequent search term if the current frequency is higher
            Map.Entry<String, Integer> minEntry = Collections.min(topSearches.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
            if (node.frequency > minEntry.getValue() && !topSearches.containsKey(node.word)) {
                topSearches.remove(minEntry.getKey());
                topSearches.put(node.word, node.frequency);
            }
        }

        updateTopSearches(node.left);
    }

    // Method to create or load the search history file
    public void createOrLoadSearchHistory() throws IOException {
        try {
            File file = new File(SEARCH_HISTORY_FILE);
            if (!file.exists()) {
                createSearchHistoryFile(file); // Create search history file if it doesn't exist
            } else {
                root = null; // Reset root before loading the file
                loadSearchHistoryFile(file); // Load search history from file
            }
        } catch (IOException e) {
            // Handle IOException
            System.err.println("IOException occurred while creating or loading search history: " + e.getMessage());
            throw e; // Re-throw the exception
        }
    }

    // Method to create a new search history file
    private void createSearchHistoryFile(File file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("SearchHistory");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Word");
        headerRow.createCell(1).setCellValue("Frequency");
        FileOutputStream fos = new FileOutputStream(file);
        workbook.write(fos);
        fos.close();
        workbook.close();
    }

    // Method to load search history from file
    private void loadSearchHistoryFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheet("SearchHistory");

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            String word = row.getCell(0).getStringCellValue();
            int frequency = (int) row.getCell(1).getNumericCellValue();
            root = insertHistory(root, word, frequency); // Update frequency when loading history
        }

        fis.close();
        workbook.close();
    }

    // Method to insert search history into the search history tree while loading from file
    private HistoryNode insertHistory(HistoryNode node, String word, int frequency) {
        if (node == null) {
            return new HistoryNode(word, frequency);
        }

        int cmp = word.compareToIgnoreCase(node.word);
        if (cmp < 0) {
            node.left = insertHistory(node.left, word, frequency);
        } else if (cmp > 0) {
            node.right = insertHistory(node.right, word, frequency);
        } else {
            node.frequency = frequency; // Set frequency if word already exists
        }

        return node;
    }

    // Method to update the search history file with a new search term
    public void updateSearchHistory(String fileName, String word) throws IOException {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheet("SearchHistory");

            boolean wordFound = false;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                String currentWord = row.getCell(0).getStringCellValue();
                if (currentWord.equalsIgnoreCase(word)) {
                    int frequency = (int) row.getCell(1).getNumericCellValue();
                    row.getCell(1).setCellValue(frequency + 1); // Increment frequency by 1
                    wordFound = true;
                    break;
                }
            }

            if (!wordFound) {
                // If word not found, add it with frequency 1
                XSSFRow newRow = sheet.createRow(sheet.getLastRowNum() + 1);
                newRow.createCell(0).setCellValue(word);
                newRow.createCell(1).setCellValue(1); // Set frequency to 1
            }

            fis.close();
            FileOutputStream fos = new FileOutputStream(fileName);
            workbook.write(fos);
            fos.close();
            workbook.close();
        } catch (IOException e) {
            // Handle IOException
            System.err.println("IOException occurred while updating search history: " + e.getMessage());
            throw e; // Re-throw the exception
        }
    }
}
