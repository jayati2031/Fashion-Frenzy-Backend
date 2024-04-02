package com.example.fashionfrenzy;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class TopSearches {

    private static final String SEARCH_HISTORY_FILE = "SearchHistory.xlsx";
    private HistoryNode root;
    private Map<String, Integer> topSearches;

    public class HistoryNode {
        String word;
        int frequency;
        HistoryNode left, right;

        HistoryNode(String word, int frequency) {
            this.word = word;
            this.frequency = frequency;
        }
    }

    public TopSearches() {
        topSearches = new LinkedHashMap<>();
    }

    public void insertHistory(String word) throws IOException {
        root = insertHistory(root, word);
        updateSearchHistory(SEARCH_HISTORY_FILE, word);
    }

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

    public void displayTopSearches() {
        topSearches.clear();
        updateTopSearches(root);
        System.out.println("\tTrending Searches");
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(topSearches.entrySet());
        Collections.sort(sortedList, (o1, o2) -> o2.getValue().compareTo(o1.getValue())); // Sort by value in descending order
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedList) {
            if (count >= 5) break;
            System.out.println(entry.getKey() + " : " + entry.getValue());
            count++;
        }
    }

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

    public void createOrLoadSearchHistory() throws IOException {
        File file = new File(SEARCH_HISTORY_FILE);
        if (!file.exists()) {
            createSearchHistoryFile(file);
        } else {
            root = null; // Reset root before loading the file
            loadSearchHistoryFile(file);
        }
    }

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

    public void updateSearchHistory(String fileName, String word) throws IOException {
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
    }
}

