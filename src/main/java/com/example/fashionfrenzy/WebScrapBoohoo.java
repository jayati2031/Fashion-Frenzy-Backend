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
            List<WebElement> imageElements = drvrJs.findElements(By.xpath("(//img[@class='null'])"));
            List<WebElement> titleElements = drvrJs.findElements(By.className("b-product_tile-link"));
            List<WebElement> priceElements = drvrJs.findElements(By.xpath("(//span[@class='b-price-item m-new'])"));
            List<WebElement> urlElements = drvrJs.findElements(By.xpath("(//a[@class='b-product_tile-image_link'])"));

            if (!imageElements.isEmpty() || !titleElements.isEmpty() || !priceElements.isEmpty()|| !urlElements.isEmpty()) {
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
                System.out.println("Scrapped " + Math.min(Math.min(images.size(), title.size()), Math.min(price.size(), urls.size())) + " products from Boohoo");

                // Write product information to xlsx
                writeProductInfoToXLSX(images, title, price, urls, fileName);
            } else {
                System.out.println("No product found.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void writeProductInfoToXLSX(List<String> image, List<String> title, List<String> price, List<String> url, String fileName) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Product Info");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Id", "Image", "Brand", "Title", "Price", "URL"};

            String fileNameWithExtension = fileName.substring(fileName.lastIndexOf('/') + 1);
            String fileNameWithoutExtension = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf('.'));
            String[] parts = fileNameWithoutExtension.split("(?=[A-Z])");
            String convertedName = String.join("_", parts).toLowerCase();

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            int rowCount = 1;
            for (int j = 0; j < image.size() && j < title.size() && j < price.size() && j < url.size(); j++) {
                Row row = sheet.createRow(rowCount++);
                row.createCell(0).setCellValue(convertedName + "_" + (j+1));
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
}
