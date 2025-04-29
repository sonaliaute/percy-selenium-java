package io.percy.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Import the PopupHandler from your utils package
import io.percy.selenium.utils.PopupHandler;

class BajajDemoTestPopupHandled {
    private static final String USERNAME = "monishkhanzode_WJlN0B";
    private static final String ACCESSKEY = "eETrxNx2VwYzmLcUP2Su";
    private static final String URL = "https://" + USERNAME + ":" + ACCESSKEY + "@hub-cloud.browserstack.com/wd/hub";
    private WebDriver driver;
    private io.percy.selenium.Percy percy;
    private String TEST_URL;
    private XSSFSheet sheet;
    private final String user_path = System.getProperty("user.dir");
    
    // Add a field for the PopupHandler
    @SuppressWarnings("unused")
	private PopupHandler popupHandler;

    /**
     * Enhanced SSL Certificate handler with custom truststore support
     */
    public static class SSLCertificateHandler {
        /**
         * Simple runtime SSL bypass - works for current session only
         */
        public static void disableSslVerificationRuntime() {
            try {
                System.out.println("Setting up runtime SSL bypass...");
                
                // Create a trust manager that does not validate certificate chains
                TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
                };

                // Install the all-trusting trust manager
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                // Create all-trusting host name verifier
                HostnameVerifier allHostsValid = new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };

                // Install the all-trusting host verifier
                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
                
                // Also set the default SSLContext for the JVM
                SSLContext.setDefault(sc);
                
                System.out.println("Runtime SSL bypass successful");
            } catch (Exception e) {
                System.err.println("Error setting up SSL bypass: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        /**
         * Set up and use custom truststore with BrowserStack certificate
         * This provides a more permanent solution without admin rights
         */
        public static void setupCustomTruststore() {
            try {
                System.out.println("Setting up custom truststore...");
                
                // Define paths for custom truststore and certificate
                String userHome = System.getProperty("user.home");
                String certDir = userHome + "/Certificates";
                String certPath = certDir + "/browserstack.der";
                String trustStorePath = certDir + "/custom-truststore.jks";
                
                // Create directories if needed
                new File(certDir).mkdirs();
                
                // Check if certificate exists
                File certFile = new File(certPath);
                File trustStoreFile = new File(trustStorePath);
                
                // If we already have a custom truststore, just use it
                if (trustStoreFile.exists()) {
                    System.out.println("Using existing custom truststore: " + trustStorePath);
                    System.setProperty("javax.net.ssl.trustStore", trustStorePath);
                    System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
                    return;
                }
                
                // If certificate doesn't exist but we have admin access, copy from existing location
                if (!certFile.exists()) {
                    File existingCert = new File("C:/Users/202503199/Certificates/browserstack.der");
                    if (existingCert.exists()) {
                        System.out.println("Copying existing certificate to user directory...");
                        Files.copy(existingCert.toPath(), certFile.toPath());
                    } else {
                        System.out.println("Certificate file not found, will use runtime bypass instead");
                        disableSslVerificationRuntime();
                        return;
                    }
                }
                
                // Create custom truststore using keytool
                if (certFile.exists()) {
                    String[] command = {
                        System.getProperty("java.home") + "/bin/keytool",
                        "-importcert",
                        "-noprompt",
                        "-file", certPath,
                        "-keystore", trustStorePath,
                        "-storepass", "changeit",
                        "-alias", "browserstack"
                    };
                    
                    System.out.println("Running keytool to create custom truststore...");
                    Process process = Runtime.getRuntime().exec(command);
                    int exitCode = process.waitFor();
                    
                    // Get any error output
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line;
                    StringBuilder errorOutput = new StringBuilder();
                    while ((line = errorReader.readLine()) != null) {
                        errorOutput.append(line).append("\n");
                    }
                    
                    if (exitCode == 0) {
                        System.out.println("Custom truststore created successfully");
                    } else if (errorOutput.toString().contains("already exists")) {
                        System.out.println("Certificate already exists in truststore (which is fine)");
                    } else {
                        System.out.println("Keytool error: " + errorOutput.toString());
                        System.out.println("Will use runtime bypass instead");
                        disableSslVerificationRuntime();
                        return;
                    }
                    
                    // Set system properties to use our custom truststore
                    System.setProperty("javax.net.ssl.trustStore", trustStorePath);
                    System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
                    
                    System.out.println("Custom truststore configured successfully");
                }
            } catch (Exception e) {
                System.err.println("Error setting up custom truststore: " + e.getMessage());
                e.printStackTrace();
                
                // Fall back to runtime solution if anything goes wrong
                disableSslVerificationRuntime();
            }
        }
        
        /**
         * Configure SSL related system properties
         */
        public static void setupSystemProperties() {
            // Use all available TLS protocols
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,TLSv1.3");
            
            // Increase timeouts
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
            System.setProperty("sun.net.client.defaultReadTimeout", "30000");
            
            // Additional options for Java 8+ to help with SSL
            System.setProperty("jsse.enableSNIExtension", "false");
        }
    }

    @BeforeSuite
    public void testSetup() throws Exception {
        try {
            System.out.println("Setting up SSL certificate handling...");
            
            // Set system properties first
            SSLCertificateHandler.setupSystemProperties();
            
            // Try to use custom truststore approach (more permanent)
            SSLCertificateHandler.setupCustomTruststore();
            
            // Also apply runtime bypass as a failsafe
            SSLCertificateHandler.disableSslVerificationRuntime();
            
            System.out.println("Setting up WebDriver capabilities...");
            
            // Use W3C standard capabilities format - fix for MutableCapabilities resolution
            org.openqa.selenium.MutableCapabilities options = new org.openqa.selenium.MutableCapabilities();
            
            // Create browserstack options
            Map<String, Object> bstackOptions = new HashMap<>();
            bstackOptions.put("os", "Windows");
            bstackOptions.put("osVersion", "10");
            bstackOptions.put("browserVersion", "latest");
            bstackOptions.put("sessionName", "Percy Demo Test");
            
            // Set capabilities using the W3C standard format
            options.setCapability("bstack:options", bstackOptions);
            options.setCapability("browserName", "chrome");
            
            System.out.println("Connecting to BrowserStack...");
            
            // Create remote webdriver using URI to avoid deprecated URL constructor
            driver = new RemoteWebDriver(URI.create(URL).toURL(), options);
            
            // Initialize Percy (without custom config parameters)
            percy = new io.percy.selenium.Percy(driver);
            
            // Initialize the PopupHandler after driver is set up
            popupHandler = new PopupHandler(driver);
            
            System.out.println("Successfully connected to BrowserStack");

            // Read TEST_URL from properties file
            Properties properties = new Properties();
            try {
                FileInputStream propsInput = new FileInputStream(user_path + "/src/test/resources/BajajURLs.properties");
                properties.load(propsInput);
                TEST_URL = properties.getProperty("bajajHomePage");
                System.out.println("Loaded TEST_URL: " + TEST_URL);
                propsInput.close();
            } catch (Exception e) {
                System.err.println("Error loading properties file: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

            // Load Excel sheet
            try {
                File excelFile = new File(user_path + "/src/main/java/io/percy/selenium/Percy.xlsx");
                System.out.println("Excel file path: " + excelFile.getAbsolutePath());
                System.out.println("Excel file exists: " + excelFile.exists());
                
                FileInputStream fis = new FileInputStream(excelFile);
                XSSFWorkbook workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheet("Sheet1");
                System.out.println("Excel sheet loaded successfully with " + (sheet.getLastRowNum() + 1) + " rows");
                workbook.close();
                fis.close();
            } catch (Exception e) {
                System.err.println("Error loading Excel file: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        } catch (Exception e) {
            System.err.println("Error in testSetup: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @AfterSuite
    public void testTeardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void takesMultipleSnapshotsInOneTestCase() throws InterruptedException {
        if (sheet == null) {
            Assert.fail("Excel sheet is null. Check if file exists and is loaded properly.");
            return;
        }
        
        int lastRow = sheet.getLastRowNum();
        System.out.println("Processing " + lastRow + " rows from Excel sheet");

        for (int i = 1; i <= lastRow; i++) {
            try {
                String url = sheet.getRow(i).getCell(1).getStringCellValue();
                String screenShotName = sheet.getRow(i).getCell(0).getStringCellValue();

                if (screenShotName != null && !screenShotName.isEmpty()) {
                    System.out.println("Processing row " + i + ": " + screenShotName + " -> " + url);
                    driver.get(TEST_URL + url);
                    
                    // Set window to mobile size for initial load
                    driver.manage().window().setSize(new Dimension(375, 812));
                    
                    // Wait for page to load
                    waitForPageLoad();
                    
                    // Handle popups more aggressively
                    handlePopupsWithRetry();
                    
                    // Now resize to full height for one complete screenshot
                    driver.manage().window().setSize(new Dimension(375, 14050));
                    
                    // Wait for the resize to take effect
                    Thread.sleep(1000);
                    
                    // Scroll to top for consistent capture
                    ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
                    Thread.sleep(1000);
                    
                    // One final popup check
                    handlePopupsWithRetry();
                    
                    // Take a single Percy snapshot
                    percy.snapshot(screenShotName, java.util.List.of(375));
                    System.out.println("Snapshot taken: " + screenShotName);
                }
            } catch (Exception e) {
                System.err.println("Error processing row " + i + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * More aggressive popup handling with retries
     */
    private void handlePopupsWithRetry() {
        // Try handling popups multiple times
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                System.out.println("Popup handling attempt " + (attempt + 1));
                
                // Handle specific popups first
                handleGetOtpPopup();
                
                // Handle generic popups
                handleGenericPopups();
                
                // Check if any overlays remain
                if (!isAnyPopupVisible()) {
                    System.out.println("No popups detected, continuing...");
                    break;
                }
                
                Thread.sleep(1000);
            } catch (Exception e) {
                System.err.println("Error in popup handling: " + e.getMessage());
            }
        }
    }

    /**
     * Handle the specific GET OTP popup
     */
    private void handleGetOtpPopup() {
        try {
            // Look specifically for the OTP popup by class
            List<WebElement> otpPopups = driver.findElements(By.className("cusgetotp"));
            
            if (!otpPopups.isEmpty()) {
                System.out.println("Found GET OTP popup, attempting to close...");
                
                // Try different strategies to close it
                WebElement otpPopup = otpPopups.get(0);
                
                // First try clicking the element directly
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", otpPopup);
                
                // If that doesn't work, try finding and clicking a close button
                try {
                    WebElement closeButton = driver.findElement(By.cssSelector(".popup-close, .close-icon, .btn-close"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);
                } catch (Exception e) {
                    // If no close button, try hitting escape key
                    new Actions(driver).sendKeys(Keys.ESCAPE).perform();
                }
                
                // Wait a moment to see if it worked
                Thread.sleep(1000);
                
                // Check if it's still there
                otpPopups = driver.findElements(By.className("cusgetotp"));
                if (otpPopups.isEmpty()) {
                    System.out.println("Successfully closed GET OTP popup");
                } else {
                    System.out.println("Failed to close GET OTP popup, will try alternative approaches");
                    
                    // Try removing it with JavaScript as a last resort
                    ((JavascriptExecutor) driver).executeScript(
                        "document.querySelectorAll('.cusgetotp').forEach(e => e.remove());"
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error handling OTP popup: " + e.getMessage());
        }
    }

    /**
     * Handle any type of popups
     */
    private void handleGenericPopups() {
        try {
            // Common popup selectors
            String[] popupSelectors = {
                ".popup", ".modal", ".overlay", "[class*='popup']", "[class*='modal']", 
                ".sidebar-menu__close", ".bf-icon-right-arrow", ".fs-12",
                "[id*='popup']", "[id*='modal']", "[id*='overlay']"
            };
            
            for (String selector : popupSelectors) {
                List<WebElement> popups = driver.findElements(By.cssSelector(selector));
                
                for (WebElement popup : popups) {
                    if (popup.isDisplayed()) {
                        try {
                            System.out.println("Found popup element: " + popup.getAttribute("class"));
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", popup);
                            Thread.sleep(500);
                        } catch (Exception e) {
                            // Just log and continue
                            System.out.println("Could not interact with popup: " + e.getMessage());
                        }
                    }
                }
            }
            
            // Also try hitting escape key
            new Actions(driver).sendKeys(Keys.ESCAPE).perform();
            
            // Handle any overlays by removing them via JavaScript
            ((JavascriptExecutor) driver).executeScript(
                "document.querySelectorAll('.overlay, .modal, .popup, .dialog').forEach(e => e.remove());"
            );
        } catch (Exception e) {
            System.err.println("Error in generic popup handling: " + e.getMessage());
        }
    }

    /**
     * Check if any popups are still visible
     */
    private boolean isAnyPopupVisible() {
        try {
            String[] popupSelectors = {
                ".popup:visible", ".modal:visible", ".overlay:visible", ".cusgetotp:visible"
            };
            
            for (String selector : popupSelectors) {
                Boolean hasVisiblePopups = (Boolean) ((JavascriptExecutor) driver).executeScript(
                    "return document.querySelectorAll('" + selector + "').length > 0"
                );
                
                if (Boolean.TRUE.equals(hasVisiblePopups)) {
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }  
    /**
     * Wait for page to be fully loaded
     */
    
	private void waitForPageLoad() {
        try {
            System.out.println("Waiting for page to load...");
            
            // Wait for document ready state
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(60))
                .until(driver -> ((JavascriptExecutor) driver)
                    .executeScript("return document.readyState").equals("complete"));
            
            System.out.println("Document ready state is complete");
            
            // Also wait for jQuery if present
            Boolean jQueryPresent = (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return typeof jQuery !== 'undefined'");
                
            if (Boolean.TRUE.equals(jQueryPresent)) {
                System.out.println("jQuery detected, waiting for AJAX requests to complete...");
                new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(driver -> (Boolean) ((JavascriptExecutor) driver)
                        .executeScript("return jQuery.active == 0"));
                System.out.println("jQuery AJAX requests completed");
            }
            
            // Additional wait for any animations or delayed content
            System.out.println("Additional wait for final rendering...");
            Thread.sleep(5000);
            System.out.println("Page load wait completed");
        } catch (Exception e) {
            System.err.println("Error waiting for page load: " + e.getMessage());
        }
    }
}