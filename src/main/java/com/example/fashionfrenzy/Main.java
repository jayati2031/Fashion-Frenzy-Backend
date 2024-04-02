package com.example.fashionfrenzy;


import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    // Paths for source folder and parsed files folder
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ProductWordCompletion wordCompletion = new ProductWordCompletion();
        TopSearches searchFrequency = new TopSearches();

        while (true) {
            System.out.println(ANSI_RED + "\n============ WELCOME TO FASHION FRENZY============" + ANSI_RESET);
            System.out.println(ANSI_BLUE + "Find the best fashion products of different brands." + ANSI_RESET);

            System.out.println(ANSI_GREEN + "Select gender - men or women (type 'exit' to quit):" + ANSI_RESET);
            String gender = scanner.nextLine().trim();

            String category = null;
            if (gender.equalsIgnoreCase("exit")) {
                break; // Exit the loop if the user inputs 'exit'
            } else if (gender.equals("men") || gender.equals("women")) {
                if (gender.equals("men")) {
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
                        break; // Exit the category input loop if a valid category is provided
                    } else {
                        System.out.println(ANSI_RED + "Please select a valid category." + ANSI_RESET);
                    }
                }
            } else {
                System.out.println(ANSI_RED + "Write a valid gender." + ANSI_RESET);
            }

            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println(ANSI_BLUE + "Select an option:" + ANSI_RESET);
            System.out.println("\uD83C\uDF10\uD83D\uDCE1 1. Web Scrap");
            System.out.println("\uD83D\uDD0D 2. Search for a Product");
            System.out.println("\uD83D\uDCDA 3. Use Inverted Indexing to Search the product faster");
            System.out.println("\uD83D\uDD22 4. Sort products on basis of brand, title or price");
            System.out.println("⚙️ 5. Filter Products on basis of brand or price");
            System.out.println("\uD83D\uDCCA 6. Rank Pages based on a keyword");
            System.out.println("\uD83D\uDCC8 7. Get the trending Searches");
            System.out.println("\uD83D\uDEAA 8. Exit");

            int option = Integer.parseInt(scanner.nextLine());

            switch (option) {
                case 1:
                    FashionWebsitesWebScrapper.scrapeProductInfo();
                    break;
                case 2:
                    String searchTitle = getValidInput("Enter the title to search: ", scanner, false);
                    productSearch(gender, category, searchTitle);
                    break;
                case 3:
                    productSort();
                    break;
                case 4:
                    filter();
                    break;
                case 5:
                    pageRank();
                    break;
                case 6:
                    trendingSearches();
                    break;
                case 7:
                    System.out.println("Exiting Fashion Frenzy. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please select a valid option.");
            }

            String searchTitle = getValidInput("Enter the title to search: ", scanner, false);
            wordCompletion.suggestWords(searchTitle);

            System.out.println("Exiting Flight Booking App. Thank you for using our service!");

            System.out.println(ANSI_RED + "Invalid input! Please enter 1 or 2" + ANSI_RESET);

            if (!promptToContinue(scanner)) {
                System.out.println("Thank You for using our program!!");
                System.out.println("Have a nice day!");
                System.exit(0);
            }
        }
        scanner.close();
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
            System.out.println("3. Inverted Indexing");
            System.out.println("4. Frequency Count");

            int searchOption = Integer.parseInt(scanner.nextLine());

            switch (searchOption) {
                case 1:
                    spellCheck();
                    break;
                case 2:
                    wordCompletion();
                    break;
                case 3:
                    invertedIndexing();
                    break;
                case 4:
                    frequencyCount();
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

    private static void productSort() {
        System.out.println("Product Sort functionality is not implemented yet.");
    }

    private static void filter() {
        System.out.println("Filter functionality is not implemented yet.");
    }

    private static void pageRank() {
        System.out.println("Page Rank functionality is not implemented yet.");
    }

    private static void trendingSearches() {
        System.out.println("Trending Searches functionality is not implemented yet.");
    }

    private static void spellCheck() {
        System.out.println("Spell Check functionality is not implemented yet.");
    }

    private static void wordCompletion() {
        System.out.println("Word Completion functionality is not implemented yet.");
    }

    private static void invertedIndexing() {
        System.out.println("Inverted Indexing functionality is not implemented yet.");
    }

    private static void frequencyCount() {
        System.out.println("Frequency Count functionality is not implemented yet.");
    }

    private static void initiatePatternSearch(Scanner userInput) {
        System.out.println();
        System.out.println("\nEnter the type of pattern you want to search for:");
        System.out.println("[1] Duration");
        System.out.println("[2] Links");
        System.out.println("[3] Email");
        System.out.println("[4] Phone Number");
        System.out.println("[5] Price");
        // System.out.print("Input: ");

        int patternChoice = getValidNumericInput("Input: ", userInput);

        String pattern = "";
        String patternDescription = "";

        switch (patternChoice) {
            case 1:
                pattern = "\\d{1,2}h\\s+\\d{1,2}m";
                patternDescription = "Duration patterns";
                break;
            case 2:
                pattern = "\\b(https?://|www\\.)[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?";
                patternDescription = "URL patterns";
                break;
            case 3:
                pattern = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b";
                patternDescription = "email patterns";
                break;
            case 4:
                pattern = "\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b";
                patternDescription = "phone number patterns";
                break;
            case 5:
                pattern = "C\\$\\s*\\d{1,3}(,\\d{3})*";
                patternDescription = "price patterns";
                break;
            default:
                System.out.println("\nInvalid choice. Please try again.");
                break;
        }
    }

    private static void initiateFrequencyCount(Scanner userInput) {
        // Frequency Count
        System.out.println();
        String frequencyTerm = getValidInput("\nEnter any keyword to start frequency counting: ", userInput, false);
    }

    private static void initiatePageRanking(Scanner userInput, String folderPath) {
        // Page Ranking
        System.out.println();
        String rankingTerm = getValidInput("\nEnter any keyword to start page ranking: ", userInput, false);

        // try {
        // PageRanker.rankPages(folderPath, rankingTerm);
        // } catch (FileNotFoundException e) {
        // System.out.println("An error occurred during page ranking: " +
        // e.getMessage());
        // e.printStackTrace();
        // }
    }

    private static String getValidInput(String prompt, Scanner scanner, boolean allowEmpty) {
        String input;
        boolean isValid;

        do {
            System.out.print("\n" + prompt);
            input = scanner.nextLine().trim().toLowerCase();

            isValid = true;

            if (input.matches("^\\s*$")) {
                if (allowEmpty) {
                    isValid = true;
                } else {
                    System.out.println(ANSI_RED + "Input cannot be empty. Please try again." + ANSI_RESET);
                    isValid = false;
                }
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

    private static void deleteFiles(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
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
