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
    private static final String DUMMY_USER = "testingone";
    private static final String DUMMY_PASS = "Testing1";

    @Step("Add product to cart, proceed to checkout, fill shipping/payment and complete transaction")
    public boolean performCheckoutAndComplete() throws Exception {
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
        checkoutBtn.click();

        // 4. Validate order payment page
        wait.until(ExpectedConditions.urlContains("orderPayment"));
        try {
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[contains(text(),'ORDER PAYMENT')]")));
        } catch (Exception ignored) {
        }

        // 5. Click Next (shipping -> payment)
        WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("next_btn")));
        nextBtn.click();

        // 6. Fill SafePay payment (use dummy data)
        WebElement safepayUser = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("safepay_username")));
        WebElement safepayPass = driver.findElement(By.name("safepay_password"));
        safepayUser.clear();
        safepayUser.sendKeys(DUMMY_USER);
        safepayPass.clear();
        safepayPass.sendKeys(DUMMY_PASS);

        // 7. Attempt to click Pay button (try common labels/ids)
        boolean payClicked = false;
        try {
            WebElement payBtn = driver.findElement(
                    By.xpath("//button[contains(translate(text(),'PAY','pay'),'pay') or contains(.,'Pay Now')]"));
            payBtn.click();
            payClicked = true;
        } catch (Exception ignored) {
            try {
                WebElement payBtn2 = driver.findElement(By.id("pay_now_btn_SAFEPAY"));
                payBtn2.click();
                payClicked = true;
            } catch (Exception ignored2) {
            }
        }

        // 8. Wait for confirmation (either thank you message or order confirmation URL)
        try {
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            boolean confirmed = longWait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//p[contains(text(),'Thank you') or contains(.,'Thank you') ]")),
                    ExpectedConditions.urlContains("orderConf"),
                    ExpectedConditions.urlContains("orderConfirmation")));
            return confirmed;
        } catch (TimeoutException te) {
            throw te;
        }
    }

    @Test(description = "Complete checkout using dummy data without timeout errors")
    public void checkoutFlowTest() {
        try {
            boolean success = performCheckoutAndComplete();
            System.out.println("Checkout completed: " + success);
            Reporter.log("Checkout completed: " + success, true);
            Assert.assertTrue(success, "Checkout did not complete successfully (no confirmation detected)");
            System.out.println("Conclusion: PASS - checkout completed successfully");
            Reporter.log("Conclusion: PASS - checkout completed successfully", true);
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
