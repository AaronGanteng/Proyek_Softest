import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;

public class InitialLoadTest extends BaseTest {

    @Test
    public void initialLoadTiming() {
        String url = "https://www.advantageonlineshopping.com";
        long duration = -1;
        String conclusion = "UNKNOWN";
        try {
            // Clear browser cache and cookies via Chrome CDP
            if (driver instanceof ChromeDriver) {
                ChromeDriver chrome = (ChromeDriver) driver;
                chrome.executeCdpCommand("Network.clearBrowserCache", new java.util.HashMap<>());
                chrome.executeCdpCommand("Network.clearBrowserCookies", new java.util.HashMap<>());
                Reporter.log("Cleared browser cache and cookies via CDP");
            } else {
                Reporter.log("Driver is not ChromeDriver; skipping CDP cache clear");
            }

            duration = measureExecutionTime(() -> {
                driver.get(url);
                new WebDriverWait(driver, Duration.ofSeconds(30))
                        .until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState")
                                .equals("complete"));
            });

            Reporter.log("Page load time: " + duration + " ms");

            Assert.assertTrue(duration < 3000,
                    "Initial load time should be under 3000ms but was " + duration + "ms");
            conclusion = "PASS - load time " + duration + " ms";
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
