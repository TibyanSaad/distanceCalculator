package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

// to get the hidden API and URL values
public class AppConfig {
    private static final Properties props = new Properties();

    // static block runs once when the class is loaded
    static {
        try (FileInputStream fis = new FileInputStream("application.properties")) {
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to get values from file
    public static String get(String key) {
        return props.getProperty(key);
    }
}
