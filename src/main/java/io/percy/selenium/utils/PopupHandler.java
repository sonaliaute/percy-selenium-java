package io.percy.selenium.utils; // Update this to match your package

import org.openqa.selenium.*;

import java.util.List;

/**
 * Custom popup handler for Bajaj Finance website
 */
public class PopupHandler {
    private WebDriver driver;
    
    public PopupHandler(WebDriver driver) {
        this.driver = driver;
    }
    
    /**
     * Handle welcome screens and initial popups specifically for Bajaj Finance
     */
    public void handleWelcomeScreens() {
        try {
            System.out.println("Checking for welcome screens and initial popups...");
            
            // 1. Check for Sign-in screen
          /*  List<WebElement> signInElements = driver.findElements(
                By.xpath("//h1[contains(text(),'Sign-in')] | //h1[contains(text(),'Sign in')]"));
            
            if (!signInElements.isEmpty() && signInElements.get(0).isDisplayed()) {
                System.out.println("Sign-in screen detected, attempting to bypass...");
                
                try {
                    // Try to navigate to a different page to bypass login
                    driver.get(driver.getCurrentUrl().split("\\?")[0]);
                    Thread.sleep(1000);
                    
                    // If still on login page, try clearing cookies
                    List<WebElement> stillOnLogin = driver.findElements(
                        By.xpath("//h1[contains(text(),'Sign-in')] | //h1[contains(text(),'Sign in')]"));
                    
                    if (!stillOnLogin.isEmpty() && stillOnLogin.get(0).isDisplayed()) {
                        System.out.println("Still on login page, clearing cookies and refreshing...");
                        driver.manage().deleteAllCookies();
                        driver.navigate().refresh();
                    }
                } catch (Exception e) {
                    System.out.println("Error bypassing login: " + e.getMessage());
                }
            }
            
            */
            
            // 2. Welcome to new Bajaj Finserv screen
            List<WebElement> welcomeTour = driver.findElements(
                By.xpath("//div[contains(text(),'Welcome to the') and contains(text(),'new Bajaj Finserv')]"));
            
            if (!welcomeTour.isEmpty() && welcomeTour.get(0).isDisplayed()) {
                System.out.println("Welcome tour detected, attempting to skip...");
                
                try {
                    // Look for SKIP button
                    List<WebElement> skipButtons = driver.findElements(
                        By.xpath("//button[text()='SKIP']"));
                    
                    if (!skipButtons.isEmpty() && skipButtons.get(0).isDisplayed()) {
                        skipButtons.get(0).click();
                        System.out.println("Skipped welcome tour with SKIP button");
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    System.out.println("Error skipping welcome tour: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error handling welcome screens: " + e.getMessage());
        }
    }
    
    /**
     * Handle general popups, notifications, and modals
     */
    public void handlePopups() {
        try {
            System.out.println("Checking for popups and modals...");
            
         // Skip buttons on bajajfinserv.in
            List<WebElement> skipButtons = driver.findElements(By.xpath("//button[contains(text(),'Skip') or contains(text(),'No thanks') or contains(text(),'Close') or contains(text(),'×')]"));
            if (!skipButtons.isEmpty() && skipButtons.get(0).isDisplayed()) {
                skipButtons.get(0).click();
                System.out.println("Clicked skip button on bajajfinserv.in");
                Thread.sleep(500);
            }
            
            // 1. Cookie consent dialogs
            List<WebElement> cookieButtons = driver.findElements(
                By.xpath("//button[contains(text(),'Accept') or contains(text(),'OK') or contains(text(),'Got it') or contains(text(),'Agree')]"));
            if (!cookieButtons.isEmpty() && cookieButtons.get(0).isDisplayed()) {
                cookieButtons.get(0).click();
                System.out.println("Closed cookie consent dialog");
                Thread.sleep(500);
            }
            
            // 2. Newsletter/subscription popups
            List<WebElement> closeButtons = driver.findElements(
                By.xpath("//button[contains(@class,'close') or contains(@aria-label,'Close')] | //i[contains(@class,'close')]"));
            for (WebElement closeButton : closeButtons) {
                if (closeButton.isDisplayed()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);
                    System.out.println("Closed popup with close button");
                    Thread.sleep(500);
                    break;
                }
            }
            
            // 3. Notification permission dialogs
            List<WebElement> notifyDialogs = driver.findElements(
                By.xpath("//div[contains(text(),'notification') or contains(text(),'Notification')]"));
            if (!notifyDialogs.isEmpty() && notifyDialogs.get(0).isDisplayed()) {
                List<WebElement> laterButtons = driver.findElements(
                    By.xpath("//button[contains(text(),'Later') or contains(text(),'No') or contains(text(),'Cancel')]"));
                if (!laterButtons.isEmpty()) {
                    laterButtons.get(0).click();
                    System.out.println("Dismissed notification dialog");
                    Thread.sleep(500);
                }
            }
            
            // 4. App download suggestions
            List<WebElement> appDownloadDialogs = driver.findElements(
                By.xpath("//div[contains(text(),'download') and contains(text(),'app')]"));
            if (!appDownloadDialogs.isEmpty() && appDownloadDialogs.get(0).isDisplayed()) {
                List<WebElement> dismissButtons = driver.findElements(
                    By.xpath("//button[contains(text(),'Later') or contains(text(),'Skip') or contains(text(),'Cancel')]"));
                if (!dismissButtons.isEmpty()) {
                    dismissButtons.get(0).click();
                    System.out.println("Dismissed app download suggestion");
                    Thread.sleep(500);
                }
            }
            
            // 5. "Later" or "Remind me later" buttons for any popups
            List<WebElement> laterButtons = driver.findElements(
                By.xpath("//button[contains(text(),'later')] | //a[contains(text(),'later')]"));
            for (WebElement laterButton : laterButtons) {
                if (laterButton.isDisplayed()) {
                    laterButton.click();
                    System.out.println("Clicked 'later' button on popup");
                    Thread.sleep(500);
                    break;
                }
            }
            
            // 6. Disable animations to prevent sporadic popups
            disableAnimationsAndDynamicContent();
            
        } catch (Exception e) {
            System.out.println("Error handling popups: " + e.getMessage());
        }
    }
    
    /**
     * Scroll through the page and handle any popups that appear during scrolling
     */
    public void scrollPageAndHandlePopups() {
        try {
            System.out.println("Scrolling page and handling popups...");
            
            // Get window height
            int height = driver.manage().window().getSize().getHeight();
            int currentposition = 0;
            int increment = height / 10;
            
            // Scroll gradually through the page
            for (int j = 0; j <= 12; j++) {
                currentposition = j * increment;
                ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, " + currentposition + ")");
                Thread.sleep(500);
                
                // Check for popups after each scroll increment
                if (j % 3 == 0) { // Check every 3rd scroll to avoid excessive checks
                    handlePopups();
                }
            }
            
            System.out.println("Page scrolling completed");
        } catch (Exception e) {
            System.out.println("Error during page scrolling: " + e.getMessage());
        }
    }
    
    /**
     * Disable animations and dynamic content for consistent screenshots
     */
    private void disableAnimationsAndDynamicContent() {
        try {
            ((JavascriptExecutor) driver).executeScript(
                "document.querySelectorAll('*').forEach(function(node) {" +
                "   if (node.nodeType === 1) {" +
                "       node.style.animation = 'none';" +
                "       node.style.transition = 'none';" +
                "       node.style.transform = 'none';" +
                "   }" +
                "});" +
                // Stop videos if any
                "document.querySelectorAll('video').forEach(function(v) { v.pause(); });" +
                // Stop carousels/sliders
                "document.querySelectorAll('[class*=\"carousel\"],[class*=\"slider\"]').forEach(function(el) {" +
                "   el.style.transition = 'none';" +
                "});"
            );
        } catch (Exception e) {
            System.out.println("Error disabling animations: " + e.getMessage());
        }
    }
}