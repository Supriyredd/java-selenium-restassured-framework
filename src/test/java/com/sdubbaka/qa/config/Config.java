package com.sdubbaka.qa.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Reads config.properties, then lets system properties and environment variables override it. */
public final class Config {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read config.properties", e);
        }
    }

    private Config() {}

    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;
        String env = System.getenv(key.toUpperCase().replace('.', '_'));
        if (env != null && !env.isBlank()) return env;
        return PROPS.getProperty(key);
    }

    public static String baseUrl() { return get("base.url"); }
    public static String browser() { return get("browser"); }
    public static boolean headless() { return Boolean.parseBoolean(get("headless")); }
    public static String username() { return get("test.username"); }
    public static String password() { return get("test.password"); }
    public static int timeoutSeconds() { return Integer.parseInt(get("timeout.seconds")); }
}
