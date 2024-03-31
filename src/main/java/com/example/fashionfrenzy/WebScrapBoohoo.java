package com.example.fashionfrenzy;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WebScrapBoohoo {
    private WebDriver drvrJs;

    public void crawlBoohooJs(String urlToCrwlJs, String ctgryJs, String srchKeyJs, String fileNmJs) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions optnJs = new ChromeOptions();
        optnJs.addArguments("--start-maximized");

        try {
            drvrJs = new ChromeDriver(optnJs);
            drvrJs.get(urlToCrwlJs);
            WebDriverWait wtJs = new WebDriverWait(drvrJs, Duration.ofSeconds(20));

            WebElement clickAcceptCookies = wtJs.until(ExpectedConditions.elementToBeClickable(By.id("onetrust-accept-btn-handler")));
            clickAcceptCookies.click();

            WebElement srchBxJs = wtJs.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@role='combobox']"))); // Wait for search box
            srchBxJs.click(); // Click on search box
            for (int cd = 0; cd < 50; cd++) { // Loop to clear search box
                srchBxJs.sendKeys(Keys.BACK_SPACE); // Press backspace key
            }
            srchBxJs.sendKeys(ctgryJs + " " + srchKeyJs); // Enter search keyword
            srchBxJs.sendKeys(Keys.ENTER); // Press enter to search

            wtJs.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loader")));
            scrapeProductInfoJs(fileNmJs);
        } catch (Exception eJs) {
            System.out.println("Exception caught: " + eJs.getMessage());
        } finally {
            if (drvrJs != null) {
                drvrJs.quit();
            }
        }
    }

    public void scrapeProductInfoJs(String fileName) {
        try {
            // Extract product information
            WebElement categoryElement = drvrJs.findElement(By.className("b-header_search-keywords"));
            List<WebElement> imageElements = drvrJs.findElements(By.xpath("(//img[@class='null'])"));
            List<WebElement> titleElements = drvrJs.findElements(By.className("b-product_tile-link"));
            List<WebElement> priceElements = drvrJs.findElements(By.xpath("(//span[@class='b-price-item m-new'])"));
            List<WebElement> urlElements = drvrJs.findElements(By.xpath("(//a[@class='b-product_tile-image_link'])"));

            if (categoryElement != null || !imageElements.isEmpty() || !titleElements.isEmpty() || !priceElements.isEmpty()|| !urlElements.isEmpty()) {
                assert categoryElement != null;
                String category = categoryElement.getText();
                List<String> images = new ArrayList<>();
                for (WebElement element : imageElements) {
                    String src = element.getAttribute("src");
                    images.add(src);
                }
                List<String> title = new ArrayList<>();
                for (WebElement element : titleElements) {
                    title.add(element.getText());
                }
                List<String> price = new ArrayList<>();
                for (WebElement element : priceElements) {
                    price.add(element.getText());
                }
                List<String> urls = new ArrayList<>();
                for (WebElement element : urlElements) {
                    String href = element.getAttribute("href");
                    urls.add(href);
                }

                // Print product information
                System.out.println("Category: " + category);
                System.out.println("----------------------------------");
                for (int j = 0; j < title.size() && j < price.size() && j < urls.size(); j++) {
                    System.out.println("Image Src: " + images.get(j));
                    System.out.println("Brand: Boohoo");
                    System.out.println("Title: " + title.get(j));
                    System.out.println("Price: " + price.get(j));
                    System.out.println("Url: " + urls.get(j));
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                }

                // Write product information to xlsx
                writeProductInfoToXLSX(category, images, title, price, urls, fileName);
            } else {
                System.out.println("No product found.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void writeProductInfoToXLSX(String category, List<String> image, List<String> title, List<String> price, List<String> url, String fileName) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Product Info");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Category", "Image", "Brand", "Title", "Price", "URL"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            int rowCount = 1;
            for (int j = 0; j < image.size() && j < title.size() && j < price.size() && j < url.size(); j++) {
                Row row = sheet.createRow(rowCount++);
                row.createCell(0).setCellValue(category);
                row.createCell(1).setCellValue(image.get(j));
                row.createCell(2).setCellValue("Boohoo");
                row.createCell(3).setCellValue(title.get(j));
                row.createCell(4).setCellValue(price.get(j));
                row.createCell(5).setCellValue(url.get(j));
            }
            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                workbook.write(outputStream);
            }
        }
        System.out.println("Product information has been written to " + fileName);
    }

    public static void main(String[] args) {
        WebScrapBoohoo webScrapBoohoo = new WebScrapBoohoo();
        // change this to take a list of URLs
        webScrapBoohoo.crawlBoohooJs("https://ca.boohoo.com/", "Men", "hoodies", "product_info.xlsx");
    }
}
