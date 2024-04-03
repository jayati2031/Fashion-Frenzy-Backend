package com.example.fashionfrenzy.controller;

import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class TopSearchesController {

    @GetMapping("/trending-searches")
    public List<Map<String, Integer>> getTopTrendingSearches() {
        List<Map<String, Integer>> result = new ArrayList<>();
        boolean isFirstRow = true; // Flag to skip the first row
        FileInputStream fis = null;
        Workbook workbook = null;
        try {
            // Load the Excel file from the classpath
            fis = new FileInputStream("SearchHistory.xlsx");
            // Create a workbook instance for the XLSX file
            workbook = new XSSFWorkbook(fis);
            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);
            // Create a TreeMap to store searchKey and count, sorted by count
            TreeMap<Integer, String> sortedMap = new TreeMap<>(Collections.reverseOrder());
            // Iterate through the rows
            for (Row row : sheet) {
                // Skip the first row
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }
                // Assuming the first column contains searchKey and the second column contains count
                Cell searchKeyCell = row.getCell(0);
                Cell countCell = row.getCell(1);
                if (searchKeyCell != null && countCell != null) {
                    // Retrieve data from cells
                    String searchKey = searchKeyCell.getStringCellValue();
                    Integer count = (int) countCell.getNumericCellValue();
                    // Put searchKey and count into the TreeMap
                    sortedMap.put(count, searchKey);
                }
            }
            // Get the top 3 results from the sorted TreeMap
            int count = 0;
            for (Map.Entry<Integer, String> entry : sortedMap.entrySet()) {
                // Create a map to hold current row's data
                Map<String, Integer> rowData = new HashMap<>();
                rowData.put(entry.getValue(), entry.getKey());
                // Add rowData to result list
                result.add(rowData);
                count++;
                if (count >= 3) {
                    break; // Exit the loop after adding the top 3 results
                }
            }
        } catch (IOException e) {
            // Handle IOException
            System.out.println("IOException occurred: " + e.getMessage());
        } finally {
            // Close resources in finally block to ensure they are closed even if an exception occurs
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    System.out.println("Error while closing workbook: " + e.getMessage());
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    System.out.println("Error while closing FileInputStream: " + e.getMessage());
                }
            }
        }
        return result;
    }
}
