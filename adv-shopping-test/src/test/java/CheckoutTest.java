import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class CheckoutTest extends BaseTest {
    private static final String HOME_URL = "https://www.advantageonlineshopping.com";
    private static final String DUMMY_USER = "testingbaru2";
    private static final String DUMMY_PASS = "Testingbaru2";

    @Step("Add product to cart, proceed to checkout, fill shipping/payment and complete transaction")
    public long performCheckoutAndComplete() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // 1. Go to homepage and pick a product
        driver.get(HOME_URL);

        // Navigate to Speakers category to ensure products exist
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("speakersImg")));
        driver.findElement(By.id("speakersImg")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("imgProduct")));
        List<WebElement> products = driver.findElements(By.className("imgProduct"));
        if (products.isEmpty())
            throw new RuntimeException("No products found to add to cart");
        products.get(0).click();

        // 2. Add to cart and wait for tooltip
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(By.name("save_to_cart")));
        addBtn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("toolTipCart")));

        // 3. Go to cart
        driver.findElement(By.id("menuCart")).click();
        wait.until(ExpectedConditions.urlContains("shoppingCart"));

        // 3.a If checkout requires login, perform login using dummy creds
        try {
            // If menuUserLink already shows username, skip
            if (driver.findElements(By.id("menuUserLink")).isEmpty()
                    || !driver.findElement(By.id("menuUserLink")).getText().contains(DUMMY_USER)) {
                driver.findElement(By.id("menuUser")).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
                driver.findElement(By.name("username")).sendKeys(DUMMY_USER);
                driver.findElement(By.name("password")).sendKeys(DUMMY_PASS);
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.loader")));
                wait.until(ExpectedConditions.elementToBeClickable(By.id("sign_in_btn")));
                driver.findElement(By.id("sign_in_btn")).click();
                wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("menuUserLink"), DUMMY_USER));
            }
        } catch (Exception ignored) {
            // continue; some sites may auto-login or not require it
        }

        WebElement checkoutBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("checkOutButton")));

        // Measure duration from clicking checkout button until payment page loads
        Runnable action = () -> {
            checkoutBtn.click();

            // 4. Validate order payment page
            wait.until(ExpectedConditions.urlContains("orderPayment"));
            try {
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(By.xpath("//h3[contains(text(),'ORDER PAYMENT')]")));
            } catch (Exception ignored) {
            }

            // 5. Click Next (shipping -> payment)
            WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("next_btn")));
            nextBtn.click();

            // 6. Validate payment page is reached (network performance goal)
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("safepay_username")));
            } catch (TimeoutException te) {
                throw new RuntimeException("Payment page did not load");
            }
        };

        return measureExecutionTime(action);
    }

    @Test(description = "Checkout flow reaches payment page successfully")
    public void checkoutFlowTest() {
        try {
            long elapsed = performCheckoutAndComplete();
            System.out.println("Checkout duration: " + elapsed + " ms");
            Reporter.log("Checkout duration: " + elapsed + " ms", true);

            Assert.assertTrue(elapsed < 15000, "Checkout should be < 15000ms but was " + elapsed + "ms");
            System.out.println("Conclusion: PASS - Checkout within threshold (" + elapsed + " ms)");
            Reporter.log("Conclusion: PASS - Checkout within threshold (" + elapsed + " ms)", true);
        } catch (AssertionError e) {
            System.err.println("Conclusion: FAIL - Checkout exceeded threshold");
            Reporter.log("Conclusion: FAIL - Checkout exceeded threshold", true);
            throw e;
        } catch (TimeoutException te) {
            System.err.println("Conclusion: FAIL - Timeout occurred during checkout: " + te.getMessage());
            Reporter.log("Conclusion: FAIL - Timeout occurred during checkout: " + te.getMessage(), true);
            Assert.fail("Timeout during checkout: " + te.getMessage());
        } catch (Exception e) {
            System.err.println("Conclusion: FAIL - Error during checkout: " + e.getMessage());
            Reporter.log("Conclusion: FAIL - Error during checkout: " + e.getMessage(), true);
            Assert.fail("Error during checkout: " + e.getMessage());
        }
    }
}
