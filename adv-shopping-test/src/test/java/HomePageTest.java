import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;

public class HomePageTest extends BaseTest {
    private static final String HOME_URL = "https://www.advantageonlineshopping.com";

    @Step("Open home page and measure time until logo or main slider is visible")
    public long openHomeAndMeasure() {
        By logo = By.cssSelector(
                "img[id*='logo'], img[class*='logo'], img[alt*='logo'], img[alt*='Logo'], img[alt*='advantage']");
        By slider = By
                .cssSelector("div[id*='slider'], div[class*='slider'], div[class*='carousel'], div[id*='MainSlider']");

        Runnable action = () -> {
            driver.get(HOME_URL);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(logo),
                    ExpectedConditions.visibilityOfElementLocated(slider)));
        };

        return measureExecutionTime(action);
    }

    @Test(description = "Home page loads within 5s (logo or main slider visible)")
    public void homePageLoadTime() {
        long elapsed = openHomeAndMeasure();
        System.out.println("Home page load time: " + elapsed + " ms");
        try {
            Assert.assertTrue(elapsed < 5000, "Load time should be < 5000ms but was " + elapsed + "ms");
        } catch (AssertionError e) {
            System.err.println("Home page load time on failure: " + elapsed + " ms");
            Reporter.log("Home page load time on failure: " + elapsed + " ms", true);
            throw e;
        }
    }
}
