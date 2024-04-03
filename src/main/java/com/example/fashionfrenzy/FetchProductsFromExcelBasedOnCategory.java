package com.example.fashionfrenzy;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class FetchProductsFromExcelBasedOnCategory {
    // Read data from specified Excel files based on selected category
    public static List<Map<String, String>> readData(List<String> filePaths) {
        List<Map<String, String>> productsList = new ArrayList<>();

        for (String filePath : filePaths) {
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
                        String productImage = row.getCell(1).getStringCellValue();
                        String productBrand = row.getCell(2).getStringCellValue();
                        String productTitle = row.getCell(3).getStringCellValue();
                        String productPrice = row.getCell(4).getStringCellValue();
                        String productUrl = row.getCell(5).getStringCellValue();

                        // Create a map to store product details
                        Map<String, String> productDetails = new HashMap<>();
                        productDetails.put("Image", productImage);
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
        }
        return productsList;
    }
}
