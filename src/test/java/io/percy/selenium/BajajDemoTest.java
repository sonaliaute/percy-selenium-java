package io.percy.selenium;

import io.percy.selenium.core.ConfigManager;
import io.percy.selenium.core.WebDriverManager;
import io.percy.selenium.utils.ExcelReader;
import io.percy.selenium.Percy;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BajajDemoTest {
    private static final Logger LOGGER = Logger.getLogger(BajajDemoTest.class.getName());
    
    private static final String USERNAME = System.getenv("PERCY_USERNAME") != null ? 
        System.getenv("PERCY_USERNAME") : "monishkhanzode_WJlN0B";
    private static final String ACCESSKEY = System.getenv("PERCY_ACCESSKEY") != null ? 
        System.getenv("PERCY_ACCESSKEY") : "eETrxNx2VwYzmLcUP2Su";
    private static final String URL = "https://" + USERNAME + ":" + ACCESSKEY + "@hub-cloud.browserstack.com/wd/hub";
    
    private static WebDriver driver;
    private static Percy percy;
    private static String TEST_URL;
    private static XSSFSheet sheet;
    private static final String user_path = System.getProperty("user.dir");
    
    @BeforeAll
    public static void testSetup() throws Exception {
        // Load test URLs from properties file
        Properties properties = new Properties();
        String propertiesPath = user_path + "/src/test/resources/BajajURLs.properties";
        try (FileInputStream fis = new FileInputStream(propertiesPath)) {
            properties.load(fis);
            TEST_URL = properties.getProperty("bajajHomePage");
        } catch (IOException e) {
            LOGGER.severe("Error loading properties: " + e.getMessage());
            throw e;
        }
        
        // Initialize WebDriver
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("os", "Windows");
        caps.setCapability("os_version", "10");
        caps.setCapability("browser", "Chrome");
        caps.setCapability("browser_version", "80");
        caps.setCapability("name", "Percy Demo Test");
        
        // Convert DesiredCapabilities to Map for WebDriverManager
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("os", "Windows");
        capabilities.put("os_version", "10");
        capabilities.put("browser", "Chrome");
        capabilities.put("browser_version", "80");
        capabilities.put("name", "Percy Demo Test");
        
        // Create WebDriver instance
        boolean useRemoteDriver = System.getenv("USE_REMOTE_DRIVER") != null && 
                                  Boolean.parseBoolean(System.getenv("USE_REMOTE_DRIVER"));
        
        if (useRemoteDriver) {
            driver = WebDriverManager.createDriver("chrome", true, URL, capabilities);
        } else {
            driver = WebDriverManager.createDriver("chrome", false, null, null);
        }
        
        // Initialize Percy
        percy = new Percy(driver);
        
        // Load Excel sheet
        String excelPath = user_path + "/src/main/java/io/percy/selenium/Percy.xlsx";
        FileInputStream fis = new FileInputStream(new File(excelPath));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        sheet = workbook.getSheet("Sheet1");
    }
    
    @AfterAll
    public static void testTeardown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    public void takesMultipleSnapshotsInOneTestCase() throws InterruptedException {
        int lastRow = sheet.getLastRowNum();
        
        for (int i = 1; i <= lastRow; i++) {
            String url = sheet.getRow(i).getCell(1).getStringCellValue();
            String screenShotName = sheet.getRow(i).getCell(0).getStringCellValue();
            
            if (screenShotName != null && !screenShotName.isEmpty()) {
                driver.get(TEST_URL + url);
                driver.manage().window().fullscreen();
                driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                
                int height = driver.manage().window().getSize().getHeight();
                int currentposition = 0;
                int increment = height / 10; // divide height into 10 parts
                
                // Scroll through the page
                for (int j = 0; j <= 12; j++) {
                    currentposition = j * increment;
                    ((JavascriptExecutor) driver)
                            .executeScript("window.scrollBy(0," + currentposition + ")", "");
                    Thread.sleep(3000);
                }
                
                // Scroll back to top
                ((JavascriptExecutor) driver)
                        .executeScript("window.scrollTo(0, -document.body.scrollHeight)");
                Thread.sleep(2000);
                
                // Take Percy snapshot
                percy.snapshot(screenShotName);
                
                LOGGER.info("Captured snapshot: " + screenShotName + " for URL: " + url);
            }
        }
    }
}