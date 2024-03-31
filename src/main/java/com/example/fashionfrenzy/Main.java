package com.example.fashionfrenzy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // Read products from Excel files
        List<String> filePaths = new ArrayList<>();
        filePaths.add("src\\main\\resources\\womenAmazonDress.xlsx");
        filePaths.add("src\\main\\resources\\womenBoohooDress.xlsx");
        filePaths.add("src\\main\\resources\\womenRevolveDress.xlsx");

        List<Map<String, String>> products = FetchProductsFromExcelBasedOnCategory.readData(filePaths);

        System.out.println("Select an option:");
        System.out.println("1. Search");
        System.out.println("2. Sort");
        System.out.println("3. Word Completion");
        System.out.println("4. Send Deals");
        System.out.println("5. Filter");

        Scanner scanner = new Scanner(System.in);
        // Read user input
        int option = scanner.nextInt();

        // Process user choice
        switch (option) {
            case 1:
                // Search and Spell Check
                
                break;
            case 2:
                // Sorting
                
                break;
            case 3:
                // Word completion
                
                break;
            case 4:
                // Send deals

                break;
            case 5:
                // Filter Products

                break;
            default:
                System.out.println("Invalid option");
                break;
        }
        scanner.close();
    }
}
