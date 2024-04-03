package com.example.fashionfrenzy;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class FetchProductsFromExcelBasedOnCategory {
    // Read data from specified Excel files based on selected category
    // Reading the data from the Excel file
    public static List<Map<String, String>> readData(List<String> filePaths) {
        List<Map<String, String>> productsList = new ArrayList<>();

        for (String filePath : filePaths) {
            try (FileInputStream fileInputStream = new FileInputStream(filePath);
                 Workbook workbook = new XSSFWorkbook(fileInputStream)) {
                // Get the sheet named "Product Info" from the workbook
                Sheet sheet = workbook.getSheet("Product Info");
                // Skip the first row (header row)
                boolean firstRowSkipped = false;
                // Iterate through each row in the sheet
                // repeated for each row in the Excel sheet
                for (Row row : sheet) {
                    if (!firstRowSkipped) {
                        firstRowSkipped = true;
                        continue; // Skip the header row
                    }
                    // Assuming category is stored in the first column
                    Cell cell = row.getCell(0);
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        // Assuming product data starts from the second column
                        String productImage = row.getCell(1).getStringCellValue();
                        String productBrand = row.getCell(2).getStringCellValue();
                        String productTitle = row.getCell(3).getStringCellValue();
                        String productPrice = row.getCell(4).getStringCellValue();
                        String productUrl = row.getCell(5).getStringCellValue();

                        // Create a map to store product details
                        // Store image, brand, title, price, url
                        Map<String, String> productDetails = new HashMap<>();
                        productDetails.put("Image", productImage);
                        productDetails.put("Brand", productBrand);
                        productDetails.put("Title", productTitle);
                        productDetails.put("Price", productPrice);
                        productDetails.put("URL", productUrl);

                        // Add product details to the list
                        // insertion operation in the list
                        productsList.add(productDetails);
                    }
                }
            } catch (IOException e) {
                // Handle IOException
                // Perform exception handling
                System.out.println("IOException Occurred while reading file '" + filePath + "': " + e.getMessage());
            } catch (Exception e) {
                // Handle other exceptions

                // Perform exception handling
                System.out.println("An error occurred while reading file '" + filePath + "': " + e.getMessage());
            }
        }
        // return the list of products
        return productsList;
    }
}
