package com.example.fashionfrenzy;

import java.util.List;

public class FashionWebsitesWebScrapper {
    public static void scrapeProductInfo() {
        //Women Categories: Dresses, Tops, Jeans, Coats, Sweaters
        List<String> womenCategory = List.of("Dress", "Top", "Jeans", "Coat", "Sweater");

        //Men Categories: Shirts, Hoodies, Jeans, Coats, Sweaters
        List<String> menCategory = List.of("Shirt", "Hoodie", "Jeans", "Coat", "Sweater");

        for (String wmCtgry : womenCategory) {
            WebScrapAmazon webScrapAmazon = new WebScrapAmazon();
            webScrapAmazon.crawlAmazonJs("https://www.amazon.ca/", "Women", wmCtgry, "src/main/resources/womenAmazon" + wmCtgry + ".xlsx");

            WebScrapBoohoo webScrapBoohoo = new WebScrapBoohoo();
            webScrapBoohoo.crawlBoohooJs("https://ca.boohoo.com/", "Women", wmCtgry, "src/main/resources/womenBoohoo" + wmCtgry + ".xlsx");

            WebScrapRevolve webScrapRevolve = new WebScrapRevolve();
            webScrapRevolve.crawlRevolveJs("https://www.revolve.com/", "Women", wmCtgry, "src/main/resources/womenRevolve" + wmCtgry + ".xlsx");
        }

        for (String mCtgry : menCategory) {
            WebScrapAmazon webScrapAmazon = new WebScrapAmazon();
            webScrapAmazon.crawlAmazonJs("https://www.amazon.ca/", "Men", mCtgry, "src/main/resources/menAmazon" + mCtgry + ".xlsx");

            WebScrapBoohoo webScrapBoohoo = new WebScrapBoohoo();
            webScrapBoohoo.crawlBoohooJs("https://ca.boohoo.com/", "Men", mCtgry, "src/main/resources/menBoohoo" + mCtgry + ".xlsx");

            WebScrapRevolve webScrapRevolve = new WebScrapRevolve();
            webScrapRevolve.crawlRevolveJs("https://www.revolve.com/", "Men", mCtgry, "src/main/resources/menRevolve" + mCtgry + ".xlsx");
        }
    }
}
