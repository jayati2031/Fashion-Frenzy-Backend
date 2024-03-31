package com.example.fashionfrenzy;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

// B-tree Node class
class BTreeNode {
    List<Double> keys;
    List<Map<String, String>> products;
    List<BTreeNode> children;

    BTreeNode() {
        keys = new ArrayList<>();
        products = new ArrayList<>();
        children = new ArrayList<>();
    }
}

public class ProductFilter {
    private static final int MIN_DEGREE = 3; // Minimum degree of the B-tree
    private BTreeNode root;

    public ProductFilter() {
        root = new BTreeNode();
    }

    public void insert(Map<String, String> product) {
        if (root.keys.size() == 2 * MIN_DEGREE - 1) {
            BTreeNode newRoot = new BTreeNode();
            newRoot.children.add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }
        insertNonFull(root, product);
    }

    private void insertNonFull(BTreeNode node, Map<String, String> product) {
        int i = node.keys.size() - 1;
        if (node.children.isEmpty()) {
            while (i >= 0 && Double.parseDouble(product.get("Price").replaceAll("[^\\d.]", "")) < node.keys.get(i)) {
                i--;
            }
            node.keys.add(i + 1, Double.parseDouble(product.get("Price").replaceAll("[^\\d.]", "")));
            node.products.add(i + 1, product);
        } else {
            while (i >= 0 && Double.parseDouble(product.get("Price").replaceAll("[^\\d.]", "")) < node.keys.get(i)) {
                i--;
            }
            i++;
            if (node.children.get(i).keys.size() == 2 * MIN_DEGREE - 1) {
                splitChild(node, i);
                if (Double.parseDouble(product.get("Price").replaceAll("[^\\d.]", "")) > node.keys.get(i)) {
                    i++;
                }
            }
            insertNonFull(node.children.get(i), product);
        }
    }


    private void splitChild(BTreeNode parentNode, int index) {
        BTreeNode nodeToSplit = parentNode.children.get(index);
        BTreeNode newNode = new BTreeNode();
        parentNode.keys.add(index, nodeToSplit.keys.get(MIN_DEGREE - 1));
        parentNode.products.add(index, nodeToSplit.products.get(MIN_DEGREE - 1));
        parentNode.children.add(index + 1, newNode);
        newNode.keys.addAll(nodeToSplit.keys.subList(MIN_DEGREE, 2 * MIN_DEGREE - 1));
        newNode.products.addAll(nodeToSplit.products.subList(MIN_DEGREE, 2 * MIN_DEGREE - 1));
        nodeToSplit.keys.subList(MIN_DEGREE - 1, 2 * MIN_DEGREE - 1).clear();
        nodeToSplit.products.subList(MIN_DEGREE - 1, 2 * MIN_DEGREE - 1).clear();

        if (!nodeToSplit.children.isEmpty()) {
            newNode.children.addAll(nodeToSplit.children.subList(MIN_DEGREE, 2 * MIN_DEGREE));
            nodeToSplit.children.subList(MIN_DEGREE, 2 * MIN_DEGREE).clear();
        }
    }

    // Search for products in the B-tree based on price range
    public List<Map<String, String>> searchProductsByPriceRange(double minPrice, double maxPrice) {
        List<Map<String, String>> result = new ArrayList<>();
        searchProducts(root, minPrice, maxPrice, result, new HashSet<>()); // Add empty set for unique products
        Collections.sort(result, Comparator.comparingDouble(p -> Double.parseDouble(p.get("Price").replaceAll("[^\\d.]", ""))));
        return result;
    }


    private void searchProducts(BTreeNode node, double minPrice, double maxPrice, List<Map<String, String>> result, Set<String> uniqueProducts) {
        int i = 0;
        while (i < node.keys.size() && node.keys.get(i) < minPrice) {
            i++;
        }
        if (!node.children.isEmpty()) {
            if (i < node.keys.size() && node.keys.get(i) == minPrice) {
                i++;
            }
            searchProducts(node.children.get(i), minPrice, maxPrice, result, uniqueProducts);
        }
        while (i < node.keys.size() && node.keys.get(i) <= maxPrice) {
            Map<String, String> product = node.products.get(i);
            String productKey = product.get("Title") + "_" + product.get("Price");
            if (uniqueProducts.add(productKey)) { // Add the product to the result if it's not already present
                result.add(product);
            }
            i++;
        }
        if (!node.children.isEmpty()) {
            searchProducts(node.children.get(i), minPrice, maxPrice, result, uniqueProducts);
        }
    }


    // Read all product information from Excel files and insert into the B-tree
    private void readAndInsertAllProducts(String[] files) throws IOException {
        for (String file : files) {
            try {
                List<Map<String, String>> data = readProductInfoFromExcel(file);
                for (Map<String, String> product : data) {
                    insert(product);
                }
            } catch (IOException e) {
                System.err.println("An error occurred while reading product information from " + file + ": " + e.getMessage());
                throw e;
            }
        }
    }

    // Read product information from Excel file
    private List<Map<String, String>> readProductInfoFromExcel(String filePath) throws IOException {
        List<Map<String, String>> products = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            int colCount = headerRow.getLastCellNum();
            String[] headers = new String[colCount];
            for (int i = 0; i < colCount; i++) {
                headers[i] = headerRow.getCell(i).getStringCellValue();
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Map<String, String> product = new HashMap<>();
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        if (cell.getCellType() == CellType.NUMERIC) {
                            product.put(headers[j], String.valueOf(cell.getNumericCellValue()));
                        } else if (cell.getCellType() == CellType.STRING) {
                            product.put(headers[j], cell.getStringCellValue());
                        }
                    }
                }
                products.add(product);
            }
            workbook.close();
        }
        return products;
    }

    // Filter products by brands
    private void filterProductsByBrands(String[] brands) throws IOException {
        List<Map<String, String>> products = getAllProducts();

        System.out.println("Products for selected brand(s):");
        for (String brandInput : brands) {
            boolean brandFound = false;
            for (Map<String, String> product : products) {
                String brand = product.get("Brand");
                if (brand.toLowerCase().contains(brandInput.trim().toLowerCase())) {
                    printProductInfo(product);
                    brandFound = true;
                }
            }
            if (!brandFound) {
                System.out.println("Sorry!... Brand with the name " + brandInput + " doesn't exist");
            }
        }
    }

    // Filter products by both brand and price range
    private void filterProductsByBothBrandAndPriceRange(String[] brands, double minPrice, double maxPrice) throws IOException {
        List<Map<String, String>> products = getAllProducts();

        System.out.println("Products for selected brand(s) and price range:");
        List<Map<String, String>> filteredProducts = new ArrayList<>();

        for (String brandInput : brands) {
            boolean brandFound = false;
            boolean productsFound = false;

            for (Map<String, String> product : products) {
                String brand = product.get("Brand");
                double price = Double.parseDouble(product.get("Price").replaceAll("[^\\d.]", ""));
                if (brand.toLowerCase().contains(brandInput.trim().toLowerCase()) && price >= minPrice && price <= maxPrice) {
                    filteredProducts.add(product);
                    brandFound = true;
                    productsFound = true;
                } else if (brand.toLowerCase().contains(brandInput.trim().toLowerCase())) {
                    brandFound = true;
                }
            }

            if (!brandFound) {
                System.out.println("Sorry!.... Brand with the name " + brandInput + " doesn't exist");
            } else if (!productsFound) {
                System.out.println("Sorry!.... No products found within the specified price range for the brand: " + brandInput);
            }
        }

        if (!filteredProducts.isEmpty()) {
            // Sort filtered products by price
            filteredProducts.sort(Comparator.comparingDouble(p -> Double.parseDouble(p.get("Price").replaceAll("[^\\d.]", ""))));
            printProducts(filteredProducts);
        }
    }

    // Read all product information from Excel files
    private List<Map<String, String>> getAllProducts() throws IOException {
        List<Map<String, String>> products = new ArrayList<>();
        String[] files = {
                "src\\main\\resources\\womenBoohooDress.xlsx",
                "src\\main\\resources\\womenAmazonDress.xlsx",
                "src\\main\\resources\\womenRevolveDress.xlsx"
        };
        for (String file : files) {
            try {
                List<Map<String, String>> data = readProductInfoFromExcel(file);
                products.addAll(data);
            } catch (IOException e) {
                // Handle IOException when reading Excel files
                System.err.println("An error occurred while reading product information from " + file + ": " + e.getMessage());
                throw e; // Re-throw the exception to the calling method
            }
        }
        return products;
    }

    // Main method
    public static void main(String[] args) {
        ProductFilter productFilter = new ProductFilter();
        String[] files = {
                "src\\main\\resources\\womenBoohooDress.xlsx",
                "src\\main\\resources\\womenAmazonDress.xlsx",
                "src\\main\\resources\\womenRevolveDress.xlsx"};
        try {
            productFilter.readAndInsertAllProducts(files);
        } catch (IOException e) {
            System.err.println("An error occurred while reading product information: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        double minPrice, maxPrice;
        String brandInput;
        String[] brands;

        while (true) {
            System.out.println("Select the filtering option:");
            System.out.println("1. Based on Brands");
            System.out.println("2. Based on Price Range");
            System.out.println("3. Based on both Brand and Price Range");
            System.out.println("4. Exit");

            int option = scanner.nextInt();
            switch (option) {
                case 1:
                    System.out.print("Enter brand names separated by commas: ");
                    scanner.nextLine(); // Consume newline
                    brandInput = scanner.nextLine();
                    brands = brandInput.split(",");
                    try {
                        productFilter.filterProductsByBrands(brands);
                    } catch (IOException e) {
                        System.err.println("An error occurred while filtering products by brands: " + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.print("Enter the minimum price: ");
                    minPrice = scanner.nextDouble();
                    System.out.print("Enter the maximum price: ");
                    maxPrice = scanner.nextDouble();
                    List<Map<String, String>> filteredProductsByPrice = productFilter.searchProductsByPriceRange(minPrice, maxPrice);
                    if (!filteredProductsByPrice.isEmpty()) {
                        System.out.println("Products in the price range $" + minPrice + " - $" + maxPrice + ":\n");
                        productFilter.printProducts(filteredProductsByPrice);
                    } else {
                        System.out.println("No products found in the specified price range.");
                    }
                    break;
                case 3:
                    System.out.print("Enter brand names separated by commas: ");
                    scanner.nextLine(); // Consume newline
                    brandInput = scanner.nextLine();
                    brands = brandInput.split(",");
                    System.out.print("Enter the minimum price: ");
                    minPrice = scanner.nextDouble();
                    System.out.print("Enter the maximum price: ");
                    maxPrice = scanner.nextDouble();
                    try {
                        productFilter.filterProductsByBothBrandAndPriceRange(brands, minPrice, maxPrice);
                    } catch (IOException e) {
                        System.err.println("An error occurred while filtering products by both brand and price range: " + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option selected.");
            }
        }
    }

    // Print product information
    private void printProductInfo(Map<String, String> product) {
        System.out.println("Brand: " + product.get("Brand"));
        System.out.println("Title: " + product.get("Title"));
        System.out.println("Price: " + product.get("Price"));
        System.out.println("URL: " + product.get("URL"));
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    // Print list of products
    private void printProducts(List<Map<String, String>> products) {
        for (Map<String, String> product : products) {
            printProductInfo(product);
        }
    }
}
