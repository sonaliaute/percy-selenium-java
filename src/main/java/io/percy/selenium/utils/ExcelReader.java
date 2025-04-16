package io.percy.selenium.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExcelReader {
    private static final Logger LOGGER = Logger.getLogger(ExcelReader.class.getName());
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    
    /**
     * Constructor that opens an Excel file and selects a sheet
     * 
     * @param filePath Path to the Excel file
     * @param sheetName Name of the sheet to select
     * @throws IOException If file cannot be read
     */
    public ExcelReader(String filePath, String sheetName) throws IOException {
        FileInputStream fis = new FileInputStream(new File(filePath));
        workbook = new XSSFWorkbook(fis);
        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new IllegalArgumentException("Sheet " + sheetName + " not found in " + filePath);
        }
    }
    
    /**
     * Constructor that opens an Excel file and selects the first sheet
     * 
     * @param filePath Path to the Excel file
     * @throws IOException If file cannot be read
     */
    public ExcelReader(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(new File(filePath));
        workbook = new XSSFWorkbook(fis);
        sheet = workbook.getSheetAt(0);
    }
    
    /**
     * Gets the number of rows in the sheet
     * 
     * @return Number of rows
     */
    public int getRowCount() {
        return sheet.getLastRowNum();
    }
    
    /**
     * Gets cell data as String
     * 
     * @param rowNum Row number (0-based)
     * @param colNum Column number (0-based)
     * @return Cell data as String
     */
    public String getCellData(int rowNum, int colNum) {
        try {
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                return "";
            }
            
            Cell cell = row.getCell(colNum);
            if (cell == null) {
                return "";
            }
            
            if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                return String.valueOf(cell.getNumericCellValue());
            } else if (cell.getCellType() == CellType.BOOLEAN) {
                return String.valueOf(cell.getBooleanCellValue());
            } else {
                return "";
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error reading cell data at row " + rowNum + ", column " + colNum, e);
            return "";
        }
    }
    
    /**
     * Gets all data from the sheet as list of maps
     * Each map represents a row with column name as key
     * 
     * @return List of data rows
     */
    public List<Map<String, String>> getSheetData() {
        List<Map<String, String>> data = new ArrayList<>();
        
        // Get header row for column names
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            return data;
        }
        
        int colCount = headerRow.getLastCellNum();
        String[] headers = new String[colCount];
        
        for (int i = 0; i < colCount; i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                headers[i] = cell.getStringCellValue();
            } else {
                headers[i] = "Column" + i;
            }
        }
        
        // Read data rows
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row dataRow = sheet.getRow(i);
            if (dataRow == null) {
                continue;
            }
            
            Map<String, String> rowData = new HashMap<>();
            for (int j = 0; j < colCount; j++) {
                Cell cell = dataRow.getCell(j);
                String value = "";
                
                if (cell != null) {
                    switch (cell.getCellType()) {
                        case STRING:
                            value = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            value = String.valueOf(cell.getNumericCellValue());
                            break;
                        case BOOLEAN:
                            value = String.valueOf(cell.getBooleanCellValue());
                            break;
                        default:
                            value = "";
                    }
                }
                
                rowData.put(headers[j], value);
            }
            
            data.add(rowData);
        }
        
        return data;
    }
    
    /**
     * Closes the workbook to release resources
     */
    public void close() {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing Excel workbook", e);
        }
    }
    
    /**
     * Gets the XSSFSheet object for direct manipulation
     * 
     * @return XSSFSheet object
     */
    public XSSFSheet getSheet() {
        return sheet;
    }
}