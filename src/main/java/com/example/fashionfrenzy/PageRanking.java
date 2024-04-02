package com.example.fashionfrenzy;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class PageRanking {
    public static void main(String[] args) {
//        PageRanking pageRanking = new PageRanking();
//        pageRanking.runPageRanking();
    }

    public void run(String searchQuery, List<String> filePaths) {
        try {
            List<List<String>> allTitles = readTitlesFromExcel(filePaths);
            List<String> websites = Arrays.asList(
                    "https://www.amazon.ca/",
                    "https://ca.boohoo.com/",
                    "https://www.revolve.com/"
            );

            // Calculate and display frequency count for each website
            displayFrequencyCounts(allTitles, websites, searchQuery);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("An IO Exception occurred: " + e.getMessage());
        }
    }

    private List<List<String>> readTitlesFromExcel(List<String> filePaths) throws IOException {
        List<List<String>> allTitles = new ArrayList<>();

        for (String filePath : filePaths) {
            List<String> titles = new ArrayList<>();
            FileInputStream fileInputStream = null;
            Workbook workbook = null;
            try {
                fileInputStream = new FileInputStream(filePath);
                workbook = new XSSFWorkbook(fileInputStream);

                Sheet sheet = workbook.getSheet("Product Info");

                // Skip the first row (header row)
                boolean firstRowSkipped = false;

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
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + e.getMessage());
                throw e; // Re-throw the exception to indicate failure in processing this file
            } catch (IOException e) {
                System.out.println("An IO Exception occurred while reading file '" + filePath + "': " + e.getMessage());
                throw e; // Re-throw the exception to indicate failure in processing this file
            } finally {
                if (workbook != null) {
                    workbook.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }
            allTitles.add(titles);
        }

        return allTitles;
    }

    private void displayFrequencyCounts(List<List<String>> allTitles, List<String> websites, String searchQuery) {
        try {
            System.out.println("\nPage Ranking for '" + searchQuery + "':");
            List<Map.Entry<String, Integer>> counts = new ArrayList<>();
            for (int i = 0; i < websites.size(); i++) {
                String website = websites.get(i);
                int count = getTotalFrequencyCount(allTitles.get(i), searchQuery);
                counts.add(new AbstractMap.SimpleEntry<>(website, count));
            }
            // Sort the counts in descending order
            counts.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            for (Map.Entry<String, Integer> entry : counts) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        } catch (Exception e) {
            System.out.println("An error occurred while displaying frequency counts: " + e.getMessage());
        }
    }

    private int getTotalFrequencyCount(List<String> titles, String searchQuery) {
        try {
            int totalCount = 0;
            for (String title : titles) {
                if (title != null && title.toLowerCase().contains(searchQuery.toLowerCase())) {
                    totalCount++;
                }
            }
            return totalCount;
        } catch (Exception e) {
            System.out.println("An error occurred while calculating total frequency count: " + e.getMessage());
            return -1; // Return an indicator of failure
        }
    }
}
