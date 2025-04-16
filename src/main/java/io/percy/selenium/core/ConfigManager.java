package io.percy.selenium.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigManager {
    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";
    private static boolean isInitialized = false;
    
    /**
     * Initializes the configuration properties
     */
    public static void init() {
        if (!isInitialized) {
            loadProperties(CONFIG_FILE);
            isInitialized = true;
        }
    }
    
    /**
     * Loads properties from a file
     * 
     * @param fileName Name of the properties file to load
     */
    public static void loadProperties(String fileName) {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                LOGGER.warning("Unable to find " + fileName);
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading properties file " + fileName, e);
        }
    }
    
    /**
     * Loads properties from an absolute file path
     * 
     * @param filePath Absolute path to the properties file
     */
    public static void loadPropertiesFromPath(String filePath) {
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading properties file " + filePath, e);
        }
    }
    
    /**
     * Gets a property value
     * 
     * @param key Property key
     * @return Property value or null if not found
     */
    public static String getProperty(String key) {
        if (!isInitialized) {
            init();
        }
        return properties.getProperty(key);
    }
    
    /**
     * Gets a property value with a default value if not found
     * 
     * @param key Property key
     * @param defaultValue Default value to return if property not found
     * @return Property value or defaultValue if not found
     */
    public static String getProperty(String key, String defaultValue) {
        if (!isInitialized) {
            init();
        }
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Sets a property value
     * 
     * @param key Property key
     * @param value Property value
     */
    public static void setProperty(String key, String value) {
        if (!isInitialized) {
            init();
        }
        properties.setProperty(key, value);
    }
}