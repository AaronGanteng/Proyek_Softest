import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;

public class LoginTest extends BaseTest {
    private static final String DUMMY_USER = "testingone";
    private static final String DUMMY_PASS = "Testing1";

    @Step("Perform login and measure time until username appears in UI")
    public long performLoginAndMeasure() {
        final String HOME_URL = "https://www.advantageonlineshopping.com";

        Runnable action = () -> {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Navigate to home page first
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

            // Click sign in (wait until clickable / loader gone)
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.loader")));
            wait.until(ExpectedConditions.elementToBeClickable(By.id("sign_in_btn")));
            driver.findElement(By.id("sign_in_btn")).click();

            // Wait until the username appears in the menu (login success indicator)
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("menuUserLink"), DUMMY_USER));
        };

        return measureExecutionTime(action);
    }

    @Test(description = "Login completes and username appears within 3s")
    public void loginPerformanceTest() {
        long elapsed = performLoginAndMeasure();
        System.out.println("Login flow duration: " + elapsed + " ms");
        Reporter.log("Login flow duration: " + elapsed + " ms", true);

        try {
            Assert.assertTrue(elapsed < 3000, "Login process should be < 3000ms but was " + elapsed + "ms");
            System.out.println("Conclusion: PASS - login within threshold (" + elapsed + " ms)");
            Reporter.log("Conclusion: PASS - login within threshold (" + elapsed + " ms)", true);
        } catch (AssertionError e) {
            System.err.println("Conclusion: FAIL - login exceeded threshold (" + elapsed + " ms)");
            Reporter.log("Conclusion: FAIL - login exceeded threshold (" + elapsed + " ms)", true);
            throw e;
        }
    }
}
