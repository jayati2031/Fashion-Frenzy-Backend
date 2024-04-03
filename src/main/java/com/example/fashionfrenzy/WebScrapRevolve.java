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

public class WebScrapRevolve {
    private WebDriver driver;

    /**
     * Crawls the Revolve website using JavaScript to scrape product information based on given parameters.
     *
     * @param urlToCrawlJs URL of the Revolve website to crawl.
     * @param genderJs   Product category to search for on the Revolve page.
     * @param categoryJs  Search keyword to use on the Revolve page.
     * @param fileNameJs   Name of the Excel file to save the scraped product information.
     */
    public void crawlRevolveJs(String urlToCrawlJs, String genderJs, String categoryJs, String fileNameJs) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions optionsJs = new ChromeOptions();
        optionsJs.addArguments("--start-maximized");

        try {
            driver = new ChromeDriver(optionsJs);
            driver.get(urlToCrawlJs);
            WebDriverWait waitJs = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Close pop-up if exists
            WebElement closeButton = driver.findElement(By.id("ntf_just_let_shop"));
            closeButton.click();

            genderJs = genderJs.substring(0, 1).toUpperCase() + genderJs.substring(1).toLowerCase();
            genderJs = genderJs.substring(0, 1).toUpperCase() + genderJs.substring(1).toLowerCase();

            // Select gender based on category
            WebElement selectGender;
            if (genderJs.equals("Men")) {
                selectGender = waitJs.until(ExpectedConditions.elementToBeClickable(driver.findElements(By.className("h5-secondary")).get(1)));
            } else {
                selectGender = waitJs.until(ExpectedConditions.elementToBeClickable(driver.findElements(By.className("h5-secondary")).get(0)));
            }
            selectGender.click();

            // Perform search
            WebElement searchBoxJs = waitJs.until(ExpectedConditions.elementToBeClickable(By.id("search_term_new")));
            searchBoxJs.click();
            searchBoxJs.sendKeys(categoryJs);
            searchBoxJs.sendKeys(Keys.ENTER);

            waitJs.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loader")));

            // Scroll down the page
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy({ top: 5000, behavior: 'smooth' });");

            scrapeProductInfoJs(fileNameJs);
        } catch (WebDriverException e) {
            System.out.println("The Chrome Revolve tab was closed while the program was still running.");
        } catch (Exception eJs) {
            System.out.println("Exception caught: " + eJs.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    /**
     * Parses product information from the Revolve page and writes it to an Excel file.
     *
     * @param fileName Name of the Excel file to save the scraped product information.
     */
    public void scrapeProductInfoJs(String fileName) {
        try {
            // Extract product information
            Thread.sleep(2000);
            List<WebElement> imageElements = driver.findElements(By.className("products-grid__image-link-img"));
            List<WebElement> titleElements = driver.findElements(By.className("product-name"));
            List<WebElement> brandElements = driver.findElements(By.className("product-brand"));
            List<WebElement> divElements = driver.findElements(By.className("js-plp-prices-div"));
            List<WebElement> priceElements = new ArrayList<>();
            for (WebElement divElement : divElements) {
                List<WebElement> prices = divElement.findElements(By.className("plp_price"));
                priceElements.addAll(prices);
            }
            List<WebElement> urlElements = driver.findElements(By.xpath("(//a[@class='u-text-decoration--none js-plp-pdp-link2 product-link'])"));

            if (!imageElements.isEmpty() || !titleElements.isEmpty() || !brandElements.isEmpty() ||
                    (!priceElements.isEmpty() || !urlElements.isEmpty())) {
                List<String> images = new ArrayList<>();
                for (int i = 0; i < Math.min(imageElements.size(), 40); i++) {
                    String srcset = imageElements.get(i).getAttribute("srcset");
                    String[] srcsetParts = srcset.split("\\s+");
                    images.add(srcsetParts[0]);
                }
                List<String> title = new ArrayList<>();
                for (int i = 0; i < Math.min(titleElements.size(), 40); i++) {
                    title.add(titleElements.get(i).getText());
                }
                List<String> brand = new ArrayList<>();
                for (int i = 0; i < Math.min(brandElements.size(), 40); i++) {
                    brand.add(brandElements.get(i).getText());
                }
                List<String> price = new ArrayList<>();
                for (int i = 0; i < Math.min(priceElements.size(), 40); i++) {
                    price.add(priceElements.get(i).getText());
                }
                List<String> urls = new ArrayList<>();
                for (int i = 0; i < Math.min(urlElements.size(), 40); i++) {
                    String href = urlElements.get(i).getAttribute("href");
                    urls.add(href);
                }

                // Print product information
                System.out.println("Scrapped " + Math.min(Math.min(images.size(), title.size()), Math.min(price.size(), urls.size())) + " products from Revolve");

                // Write product information to xlsx
                writeProductInfoToXLSX(images, title, brand, price, urls, fileName);
            } else {
                System.out.println("No product found.");
            }
        } catch (WebDriverException e) {
            System.out.println("The Chrome Revolve tab was closed while the program was still running.");
        } catch (NullPointerException npex) {
            System.out.println("Null Pointer Exception: " + npex.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Writes product information to an Excel file.
     *
     * @param image    List of product images.
     * @param title    List of product titles.
     * @param brand    List of product brands.
     * @param price    List of product prices.
     * @param url      List of product URLs.
     * @param fileName Name of the Excel file to save the product information.
     * @throws IOException If an I/O error occurs.
     */
    private void writeProductInfoToXLSX(List<String> image, List<String> title, List<String> brand, List<String> price, List<String> url, String fileName) throws IOException {
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
                row.createCell(2).setCellValue(brand.get(j));
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