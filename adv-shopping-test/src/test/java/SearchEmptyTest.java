import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;

public class SearchEmptyTest extends BaseTest {

    @Test
    public void searchEmptyTiming() {
        final String HOME = "https://www.advantageonlineshopping.com";
        final String KEY = "AJSKD123";

        long duration = -1;
        String conclusion = "UNKNOWN";

        try {
            driver.get(HOME);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Open search
            wait.until(ExpectedConditions.elementToBeClickable(By.id("menuSearch"))).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("autoComplete")));

            duration = measureExecutionTime(() -> {
                try {
                    WebElement input = driver.findElement(By.id("autoComplete"));
                    input.clear();
                    input.sendKeys(KEY);

                    // Trigger search by clicking search icon again
                    try {
                        driver.findElement(By.id("menuSearch")).click();
                    } catch (Exception e) {
                        try {
                            ((JavascriptExecutor) driver)
                                    .executeScript("document.getElementById('menuSearch').click();");
                        } catch (Exception ignored) {
                        }
                    }

                    // Poll for 'No results' message up to 2000ms
                    long start = System.currentTimeMillis();
                    long timeout = 30000;
                    boolean seen = false;
                    while (System.currentTimeMillis() - start < timeout) {
                        try {
                            Object found = ((JavascriptExecutor) driver).executeScript(
                                    "return (document.body && document.body.innerText && document.body.innerText.indexOf('No results for')!=-1) ? true : false;");
                            if (found instanceof Boolean && (Boolean) found) {
                                seen = true;
                                break;
                            }
                        } catch (Exception ignored) {
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    if (!seen)
                        throw new RuntimeException("No results message not observed within timeout");
                } catch (RuntimeException re) {
                    throw re;
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            });

            Reporter.log("No-results response time: " + duration + " ms");
            try {
                Assert.assertTrue(duration < 1000, "No-results response should be < 1000ms but was " + duration + "ms");
                conclusion = "PASS - no-results response time " + duration + " ms";
                Reporter.log(conclusion);
                System.out.println(conclusion);
            } catch (AssertionError ae) {
                conclusion = "FAIL - no-results response time " + duration + " ms";
                Reporter.log(conclusion);
                System.out.println(conclusion);
                throw ae;
            }

        } catch (Throwable t) {
            conclusion = "ERROR - " + t.getClass().getSimpleName() + ": " + t.getMessage() + " (measured " + duration
                    + " ms)";
            Reporter.log(conclusion);
            System.out.println(conclusion);
            throw new RuntimeException(t);
        } finally {
            Reporter.log("Conclusion: " + conclusion);
            System.out.println("Conclusion: " + conclusion);
        }
    }
}
