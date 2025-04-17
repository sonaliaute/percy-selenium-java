package io.percy.selenium;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class BajajDemoTest {
    private static final String USERNAME = "monishkhanzode_WJlN0B";
    private static final String ACCESSKEY = "eETrxNx2VwYzmLcUP2Su";
    private static final String URL = "https://" + USERNAME + ":" + ACCESSKEY + "@hub-cloud.browserstack.com/wd/hub";
    private static WebDriver driver;
    private static io.percy.selenium.Percy percy;
    private static String TEST_URL;
    private static XSSFSheet sheet;
    private static final String user_path = System.getProperty("user.dir");

    @BeforeAll
    public static void testSetup() throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("os", "Windows");
        caps.setCapability("os_version", "10");
        caps.setCapability("browser", "Chrome");
        caps.setCapability("browser_version", "latest");
        caps.setCapability("name", "Percy Demo Test");

        driver = new RemoteWebDriver(URI.create(URL).toURL(), caps);
        percy = new io.percy.selenium.Percy(driver);

        // Read TEST_URL from properties file
        Properties properties = new Properties();
        properties.load(new FileInputStream(user_path + "/src/test/resources/BajajURLs.properties"));
        TEST_URL = properties.getProperty("bajajHomePage");

        // Load Excel sheet
        FileInputStream fis = new FileInputStream(new File(user_path + "/src/main/java/io/percy/selenium/Percy.xlsx"));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        sheet = workbook.getSheet("Sheet1");
    }

    @AfterAll
    public static void testTeardown() {
        driver.quit();
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
                for (int j = 0; j <= 12; j++) {
                    currentposition = j * increment;
                    ((JavascriptExecutor) driver)
                            .executeScript("window.scrollBy(0," + currentposition + ")", "");
                    Thread.sleep(3000);
                }
                ((JavascriptExecutor) driver)
                        .executeScript("window.scrollTo(0, -document.body.scrollHeight)");
                Thread.sleep(2000);
                percy.snapshot(sheet.getRow(i).getCell(0).getStringCellValue());
            }
        }
    }
}