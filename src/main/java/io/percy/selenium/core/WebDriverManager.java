package io.percy.selenium.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class WebDriverManager {
    private static final Logger LOGGER = Logger.getLogger(WebDriverManager.class.getName());
    
    /**
     * Creates and returns a WebDriver instance based on configuration parameters
     * 
     * @param browser Browser name (chrome, firefox)
     * @param remote If true, creates a remote driver
     * @param remoteUrl URL for remote WebDriver
     * @param capabilities Additional capabilities
     * @return WebDriver instance
     */
    public static WebDriver createDriver(String browser, boolean remote, String remoteUrl, 
                                        Map<String, Object> capabilities) {
        WebDriver driver;
        
        if (remote) {
            driver = createRemoteDriver(browser, remoteUrl, capabilities);
        } else {
            driver = createLocalDriver(browser, capabilities);
        }
        
        return driver;
    }
    
    private static WebDriver createLocalDriver(String browser, Map<String, Object> capabilities) {
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        
        switch (browser.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless");
                }
                chromeOptions.addArguments("--window-size=1920,1080");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                
                if (capabilities != null) {
                    for (Map.Entry<String, Object> entry : capabilities.entrySet()) {
                        chromeOptions.setCapability(entry.getKey(), entry.getValue());
                    }
                }
                
                return new ChromeDriver(chromeOptions);
                
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("--headless");
                }
                
                if (capabilities != null) {
                    for (Map.Entry<String, Object> entry : capabilities.entrySet()) {
                        firefoxOptions.setCapability(entry.getKey(), entry.getValue());
                    }
                }
                
                return new FirefoxDriver(firefoxOptions);
                
            default:
                throw new IllegalArgumentException("Browser " + browser + " is not supported");
        }
    }
    
    private static WebDriver createRemoteDriver(String browser, String remoteUrl, 
                                              Map<String, Object> capabilities) {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        
        if ("chrome".equalsIgnoreCase(browser)) {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--window-size=1920,1080");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        } else if ("firefox".equalsIgnoreCase(browser)) {
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            desiredCapabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);
        }
        
        if (capabilities != null) {
            capabilities.forEach(desiredCapabilities::setCapability);
        }
        
        try {
            return new RemoteWebDriver(new URL(remoteUrl), desiredCapabilities);
        } catch (MalformedURLException e) {
            LOGGER.severe("Invalid remote WebDriver URL: " + e.getMessage());
            throw new RuntimeException("Failed to create remote WebDriver", e);
        }
    }
}