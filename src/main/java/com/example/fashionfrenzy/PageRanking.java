package com.example.fashionfrenzy;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * This class implements page ranking based on frequency count of search queries in Excel files.
 */
@Component
public class PageRanking {

    /**
     * Runs the page ranking algorithm.
     *
     * @param searchQuery The search query to rank pages against
     * @param filePaths   The list of file paths to Excel files containing product data
     * @return A list of maps containing filtered product data based on the search query
     */
    public List<Map<String, String>> run(String searchQuery, List<String> filePaths) {
        try {
            // Get file paths with their frequencies
            Map<String, Integer> filePathsWithFrequencies = getFilePathsWithFrequencies(filePaths, searchQuery);
            // Read data from sorted file paths
            List<Map<String, String>> allData = FetchProductsFromExcelBasedOnCategory.readData(getSortedFilePaths(filePathsWithFrequencies));

            // Filter data based on case-insensitive search query
            List<Map<String, String>> filteredData = new ArrayList<>();
            for (Map<String, String> data : allData) {
                String title = data.get("Title");
                if (title != null && title.toLowerCase().contains(searchQuery.toLowerCase())) {
                    filteredData.add(data);
                }
            }
            return filteredData;
        } catch (Exception e) {
            System.out.println("An IO Exception occurred: " + e.getMessage());
        }
        return null; // Return null if an exception occurs
    }

    /**
     * Gets file paths with their frequencies.
     *
     * @param filePaths    The list of file paths
     * @param searchQuery  The search query to calculate frequencies for
     * @return A map containing file paths and their corresponding frequencies
     */
    private Map<String, Integer> getFilePathsWithFrequencies(List<String> filePaths, String searchQuery) {
        Map<String, Integer> filePathsWithFrequencies = new HashMap<>();
        for (String filePath : filePaths) {
            try {
                // Read titles from Excel file
                List<String> titles = readTitlesFromExcel(filePath);
                // Get total frequency count for the search query
                int frequencyCount = getTotalFrequencyCount(titles, searchQuery);
                // Store file path with its frequency in the map
                filePathsWithFrequencies.put(filePath, frequencyCount);
            } catch (IOException e) {
                // Handle IO exceptions while processing each file
                System.out.println("An IO Exception occurred while processing file: " + filePath + ", Error: " + e.getMessage());
            }
        }
        return filePathsWithFrequencies;
    }

    /**
     * Reads titles from an Excel file.
     *
     * @param filePath The file path of the Excel file
     * @return A list of titles extracted from the Excel file
     * @throws IOException if an I/O error occurs while reading the file
     */
    private List<String> readTitlesFromExcel(String filePath) throws IOException {
        List<String> titles = new ArrayList<>();
        FileInputStream fileInputStream = null;
        Workbook workbook = null;
        try {
            // Open Excel file input stream and workbook
            fileInputStream = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheet("Product Info");

            // Skip the first row (header row)
            boolean firstRowSkipped = false;

            // Iterate through rows to read titles
            for (Row row : sheet) {
                if (!firstRowSkipped) {
                    firstRowSkipped = true;
                    continue;
                }
                Cell cell = row.getCell(3); // Assuming product titles are in the third column (index 2)
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    String title = cell.getStringCellValue();
                    titles.add(title);
                }
            }
        } finally {
            // Close workbook and file input stream in finally block to ensure they are closed
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                System.out.println("Failed to close workbook: " + e.getMessage());
            }
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                System.out.println("Failed to close file input stream: " + e.getMessage());
            }
        }
        return titles; // Return the list of titles
    }

    /**
     * Gets the total frequency count of a search query within a list of titles.
     *
     * @param titles      The list of titles
     * @param searchQuery The search query to calculate frequency count for
     * @return The total frequency count of the search query within the list of titles
     */
    private int getTotalFrequencyCount(List<String> titles, String searchQuery) {
        int totalCount = 0;
        for (String title : titles) {
            try {
                if (title != null && title.toLowerCase().contains(searchQuery.toLowerCase())) {
                    totalCount++;
                }
            } catch (NullPointerException e) {
                // Handle NullPointerException while processing each title
                System.out.println("NullPointerException occurred while processing title: " + title + ", Error: " + e.getMessage());
            }
        }
        return totalCount; // Return the total frequency count
    }

    /**
     * Sorts file paths based on their frequencies.
     *
     * @param filePathsWithFrequencies A map containing file paths and their corresponding frequencies
     * @return A list of sorted file paths based on their frequencies
     */
    private List<String> getSortedFilePaths(Map<String, Integer> filePathsWithFrequencies) {
        List<String> sortedFilePaths = new ArrayList<>();
        PriorityQueue<Map.Entry<String, Integer>> priorityQueue = new PriorityQueue<>(
                (a, b) -> b.getValue().compareTo(a.getValue()));

        try {
            // Add entries to priority queue
            priorityQueue.addAll(filePathsWithFrequencies.entrySet());

            // Populate sorted file paths list and print file paths with frequencies
            while (!priorityQueue.isEmpty()) {
                Map.Entry<String, Integer> entry = priorityQueue.poll();
                sortedFilePaths.add(entry.getKey());
                System.out.println("File Path: " + entry.getKey() + " : Frequency Count: " + entry.getValue());
            }
        } catch (NullPointerException e) {
            // Handle NullPointerException while sorting file paths
            System.out.println("NullPointerException occurred while sorting file paths: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions while sorting file paths
            System.out.println("An unexpected exception occurred while sorting file paths: " + e.getMessage());
        }

        return sortedFilePaths; // Return the sorted file paths
    }
}