package com.example.fashionfrenzy;

import com.example.fashionfrenzy.controller.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FashionFrenzy {
    // Paths for source folder and parsed files folder
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    static PageRanking pageRanking = new PageRanking();
    static ProductFilter productFilter = new ProductFilter();
    static ProductSearch productSearch = new ProductSearch();
    static FrequencyCount frequencyCount = new FrequencyCount();
    static TopSearches topSearches = new TopSearches();
    static SendDeals sendDeals = new SendDeals();
    static UserDataController userDataController = new UserDataController();
    static FetchProductsController fetchProductsController = new FetchProductsController();
    static ProductSortController productSortController = new ProductSortController(fetchProductsController);
    static TopSearchesController topSearchesController = new TopSearchesController();
    static PageRankingController pageRankingController = new PageRankingController(pageRanking);
    static ProductFilterController productFilterController = new ProductFilterController(productFilter, fetchProductsController);
    static SendDealsController sendDealsController = new SendDealsController(sendDeals, fetchProductsController);
    static ProductSearchController productSearchController = new ProductSearchController(productSearch, frequencyCount, pageRanking, topSearches, fetchProductsController);
    static ProductWordCompletionController productWordCompletionController = new ProductWordCompletionController(fetchProductsController);
    static FrequencyCountController frequencyCountController = new FrequencyCountController(frequencyCount);

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String gender = null;
            String category = null;
            boolean promptToContinue = true;

            System.out.println(ANSI_RED + "\n============ WELCOME TO FASHION FRENZY============" + ANSI_RESET);
            System.out.println(ANSI_BLUE + "Find the best fashion products of different brands." + ANSI_RESET);

            String userName = getValidInput("Please enter your name: ", scanner);
            String userEmail = getValidInput("Please enter your email: ", scanner);
            //TO-DO add error handling here
            userDataController.registerUser(userName, userEmail);

            while (promptToContinue) {
                try {
                    if (gender == null || category == null) {
                        System.out.println(ANSI_GREEN + "Select gender - men or women (type 'exit' to quit):" + ANSI_RESET);
                        gender = scanner.nextLine().trim();

                        if (gender.equalsIgnoreCase("exit")) {
                            break;
                        } else if (gender.equalsIgnoreCase("men") || gender.equalsIgnoreCase("women")) {
                            category = askCategory(gender, scanner);
                        } else {
                            System.out.println(ANSI_RED + "Write a valid gender." + ANSI_RESET);
                            continue;
                        }
                    }

                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    System.out.println(ANSI_BLUE + "Select an option to explore:" + ANSI_RESET);
                    System.out.println("\uD83C\uDF10 1. Web Scrap");
                    System.out.println("\uD83D\uDD0D 2. Search for a Product");
                    System.out.println("\uD83D\uDD22 3. Sort products on basis of brand, title or price");
                    System.out.println("⚙️ 4. Filter Products on basis of brand or price");
                    System.out.println("\uD83D\uDCCA 5. Rank Pages based on a keyword");
                    System.out.println("\uD83D\uDCC8 6. Get the trending Searches");
                    System.out.println("\uD83D\uDCE7 7. Send Deals to all users");
                    System.out.println("\uD83D\uDEAA 8. Exit");

                    int option = scanner.nextInt();
                    scanner.nextLine(); // Consume newline character after reading int

                    switch (option) {
                        case 1:
                            Logger logger = Logger.getLogger("org.openqa.selenium.devtools");
                            logger.setLevel(Level.SEVERE);
                            FashionWebsitesWebScrapper.scrapeProductInfo(gender, category);
                            break;
                        case 2:
                            String searchTitle = getValidInput("Enter the title to search: ", scanner);
                            productSearch(gender, category, searchTitle);
                            break;
                        case 3:
                            int sortBy = getValidNumericInput("Do you want to sort products by 1)Brand, 2)Title, 3) Price", scanner);
                            int sortOrderInput = getValidNumericInput("Ascending(1) or Descending(0) order", scanner);
                            boolean sortOrder = sortOrderInput == 1;
                            productSortController.getSortedProducts(gender, category, sortBy, sortOrder);
                            //TO-DO print the results
                            break;
                        case 4:
                            filterProducts(gender, category);
                            break;
                        case 5:
                            String query = getValidInput("Enter a query to search in all three websites: ", scanner);
                            pageRankingController.pageRanking(gender, category, query);
                            //TO-DO print the results
                            break;
                        case 6:
                            System.out.println("Top 3 trends on our website:");
                            topSearchesController.getTopTrendingSearches();
                            //TO-DO print the results
                            break;
                        case 7:
                            System.out.println("Sending hot deals to the users...");
                            sendDealsController.sendDealsByEmail();
                            //TO-DO print the results
                            return;
                        case 8:
                            System.out.println("Exiting Fashion Frenzy. Goodbye!");
                            scanner.close();
                            return;
                        default:
                            System.out.println("Invalid option. Please select a valid option.");
                    }
                    promptToContinue = promptToContinue(scanner);
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    scanner.nextLine(); // Consume invalid input to prevent an infinite loop
                } catch (Exception e) {
                    // Handle any unexpected exceptions
                    System.out.println("An unexpected error occurred: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            // Handle any unexpected exceptions
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static String askCategory(String gender, Scanner scanner) {
        String category;
        if (gender.equalsIgnoreCase("men")) {
            System.out.println(ANSI_GREEN + "Select one category from the following - (shirt, hoodie, jeans, coat, sweater)" + ANSI_RESET);
        } else {
            System.out.println(ANSI_GREEN + "Select one category from the following - (dress, top, jeans, coat, sweater)" + ANSI_RESET);
        }
        while (true) {
            category = scanner.nextLine().trim();
            if (isValidCategory(category)) {
                System.out.println("~~~~~~~~~~~~~~~~~~~");
                System.out.println(ANSI_YELLOW + "Your choices:" + ANSI_RESET);
                System.out.println("Gender: " + gender);
                System.out.println("Category: " + category);
                break;
            } else {
                System.out.println(ANSI_RED + "Please select a valid category." + ANSI_RESET);
            }
        }
        return category;
    }

    private static boolean isValidCategory(String category) {
        return category.equalsIgnoreCase("shirt") || category.equalsIgnoreCase("hoodie") ||
                category.equalsIgnoreCase("jeans") || category.equalsIgnoreCase("coat") ||
                category.equalsIgnoreCase("sweater") || category.equalsIgnoreCase("dress") ||
                category.equalsIgnoreCase("top");
    }

    private static void productSearch(String gender, String category, String searchTitle) {
        ProductSearch productSearch = new ProductSearch();
        List<Map<String, String>> products = fetchProducts(gender, category);
        for (Map<String, String> product : products) {
            String image = product.get("Image");
            String title = product.get("Title");
            String brand = product.get("Brand");
            String price = product.get("Price");
            String url = product.get("URL");
            productSearch.insert(image, title, brand, price, url);
        }
        searchTitle = searchTitle.trim().toLowerCase();
        List<Map<String, String>> searchResults = productSearch.search(searchTitle);

        if (!searchResults.isEmpty()) {
            System.out.println("\nSearch Results");
            for (Map<String, String> productDetails : searchResults) {
                printProductDetails(productDetails);
            }
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("No product found with exact options");
            System.out.println("Product Search Sub - Options:");
            System.out.println("1. Spell Check");
            System.out.println("2. Word Completion");
            System.out.println("4. Frequency Count");

            int searchOption = Integer.parseInt(scanner.nextLine());

            switch (searchOption) {
                case 1:
                    productSearchController.performSpellCheck(gender, category, searchTitle);
                    //TO-DO print the results
                    break;
                case 2:
                    productWordCompletionController.suggestWords(gender, category, searchTitle);
                    //TO-DO print the results
                    break;
                case 3:
                    frequencyCountController.getFrequencyCount(gender,category,searchTitle);
                    //TO-DO print the results
                    break;
                default:
                    System.out.println("Invalid search option. Please select a valid option.");
            }
        }
    }

    private static List<Map<String, String>> fetchProducts(String gender, String category) {
        String categoryCapitalized = category.substring(0, 1).toUpperCase() + category.substring(1);
        List<String> websites = List.of("Amazon", "Boohoo", "Revolve");
        List<String> filePaths = websites.stream()
                .map(website -> String.format("src/main/resources/%s%s%s.xlsx", gender, website, categoryCapitalized))
                .toList();
        return FetchProductsFromExcelBasedOnCategory.readData(filePaths);
    }

    private static void printProductDetails(Map<String, String> productDetails) {
        System.out.println("Image Src: " + productDetails.get("Image"));
        System.out.println("Title: " + productDetails.get("Title"));
        System.out.println("Brand: " + productDetails.get("Brand"));
        System.out.println("Price: " + productDetails.get("Price"));
        System.out.println("URL: " + productDetails.get("URL"));
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    private static void filterProducts(String gender, String category) {
        double minPrice, maxPrice;
        String brandInput;
        String[] brands;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select the filtering option:");
        System.out.println("1. Based on Brands");
        System.out.println("2. Based on Price Range");
        System.out.println("3. Based on both Brand and Price Range");

        int filter = scanner.nextInt();
        switch (filter) {
            case 1:
                System.out.print("Enter brand names separated by commas: ");
                brandInput = scanner.nextLine();
                brands = brandInput.split(",");
                productFilterController.filterProductsByBrand(gender, category, brands);
                //TO-DO print the results
                break;
            case 2:
                System.out.print("Enter the minimum price: ");
                minPrice = scanner.nextDouble();
                System.out.print("Enter the maximum price: ");
                maxPrice = scanner.nextDouble();
                productFilterController.filterProductsByPriceRange(gender, category, minPrice, maxPrice);
                //TO-DO print the results
                break;
            case 3:
                System.out.print("Enter brand names separated by commas: ");
                brandInput = scanner.nextLine();
                brands = brandInput.split(",");
                System.out.print("Enter the minimum price: ");
                minPrice = scanner.nextDouble();
                System.out.print("Enter the maximum price: ");
                maxPrice = scanner.nextDouble();
                productFilterController.filterProductsByBrandAndPriceRange(gender, category, brands, minPrice, maxPrice);
                //TO-DO print the results
                break;
            default:
                System.out.println("Invalid option selected.");
        }
    }

    private static String getValidInput(String prompt, Scanner scanner) {
        String input;
        boolean isValid;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim().toLowerCase();
            isValid = true;
            if (input.matches("^\\s*$")) {
                System.out.println(ANSI_RED + "Input cannot be empty. Please try again." + ANSI_RESET);
                isValid = false;
            } else if (input.matches(".*\\d+.*")) {
                System.out.println(ANSI_RED + "Input cannot contain numbers. Please try again." + ANSI_RESET);
                isValid = false;
            }
        } while (!isValid);
        return input;
    }

    private static int getValidNumericInput(String prompt, Scanner scanner) {
        int input;
        boolean isValid;
        do {
            System.out.print("\n" + prompt);
            String inputString = scanner.nextLine().trim();
            try {
                input = Integer.parseInt(inputString);
                isValid = input > 0;

                if (!isValid) {
                    System.out.println(ANSI_RED + "Input must be greater than 0. Please try again." + ANSI_RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + "Invalid input. Please enter a valid number." + ANSI_RESET);
                isValid = false;
                input = 0;
            }
        } while (!isValid);
        return input;
    }

    private static boolean promptToContinue(Scanner scanner) {
        String input;
        boolean isValid;
        do {
            System.out.println("\n================================================================================");
            System.out.println("Do you want to continue? (yes/no)");
            input = scanner.nextLine().trim().toLowerCase();
            isValid = input.equals("yes") || input.equals("no");
            if (!isValid) {
                System.out.println(ANSI_RED + "Invalid input! Please enter 'yes' or 'no'" + ANSI_RESET);
            }
        } while (!isValid);
        return input.equals("yes");
    }
}
