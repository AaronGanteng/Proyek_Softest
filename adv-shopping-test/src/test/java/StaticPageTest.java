import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;

public class StaticPageTest extends BaseTest {

    @Test
    public void footerStaticPageTiming() {
        final String HOME = "https://www.advantageonlineshopping.com";
        long duration = -1;
        String conclusion = "UNKNOWN";

        try {
            driver.get(HOME);
            // ensure page ready
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }

            duration = measureExecutionTime(() -> {
                // scroll to footer
                try {
                    ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
                } catch (Exception ignored) {
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ignored) {
                }

                boolean clicked = false;
                // try Contact Us first
                try {
                    WebElement contact = driver.findElement(By.xpath(
                            "//a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'contact') or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'contact us')]"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", contact);
                    clicked = true;
                } catch (Exception ignored) {
                }

                // try About if Contact not found
                if (!clicked) {
                    try {
                        WebElement about = driver.findElement(By.xpath(
                                "//a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'about') or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'about us')]"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", about);
                        clicked = true;
                    } catch (Exception ignored) {
                    }
                }

                // fallback direct navigation
                if (!clicked) {
                    try {
                        driver.get(HOME + "/#/contact_us");
                        clicked = true;
                    } catch (Exception ignored) {
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {
                    }
                }

                if (!clicked)
                    throw new RuntimeException("Footer links not found or not clickable");

                // wait/poll for page indicator (contact_us id or ABOUT text)
                long start = System.currentTimeMillis();
                long timeout = 5000; // allow up to 5s to appear
                boolean appeared = false;
                while (System.currentTimeMillis() - start < timeout) {
                    try {
                        // check for #contact_us element
                        if (driver.findElements(By.id("contact_us")).size() > 0) {
                            appeared = true;
                            break;
                        }
                        // check for visible heading with contact/about text
                        if (driver.findElements(By.xpath(
                                "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'contact us') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'about us') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'about')]"))
                                .size() > 0) {
                            appeared = true;
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {
                    }
                }

                if (!appeared)
                    throw new RuntimeException("Static page did not appear within timeout");
            });

            Reporter.log("Static page load time: " + duration + " ms");
            try {
                Assert.assertTrue(duration < 2000, "Static page should load < 2000ms but was " + duration + "ms");
                conclusion = "PASS - static page load " + duration + " ms";
                Reporter.log(conclusion);
                System.out.println(conclusion);
            } catch (AssertionError ae) {
                conclusion = "FAIL - static page load " + duration + " ms";
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
