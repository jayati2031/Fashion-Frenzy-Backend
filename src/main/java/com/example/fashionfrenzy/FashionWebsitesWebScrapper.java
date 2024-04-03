package com.example.fashionfrenzy;

public class FashionWebsitesWebScrapper {

    /**
     * Scrape product information from various fashion websites based on gender and category.
     *
     * @param gender   The gender for which to scrape products (e.g., "Women" or "Men").
     * @param category The category of products to scrape (e.g., "shirt", "jeans").
     */
    public static void scrapeProductInfo(String gender, String category) {
        try {
            // Scrape products for women
            // Perform scraping
            if (gender.equalsIgnoreCase("Women")) {
                // Scrape products from Amazon
                // Perform scraping
                WebScrapAmazon webScrapAmazon = new WebScrapAmazon();
                webScrapAmazon.crawlAmazonJs("https://www.amazon.ca/", gender, category, "src/main/resources/womenAmazon" + category + ".xlsx");

                // Scrape products from Boohoo
                // Perform scraping
                WebScrapBoohoo webScrapBoohoo = new WebScrapBoohoo();
                webScrapBoohoo.crawlBoohooJs("https://ca.boohoo.com/", gender, category, "src/main/resources/womenBoohoo" + category + ".xlsx");

                // Scrape products from Revolve
                // Perform scraping
                WebScrapRevolve webScrapRevolve = new WebScrapRevolve();
                webScrapRevolve.crawlRevolveJs("https://www.revolve.com/", gender, category, "src/main/resources/womenRevolve" + category + ".xlsx");
            }
            // Scrape products for men
            // Perform Scraping
            else if (gender.equalsIgnoreCase("Men")) {
                // Scrape products from Amazon
                // Perform scraping
                WebScrapAmazon webScrapAmazon = new WebScrapAmazon();
                webScrapAmazon.crawlAmazonJs("https://www.amazon.ca/", gender, category, "src/main/resources/menAmazon" + category + ".xlsx");

                // Scrape products from Boohoo
                // Perform scraping
                WebScrapBoohoo webScrapBoohoo = new WebScrapBoohoo();
                webScrapBoohoo.crawlBoohooJs("https://ca.boohoo.com/", gender, category, "src/main/resources/menBoohoo" + category + ".xlsx");

                // Scrape products from Revolve
                // Perform scraping
                WebScrapRevolve webScrapRevolve = new WebScrapRevolve();
                webScrapRevolve.crawlRevolveJs("https://www.revolve.com/", gender, category, "src/main/resources/menRevolve" + category + ".xlsx");
            }
            // Handle invalid gender input
            else {
                throw new IllegalArgumentException("Invalid gender provided: " + gender);
            }
        }
        // Handle any exceptions that may occur during scraping
        catch (Exception e) {
            // Exception will be performed over here
            System.out.println("An error occurred during web scraping: " + e.getMessage());
        }
    }
}
