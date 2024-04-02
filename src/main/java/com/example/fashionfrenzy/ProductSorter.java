package com.example.fashionfrenzy;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProductSorter {
    // Merge Sort implementation for sorting products based on a given comparator
    public static void mergeSort(List<Map<String, String>> products, Comparator<Map<String, String>> comparator, boolean ascending) {
        if (products.size() <= 1) {
            return;
        }

        int mid = products.size() / 2;
        List<Map<String, String>> left = new ArrayList<>(products.subList(0, mid));
        List<Map<String, String>> right = new ArrayList<>(products.subList(mid, products.size()));

        mergeSort(left, comparator, ascending);
        mergeSort(right, comparator, ascending);

        merge(products, left, right, comparator, ascending);
    }

    private static void merge(List<Map<String, String>> products, List<Map<String, String>> left,
                              List<Map<String, String>> right, Comparator<Map<String, String>> comparator, boolean ascending) {
        int leftIndex = 0, rightIndex = 0, index = 0;

        while (leftIndex < left.size() && rightIndex < right.size()) {
            if ((ascending && comparator.compare(left.get(leftIndex), right.get(rightIndex)) <= 0) ||
                    (!ascending && comparator.compare(left.get(leftIndex), right.get(rightIndex)) >= 0)) {
                products.set(index++, left.get(leftIndex++));
            } else {
                products.set(index++, right.get(rightIndex++));
            }
        }

        while (leftIndex < left.size()) {
            products.set(index++, left.get(leftIndex++));
        }

        while (rightIndex < right.size()) {
            products.set(index++, right.get(rightIndex++));
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
            double price1 = parsePrice(p1.get("Price"));
            double price2 = parsePrice(p2.get("Price"));
            return Double.compare(price1, price2);
        };
    }

    private static double parsePrice(String price) {
        // Remove the dollar sign and any commas
        String cleanedPrice = price.replaceAll("[$,]", "");
        return Double.parseDouble(cleanedPrice);
    }

    public static void sortAndDisplayProducts(List<Map<String, String>> products, Integer choice, boolean ascending) {
        Comparator<Map<String, String>> comparator = switch (choice) {
            case 1 -> ProductSorter.getBrandComparator();
            case 2 -> ProductSorter.getTitleComparator();
            case 3 -> ProductSorter.getPriceComparator();
            default -> {
                System.out.println("Invalid choice. Defaulting to sorting by brand.");
                yield ProductSorter.getBrandComparator();
            }
        };

        mergeSort(products, comparator, ascending);
        for (Map<String, String> product : products) {
            System.out.println("Image: " + product.get("Image"));
            System.out.println("Brand: " + product.get("Brand"));
            System.out.println("Title: " + product.get("Title"));
            System.out.println("Price: " + product.get("Price"));
            System.out.println("Url: " + product.get("URL"));
            System.out.println();
        }
    }
}
