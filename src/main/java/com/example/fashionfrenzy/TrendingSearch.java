package com.example.fashionfrenzy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Frequency Count : HashMap
public class TrendingSearch {
    // File name of the Excel file
    private static final String FILE_NAME = "MostSearchedQueries.xlsx";
    // Number of top searches to display
    private static final int TOP_N = 3;

    public static Map<String, Integer> loadDataFromFile() {
        Map<String, Integer> searchMap = new HashMap<>();
        try {
            // Try to open the file
            FileInputStream file = new FileInputStream(FILE_NAME);
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);
            // Iterate through each row in the sheet
            for (Row row : sheet) {
                Cell titleCell = row.getCell(0);
                Cell frequencyCell = row.getCell(1);

                // Check if the title cell is not empty and is of type STRING
                if (titleCell == null || titleCell.getCellType() != CellType.STRING) {
                    continue; // Skip empty or non-string cells
                }
                // Extract title and frequency from cells
                String title = titleCell.getStringCellValue();
                int frequency = (frequencyCell != null && frequencyCell.getCellType() == CellType.NUMERIC)
                        ? (int) frequencyCell.getNumericCellValue()
                        : 0;
                // Add title and frequency to the searchMap, converting title to lowercase
                searchMap.put(title.toLowerCase(), frequency); // Convert title to lowercase
            }
            file.close();
        } catch (IOException e) {
            // If the file does not exist, create a new one
            createNewFile();
        }
        return searchMap;
    }

    // Method to create a new Excel file with the specified file name
    private static void createNewFile() {
        try {
            FileOutputStream fileOut = new FileOutputStream(FILE_NAME);
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Most Searched Queries");
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to display the top searched queries
    public static void displayTopSearches(Map<String, Integer> searchMap) {
        System.out.println("\nTop " + TOP_N + " most searched words:");

        // Sort the entries by frequency, limit to top N, and print them
        searchMap.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(TOP_N)
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }

    // Method to update the Excel sheet with new query and frequency
    public static void updateExcelSheet(String query, int frequency) {
        try (FileInputStream file = new FileInputStream(FILE_NAME);
             Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean found = false;
            // Iterate through rows to find the matching query
            for (Row row : sheet) {
                if (row.getCell(0).getStringCellValue().equalsIgnoreCase(query)) {
                    // Update the frequency if query found
                    row.getCell(1).setCellValue(frequency);
                    found = true;
                    break;
                }
            }
            // If query not found, add it as a new row
            if (!found) {
                int lastRowNum = sheet.getLastRowNum();
                Row newRow = sheet.createRow(lastRowNum + 1);
                newRow.createCell(0).setCellValue(query);
                newRow.createCell(1).setCellValue(frequency);
            }
            // Write the changes back to the Excel file
            try (FileOutputStream outFile = new FileOutputStream(FILE_NAME)) {
                workbook.write(outFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}