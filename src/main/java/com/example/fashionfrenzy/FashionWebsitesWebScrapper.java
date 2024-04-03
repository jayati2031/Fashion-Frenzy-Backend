package com.example.fashionfrenzy;

public class FashionWebsitesWebScrapper {
    public static void scrapeProductInfo(String gender, String category) {
        if (gender.equalsIgnoreCase("Women")) {
            WebScrapAmazon webScrapAmazon = new WebScrapAmazon();
            webScrapAmazon.crawlAmazonJs("https://www.amazon.ca/", gender, category, "src/main/resources/womenAmazon" + category + ".xlsx");

            WebScrapBoohoo webScrapBoohoo = new WebScrapBoohoo();
            webScrapBoohoo.crawlBoohooJs("https://ca.boohoo.com/", gender, category, "src/main/resources/womenBoohoo" + category + ".xlsx");

            WebScrapRevolve webScrapRevolve = new WebScrapRevolve();
            webScrapRevolve.crawlRevolveJs("https://www.revolve.com/", gender, category, "src/main/resources/womenRevolve" + category + ".xlsx");
        } else if (gender.equalsIgnoreCase("Men")) {
            WebScrapAmazon webScrapAmazon = new WebScrapAmazon();
            webScrapAmazon.crawlAmazonJs("https://www.amazon.ca/", gender, category, "src/main/resources/menAmazon" + category + ".xlsx");

            WebScrapBoohoo webScrapBoohoo = new WebScrapBoohoo();
            webScrapBoohoo.crawlBoohooJs("https://ca.boohoo.com/", gender, category, "src/main/resources/menBoohoo" + category + ".xlsx");

            WebScrapRevolve webScrapRevolve = new WebScrapRevolve();
            webScrapRevolve.crawlRevolveJs("https://www.revolve.com/", gender, category, "src/main/resources/menRevolve" + category + ".xlsx");
        } else {
            System.out.println("Invalid gender provided.");
        }
    }
}
