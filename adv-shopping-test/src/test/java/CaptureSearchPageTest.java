import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;

public class CaptureSearchPageTest extends BaseTest {

    @Test(description = "Search for 'Laptop' and measure time until results appear")
    public void searchProductPerformanceTest() {
        final String HOME_URL = "https://www.advantageonlineshopping.com";
        final String SEARCH_KEYWORD = "Laptop";

        long duration = -1;
        String conclusion = "UNKNOWN";

        try {
            // Navigate to home page
            driver.get(HOME_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Wait for search icon to be clickable
            WebElement searchIcon = wait.until(ExpectedConditions.elementToBeClickable(By.id("menuSearch")));
            searchIcon.click();

            // Measure search duration
            duration = measureExecutionTime(() -> {
                try {
                    // Wait for search input to be visible
                    WebElement searchInput = new WebDriverWait(driver, Duration.ofSeconds(10))
                            .until(ExpectedConditions.visibilityOfElementLocated(By.id("autoComplete")));

                    // Input search keyword and press Enter
                    searchInput.clear();
                    searchInput.sendKeys(SEARCH_KEYWORD);
                    searchInput.sendKeys(Keys.ENTER);

                    // Wait for search results to appear
                    new WebDriverWait(driver, Duration.ofSeconds(15))
                            .until(d -> d.findElements(By.className("imgProduct")).size() > 0);

                } catch (Exception e) {
                    throw new RuntimeException("Search failed: " + e.getMessage());
                }
            });

            System.out.println("Search for '" + SEARCH_KEYWORD + "' duration: " + duration + " ms");
            Reporter.log("Search for '" + SEARCH_KEYWORD + "' duration: " + duration + " ms", true);

            // Assert with reasonable threshold for network testing
            Assert.assertTrue(duration < 10000,
                    "Search should complete < 10000ms but was " + duration + "ms");

            conclusion = "PASS - search completed in " + duration + " ms";
            System.out.println("Conclusion: " + conclusion);
            Reporter.log("Conclusion: " + conclusion, true);

        } catch (Throwable t) {
            conclusion = "FAIL - " + t.getClass().getSimpleName() + ": " + t.getMessage()
                    + " (measured " + duration + " ms)";
            Reporter.log(conclusion);
            System.out.println(conclusion);
            throw t;
        }
    }
}
