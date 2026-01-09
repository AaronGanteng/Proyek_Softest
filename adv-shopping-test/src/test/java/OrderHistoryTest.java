import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;

public class OrderHistoryTest extends BaseTest {

    @Test
    public void ordersListTiming() {
        final String HOME = "https://www.advantageonlineshopping.com";
        final String USER = "testingbaru2";
        final String PASS = "Testingbaru2";

        long duration = -1;
        String conclusion = "UNKNOWN";

        try {
            // login (same pattern used in LoginTest)
            driver.get(HOME);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.elementToBeClickable(By.id("menuUser"))).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            driver.findElement(By.name("username")).clear();
            driver.findElement(By.name("username")).sendKeys(USER);
            driver.findElement(By.name("password")).clear();
            driver.findElement(By.name("password")).sendKeys(PASS);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.loader")));

            // Additional wait to ensure loader is fully gone
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }

            // Use JavaScript click to avoid interception issues
            WebElement signInBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("sign_in_btn")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signInBtn);
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("menuUserLink"), USER));

            // Measure time to load orders list
            duration = measureExecutionTime(() -> {
                // navigate to My Orders - try menu link, else direct urls
                boolean navigated = false;
                try {
                    driver.findElement(By.id("menuUser")).click();
                    try {
                        WebElement orders = null;
                        try {
                            orders = driver.findElement(
                                    By.xpath("//label[contains(.,'My Orders') or contains(.,'My orders')]"));
                        } catch (Exception ignored) {
                        }
                        if (orders == null) {
                            try {
                                orders = driver.findElement(By.xpath(
                                        "//a[contains(.,'Orders') or contains(.,'My Orders') or contains(.,'My orders')]"));
                            } catch (Exception ignored) {
                            }
                        }
                        if (orders != null) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", orders);
                            navigated = true;
                        }
                    } catch (Exception ignored) {
                    }
                } catch (Exception ignored) {
                }
                if (!navigated) {
                    try {
                        driver.get(HOME + "/#/orderHistory");
                        navigated = true;
                    } catch (Exception ignored) {
                    }
                }
                if (!navigated) {
                    try {
                        driver.get(HOME + "/#/myOrders");
                        navigated = true;
                    } catch (Exception ignored) {
                    }
                }

                // poll for populated orders list (JS count of likely selectors)
                long start = System.currentTimeMillis();
                long timeout = 8000; // wait up to 8s
                boolean populated = false;
                while (System.currentTimeMillis() - start < timeout) {
                    try {
                        Object cnt = ((JavascriptExecutor) driver).executeScript(
                                "return (function(){\n" +
                                        "var selectors = ['table.ordersTable tbody tr', 'table#orderList tbody tr', '.ordersList .order', '.orderHistory-list .order', 'tbody tr'];\n"
                                        +
                                        "for(var i=0;i<selectors.length;i++){ var n=document.querySelectorAll(selectors[i]); if(n && n.length>0) return n.length;}\n"
                                        +
                                        "return 0; })();");
                        int c = 0;
                        if (cnt instanceof Number)
                            c = ((Number) cnt).intValue();
                        else if (cnt != null) {
                            try {
                                c = Integer.parseInt(cnt.toString());
                            } catch (Exception ignored) {
                            }
                        }
                        if (c > 0) {
                            populated = true;
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {
                    }
                }
                if (!populated)
                    throw new RuntimeException("Order list not populated within timeout");
            });

            Reporter.log("Orders load time: " + duration + " ms");
            try {
                Assert.assertTrue(duration < 3000, "Orders list should load < 3000ms but was " + duration + "ms");
                conclusion = "PASS - orders load time " + duration + " ms";
                Reporter.log(conclusion);
                System.out.println(conclusion);
            } catch (AssertionError ae) {
                conclusion = "FAIL - orders load time " + duration + " ms";
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
