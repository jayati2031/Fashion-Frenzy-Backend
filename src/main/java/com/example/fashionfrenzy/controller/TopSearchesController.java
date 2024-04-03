package com.example.fashionfrenzy.controller;

import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class TopSearchesController {
    @GetMapping("/trending-searches")
    public List<Map<String, Integer>> getExcelData() {
        List<Map<String, Integer>> result = new ArrayList<>();
        boolean isFirstRow = true; // Flag to skip the first row
        try {
            // Load the Excel file from the classpath
            FileInputStream fis = new FileInputStream("SearchHistory.xlsx");
            // Create a workbook instance for the XLSX file
            Workbook workbook = new XSSFWorkbook(fis);
            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);
            // Iterate through the rows
            Iterator<Row> rowIterator = sheet.iterator();
            int rowCount = 0;
            while (rowIterator.hasNext() && rowCount < 3) {
                Row row = rowIterator.next();
                // Skip the first row
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }
                // Assuming the first column contains searchKey and the second column contains count
                Cell searchKeyCell = row.getCell(0);
                Cell countCell = row.getCell(1);
                if (searchKeyCell != null && countCell != null) {
                    // Retrieve data from cells
                    String searchKey = searchKeyCell.getStringCellValue();
                    Integer count = (int) countCell.getNumericCellValue();
                    // Create a map to hold current row's data
                    Map<String, Integer> rowData = new HashMap<>();
                    rowData.put(searchKey, count);
                    // Add rowData to result list
                    result.add(rowData);
                }
                rowCount++;
            }
            workbook.close();
            fis.close();
        } catch (IOException e) {
            System.out.println("IOException occurred: " + e.getMessage());
        }
        return result;
    }
}
