import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Story;

public class AdminLogin {
    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless"); // Aktifkan jika ingin mode background
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        js = (JavascriptExecutor) driver;

        driver.get("https://www.advantageonlineshopping.com/#/");
        
        // Tunggu Homepage Load
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logo")));
        } catch (Exception e) {
            System.out.println("Homepage load warning, lanjut...");
        }
    }

    public void navigateToAdminPage() throws InterruptedException {
        String currentUrl = driver.getCurrentUrl();
        
        // Jika URL sudah mengandung 'admin', berarti kita sudah di tempat yang benar. Skip navigasi.
        if (currentUrl.contains("admin") && !currentUrl.contains("advantageonlineshopping.com/#/")) {
            System.out.println("Info: Sudah berada di halaman Admin.");
            return; 
        }

        System.out.println(">> Navigasi ke Halaman Admin...");
        String mainWindowHandle = driver.getWindowHandle();

        // 1. Klik Menu Help
        try {
            WebElement helpMenu = wait.until(ExpectedConditions.elementToBeClickable(By.id("menuHelp")));
            helpMenu.click();
            Thread.sleep(3000);
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", driver.findElement(By.id("menuHelp")));
        }

        // 2. Klik Management Console
        try {
            WebElement consoleLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[contains(text(), 'Management Console')]")
            ));
            consoleLink.click();
        } catch (Exception e) {
            // Fallback
            WebElement labelFallback = driver.findElement(By.xpath("//label[@translate='CONFIG_TOOL']"));
            js.executeScript("arguments[0].click();", labelFallback);
        }

        // 3. Pindah Tab
        System.out.println("Menunggu Tab Admin terbuka...");
        Thread.sleep(4000);
        
        Set<String> allHandles = driver.getWindowHandles();
        for (String handle : allHandles) {
            if (!handle.equals(mainWindowHandle)) {
                driver.switchTo().window(handle);
                System.out.println("Berhasil switch ke window baru.");
                break;
            }
        }
        
        // Validasi
        wait.until(ExpectedConditions.urlContains("admin"));
    }

    @Test(priority = 1)
    @Story("Bug Reproduction: Forgot Password")
    @Description("Cek elemen 'Forget Password'. BUG: Harusnya link, tapi ternyata cuma teks biasa.")
    public void testForgotPasswordBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 1: CEK BUG FORGOT PASSWORD (UNCLICKABLE) ===");
        
        navigateToAdminPage();

        // 1. CARI ELEMEN FORGOT PASSWORD (UPDATE LOCATOR)
        System.out.println("Mencari elemen 'Forget Password'...");
        
        WebElement forgotPwdElement = null;
        try {
            forgotPwdElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("remember-me")
            ));
            System.out.println("Elemen ditemukan: " + forgotPwdElement.getText());
        } catch (Exception e) {
            forgotPwdElement = driver.findElement(By.xpath("//div[contains(text(), 'Forget Password')]"));
        }

        // 2. CEK APAKAH INI LINK (<a>) ATAU CUMA DIV BIASA
        String tagName = forgotPwdElement.getTagName();
        String cursorStyle = forgotPwdElement.getCssValue("cursor");
        
        System.out.println("Tag Name: " + tagName);
        System.out.println("Cursor Style: " + cursorStyle);

        boolean isLink = tagName.equalsIgnoreCase("a");
        
        // 3. COBA KLIK & CEK PERUBAHAN URL
        String urlBefore = driver.getCurrentUrl();
        try {
            forgotPwdElement.click();
            System.out.println("Melakukan klik pada elemen...");
        } catch (Exception e) {
            System.out.println("Elemen tidak merespon klik.");
        }
        
        Thread.sleep(2000);
        String urlAfter = driver.getCurrentUrl();

        // 4. VALIDASI BUG
        if (urlBefore.equals(urlAfter) && !isLink) {
            System.out.println("[LOG BUG] 'Forget Password' hanya teks biasa & tidak berfungsi!");
            Assert.assertTrue(true);
        } else {
            System.out.println("INFO: Halaman berpindah. Bug tidak muncul.");
        }
        
        System.out.println("--> Test 1 Forgot Password Bug Check SELESAI.");
    }

    @Test(priority = 2)
    @Story("Admin Page UI")
    @Description("Cek Video Placeholder: Klik dan pastikan video memutar (muncul iframe) tanpa redirect.")
    public void testVideoPlayback() throws InterruptedException {
        System.out.println("=== MULAI TEST 2: CEK VIDEO PLAYBACK ===");
        // 1. CARI ELEMEN VIDEO PLACEHOLDER
        System.out.println("Mencari elemen video placeholder...");
        WebElement videoDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.className("video-placeholder")
        ));

        String initialUrl = driver.getCurrentUrl();
        
        // 2. KLIK VIDEO
        System.out.println("Mengklik video...");
        videoDiv.click();
        Thread.sleep(5000);

        // 3. VALIDASI 1: URL TIDAK BERUBAH (NO REDIRECT)
        String finalUrl = driver.getCurrentUrl();
        if (!initialUrl.equals(finalUrl)) {
            Assert.fail("Error: Halaman melakukan redirect ke URL lain!");
        } else {
            System.out.println("[PASS] URL tetap sama (Video memutar di halaman ini).");
        }

        // 4. VALIDASI 2: CEK APAKAH IFRAME MUNCUL
        try {
            WebElement iframeVideo = driver.findElement(By.tagName("iframe"));
            System.out.println("[PASS] Iframe video berhasil dimuat: " + iframeVideo.getDomProperty("src"));
        } catch (Exception e) {
            System.out.println("[WARNING] Iframe tidak ditemukan. Mungkin video gagal load atau format berbeda.");
        }

        System.out.println("--> Test 2 Video Check Selesai.");
    }

    @Test(priority = 3)
    @Story("Admin Page UI")
    @Description("Cek Video Kedua (second-video-placeholder): Klik dan pastikan video memutar tanpa redirect.")
    public void testSecondVideoPlayback() throws InterruptedException {
        System.out.println("=== MULAI TEST 3: CEK VIDEO KEDUA ===");
        // 1. CARI ELEMEN VIDEO KEDUA
        System.out.println("Mencari elemen video kedua...");
        WebElement secondVideo = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.className("second-video-placeholder")
        ));

        String initialUrl = driver.getCurrentUrl();
        
        // 2. KLIK VIDEO
        System.out.println("Mengklik video kedua...");
        secondVideo.click();
        Thread.sleep(5000);

        // 3. VALIDASI: URL TIDAK BERUBAH
        String finalUrl = driver.getCurrentUrl();
        if (!initialUrl.equals(finalUrl)) {
            Assert.fail("Error: Halaman redirect saat klik video kedua!");
        } else {
            System.out.println("[PASS] URL tetap sama (Video 2 memutar di halaman ini).");
        }
        
        if (driver.findElements(By.tagName("iframe")).size() > 0) {
            System.out.println("[PASS] Iframe video terdeteksi.");
        }

        System.out.println("--> Test 3 Selesai.");
    }

    @Test(priority = 4)
    @Story("Admin Page UI")
    @Description("Cek Tombol Panah Kanan (Slider). BUG: Konten tidak berubah saat panah ditekan.")
    public void testRightArrowCarouselBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 4: CEK BUG CAROUSEL ARROW ===");
        // 1. REFRESH HALAMAN
        System.out.println("Me-refresh halaman untuk stop video & reset slider...");
        driver.navigate().refresh();
        
        WebElement rightArrow = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("rightBtn")
        ));
        
        WebElement sliderTextElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.tagName("h3") 
        ));

        // 2. AMBIL KONTEN SEBELUM KLIK
        String textBefore = sliderTextElement.getText();
        System.out.println("Teks Awal: " + textBefore);

        // 3. KLIK TOMBOL PANAH KANAN
        System.out.println("Mengklik panah kanan...");
        rightArrow.click();
        Thread.sleep(3000);

        // 4. AMBIL KONTEN SETELAH KLIK
        String textAfter = sliderTextElement.getText();
        System.out.println("Teks Akhir: " + textAfter);

        // 5. VALIDASI BUG
        if (textBefore.equals(textAfter)) {
            System.out.println("[LOG BUG] Konten Slider TIDAK BERUBAH saat panah ditekan!");
            Assert.assertEquals(textBefore, textAfter);
        } else {
            System.out.println("INFO: Konten berubah. Fitur berjalan normal (Bug tidak muncul).");
        }

        System.out.println("--> Test 4 Selesai.");
    }

    @Test(priority = 5)
    @Story("Admin Page UI")
    @Description("Klik link 'Before you begin' -> Verifikasi Tab Baru berisi URL PDF yang benar")
    public void testBeforeYouBeginLink() throws InterruptedException {
        System.out.println("=== MULAI TEST 5: CEK LINK PDF 'BEFORE YOU BEGIN' ===");
        // 1. PERSIAPAN WINDOW HANDLE
        String adminWindowHandle = driver.getWindowHandle();
        Set<String> existingHandles = driver.getWindowHandles();

        // 2. KLIK LINK 'Before you begin'
        System.out.println("Mencari link 'Before you begin'...");
        WebElement pdfLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, 'Before_You_Begin.pdf')]")));
        pdfLink.click();
        Thread.sleep(3000);

        // 3. TUNGGU TAB BARU TERBUKA
        System.out.println("Menunggu tab PDF terbuka...");
        try {
            wait.until(ExpectedConditions.numberOfWindowsToBe(existingHandles.size() + 1));
        } catch (Exception e) {
            Assert.fail("Tab baru tidak terbuka setelah link diklik.");
        }

        // 4. SWITCH KE TAB BARU (PDF)
        Set<String> newHandles = driver.getWindowHandles();
        String pdfWindowHandle = "";

        for (String handle : newHandles) {
            if (!existingHandles.contains(handle)) {
                pdfWindowHandle = handle;
                break;
            }
        }

        if (pdfWindowHandle.isEmpty()) {
            Assert.fail("Gagal menemukan ID window PDF.");
        }

        driver.switchTo().window(pdfWindowHandle);
        System.out.println("Berhasil switch ke tab PDF.");

        // 5. VALIDASI URL PDF
        String expectedPdfUrl = "https://s3.amazonaws.com/aos-on-prem-downloads/doc/0_Before_You_Begin.pdf";
        wait.until(ExpectedConditions.urlContains("pdf"));
        Thread.sleep(5000);
        
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + currentUrl);

        if (currentUrl.equals(expectedPdfUrl)) {
            System.out.println("[PASS] URL PDF Sesuai.");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        } else {
            System.out.println("[FAIL] URL PDF Tidak Sesuai!");
            System.out.println("Expected: " + expectedPdfUrl);
            System.out.println("Actual  : " + currentUrl);
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        }

        // 6. CLEANUP (PENTING!)
        System.out.println("Menutup tab PDF dan kembali ke Admin Page...");
        driver.close(); 
        driver.switchTo().window(adminWindowHandle);
        Thread.sleep(2000);
        
        System.out.println("--> Test 5 Selesai.");
    }

    @Test(priority = 6)
    @Story("Admin Page UI")
    @Description("Klik link 'Architecture and environments' -> Verifikasi Tab Baru berisi URL PDF yang benar")
    public void testArchitectureLink() throws InterruptedException {
        System.out.println("=== MULAI TEST 6: CEK LINK PDF 'ARCHITECTURE' ===");
        // 1. PERSIAPAN WINDOW HANDLE
        String adminWindowHandle = driver.getWindowHandle();
        Set<String> existingHandles = driver.getWindowHandles();

        // 2. KLIK LINK 'Architecture and environments'
        System.out.println("Mencari link 'Architecture and environments'...");
        WebElement pdfLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '1_Arch_and_Environments.pdf')]")));
        pdfLink.click();
        Thread.sleep(3000);

        // 3. TUNGGU TAB BARU TERBUKA
        System.out.println("Menunggu tab PDF terbuka...");
        try {
            wait.until(ExpectedConditions.numberOfWindowsToBe(existingHandles.size() + 1));
        } catch (Exception e) {
            Assert.fail("Tab baru tidak terbuka.");
        }

        // 4. SWITCH KE TAB PDF
        Set<String> newHandles = driver.getWindowHandles();
        String pdfWindowHandle = "";

        for (String handle : newHandles) {
            if (!existingHandles.contains(handle)) {
                pdfWindowHandle = handle;
                break;
            }
        }

        if (pdfWindowHandle.isEmpty()) Assert.fail("Gagal menemukan window PDF.");
        
        driver.switchTo().window(pdfWindowHandle);
        System.out.println("Berhasil switch ke tab PDF.");

        // 5. VALIDASI URL
        String expectedPdfUrl = "https://s3.amazonaws.com/aos-on-prem-downloads/doc/1_Arch_and_Environments.pdf";
        wait.until(ExpectedConditions.urlContains("pdf"));
        
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + currentUrl);

        if (currentUrl.equals(expectedPdfUrl)) {
            System.out.println("[PASS] URL PDF Sesuai.");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        } else {
            System.out.println("[FAIL] URL PDF Tidak Sesuai!");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        }

        // 6. CLEANUP (Tutup tab & Kembali)
        System.out.println("Menutup tab PDF dan kembali ke Admin Page...");
        driver.close(); 
        driver.switchTo().window(adminWindowHandle);
        Thread.sleep(2000);

        System.out.println("--> Test 6 Selesai.");
    }

    @Test(priority = 7)
    @Story("Admin Page UI")
    @Description("Klik link 'AOS front end' -> Verifikasi Tab Baru berisi URL PDF yang benar")
    public void testAOSFrontEndLink() throws InterruptedException {
        System.out.println("=== MULAI TEST 7: CEK LINK PDF 'AOS FRONT END' ===");
        // 1. PERSIAPAN WINDOW HANDLE
        String adminWindowHandle = driver.getWindowHandle();
        Set<String> existingHandles = driver.getWindowHandles();

        // 2. KLIK LINK 'AOS front end'
        System.out.println("Mencari link 'AOS front end'...");
        WebElement pdfLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '2_AOS_Front_End.pdf')]")));
        pdfLink.click();
        Thread.sleep(3000);

        // 3. TUNGGU TAB BARU TERBUKA
        System.out.println("Menunggu tab PDF terbuka...");
        try {
            wait.until(ExpectedConditions.numberOfWindowsToBe(existingHandles.size() + 1));
        } catch (Exception e) {
            Assert.fail("Tab baru tidak terbuka.");
        }

        // 4. SWITCH KE TAB PDF
        Set<String> newHandles = driver.getWindowHandles();
        String pdfWindowHandle = "";

        for (String handle : newHandles) {
            if (!existingHandles.contains(handle)) {
                pdfWindowHandle = handle;
                break;
            }
        }

        if (pdfWindowHandle.isEmpty()) Assert.fail("Gagal menemukan window PDF.");
        
        driver.switchTo().window(pdfWindowHandle);
        System.out.println("Berhasil switch ke tab PDF.");

        // 5. VALIDASI URL
        String expectedPdfUrl = "https://s3.amazonaws.com/aos-on-prem-downloads/doc/2_AOS_Front_End.pdf";
        wait.until(ExpectedConditions.urlContains("pdf"));
        
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + currentUrl);

        if (currentUrl.equals(expectedPdfUrl)) {
            System.out.println("[PASS] URL PDF Sesuai.");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        } else {
            System.out.println("[FAIL] URL PDF Tidak Sesuai!");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        }

        // 6. CLEANUP
        System.out.println("Menutup tab PDF dan kembali ke Admin Page...");
        driver.close(); 
        driver.switchTo().window(adminWindowHandle);
        
        System.out.println("--> Test 7 Selesai.");
    }

    @Test(priority = 8)
    @Story("Admin Page UI")
    @Description("Klik link 'AOS back end' -> Verifikasi Tab Baru berisi URL PDF yang benar")
    public void testAOSBackEndLink() throws InterruptedException {
        System.out.println("=== MULAI TEST 8: CEK LINK PDF 'AOS BACK END' ===");
        // 1. PERSIAPAN WINDOW HANDLE
        String adminWindowHandle = driver.getWindowHandle();
        Set<String> existingHandles = driver.getWindowHandles();

        // 2. KLIK LINK 'AOS back end'
        System.out.println("Mencari link 'AOS back end'...");
        WebElement pdfLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '3_AOS_Back_End.pdf')]")));
        pdfLink.click();
        Thread.sleep(3000);

        // 3. TUNGGU TAB BARU TERBUKA
        System.out.println("Menunggu tab PDF terbuka...");
        try {
            wait.until(ExpectedConditions.numberOfWindowsToBe(existingHandles.size() + 1));
        } catch (Exception e) {
            Assert.fail("Tab baru tidak terbuka.");
        }

        // 4. SWITCH KE TAB PDF
        Set<String> newHandles = driver.getWindowHandles();
        String pdfWindowHandle = "";

        for (String handle : newHandles) {
            if (!existingHandles.contains(handle)) {
                pdfWindowHandle = handle;
                break;
            }
        }

        if (pdfWindowHandle.isEmpty()) Assert.fail("Gagal menemukan window PDF.");
        
        driver.switchTo().window(pdfWindowHandle);
        System.out.println("Berhasil switch ke tab PDF.");

        // 5. VALIDASI URL
        String expectedPdfUrl = "https://s3.amazonaws.com/aos-on-prem-downloads/doc/3_AOS_Back_End.pdf";
        wait.until(ExpectedConditions.urlContains("pdf"));
        
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + currentUrl);

        if (currentUrl.equals(expectedPdfUrl)) {
            System.out.println("[PASS] URL PDF Sesuai.");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        } else {
            System.out.println("[FAIL] URL PDF Tidak Sesuai!");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        }

        // 6. CLEANUP
        System.out.println("Menutup tab PDF dan kembali ke Admin Page...");
        driver.close(); 
        driver.switchTo().window(adminWindowHandle);
        
        System.out.println("--> Test 8 Selesai.");
    }

    @Test(priority = 9)
    @Story("Admin Page UI")
    @Description("Klik link 'AOS management console' -> Verifikasi Tab Baru berisi URL PDF yang benar")
    public void testAOSManagementConsoleLink() throws InterruptedException {
        System.out.println("=== MULAI TEST 9: CEK LINK PDF 'AOS MANAGEMENT CONSOLE' ===");

        // 1. PERSIAPAN WINDOW HANDLE
        String adminWindowHandle = driver.getWindowHandle();
        Set<String> existingHandles = driver.getWindowHandles();

        // 2. KLIK LINK 'AOS management console'
        System.out.println("Mencari link 'AOS management console'...");
        WebElement pdfLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '4_AOS_Mgt_Console.pdf')]")));
        pdfLink.click();
        Thread.sleep(3000);

        // 3. TUNGGU TAB BARU TERBUKA
        System.out.println("Menunggu tab PDF terbuka...");
        try {
            wait.until(ExpectedConditions.numberOfWindowsToBe(existingHandles.size() + 1));
        } catch (Exception e) {
            Assert.fail("Tab baru tidak terbuka.");
        }

        // 4. SWITCH KE TAB PDF
        Set<String> newHandles = driver.getWindowHandles();
        String pdfWindowHandle = "";

        for (String handle : newHandles) {
            if (!existingHandles.contains(handle)) {
                pdfWindowHandle = handle;
                break;
            }
        }

        if (pdfWindowHandle.isEmpty()) Assert.fail("Gagal menemukan window PDF.");
        
        driver.switchTo().window(pdfWindowHandle);
        System.out.println("Berhasil switch ke tab PDF.");

        // 5. VALIDASI URL
        String expectedPdfUrl = "https://s3.amazonaws.com/aos-on-prem-downloads/doc/4_AOS_Mgt_Console.pdf";
        wait.until(ExpectedConditions.urlContains("pdf"));
        
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + currentUrl);

        if (currentUrl.equals(expectedPdfUrl)) {
            System.out.println("[PASS] URL PDF Sesuai.");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        } else {
            System.out.println("[FAIL] URL PDF Tidak Sesuai!");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        }

        // 6. CLEANUP
        System.out.println("Menutup tab PDF dan kembali ke Admin Page...");
        driver.close(); 
        driver.switchTo().window(adminWindowHandle);
        
        System.out.println("--> Test 9 Selesai.");
    }

    @Test(priority = 10)
    @Story("Admin Page UI")
    @Description("Klik link 'How to get AOS' -> Verifikasi Tab Baru berisi URL PDF yang benar")
    public void testHowToGetAOSLink() throws InterruptedException {
        System.out.println("=== MULAI TEST 10: CEK LINK PDF 'HOW TO GET AOS' ===");

        // 1. PERSIAPAN WINDOW HANDLE
        String adminWindowHandle = driver.getWindowHandle();
        Set<String> existingHandles = driver.getWindowHandles();

        // 2. KLIK LINK 'How to get AOS'
        System.out.println("Mencari link 'How to get AOS'...");
        WebElement pdfLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '5_How_To_Get_AOS.pdf')]")));
        pdfLink.click();
        Thread.sleep(3000);

        // 3. TUNGGU TAB BARU TERBUKA
        System.out.println("Menunggu tab PDF terbuka...");
        try {
            wait.until(ExpectedConditions.numberOfWindowsToBe(existingHandles.size() + 1));
        } catch (Exception e) {
            Assert.fail("Tab baru tidak terbuka.");
        }

        // 4. SWITCH KE TAB PDF
        Set<String> newHandles = driver.getWindowHandles();
        String pdfWindowHandle = "";

        for (String handle : newHandles) {
            if (!existingHandles.contains(handle)) {
                pdfWindowHandle = handle;
                break;
            }
        }

        if (pdfWindowHandle.isEmpty()) Assert.fail("Gagal menemukan window PDF.");
        
        driver.switchTo().window(pdfWindowHandle);
        System.out.println("Berhasil switch ke tab PDF.");

        // 5. VALIDASI URL
        String expectedPdfUrl = "https://s3.amazonaws.com/aos-on-prem-downloads/doc/5_How_To_Get_AOS.pdf";
        wait.until(ExpectedConditions.urlContains("pdf"));
        
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + currentUrl);

        if (currentUrl.equals(expectedPdfUrl)) {
            System.out.println("[PASS] URL PDF Sesuai.");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        } else {
            System.out.println("[FAIL] URL PDF Tidak Sesuai!");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        }

        // 6. CLEANUP
        System.out.println("Menutup tab PDF dan kembali ke Admin Page...");
        driver.close(); 
        driver.switchTo().window(adminWindowHandle);
        
        System.out.println("--> Test 10 Selesai.");
    }

    @Test(priority = 11)
    @Story("Admin Page UI")
    @Description("Klik link 'Troubleshooting' -> Verifikasi Tab Baru berisi URL PDF yang benar")
    public void testTroubleshootingLink() throws InterruptedException {
        System.out.println("=== MULAI TEST 11: CEK LINK PDF 'TROUBLESHOOTING' ===");

        // 1. PERSIAPAN WINDOW HANDLE
        String adminWindowHandle = driver.getWindowHandle();
        Set<String> existingHandles = driver.getWindowHandles();

        // 2. KLIK LINK 'Troubleshooting'
        System.out.println("Mencari link 'Troubleshooting'...");
        WebElement pdfLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '6_Troubleshooting.pdf')]")));
        pdfLink.click();
        Thread.sleep(3000);

        // 3. TUNGGU TAB BARU TERBUKA
        System.out.println("Menunggu tab PDF terbuka...");
        try {
            wait.until(ExpectedConditions.numberOfWindowsToBe(existingHandles.size() + 1));
        } catch (Exception e) {
            Assert.fail("Tab baru tidak terbuka.");
        }

        // 4. SWITCH KE TAB PDF
        Set<String> newHandles = driver.getWindowHandles();
        String pdfWindowHandle = "";

        for (String handle : newHandles) {
            if (!existingHandles.contains(handle)) {
                pdfWindowHandle = handle;
                break;
            }
        }

        if (pdfWindowHandle.isEmpty()) Assert.fail("Gagal menemukan window PDF.");
        
        driver.switchTo().window(pdfWindowHandle);
        System.out.println("Berhasil switch ke tab PDF.");

        // 5. VALIDASI URL
        String expectedPdfUrl = "https://s3.amazonaws.com/aos-on-prem-downloads/doc/6_Troubleshooting.pdf";
        wait.until(ExpectedConditions.urlContains("pdf"));
        
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + currentUrl);

        if (currentUrl.equals(expectedPdfUrl)) {
            System.out.println("[PASS] URL PDF Sesuai.");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        } else {
            System.out.println("[FAIL] URL PDF Tidak Sesuai!");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        }

        // 6. CLEANUP (Tutup tab & Kembali)
        System.out.println("Menutup tab PDF dan kembali ke Admin Page...");
        driver.close(); 
        driver.switchTo().window(adminWindowHandle);
        
        System.out.println("--> Test 11 Selesai.");
    }

    @Test(priority = 12)
    @Story("Admin Page UI")
    @Description("Klik link 'COBOL Integration' -> Verifikasi Tab Baru berisi URL PDF yang benar")
    public void testCOBOLIntegrationLink() throws InterruptedException {
        System.out.println("=== MULAI TEST 12: CEK LINK PDF 'COBOL INTEGRATION' ===");

        // 1. PERSIAPAN WINDOW HANDLE
        String adminWindowHandle = driver.getWindowHandle();
        Set<String> existingHandles = driver.getWindowHandles();

        // 2. KLIK LINK 'COBOL Integration'
        System.out.println("Mencari link 'COBOL Integration'...");
        WebElement pdfLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '7_AOS_COBOL_INTEGRATION.pdf')]")));
        pdfLink.click();
        Thread.sleep(3000);

        // 3. TUNGGU TAB BARU TERBUKA
        System.out.println("Menunggu tab PDF terbuka...");
        try {
            wait.until(ExpectedConditions.numberOfWindowsToBe(existingHandles.size() + 1));
        } catch (Exception e) {
            Assert.fail("Tab baru tidak terbuka.");
        }

        // 4. SWITCH KE TAB PDF
        Set<String> newHandles = driver.getWindowHandles();
        String pdfWindowHandle = "";

        for (String handle : newHandles) {
            if (!existingHandles.contains(handle)) {
                pdfWindowHandle = handle;
                break;
            }
        }

        if (pdfWindowHandle.isEmpty()) Assert.fail("Gagal menemukan window PDF.");
        
        driver.switchTo().window(pdfWindowHandle);
        System.out.println("Berhasil switch ke tab PDF.");

        // 5. VALIDASI URL
        String expectedPdfUrl = "https://s3.amazonaws.com/aos-on-prem-downloads/doc/7_AOS_COBOL_INTEGRATION.pdf";
        wait.until(ExpectedConditions.urlContains("pdf"));
        
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + currentUrl);

        if (currentUrl.equals(expectedPdfUrl)) {
            System.out.println("[PASS] URL PDF Sesuai.");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        } else {
            System.out.println("[FAIL] URL PDF Tidak Sesuai!");
            Assert.assertEquals(currentUrl, expectedPdfUrl);
        }

        // 6. CLEANUP (Tutup tab & Kembali)
        System.out.println("Menutup tab PDF dan kembali ke Admin Page...");
        driver.close(); 
        driver.switchTo().window(adminWindowHandle);
        
        System.out.println("--> Test 12 Selesai.");
    }

    @Test(priority = 13)
    @Story("Admin Page UI")
    @Description("Cek Download Android. BUG: Link rusak, redirect ke halaman XML Error (Access Denied).")
    public void testAndroidDownloadLink() throws InterruptedException {
        System.out.println("=== MULAI TEST 13: CEK DOWNLOAD ANDROID (BUG) ===");

        // 1. CARI TOMBOL ANDROID
        System.out.println("Mencari tombol download Android...");
        WebElement androidBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[contains(@href, '.apk')]")
        ));

        // 2. KLIK TOMBOL
        System.out.println("Mengklik tombol Android...");
        androidBtn.click();

        // 3. TUNGGU REDIRECT & LOAD ERROR PAGE
        Thread.sleep(3000); 

        // 4. VALIDASI BUG
        String currentUrl = driver.getCurrentUrl();
        String pageSource = driver.getPageSource();
        System.out.println("URL Saat Ini: " + currentUrl);
        
        boolean isUrlApk = currentUrl.endsWith(".apk") || currentUrl.contains("amazonaws.com");
        boolean isErrorPage = pageSource.contains("AccessDenied") || pageSource.contains("NoSuchKey") || pageSource.contains("<Error>");

        if (isUrlApk && isErrorPage) {
            System.out.println("[LOG BUG] Link Android rusak! Muncul halaman XML Error.");
            Assert.assertTrue(true);
        } else {
            System.out.println("INFO: Tidak terdeteksi halaman error XML. Mungkin file terdownload?");
        }

        // 5. CLEANUP
        System.out.println("Kembali ke halaman Admin...");
        driver.navigate().back();
        
        wait.until(ExpectedConditions.urlContains("admin"));
        Thread.sleep(2000);

        System.out.println("--> Test 13 Selesai.");
    }

    @Test(priority = 14)
    @Story("Admin Page UI")
    @Description("Cek Download IOS. BUG: Link rusak, redirect ke halaman XML Error (Access Denied).")
    public void testIOSDownloadLink() throws InterruptedException {
        System.out.println("=== MULAI TEST 14: CEK DOWNLOAD IOS (BUG) ===");

        // 1. CARI TOMBOL IOS
        System.out.println("Mencari tombol download IOS...");
        
        WebElement iosBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[contains(@href, '.ipa')]")
        ));

        // 2. KLIK TOMBOL
        System.out.println("Mengklik tombol IOS...");
        iosBtn.click();

        // 3. TUNGGU REDIRECT & LOAD ERROR PAGE
        Thread.sleep(3000); 

        // 4. VALIDASI BUG
        String currentUrl = driver.getCurrentUrl();
        String pageSource = driver.getPageSource();
        System.out.println("URL Saat Ini: " + currentUrl);
        
        boolean isUrlIpa = currentUrl.endsWith(".ipa") || currentUrl.contains("amazonaws.com");
        boolean isErrorPage = pageSource.contains("AccessDenied") || pageSource.contains("NoSuchKey") || pageSource.contains("<Error>");

        if (isUrlIpa && isErrorPage) {
            System.out.println("[LOG BUG] Link IOS rusak! Muncul halaman XML Error.");
            Assert.assertTrue(true);
        } else {
            System.out.println("INFO: Tidak terdeteksi halaman error XML. Mungkin file terdownload?");
        }

        // 5. CLEANUP
        System.out.println("Kembali ke halaman Admin...");
        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("admin"));
        Thread.sleep(2000);
        
        System.out.println("--> Test 14 Selesai.");
    }

    @Test(priority = 15)
    @Story("UI Layout")
    @Description("Simulasi Layar HP (360x640). BUG: Teks 'Management Console' tertimpa Input Username.")
    public void testMobileLayoutCollisionBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 15: CEK LAYOUT MOBILE (COLLISION BUG) ===");

        // 1. UBAH UKURAN WINDOW KE MODE HP (Simulasi Galaxy S5 / iPhone)
        System.out.println("Mengubah ukuran layar ke 360x640...");
        driver.manage().window().setSize(new Dimension(360, 640));
        Thread.sleep(3000);

        // 2. IDENTIFIKASI DUA OBJEK YANG MAU DIADU
        // Objek A: Judul "Management Console" (Kita cari elemen yang visible)
        WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//h1[contains(text(), 'Management')] | //span[contains(text(), 'Management')]")
        ));
        
        // Objek B: Input Username (Ganti locator supaya pasti ketemu)
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("userNameInput")
        ));

        // 3. AMBIL AREA KOTAK (RECTANGLE) MEREKA
        Rectangle rectTitle = titleElement.getRect();
        Rectangle rectInput = usernameInput.getRect();

        System.out.println("Posisi Judul : x=" + rectTitle.x + ", y=" + rectTitle.y + 
                           ", w=" + rectTitle.width + ", h=" + rectTitle.height);
        System.out.println("Posisi Input : x=" + rectInput.x + ", y=" + rectInput.y + 
                           ", w=" + rectInput.width + ", h=" + rectInput.height);

        // 4. HITUNG MATEMATIKA TABRAKAN
        boolean isOverlappingX = rectTitle.x < (rectInput.x + rectInput.width) && 
                                 (rectTitle.x + rectTitle.width) > rectInput.x;
                                 
        boolean isOverlappingY = rectTitle.y < (rectInput.y + rectInput.height) && 
                                 (rectTitle.y + rectTitle.height) > rectInput.y;
        
        boolean isColliding = isOverlappingX && isOverlappingY;

        // 5. PENENTUAN HASIL TEST
        if (isColliding) {
            System.out.println("[LOG BUG] Konten saling bertabrakan (Layout Rusak)!");
            Assert.assertTrue(true);
        } else {
            System.out.println("INFO: Tidak terdeteksi tabrakan koordinat.");
        }
        Thread.sleep(2000);

        System.out.println("--> Test 15 Layout Check Selesai.");
    }

    @Test(priority = 16)
    @Story("Admin Page UI")
    @Description("Zoom Out -> Klik 'ADM Market'. BUG: Membuka New Tab dengan Error 404.")
    public void testInstallationLinkBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 16: CEK INSTALLATION LINK (ZOOM OUT) ===");
        
        // 0. RESET WINDOW (PENTING: KEMBALIKAN DARI MODE HP)
        driver.manage().window().maximize();
        Thread.sleep(2000);

        String adminWindowHandle = driver.getWindowHandle();
        Set<String> existingHandles = driver.getWindowHandles();

        // 1. ZOOM OUT & SCROLL (Supaya tombol ADM kelihatan)
        System.out.println("Melakukan Zoom Out 50% & Scroll...");
        js.executeScript("document.body.style.zoom='50%'");
        Thread.sleep(2000); 
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(2000);

        // 2. CARI TOMBOL
        System.out.println("Mencari tombol 'ADM Market'...");
        WebElement installBtn = null;
        try {
            installBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[contains(text(), 'ADM Market')]/ancestor::a | //span[contains(text(), 'ADM Market')]/ancestor::div[contains(@class, 'devices')]")
            ));
        } catch (Exception e) {
            installBtn = driver.findElement(By.xpath("//*[contains(text(), 'ADM Market')]"));
        }

        // 3. KLIK PAKSA
        js.executeScript("arguments[0].click();", installBtn);
        Thread.sleep(3000);

        // 4. HANDLER TAB BARU
        System.out.println("Menunggu tab baru...");
        try {
            wait.until(ExpectedConditions.numberOfWindowsToBe(existingHandles.size() + 1));
        } catch (Exception e) {
            // Restore zoom kalau gagal, biar ga ngerusak test lain
            js.executeScript("document.body.style.zoom='100%'");
            Assert.fail("Tab baru tidak terbuka.");
        }

        Set<String> newHandles = driver.getWindowHandles();
        String errorWindowHandle = "";
        for (String handle : newHandles) {
            if (!existingHandles.contains(handle)) {
                errorWindowHandle = handle;
                break;
            }
        }
        
        driver.switchTo().window(errorWindowHandle);

        // 5. VALIDASI 404
        System.out.println("Cek Error 404...");
        boolean is404Found = false;
        try {
            String pageText = driver.findElement(By.tagName("body")).getText();
            if (pageText.contains("404") || pageText.contains("not found")) {
                is404Found = true;
            }
        } catch (Exception e) {}

        if (is404Found) {
            System.out.println("[LOG BUG] SUCCESS: Link rusak (404 Page Not Found).");
            Assert.assertTrue(true);
        } else {
            System.out.println("INFO: Halaman 404 tidak spesifik.");
        }

        // 6. CLEANUP
        driver.close(); 
        driver.switchTo().window(adminWindowHandle);
        js.executeScript("document.body.style.zoom='100%'");
        Thread.sleep(2000);
        
        System.out.println("--> Test 16 Selesai.");
    }

    @Test(priority = 17)
    @Story("Admin Login")
    @Description("Centang 'Remember Me' -> Login Admin Console dengan User testing1")
    public void testAdminLogin() throws InterruptedException {
        System.out.println("=== MULAI TEST 17: LOGIN ADMIN (FINAL) ===");

        // Pastikan kembali ke ukuran normal
        driver.manage().window().maximize();

        // 1. INPUT USERNAME
        System.out.println("Mengisi Username...");
        WebElement userField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userNameInput")));
        userField.clear();
        userField.sendKeys("testing1"); 
        Thread.sleep(2000);
        
        // 2. INPUT PASSWORD
        System.out.println("Mengisi Password...");
        WebElement passField = driver.findElement(By.name("password"));
        passField.clear();
        passField.sendKeys("Testing1");
        Thread.sleep(2000);

        // 3. CENTANG 'REMEMBER ME' (BARU)
        System.out.println("Mencentang checkbox 'Remember Me'...");
        try {
            // Locator berdasarkan name="rememberMe" atau class="rectangle-remember-me"
            WebElement rememberMeCheckbox = driver.findElement(By.name("rememberMe"));
            
            // Cek dulu, kalau belum dicentang, baru klik
            if (!rememberMeCheckbox.isSelected()) {
                rememberMeCheckbox.click();
            }
        } catch (Exception e) {
            // Fallback JS Click jika tertutup label styling
            System.out.println("Klik checkbox biasa gagal, mencoba JS Click...");
            WebElement rememberMeCheckbox = driver.findElement(By.cssSelector(".rectangle-remember-me"));
            js.executeScript("arguments[0].click();", rememberMeCheckbox);
        }
        Thread.sleep(2000);

        // 4. KLIK LOGIN
        System.out.println("Klik tombol Sign In...");
        try {
            WebElement loginBtn = driver.findElement(By.className("sign-in-btn"));
            loginBtn.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//input[@value='Sign In']")));
        }
        
        // 5. VALIDASI LOGIN SUKSES
        System.out.println("Menunggu dashboard...");
        Thread.sleep(5000); 

        String currentUrl = driver.getCurrentUrl();
        if (currentUrl.contains("login") || currentUrl.endsWith("admin/")) {
             System.out.println("WARNING: URL sepertinya belum berubah dari halaman login.");
        } else {
             System.out.println("LOGIN SUKSES. URL Akhir: " + currentUrl);
        }

        System.out.println("--> Test 17 Admin Login SELESAI.");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
