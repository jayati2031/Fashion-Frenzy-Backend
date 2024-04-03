package com.example.fashionfrenzy;

import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class ProductSorter {

    // Merge Sort implementation for sorting products based on a given comparator
    public static void mergeSort(List<Map<String, String>> products, Comparator<Map<String, String>> comparator, boolean ascending) {
        try {
            // Base case: If there is only one element or less, the list is already sorted
            if (products.size() <= 1) {
                return;
            }

            // Split the list into two halves
            int mid = products.size() / 2;
            List<Map<String, String>> left = new ArrayList<>(products.subList(0, mid));
            List<Map<String, String>> right = new ArrayList<>(products.subList(mid, products.size()));

            // Recursively sort the left and right halves
            mergeSort(left, comparator, ascending);
            mergeSort(right, comparator, ascending);

            // Merge the sorted halves
            merge(products, left, right, comparator, ascending);
        } catch (Exception e) {
            // Log and handle any exceptions that occur during the sorting process
            System.out.println("An error occurred during merge sort: " + e.getMessage());
            throw e;
        }
    }

    // Merge two sorted lists into one sorted list
    private static void merge(List<Map<String, String>> products, List<Map<String, String>> left,
                              List<Map<String, String>> right, Comparator<Map<String, String>> comparator, boolean ascending) {
        try {
            int leftIndex = 0, rightIndex = 0, index = 0;

            // Merge the two lists while maintaining the sorted order
            while (leftIndex < left.size() && rightIndex < right.size()) {
                if ((ascending && comparator.compare(left.get(leftIndex), right.get(rightIndex)) <= 0) ||
                        (!ascending && comparator.compare(left.get(leftIndex), right.get(rightIndex)) >= 0)) {
                    products.set(index++, left.get(leftIndex++));
                } else {
                    products.set(index++, right.get(rightIndex++));
                }
            }

            // Copy any remaining elements from the left list
            while (leftIndex < left.size()) {
                products.set(index++, left.get(leftIndex++));
            }

            // Copy any remaining elements from the right list
            while (rightIndex < right.size()) {
                products.set(index++, right.get(rightIndex++));
            }
        } catch (Exception e) {
            // Log and handle any exceptions that occur during the merging process
            System.out.println("An error occurred during merge: " + e.getMessage());
            throw e;
        }
    }

    // Comparator for sorting products by brand
    public static Comparator<Map<String, String>> getBrandComparator() {
        return Comparator.comparing(product -> product.get("Brand").toLowerCase());
    }

    // Comparator for sorting products by title
    public static Comparator<Map<String, String>> getTitleComparator() {
        return Comparator.comparing(product -> product.get("Title").toLowerCase());
    }

    // Comparator for sorting products by price
    public static Comparator<Map<String, String>> getPriceComparator() {
        return (p1, p2) -> {
            try {
                // Parse the price and compare
                double price1 = parsePrice(p1.get("Price"));
                double price2 = parsePrice(p2.get("Price"));
                return Double.compare(price1, price2);
            } catch (NumberFormatException e) {
                // Log and handle any exceptions that occur during price parsing
                System.out.println("An error occurred while parsing price: " + e.getMessage());
                throw e;
            }
        };
    }

    // Parse the price string into a double value
    private static double parsePrice(String price) {
        try {
            // Remove any extra text from the price string
            String cleanPriceString = price.replaceAll("[^\\d.]+", "");
            // Parse the cleaned price string as a double
            return Double.parseDouble(cleanPriceString);
        } catch (NumberFormatException e) {
            // Log and handle any exceptions that occur during price parsing
            System.out.println("An error occurred while parsing price: " + e.getMessage());
            throw e;
        }
    }

    // Sort and display the products based on the given choice and sorting order
    public static void sortAndDisplayProducts(List<Map<String, String>> products, Integer choice, boolean ascending) {
        try {
            // Determine the comparator based on the user's choice
            Comparator<Map<String, String>> comparator = switch (choice) {
                case 1 -> ProductSorter.getBrandComparator();
                case 2 -> ProductSorter.getTitleComparator();
                case 3 -> ProductSorter.getPriceComparator();
                default -> {
                    // Default to sorting by brand if an invalid choice is provided
                    System.out.println("Invalid choice. Defaulting to sorting by brand.");
                    yield ProductSorter.getBrandComparator();
                }
            };

            // Sort the products using merge sort and the selected comparator
            mergeSort(products, comparator, ascending);

            // Display the sorted products
            for (Map<String, String> product : products) {
                System.out.println("Image: " + product.get("Image"));
                System.out.println("Brand: " + product.get("Brand"));
                System.out.println("Title: " + product.get("Title"));
                System.out.println("Price: " + product.get("Price"));
                System.out.println("Url: " + product.get("URL"));
                System.out.println();
            }
        } catch (Exception e) {
            // Log and handle any exceptions that occur during sorting and displaying products
            System.out.println("An error occurred while sorting and displaying products: " + e.getMessage());
            throw e;
        }
    }
}
