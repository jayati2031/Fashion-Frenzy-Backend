package com.example.fashionfrenzy;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

// Frequency Count using HashMap
public class FrequencyCount {
    private static final String OUTPUT_FILE_PATH = "FrequencyCount.xlsx";

    public static void main(String[] args) {
//        FrequencyCount frequencyCount = new FrequencyCount();
//        frequencyCount.run();
    }

    public void run(String searchQuery, List<String> filePaths) {
        List<List<Map<String, String>>> allProductsLists = readDataFromExcel(filePaths);

        // Calculate total frequency count for the search query
        int totalCount = getTotalFrequencyCount(searchQuery, allProductsLists);

        // Display total frequency count
        System.out.println("Total frequency count of '" + searchQuery + "': " + totalCount);

        // Write the search word and its occurrence count to Excel
        writeToExcel(searchQuery, totalCount);
    }

    private List<List<Map<String, String>>> readDataFromExcel(List<String> filePaths) {
        List<List<Map<String, String>>> allProductsLists = new ArrayList<>();

        for (String filePath : filePaths) {
            List<Map<String, String>> productsList = new ArrayList<>();
            try (FileInputStream fileInputStream = new FileInputStream(filePath);
                 Workbook workbook = new XSSFWorkbook(fileInputStream)) {

                Sheet sheet = workbook.getSheet("Product Info");

                // Skip the first row (header row)
                boolean firstRowSkipped = false;

                for (Row row : sheet) {
                    if (!firstRowSkipped) {
                        firstRowSkipped = true;
                        continue;
                    }

                    Cell cell = row.getCell(0); // Assuming category is stored in the first column
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        // Assuming product data starts from the second column
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
                System.out.println("IOException Occurred: " + e.getMessage());
            }
            allProductsLists.add(productsList);
        }

        return allProductsLists;
    }

    private int getTotalFrequencyCount(String searchQuery, List<List<Map<String, String>>> allProductsLists) {
        int totalCount = 0;
        for (List<Map<String, String>> productsList : allProductsLists) {
            for (Map<String, String> product : productsList) {
                String title = product.get("Title");
                if (title != null && title.toLowerCase().contains(searchQuery.toLowerCase())) {
                    totalCount++;
                }
            }
        }
        return totalCount;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
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

            for (Row row : sheet) {
                if (row.getCell(0) != null && row.getCell(0).getStringCellValue().equalsIgnoreCase(searchQuery)) {
                    wordFound = true;
                    int currentCount = (int) row.getCell(1).getNumericCellValue();
                    if (currentCount != totalCount) {
                        row.getCell(1).setCellValue(totalCount);
//                    System.out.println("Updated occurrence count for '" + searchQuery + "' in the Excel file.");
                    }
                    break;
                }
            }

            if (!wordFound) {
                int lastRowNum = sheet.getLastRowNum();
                Row newRow = sheet.createRow(lastRowNum + 1);
                newRow.createCell(0).setCellValue(searchQuery);
                newRow.createCell(1).setCellValue(totalCount);
//            System.out.println("Added new word '" + searchQuery + "' to the Excel file.");
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(OUTPUT_FILE_PATH)) {
                workbook.write(fileOutputStream);
            }
        } catch (FileNotFoundException e) {
            // If the file doesn't exist, create it
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("SearchData");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Search Word");
                headerRow.createCell(1).setCellValue("Frequency Count");

                Row newRow = sheet.createRow(1);
                newRow.createCell(0).setCellValue(searchQuery);
                newRow.createCell(1).setCellValue(totalCount);

                try (FileOutputStream fileOutputStream = new FileOutputStream(OUTPUT_FILE_PATH)) {
                    workbook.write(fileOutputStream);
                }
            } catch (IOException ex) {
                System.out.println("IOException Occurred: " + ex.getMessage());
            }
        } catch (IOException e) {
            System.out.println("IOException Occurred: " + e.getMessage());
        }
    }
}
