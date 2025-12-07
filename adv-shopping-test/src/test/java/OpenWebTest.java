import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class OpenWebTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        // Selenium Manager otomatis mengurus Driver Chrome
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Menunggu elemen maksimal 10 detik jika loading lambat
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    public void testBukaWebsite() {
        // 1. Buka Website Target
        System.out.println("Sedang membuka website...");
        driver.get("https://www.advantageonlineshopping.com/#/");
        
        // 2. Ambil Judul Halaman
        String title = driver.getTitle();
        System.out.println("Judul Website saat ini: " + title);

        // 3. Validasi Sederhana (Assert)
        // Kita memastikan judulnya mengandung kata "Advantage"
        Assert.assertTrue(title.contains("Advantage"), "Ups! Judul website tidak sesuai.");
    }

    @AfterClass
    public void tearDown() {
        // Tutup browser setelah tes selesai (beri jeda 10 detik biar sempat lihat)
        try { Thread.sleep(10000); } catch (InterruptedException e) { e.printStackTrace(); }
        
        if (driver != null) {
            driver.quit();
        }
    }
}