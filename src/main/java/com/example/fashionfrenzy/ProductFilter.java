package com.example.fashionfrenzy;

import java.util.*;
import java.io.IOException;
import java.io.FileInputStream;
import org.springframework.stereotype.Component;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

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

@Component
public class ProductFilter {
    private static final int MIN_DEGREE = 3; // Minimum degree of the B-tree
    private BTreeNode root;

    public ProductFilter() {
        root = new BTreeNode();
    }

    // Insert a product into the B-tree
    public void insert(Map<String, String> product) {
        if (root.keys.size() == 2 * MIN_DEGREE - 1) {
            BTreeNode newRoot = new BTreeNode();
            newRoot.children.add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }
        insertNonFull(root, product);
    }

    // Recursive method to insert a product into the B-tree
    private void insertNonFull(BTreeNode node, Map<String, String> product) {
        try {
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
        } catch (NumberFormatException e) {
            // Handle NumberFormatException appropriately
            handleNumberFormatException(e);
        }
    }

    // Split a child node
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
        try {
            searchProducts(root, minPrice, maxPrice, result, new HashSet<>()); // Add empty set for unique products
            Collections.sort(result, Comparator.comparingDouble(p -> Double.parseDouble(p.get("Price").replaceAll("[^\\d.]", ""))));
        } catch (NumberFormatException e) {
            // Handle NumberFormatException appropriately
            handleNumberFormatException(e);
        }
        return result;
    }

    // Recursive method to search for products in the B-tree based on price range
    private void searchProducts(BTreeNode node, double minPrice, double maxPrice, List<Map<String, String>> result, Set<String> uniqueProducts) {
        try {
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
        } catch (NumberFormatException e) {
            // Handle NumberFormatException appropriately
            handleNumberFormatException(e);
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
        } catch (IOException e) {
            // Handle IOException appropriately
            handleIOException(e);
        }
        return products;
    }

    // Filter products by brands
    public List<Map<String, String>> filterProductsByBrands(List<Map<String, String>> products, String[] brands) throws IOException {
        List<Map<String, String>> filteredProducts = new ArrayList<>();
        try {
            for (String brandInput : brands) {
                boolean brandFound = false;
                for (Map<String, String> product : products) {
                    String brand = product.get("Brand");
                    if (brand.toLowerCase().contains(brandInput.trim().toLowerCase())) {
                        filteredProducts.add(product);
                        brandFound = true;
                    }
                }
                if (!brandFound) {
                    System.out.println("Sorry!... Brand with the name " + brandInput + " doesn't exist");
                }
            }
        } catch (Exception e) {
            // Handle any other exception appropriately
            handleGenericException(e);
        }
        return filteredProducts;
    }

    // Filter products by both brand and price range
    public List<Map<String, String>> filterProductsByBothBrandAndPriceRange(List<Map<String, String>> products,
                                                                            String[] brands, double minPrice, double maxPrice)
            throws IOException {
        List<Map<String, String>> filteredProducts = new ArrayList<>();
        try {
            for (String brandInput : brands) {
                boolean brandFound = false;
                boolean productsFound = false;

                for (Map<String, String> product : products) {
                    String brand = product.get("Brand");
                    double price = Double.parseDouble(product.get("Price").replaceAll("[^\\d.]", ""));
                    boolean contains = brand.toLowerCase().contains(brandInput.trim().toLowerCase());
                    if (contains && price >= minPrice && price <= maxPrice) {
                        filteredProducts.add(product);
                        brandFound = true;
                        productsFound = true;
                    } else if (contains) {
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
            }
        } catch (NumberFormatException e) {
            // Handle NumberFormatException appropriately
            handleNumberFormatException(e);
        } catch (Exception e) {
            // Handle any other exception appropriately
            handleGenericException(e);
        }
        return filteredProducts;
    }

    // Handle NumberFormatException
    private void handleNumberFormatException(NumberFormatException e) {
        // Log or handle the exception appropriately
        System.err.println("NumberFormatException occurred: " + e.getMessage());
    }

    // Handle IOException
    private void handleIOException(IOException e) {
        // Log or handle the exception appropriately
        System.err.println("IOException occurred: " + e.getMessage());
    }

    // Handle any other generic exception
    private void handleGenericException(Exception e) {
        // Log or handle the exception appropriately
        System.err.println("An unexpected error occurred: " + e.getMessage());
    }
}
