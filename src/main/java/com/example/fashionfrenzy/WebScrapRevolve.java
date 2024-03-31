package com.example.fashionfrenzy;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

public class WebScrapRevolve {
    private WebDriver drvrJs;

    public void crawlRevolveJs(String urlToCrwlJs, String ctgryJs, String srchKeyJs, String fileNmJs) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions optnJs = new ChromeOptions();
        optnJs.addArguments("--start-maximized");

        try {
            drvrJs = new ChromeDriver(optnJs);
            drvrJs.get(urlToCrwlJs);
            WebDriverWait wtJs = new WebDriverWait(drvrJs, Duration.ofSeconds(20));

            WebElement closeButton = drvrJs.findElement(By.id("ntf_just_let_shop"));
            closeButton.click();

            WebElement selectGender;
            if (ctgryJs.equals("Men")) {
                selectGender = wtJs.until(ExpectedConditions.elementToBeClickable(drvrJs.findElements(By.className("h5-secondary")).get(1)));
            } else {
                selectGender = wtJs.until(ExpectedConditions.elementToBeClickable(drvrJs.findElements(By.className("h5-secondary")).get(0)));
            }
            selectGender.click();

            WebElement srchBxJs = wtJs.until(ExpectedConditions.elementToBeClickable(By.id("search_term_new"))); // Wait for search box
            srchBxJs.click(); // Click on search box
            srchBxJs.sendKeys(srchKeyJs); // Enter search keyword
            srchBxJs.sendKeys(Keys.ENTER); // Press enter to search

            wtJs.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loader")));

            JavascriptExecutor js = (JavascriptExecutor) drvrJs;
            // Scroll the page by 5000px
            js.executeScript("window.scrollBy({ top: 5000, behavior: 'smooth' });");

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
            WebElement categoryElement = drvrJs.findElement(By.xpath("(//h1[@class='page-title__hed u-margin-t--sm u-margin-b--none'])"));
            Thread.sleep(2000);
            List<WebElement> imageElements = drvrJs.findElements(By.className("products-grid__image-link-img"));
            List<WebElement> titleElements = drvrJs.findElements(By.className("product-name"));
            List<WebElement> brandElements = drvrJs.findElements(By.className("product-brand"));
            List<WebElement> divElements = drvrJs.findElements(By.className("js-plp-prices-div"));
            List<WebElement> priceElements = new ArrayList<>();
            for (WebElement divElement : divElements) {
                List<WebElement> prices = divElement.findElements(By.className("plp_price"));
                priceElements.addAll(prices);
            }
            List<WebElement> urlElements = drvrJs.findElements(By.xpath("(//a[@class='u-text-decoration--none js-plp-pdp-link2 product-link'])"));

            if (categoryElement != null || !imageElements.isEmpty() || !titleElements.isEmpty() || !brandElements.isEmpty() ||
                    (!priceElements.isEmpty() || !urlElements.isEmpty())) {
                assert categoryElement != null;
                String category = categoryElement.getText();
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
                System.out.println("Category: " + category);
                System.out.println("----------------------------------");
                for (int j = 0; j < title.size() && j < brand.size() && j < price.size() && j < urls.size(); j++) {
                    System.out.println("Image Src: " + images.get(j));
                    System.out.println("Brand: " + brand.get(j));
                    System.out.println("Title: " + title.get(j));
                    System.out.println("Price: " + price.get(j));
                    System.out.println("Url: " + urls.get(j));
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                }

                // Write product information to xlsx
                writeProductInfoToXLSX(category, images, title, brand, price, urls, fileName);
            } else {
                System.out.println("No product found.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void writeProductInfoToXLSX(String category, List<String> image, List<String> title, List<String> brand, List<String> price, List<String> url, String fileName) throws IOException {
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

    public static void main(String[] args) {
        WebScrapRevolve webScrapRevolve = new WebScrapRevolve();
        // change this to take a list of URLs
        webScrapRevolve.crawlRevolveJs("https://www.revolve.com/", "Women", "jacket", "product_info.xlsx");
    }
}
