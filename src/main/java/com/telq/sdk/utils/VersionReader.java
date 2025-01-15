package com.telq.sdk.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VersionReader {
    private static final String PROPERTIES_FILE = "version.properties";

    public static String getVersion() {
        try (InputStream in = VersionReader.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (in == null) {
                return "Unknown";
            }
            Properties props = new Properties();
            props.load(in);
            return props.getProperty("project.version", "Unknown");
        } catch (IOException e) {
            return "Unknown";
        }
    }
}