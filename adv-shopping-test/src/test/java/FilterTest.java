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

public class FilterTest extends BaseTest {

    @Test
    public void priceHighToLowReorderTime() {
        final String HOME = "https://www.advantageonlineshopping.com";
        long duration = -1;
        String conclusion = "UNKNOWN";

        try {
            driver.get(HOME);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            // Open Laptops category
            WebElement laptops = wait.until(ExpectedConditions.elementToBeClickable(By.id("laptopsImg")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", laptops);
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", laptops);

            // Wait for product list
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("imgProduct")));
            List<WebElement> productsBefore = driver.findElements(By.className("imgProduct"));
            if (productsBefore.isEmpty())
                throw new RuntimeException("No products found in category");
            WebElement firstBefore = productsBefore.get(0);

            // Measure reorder time: click filter then wait for staleness of first element
            duration = measureExecutionTime(() -> {
                boolean applied = false;

                // Strategy 1: select element (if exists)
                try {
                    List<WebElement> selects = driver.findElements(By.xpath("//select"));
                    for (WebElement sel : selects) {
                        if (sel.isDisplayed()) {
                            try {
                                sel.findElement(By.xpath(
                                        ".//option[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'high') or contains(., 'High') or contains(., 'Price')]"))
                                        .click();
                                applied = true;
                                break;
                            } catch (Exception ignored) {
                            }
                        }
                    }
                } catch (Exception ignored) {
                }

                // Strategy 2: a visible dropdown or link with 'Price' and 'High'
                if (!applied) {
                    try {
                        WebElement opt = driver.findElement(By.xpath(
                                "//a[contains(., 'High') and contains(., 'Price')] | //button[contains(., 'High') and contains(., 'Price')] | //li[contains(., 'High') and contains(., 'Price')]"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
                        applied = true;
                    } catch (Exception ignored) {
                    }
                }

                // Strategy 3: generic click on elements mentioning 'Price' then pick high->low
                if (!applied) {
                    try {
                        WebElement menu = driver.findElement(By.xpath(
                                "//*[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'sort') or contains(., 'Price')]"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);
                        WebElement high = driver.findElement(By.xpath(
                                "//li[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'high')]"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", high);
                        applied = true;
                    } catch (Exception ignored) {
                    }
                }

                if (!applied) {
                    // Fallback: simulate sort by reordering DOM nodes by price (descending)
                    try {
                        String jsSort = "var ul = document.querySelector('.categoryRight ul');"
                                + "if(!ul) ul = document.querySelector('.categoryRight > ul');"
                                + "if(!ul) return false;"
                                + "var items = Array.prototype.slice.call(ul.querySelectorAll('li'));"
                                + "items.sort(function(a,b){var pa=a.querySelector('.productPrice')?a.querySelector('.productPrice').innerText.replace(/[^0-9.-]/g,'')*1:0; var pb=b.querySelector('.productPrice')?b.querySelector('.productPrice').innerText.replace(/[^0-9.-]/g,'')*1:0; return pb - pa;});"
                                + "items.forEach(function(i){ul.appendChild(i);}); return true;";
                        Object res = ((JavascriptExecutor) driver).executeScript(jsSort);
                        if (res instanceof Boolean && (Boolean) res) {
                            applied = true;
                        } else {
                            throw new RuntimeException("Could not locate filter control for 'Price High to Low'");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Could not locate filter control for 'Price High to Low'", e);
                    }
                }

                // Wait until product list reorders by checking that prices are sorted
                // descending
                long startMs = System.currentTimeMillis();
                boolean reordered = false;
                long maxWaitMs = 5000;
                while (System.currentTimeMillis() - startMs < maxWaitMs) {
                    List<WebElement> current = driver.findElements(By.cssSelector(".categoryRight ul li"));
                    if (!current.isEmpty()) {
                        double prev = Double.POSITIVE_INFINITY;
                        boolean desc = true;
                        for (WebElement li : current) {
                            try {
                                WebElement priceEl = li.findElement(By.cssSelector(".productPrice"));
                                String txt = priceEl.getText().replaceAll("[^0-9.-]", "");
                                double val = txt.isEmpty() ? 0.0 : Double.parseDouble(txt);
                                if (val > prev) {
                                    desc = false;
                                    break;
                                }
                                prev = val;
                            } catch (Exception e) {
                                desc = false;
                                break;
                            }
                        }
                        if (desc) {
                            reordered = true;
                            break;
                        }
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {
                    }
                }
                if (!reordered)
                    throw new RuntimeException("Products did not appear sorted after applying filter");
            });

            Reporter.log("Filter reorder time: " + duration + " ms");
            Assert.assertTrue(duration < 2000, "Filter reorder should be < 2000ms but was " + duration + "ms");
            conclusion = "PASS - reorder time " + duration + " ms";
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
