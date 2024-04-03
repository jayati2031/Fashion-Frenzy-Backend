package com.example.fashionfrenzy;

import java.util.*;
import java.io.*;

import org.springframework.stereotype.Component;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

// Component class to perform frequency count
@Component
public class FrequencyCount {
    private static final String OUTPUT_FILE_PATH = "FrequencyCount.xlsx";

    // Method to execute frequency count
    public void run(String searchQuery, List<String> filePaths) {
        // List to store product lists from each file
        List<List<Map<String, String>>> allProductsLists = readDataFromExcel(filePaths);

        // Calculate total frequency count for the search query
        int totalCount = getTotalFrequencyCount(searchQuery, allProductsLists);

        // Display total frequency count
        System.out.println("Total frequency count of '" + searchQuery + "': " + totalCount);

        // Write search word and its occurrence count to Excel
        writeToExcel(searchQuery, totalCount);
    }

    // Method to read data from Excel files
    private List<List<Map<String, String>>> readDataFromExcel(List<String> filePaths) {
        // List to store product lists from each file
        List<List<Map<String, String>>> allProductsLists = new ArrayList<>();

        // Iterate through each file path
        for (String filePath : filePaths) {
            // List to store product details from a single file
            List<Map<String, String>> productsList = new ArrayList<>();
            try (FileInputStream fileInputStream = new FileInputStream(filePath);
                 Workbook workbook = new XSSFWorkbook(fileInputStream)) {

                Sheet sheet = workbook.getSheet("Product Info");

                boolean firstRowSkipped = false;

                // Iterate through each row in the sheet
                for (Row row : sheet) {
                    // Skip the first row (header row)
                    if (!firstRowSkipped) {
                        firstRowSkipped = true;
                        continue;
                    }

                    Cell cell = row.getCell(0);
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        // Get product details from the row
                        String productBrand = getCellValueAsString(row.getCell(2));
                        String productTitle = getCellValueAsString(row.getCell(3));
                        String productPrice = getCellValueAsString(row.getCell(4));
                        String productUrl = getCellValueAsString(row.getCell(5));

                        // Create a map to store product details
                        Map<String, String> productDetails = new HashMap<>();
                        productDetails.put("Brand", productBrand);
                        productDetails.put("Title", productTitle);
                        productDetails.put("Price", productPrice);
                        productDetails.put("URL", productUrl);

                        // Add product details to the list
                        productsList.add(productDetails);
                    }
                }
            } catch (IOException e) {
                // Handle IOException when reading Excel files
                System.err.println("An error occurred while reading from Excel file: " + e.getMessage());
            }
            // Add the product list to the list of all products
            allProductsLists.add(productsList);
        }

        return allProductsLists;
    }

    // Method to calculate total frequency count
    private int getTotalFrequencyCount(String searchQuery, List<List<Map<String, String>>> allProductsLists) {
        // Variable to store total frequency count
        int totalCount = 0;
        // Iterate through each product list
        for (List<Map<String, String>> productsList : allProductsLists) {
            // Iterate through each product in the list
            for (Map<String, String> product : productsList) {
                String title = product.get("Title");
                if (title != null && title.toLowerCase().contains(searchQuery.toLowerCase())) {
                    // Increment the count if the search query is found in the title
                    totalCount++;
                }
            }
        }
        return totalCount;
    }

    // Method to handle different cell types and retrieve cell value as string
    private String getCellValueAsString(Cell cell) {
        // Check if the cell is null
        if (cell == null) {
            return "";
        }
        // Determine the cell type and return the cell value as string
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else {
            return "";
        }
    }

    // Method to write search word and its occurrence count to Excel
    private void writeToExcel(String searchQuery, int totalCount) {
        try (FileInputStream fileInputStream = new FileInputStream(OUTPUT_FILE_PATH);
             Workbook workbook = fileInputStream.available() > 0 ? new XSSFWorkbook(fileInputStream) : new XSSFWorkbook()) {
            Sheet sheet = workbook.getSheet("SearchData");
            if (sheet == null) {
                sheet = workbook.createSheet("SearchData");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Search Word");
                headerRow.createCell(1).setCellValue("Frequency Count");
            }

            boolean wordFound = false;

            // Iterate through each row in the sheet
            for (Row row : sheet) {
                // Check if the search query already exists in the Excel sheet
                if (row.getCell(0) != null && row.getCell(0).getStringCellValue().equalsIgnoreCase(searchQuery)) {
                    wordFound = true;
                    int currentCount = (int) row.getCell(1).getNumericCellValue();
                    if (currentCount != totalCount) {
                        // Update the occurrence count if it has changed
                        row.getCell(1).setCellValue(totalCount);
                    }
                    break;
                }
            }

            // If the search query is not found, add a new row to the sheet
            if (!wordFound) {
                int lastRowNum = sheet.getLastRowNum();
                Row newRow = sheet.createRow(lastRowNum + 1);
                newRow.createCell(0).setCellValue(searchQuery);
                newRow.createCell(1).setCellValue(totalCount);
            }

            // Write the updated workbook to the output file
            try (FileOutputStream fileOutputStream = new FileOutputStream(OUTPUT_FILE_PATH)) {
                workbook.write(fileOutputStream);
            }
        } catch (FileNotFoundException e) {
            // Handle FileNotFoundException when the output file doesn't exist
            System.err.println("The file " + OUTPUT_FILE_PATH + " was not found.");
        } catch (IOException e) {
            // Handle IOException when writing to Excel file
            System.err.println("An error occurred while writing to Excel file: " + e.getMessage());
        }
    }
}