package com.example.bookmyshow.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KafkaConfigLoader {
    public Properties loadConfig() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = KafkaConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
        }
        return properties;
    }
}
