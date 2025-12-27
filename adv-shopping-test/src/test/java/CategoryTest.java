import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class CategoryTest extends BaseTest {

    @Test
    public void laptopsCategoryRenderTime() {
        long duration = -1;
        String conclusion = "UNKNOWN";
        try {
            // Navigate to homepage and click the Laptops card (use existing pattern
            // laptopsImg)
            String home = "https://www.advantageonlineshopping.com/#/";
            driver.get(home);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement laptopsCard = wait.until(ExpectedConditions.elementToBeClickable(By.id("laptopsImg")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", laptopsCard);
            // give a small pause to allow any animations to settle
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", laptopsCard);

            By productThumbs = By.className("imgProduct");

            duration = measureExecutionTime(() -> {
                new WebDriverWait(driver, Duration.ofSeconds(30))
                        .until(d -> {
                            List<WebElement> items = d.findElements(productThumbs);
                            return items != null && items.size() > 0 && items.get(0).isDisplayed();
                        });
            });

            Reporter.log("Laptops product list render time: " + duration + " ms");

            Assert.assertTrue(duration < 3000, "Laptops category render should be < 3000ms but was " + duration + "ms");
            conclusion = "PASS - render time " + duration + " ms";
        } catch (Throwable t) {
            conclusion = "FAIL - " + t.getClass().getSimpleName() + ": " + t.getMessage() + " (measured " + duration
                    + " ms)";
            Reporter.log(conclusion);
            System.out.println(conclusion);
            throw t;
        } finally {
            Reporter.log("Conclusion: " + conclusion);
            System.out.println("Conclusion: " + conclusion);
        }
    }
}
