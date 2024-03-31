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

    private static void getFileAndPrintProducts(String genderChoice, String categoryChoice) {
        categoryChoice = Character.toUpperCase(categoryChoice.charAt(0)) + categoryChoice.substring(1);
        List<String> websites = List.of(new String[]{"Amazon", "Boohoo", "Revolve"});
        List<String> filePaths = new ArrayList<>();
        for(String website: websites) {
            filePaths.add("src/main/resources/" + genderChoice + website + categoryChoice + ".xlsx");
        }

        List<Map<String, String>> products = readData(filePaths);
        System.out.println("Products in category " + categoryChoice + " from all sites:");
        for (Map<String, String> product : products) {
            System.out.println("Image: " + product.get("Image"));
            System.out.println("Brand: " + product.get("Brand"));
            System.out.println("Title: " + product.get("Title"));
            System.out.println("Price: " + product.get("Price"));
            System.out.println("URL: " + product.get("URL"));
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Select gender (men or women): ");
        String genderChoice = scanner.nextLine();

        // Display category options
        String categoryChoice = "";
        if(genderChoice.equals("men")) {
            System.out.println("Select category from the following (Shirt, Hoodie, Jeans, Coat, Sweater):");
            categoryChoice = scanner.nextLine().toLowerCase();
            getFileAndPrintProducts(genderChoice, categoryChoice);
        } else if(genderChoice.equals("women")) {
            System.out.println("Select category from the following (Dress, Top, Jeans, Coat, Sweater):");
            categoryChoice = scanner.nextLine().toLowerCase();
            getFileAndPrintProducts(genderChoice, categoryChoice);
        } else {
            System.out.println("Incorrect gender");
        }
        scanner.close();
    }
}
