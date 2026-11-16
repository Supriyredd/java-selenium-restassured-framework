package com.sdubbaka.qa.utils;

import com.sdubbaka.qa.tests.ui.BaseUiTest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

/** Saves a screenshot to target/screenshots for every failed UI test. */
public class ScreenshotOnFailureListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        if (!(result.getInstance() instanceof BaseUiTest ui) || ui.currentDriver() == null) return;
        try {
            Path dir = Path.of("target", "screenshots");
            Files.createDirectories(dir);
            Path src = ((TakesScreenshot) ui.currentDriver()).getScreenshotAs(OutputType.FILE).toPath();
            Files.copy(src, dir.resolve(result.getName() + ".png"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | RuntimeException e) {
            System.err.println("Could not save screenshot: " + e.getMessage());
        }
    }
}
