import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import java.time.Duration;
import java.util.Set;

public class FooterTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        // Setup standar seperti biasa
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        driver.get("https://www.advantageonlineshopping.com/#/");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("our_products")));
            System.out.println("Website siap untuk tes Footer!");
            Thread.sleep(15000);
        } catch (Exception e) {
            System.out.println("Website loading lama/timeout.");
        }
    }

    @Test(priority = 1)
    @Story("Facebook Link Test")
    @Description("Klik ikon Facebook di Footer -> Buka Tab Baru -> Tunggu 5 detik -> Tutup Tab")
    public void testLinkFacebook() throws InterruptedException {
        System.out.println("=== TEST: Klik Facebook Button ===");

        // 1. Simpan ID Tab Utama (Homepage)
        String mainWindowHandle = driver.getWindowHandle();

        // 2. Scroll ke Bawah (PENTING untuk Footer)
        WebElement menuProducts = driver.findElement(By.xpath("//a[contains(text(), 'CONTACT US')]"));
        menuProducts.click();
        Thread.sleep(5000);

        // 3. Klik Tombol Facebook
        WebElement fbButton = driver.findElement(By.name("follow_facebook"));
        Assert.assertTrue(fbButton.isDisplayed(), "Tombol Facebook tidak terlihat!");
        
        fbButton.click();
        System.out.println("Tombol Facebook diklik.");

        // 4. HANDLING NEW TAB
        Thread.sleep(5000);
        
        Set<String> allWindowHandles = driver.getWindowHandles();
        
        // Pindah fokus ke tab baru (Facebook)
        for (String handle : allWindowHandles) {
            if (!handle.equals(mainWindowHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        // 5. Tunggu 5 Detik di Tab Facebook (Sesuai Request)
        System.out.println("Menunggu 5 detik di halaman Facebook...");
        Thread.sleep(5000);

        // Validasi: Cek URL mengandung 'facebook'
        String newTabUrl = driver.getCurrentUrl();
        System.out.println("URL saat ini: " + newTabUrl);
        
        // Note: Kadang facebook redirect login, tapi URL pasti ada kata 'facebook'
        Assert.assertTrue(newTabUrl.contains("facebook"), "Gagal membuka halaman Facebook!");

        // 6. Tutup Tab Facebook & Kembali ke Utama
        driver.close(); // Tutup tab aktif
        driver.switchTo().window(mainWindowHandle); // Balik ke Home
        System.out.println("Tab Facebook ditutup. Kembali ke Homepage.");
        
        // Validasi akhir
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "Gagal fokus kembali ke Homepage!");
        Thread.sleep(3000);
    }

    @Test(priority = 2)
    @Story("Twitter Link Test")
    @Description("Klik ikon Twitter di Footer -> Buka Tab Baru -> Tunggu 5 detik -> Tutup Tab")
    public void testLinkTwitter() throws InterruptedException {
        System.out.println("=== TEST: Klik Twitter Button ===");

        // 1. Simpan ID Tab Utama
        String mainWindowHandle = driver.getWindowHandle();

        // 2. Scroll ke Bawah (Opsional, jaga-jaga kalau belum di bawah)
        WebElement menuProducts = driver.findElement(By.xpath("//a[contains(text(), 'CONTACT US')]"));
        menuProducts.click();
        Thread.sleep(3000);

        // 3. Klik Tombol Twitter
        WebElement twitterButton = driver.findElement(By.name("follow_twitter"));
        Assert.assertTrue(twitterButton.isDisplayed(), "Tombol Twitter tidak terlihat!");
        
        twitterButton.click();
        System.out.println("Tombol Twitter diklik.");

        // 4. HANDLING NEW TAB
        Thread.sleep(5000); // Tunggu tab terbuka
        
        Set<String> allWindowHandles = driver.getWindowHandles();
        
        // Pindah fokus ke tab baru
        for (String handle : allWindowHandles) {
            if (!handle.equals(mainWindowHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        // 5. Tunggu 5 Detik
        System.out.println("Menunggu 5 detik di halaman Twitter...");
        Thread.sleep(5000);

        // Validasi URL (Bisa 'twitter.com' atau 'x.com')
        String newTabUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + newTabUrl);
        
        // Kita pakai logika OR karena Twitter sekarang sudah rebranding jadi X
        boolean isTwitter = newTabUrl.contains("twitter.com") || newTabUrl.contains("x.com");
        Assert.assertTrue(isTwitter, "Gagal membuka halaman Twitter/X!");

        // 6. Tutup Tab & Kembali
        driver.close();
        driver.switchTo().window(mainWindowHandle);
        
        System.out.println("Tab Twitter ditutup. Kembali ke Homepage.");
        
        // Validasi akhir
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "Gagal fokus kembali ke Homepage!");
        Thread.sleep(3000);
    }

    @Test(priority = 3)
    @Story("Social Media Link")
    @Description("Klik ikon LinkedIn di Footer -> Buka Tab Baru -> Tunggu 5 detik -> Tutup Tab")
    public void testLinkLinkedIn() throws InterruptedException {
        System.out.println("=== TEST: Klik LinkedIn Button ===");

        // 1. Simpan ID Tab Utama
        String mainWindowHandle = driver.getWindowHandle();

        // 2. Scroll ke Bawah
        WebElement menuProducts = driver.findElement(By.xpath("//a[contains(text(), 'CONTACT US')]"));
        menuProducts.click();
        Thread.sleep(3000);

        // 3. Klik Tombol LinkedIn
        // Locator: By.name("follow_linkedin")
        WebElement linkedinButton = driver.findElement(By.name("follow_linkedin"));
        
        Assert.assertTrue(linkedinButton.isDisplayed(), "Tombol LinkedIn tidak terlihat!");
        
        linkedinButton.click();
        System.out.println("Tombol LinkedIn diklik.");

        // 4. HANDLING NEW TAB
        Thread.sleep(3000); // Tunggu tab terbuka
        
        Set<String> allWindowHandles = driver.getWindowHandles();
        
        // Pindah fokus ke tab baru
        for (String handle : allWindowHandles) {
            if (!handle.equals(mainWindowHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        // 5. Tunggu 5 Detik
        System.out.println("Menunggu 5 detik di halaman LinkedIn...");
        Thread.sleep(5000);

        // Validasi URL
        String newTabUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + newTabUrl);
        Assert.assertTrue(newTabUrl.contains("linkedin.com"), "Gagal membuka halaman LinkedIn!");

        // 6. Tutup Tab & Kembali
        driver.close();
        driver.switchTo().window(mainWindowHandle);
        
        System.out.println("Tab LinkedIn ditutup. Kembali ke Homepage.");
        
        // Validasi akhir
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "Gagal fokus kembali ke Homepage!");
    }

    @AfterClass
    public void tearDown() {
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        if (driver != null) {
            driver.quit();
        }
    }
}
