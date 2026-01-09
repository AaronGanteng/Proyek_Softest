import io.qameta.allure.Step;
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

public class AddToCartTest extends BaseTest {
    private static final String HOME_URL = "https://www.advantageonlineshopping.com";
    private static final String DUMMY_USER = "testingbaru2";
    private static final String DUMMY_PASS = "Testingbaru2";

    @Step("Login first before adding to cart")
    private void performLogin() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Navigate to home page
        driver.get(HOME_URL);

        // Ensure user icon is present
        wait.until(ExpectedConditions.elementToBeClickable(By.id("menuUser")));

        // Open login modal
        driver.findElement(By.id("menuUser")).click();

        // Wait for username/password inputs
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));

        // Input credentials
        driver.findElement(By.name("username")).sendKeys(DUMMY_USER);
        driver.findElement(By.name("password")).sendKeys(DUMMY_PASS);

        // Wait for loader to disappear and button to be clickable
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.loader")));

        // Additional wait to ensure loader is fully gone
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }

        // Use JavaScript click to avoid interception issues
        WebElement signInBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("sign_in_btn")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signInBtn);

        // Wait until the username appears in the menu (login success indicator)
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("menuUserLink"), DUMMY_USER));

        System.out.println("Login successful for user: " + DUMMY_USER);
    }

    @Step("Click product, Add to Cart and measure time until cart notification appears")
    public long performAddToCartAndMeasure() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

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

        // Measure ONLY from clicking add to cart until notification appears
        Runnable action = () -> {
            addBtn.click();
            // Wait for cart notification (tooltip) to appear
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("toolTipCart")));
        };

        return measureExecutionTime(action);
    }

    @Test(description = "Login then add to cart - measure performance within 20s")
    public void addToCartPerformanceTest() {
        try {
            // Step 1: Login first
            System.out.println("Step 1: Performing login...");
            performLogin();

            // Step 2: Add to cart and measure time
            System.out.println("Step 2: Adding product to cart...");
            long elapsed = performAddToCartAndMeasure();
            System.out.println("Add to Cart duration: " + elapsed + " ms");
            Reporter.log("Add to Cart duration: " + elapsed + " ms", true);

            Assert.assertTrue(elapsed < 20000, "Add to Cart should be < 20000ms but was " + elapsed + "ms");
            System.out.println("Conclusion: PASS - Add to Cart within threshold (" + elapsed + " ms)");
            Reporter.log("Conclusion: PASS - Add to Cart within threshold (" + elapsed + " ms)", true);
        } catch (AssertionError e) {
            System.err.println("Conclusion: FAIL - Add to Cart exceeded threshold");
            Reporter.log("Conclusion: FAIL - Add to Cart exceeded threshold", true);
            throw e;
        } catch (Exception e) {
            System.err.println("Conclusion: ERROR - " + e.getMessage());
            Reporter.log("Conclusion: ERROR - " + e.getMessage(), true);
            throw e;
        }
    }
}
