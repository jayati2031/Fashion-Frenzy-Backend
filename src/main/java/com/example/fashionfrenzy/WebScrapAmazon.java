package com.example.fashionfrenzy;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

public class WebScrapAmazon {
    private WebDriver drvrJs;

    public void crawlAmazonJs(String urlToCrwlJs, String genderJs, String categoryJs, String fileNmJs) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions optnJs = new ChromeOptions();
        optnJs.addArguments("--start-maximized");

        try {
            drvrJs = new ChromeDriver(optnJs);
            drvrJs.get(urlToCrwlJs);
            WebDriverWait wtJs = new WebDriverWait(drvrJs, Duration.ofSeconds(20));

            WebElement drpDwnJs = drvrJs.findElement(By.id("searchDropdownBox"));
            Select slctJs = new Select(drpDwnJs);
            List<WebElement> options = slctJs.getOptions();
            for (WebElement option : options) {
                String optionText = option.getText();
                if (optionText.contains(genderJs)) {
                    slctJs.selectByVisibleText(optionText);
                    break;
                }
            }

            WebElement srchBxJs = wtJs.until(ExpectedConditions.elementToBeClickable(By.id("twotabsearchtextbox"))); // Wait for search box
            srchBxJs.sendKeys(categoryJs);
            srchBxJs.sendKeys(Keys.ENTER);

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
            WebElement categoryElement = drvrJs.findElement(By.xpath("(//span[@class='a-size-base a-color-base a-text-bold'])"));
            List<WebElement> imageElements = drvrJs.findElements(By.xpath("(//img[@class='s-image'])"));
            List<WebElement> brandElements = drvrJs.findElements(By.xpath("(//h2[@class='a-size-mini s-line-clamp-1'])"));
            List<WebElement> titleElements = drvrJs.findElements(By.xpath("(//span[@class='a-size-base-plus a-color-base a-text-normal'])"));
            List<WebElement> priceElements = drvrJs.findElements(By.xpath("(//span[@class='a-price'])"));
            List<WebElement> urlElements = drvrJs.findElements(By.xpath("(//a[@class='a-link-normal s-underline-text s-underline-link-text s-link-style a-text-normal'])"));

            if (categoryElement != null || !imageElements.isEmpty() || !brandElements.isEmpty() || !titleElements.isEmpty() || !priceElements.isEmpty()) {
                assert categoryElement != null;
                String category = categoryElement.getText();
                List<String> images = new ArrayList<>();
                for (WebElement element : imageElements) {
                    String src = element.getAttribute("src");
                    images.add(src);
                }
                List<String> brand = new ArrayList<>();
                for (WebElement element : brandElements) {
                    brand.add(element.getText());
                }
                List<String> title = new ArrayList<>();
                for (WebElement element : titleElements) {
                    title.add(element.getText());
                }
                List<String> price = new ArrayList<>();
                for (WebElement element : priceElements) {
                    String pr = element.getText().replaceAll("\n", ".");
                    price.add(pr);
                }
                List<String> urls = new ArrayList<>();
                for (WebElement element : urlElements) {
                    String href = element.getAttribute("href");
                    urls.add(href);
                }

                // Print product information
                System.out.println("Category: " + category);
                System.out.println("----------------------------------");
                for (int j = 0; j < images.size() && j < brand.size() && j < title.size()
                        && j < price.size() && j < urls.size(); j++) {
                    System.out.println("Image Src: " + images.get(j));
                    System.out.println("Brand: " + brand.get(j));
                    System.out.println("Title: " + title.get(j));
                    System.out.println("Price: " + price.get(j));
                    System.out.println("Url: " + urls.get(j));
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                }

                // Write product information to xlsx
                writeProductInfoToXLSX(images, brand, title, price, urls, fileName);
            } else {
                System.out.println("No product found.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void writeProductInfoToXLSX(List<String> image, List<String> brand, List<String> title, List<String> price, List<String> url, String fileName) throws IOException {
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
            for (int j = 0; j < image.size() && j < brand.size() && j < title.size() && j < price.size() && j < url.size(); j++) {
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
