import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class ProductDetailTest extends BaseTest {

    @Test
    public void productDetailLoadTime() {
        final String HOME_URL = "https://www.advantageonlineshopping.com";
        long duration = -1;
        String conclusion = "UNKNOWN";

        try {
            driver.get(HOME_URL);

            // Navigate to Laptops category
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement laptopsCard = wait.until(ExpectedConditions.elementToBeClickable(By.id("laptopsImg")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", laptopsCard);
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
            }
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", laptopsCard);

            // Wait for product thumbnails
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("imgProduct")));

            duration = measureExecutionTime(() -> {
                // Try to find specific product 'HP Pavilion'
                List<WebElement> matches = driver.findElements(
                        By.xpath("//h3[contains(., 'HP Pavilion') or contains(., 'Pavilion') or contains(., 'HP')]"));
                if (!matches.isEmpty()) {
                    try {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
                                matches.get(0));
                        matches.get(0).click();
                        return;
                    } catch (Exception ignored) {
                    }
                }

                // Fallback: click first product thumbnail
                List<WebElement> products = driver.findElements(By.className("imgProduct"));
                if (products.isEmpty()) {
                    throw new RuntimeException("No products found in Laptops category");
                }
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", products.get(0));

                // Wait until product controls (Add to Cart) and a description/title are visible
                WebDriverWait innerWait = new WebDriverWait(driver, Duration.ofSeconds(30));
                innerWait.until(d -> {
                    boolean addVisible = d.findElements(By.name("save_to_cart")).stream()
                            .anyMatch(WebElement::isDisplayed);
                    boolean descVisible = d.findElements(By.xpath(
                            "//h3|//div[contains(@class,'product') and .//p]|//p[contains(@class,'description')]"))
                            .stream().anyMatch(WebElement::isDisplayed);
                    return addVisible && descVisible;
                });
            });

            Reporter.log("Product detail load time: " + duration + " ms");

            Assert.assertTrue(duration < 5000, "Product detail should load < 5000ms but was " + duration + "ms");
            conclusion = "PASS - product detail load " + duration + " ms";
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
