package com.example.fashionfrenzy;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
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
    private WebDriver driver;

    /**
     * Crawls Boohoo website using JavaScript to scrape product information based on given parameters.
     *
     * @param urlToCrawlJs URL of the Boohoo website to crawl.
     * @param categoryJs   Product category to search for on the Boohoo page.
     * @param searchKeyJs   Search keyword to use on the Boohoo page.
     * @param fileNameJs   Name of the Excel file to save the scraped product information.
     */
    public void crawlBoohooJs(String urlToCrawlJs, String categoryJs, String searchKeyJs, String fileNameJs) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions optionsJs = new ChromeOptions();
        optionsJs.addArguments("--start-maximized");

        try {
            driver = new ChromeDriver(optionsJs);
            driver.get(urlToCrawlJs);
            WebDriverWait waitJs = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Accept cookies
            WebElement clickAcceptCookies = waitJs.until(ExpectedConditions.elementToBeClickable(By.id("onetrust-accept-btn-handler")));
            clickAcceptCookies.click();

            // Perform search
            WebElement searchBoxJs = waitJs.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@role='combobox']")));
            searchBoxJs.click();
            for (int cd = 0; cd < 50; cd++) {
                searchBoxJs.sendKeys(Keys.BACK_SPACE);
            }
            searchBoxJs.sendKeys(categoryJs + " " + searchKeyJs);
            searchBoxJs.sendKeys(Keys.ENTER);

            waitJs.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loader")));
            scrapeProductInfoJs(fileNameJs);
        }  catch (WebDriverException e) {
            System.out.println("The Chrome Boohoo tab was closed while the program was still running.");
        } catch (Exception eJs) {
            System.out.println("Exception caught: " + eJs.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    /**
     * Scrapes product information from the Boohoo page and writes it to an Excel file.
     *
     * @param fileName Name of the Excel file to save the scraped product information.
     */
    public void scrapeProductInfoJs(String fileName) {
        try {
            // Extract product information
            List<WebElement> imageElements = driver.findElements(By.xpath("(//img[@class='null'])"));
            List<WebElement> titleElements = driver.findElements(By.className("b-product_tile-link"));
            List<WebElement> priceElements = driver.findElements(By.xpath("(//span[@class='b-price-item m-new'])"));
            List<WebElement> urlElements = driver.findElements(By.xpath("(//a[@class='b-product_tile-image_link'])"));

            if (!imageElements.isEmpty() || !titleElements.isEmpty() || !priceElements.isEmpty() || !urlElements.isEmpty()) {
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
        } catch (NullPointerException npex) {
            System.out.println("Null Pointer Exception: " + npex.getMessage());
        } catch (WebDriverException e) {
            System.out.println("The Chrome Boohoo tab was closed while the program was still running.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Writes product information to an Excel file.
     *
     * @param image   List of product images.
     * @param title   List of product titles.
     * @param price   List of product prices.
     * @param url     List of product URLs.
     * @param fileName Name of the Excel file to save the product information.
     * @throws IOException If an I/O error occurs.
     */
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
                row.createCell(0).setCellValue(convertedName + "_" + (j + 1));
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