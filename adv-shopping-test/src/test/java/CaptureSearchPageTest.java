import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

public class CaptureSearchPageTest extends BaseTest {

    @Test
    public void captureSearchPage() throws Exception {
        String url = "https://www.advantageonlineshopping.com/#/search/";
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        // wait for product list or filter sidebar
        wait.until(d -> d.findElements(org.openqa.selenium.By.className("imgProduct")).size() > 0);

        // Save page source
        String html = driver.getPageSource();
        Path outHtml = Path.of("target", "search-page.html");
        Files.createDirectories(outHtml.getParent());
        Files.writeString(outHtml, html);

        // Save screenshot (ChromeDriver required)
        if (driver instanceof ChromeDriver) {
            File scr = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
            Path outPng = Path.of("target", "search-page.png");
            Files.copy(scr.toPath(), outPng, StandardCopyOption.REPLACE_EXISTING);
        }

        System.out
                .println("Saved search page HTML -> target/search-page.html and screenshot -> target/search-page.png");
    }
}
