import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class AddToCartTest extends BaseTest {
    private static final String HOME_URL = "https://www.advantageonlineshopping.com";

    @Step("Click product, Add to Cart and measure time until cart notification appears")
    public long performAddToCartAndMeasure() {
        Runnable action = () -> {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Go to home
            driver.get(HOME_URL);

            // Navigate to a category to ensure products are listed (use Speakers)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("speakersImg")));
            driver.findElement(By.id("speakersImg")).click();

            // Wait for product thumbnails to appear on category page
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("imgProduct")));
            List<WebElement> products = driver.findElements(By.className("imgProduct"));

            if (products.isEmpty()) {
                throw new RuntimeException("No products found on category page");
            }

            // Pick a random product
            WebElement chosen = products.get((int) (Math.random() * products.size()));
            chosen.click();

            // Wait for Add to Cart button on product page
            WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(By.name("save_to_cart")));
            addBtn.click();

            // Wait for cart notification (tooltip) to appear
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("toolTipCart")));
        };

        return measureExecutionTime(action);
    }

    @Test(description = "Add to cart updates visible within 2s")
    public void addToCartPerformanceTest() {
        long elapsed = performAddToCartAndMeasure();
        System.out.println("Add to Cart duration: " + elapsed + " ms");
        Reporter.log("Add to Cart duration: " + elapsed + " ms", true);

        try {
            Assert.assertTrue(elapsed < 2000, "Add to Cart should be < 2000ms but was " + elapsed + "ms");
            System.out.println("Conclusion: PASS - Add to Cart within threshold (" + elapsed + " ms)");
            Reporter.log("Conclusion: PASS - Add to Cart within threshold (" + elapsed + " ms)", true);
        } catch (AssertionError e) {
            System.err.println("Conclusion: FAIL - Add to Cart exceeded threshold (" + elapsed + " ms)");
            Reporter.log("Conclusion: FAIL - Add to Cart exceeded threshold (" + elapsed + " ms)", true);
            throw e;
        }
    }
}
