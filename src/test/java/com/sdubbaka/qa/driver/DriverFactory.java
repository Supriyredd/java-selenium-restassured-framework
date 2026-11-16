package com.sdubbaka.qa.driver;

import com.sdubbaka.qa.config.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/** One WebDriver per thread so TestNG can run classes in parallel safely. Selenium Manager resolves drivers. */
public final class DriverFactory {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {}

    public static WebDriver get() {
        if (DRIVER.get() == null) DRIVER.set(create());
        return DRIVER.get();
    }

    /** Current driver for this thread, or null. Never starts a browser. */
    public static WebDriver current() {
        return DRIVER.get();
    }

    public static void quit() {
        WebDriver d = DRIVER.get();
        if (d != null) {
            d.quit();
            DRIVER.remove();
        }
    }

    private static WebDriver create() {
        boolean headless = Config.headless();
        switch (Config.browser().toLowerCase()) {
            case "firefox": {
                FirefoxOptions o = new FirefoxOptions();
                if (headless) o.addArguments("-headless");
                return new FirefoxDriver(o);
            }
            case "chrome":
            default: {
                ChromeOptions o = new ChromeOptions();
                if (headless) o.addArguments("--headless=new");
                o.addArguments("--window-size=1366,900", "--no-sandbox", "--disable-dev-shm-usage");
                return new ChromeDriver(o);
            }
        }
    }
}
