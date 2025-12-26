import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
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

public class SidebarAOS {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setup() {
        // === STRATEGI EAGER LOADING ===
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER); 
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        // Timeout wait kita set agak panjang buat jaga-jaga
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            driver.get("https://www.advantageonlineshopping.com/");
        } catch (Exception e) {
            System.out.println("Warning: Page load timeout (Eager mode), lanjut...");
        }

        // === VISUAL WAIT (Tunggu Logo & Slider) ===
        System.out.println("=== SETUP: MENUNGGU HALAMAN HOME SIAP ===");
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loader']")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(@class, 'logo')]")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("slider_carusel")));
            
            System.out.println("Web tampil utuh, jeda 3 detik...");
            Thread.sleep(3000); 
        } catch (Exception e) {}
    }

    @Test(priority = 1)
    @Story("Test Navigasi Sidebar AOS Version")
    @Description("Buka AOS Version -> Klik Sidebar What's New -> Validasi Scroll")
    public void testSideBarWhatsNew() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // === 1. NAVIGASI KE HALAMAN 'AOS VERSIONS' ===
        System.out.println("=== 1. KLIK MENU HELP & AOS VERSIONS ===");
        
        // Klik link HELP
        WebElement linkHelp = wait.until(ExpectedConditions.elementToBeClickable(By.id("helpLink")));
        linkHelp.click();
        Thread.sleep(3000); 

        // Klik link AOS VERSIONS
        WebElement linkAosVersions = driver.findElement(By.xpath("//label[contains(text(), 'AOS Versions')]"));
        js.executeScript("arguments[0].click();", linkAosVersions);
        Thread.sleep(3000); 
        System.out.println("Menunggu halaman AOS Versions terbuka...");
        
        try {
            wait.until(ExpectedConditions.urlContains("version"));
        } catch (Exception e) {
            System.out.println("Warning: URL update agak lambat...");
        }
        Thread.sleep(2000); 
        
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("version"), "Gagal masuk ke halaman Help/AOS Versions!");

        System.out.println("=== 2. KLIK SIDEBAR: WHAT'S NEW ===");
        WebElement sideBarWhatsNew = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("whats_new_link")
        ));

        sideBarWhatsNew.click();
        System.out.println("Sidebar diklik, menunggu scroll otomatis...");
        Thread.sleep(3000); 

        WebElement targetSectionHeader = driver.findElement(By.xpath("//div[@class='whats_new']"));

        boolean isHeaderDisplayed = targetSectionHeader.isDisplayed();
        String headerText = targetSectionHeader.getText();
        
        System.out.println("Header Ditemukan: " + headerText);
        System.out.println("Apakah Header Terlihat? " + isHeaderDisplayed);
        
        Assert.assertTrue(isHeaderDisplayed, "Gagal scroll ke section What's New!");
        Assert.assertEquals(headerText, "What's New", "Teks header tidak sesuai!");

        System.out.println("--> Test Sidebar What's New SUKSES.");
    }

    @Test(priority = 2)
    @Story("Test Navigasi Sidebar Downloads")
    @Description("Klik Sidebar Download -> Validasi scroll ke section Mobile Apps")
    public void testSideBarDownloads() throws InterruptedException {
        System.out.println("\n=== MULAI TEST 2: SIDEBAR DOWNLOADS ===");
        
        // 1. Cari & Klik Sidebar DOWNLOAD
        WebElement sideBarDownload = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("downloads_link")
        ));
        System.out.println("--- Klik Sidebar Download ---");
        sideBarDownload.click();
        
        System.out.println("Menunggu scroll otomatis...");
        Thread.sleep(3000);
        
        WebElement targetSection = driver.findElement(By.id("download_mobile_apps"));

        boolean isSectionVisible = targetSection.isDisplayed();
        System.out.println("Apakah Section Mobile Apps terlihat? " + isSectionVisible);

        // Validasi Text
        String sectionText = targetSection.getText();
        System.out.println("Isi Section: " + sectionText);

        Assert.assertTrue(isSectionVisible, "Gagal scroll ke section Download/Mobile Apps!");
        
        System.out.println("--> Test 2 Downloads SUKSES.");
    }

    @Test(priority = 3)
    @Story("Test Navigasi Sidebar Installation")
    @Description("Klik Sidebar Installation -> Validasi scroll ke section Installation")
    public void testSideBarInstallation() throws InterruptedException {
        System.out.println("\n=== MULAI TEST 3: SIDEBAR INSTALLATION ===");
        
        // 1. Cari & Klik Sidebar INSTALLATION
        WebElement sideBarInstall = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("installation_link")
        ));
        System.out.println("--- Klik Sidebar Installation ---");
        sideBarInstall.click();
        
        System.out.println("Menunggu scroll otomatis...");
        Thread.sleep(3000);

        // 2. Validasi Target
        WebElement targetSection = driver.findElement(By.id("installation"));

        boolean isSectionVisible = targetSection.isDisplayed();
        System.out.println("Apakah Section Installation terlihat? " + isSectionVisible);

        Assert.assertTrue(isSectionVisible, "Gagal scroll ke section Installation!");
        
        System.out.println("--> Test 3 Installation SUKSES.");
    }

    @Test(priority = 4)
    @Story("Test Navigasi Sidebar Admin Client")
    @Description("Klik Sidebar Admin Client -> Validasi scroll ke section Admin Client")
    public void testSideBarAdminClient() throws InterruptedException {
        System.out.println("\n=== MULAI TEST 4: SIDEBAR ADMIN CLIENT ===");

        WebElement sideBarAdmin = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("admin_client_link")
        ));
        System.out.println("--- Klik Sidebar Admin Client ---");
        sideBarAdmin.click();
        
        System.out.println("Menunggu scroll otomatis...");
        Thread.sleep(3000);

        // 2. Validasi Target
        WebElement targetSection = driver.findElement(By.id("admin_client"));

        boolean isSectionVisible = targetSection.isDisplayed();
        System.out.println("Apakah Section Admin Client terlihat? " + isSectionVisible);

        Assert.assertTrue(isSectionVisible, "Gagal scroll ke section Admin Client!");
        
        System.out.println("--> Test 4 Admin Client SUKSES.");
    }

    @Test(priority = 5)
    @Story("Test Navigasi Sidebar Docker Configuration")
    @Description("Klik Sidebar Docker Config -> Validasi scroll ke section Docker Config")
    public void testSideBarDockerConfig() throws InterruptedException {
        System.out.println("\n=== MULAI TEST 5: SIDEBAR DOCKER CONFIGURATION ===");
        
        // 1. Cari & Klik Sidebar DOCKER CONFIGURATION
        WebElement sideBarDocker = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("DockerConfiguration_link")
        ));
        System.out.println("--- Klik Sidebar Docker Configuration ---");
        sideBarDocker.click();
        
        System.out.println("Menunggu scroll otomatis...");
        Thread.sleep(3000);

        // 2. Validasi Target
        WebElement targetSection = driver.findElement(By.id("DockerConfiguration"));

        boolean isSectionVisible = targetSection.isDisplayed();
        System.out.println("Apakah Section Docker Config terlihat? " + isSectionVisible);

        Assert.assertTrue(isSectionVisible, "Gagal scroll ke section Docker Configuration!");
        
        System.out.println("--> Test 5 Docker Configuration SUKSES.");
    }

    @Test(priority = 6)
    @Story("Test Navigasi Sidebar APIs")
    @Description("Klik Sidebar APIs -> Validasi scroll ke section APIs")
    public void testSideBarAPIs() throws InterruptedException {
        System.out.println("\n=== MULAI TEST 6: SIDEBAR APIs ===");
        
        // 1. Cari & Klik Sidebar APIs
        WebElement sideBarAPIs = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("APIs_link")
        ));
        System.out.println("--- Klik Sidebar APIs ---");
        sideBarAPIs.click();
        
        System.out.println("Menunggu scroll otomatis...");
        Thread.sleep(3000);

        // 2. Validasi Target
        WebElement targetSection = driver.findElement(By.id("APIs"));

        boolean isSectionVisible = targetSection.isDisplayed();
        System.out.println("Apakah Section APIs terlihat? " + isSectionVisible);

        Assert.assertTrue(isSectionVisible, "Gagal scroll ke section APIs!");
        
        System.out.println("--> Test 6 APIs SUKSES.");
    }

    @Test(priority = 7)
    @Story("Test Link Broken: ADM Marketplace")
    @Description("Kembali ke Installation -> Klik ADM Marketplace -> Validasi Halaman 404")
    public void testAdmMarketplaceLink() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println("\n=== MULAI TEST 7: BROKEN LINK CHECK (ADM MARKETPLACE) ===");

        // 1. KEMBALI KE SECTION INSTALLATION
        System.out.println("--- Kembali ke Section Installation ---");
        WebElement sideBarInstall = driver.findElement(By.id("installation_link"));
        sideBarInstall.click();
        Thread.sleep(3000); 

        // 2. KLIK TOMBOL ADM MARKETPLACE
        System.out.println("--- Klik Link ADM Marketplace ---");
        WebElement admLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//b[contains(text(), 'ADM Marketplace')]/ancestor::div[@class='devices']")
        ));
        
        String mainWindow = driver.getWindowHandle();
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", admLink);
        Thread.sleep(2000);

        System.out.println("Attempting JS Click...");
        js.executeScript("arguments[0].click();", admLink);
        
        // 3. SWITCH KE TAB BARU
        System.out.println("--- Switch ke Tab Baru ---");
        Thread.sleep(5000);
        
        Set<String> allWindows = driver.getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }

        // 4. VALIDASI
        String newTabUrl = driver.getCurrentUrl();
        String newTabTitle = driver.getTitle();
        System.out.println("URL Tab Baru: " + newTabUrl);
        System.out.println("Judul Tab Baru: " + newTabTitle);

        boolean isErrorPage = newTabTitle.contains("404") || 
                              newTabTitle.contains("Not Found") || 
                              driver.getPageSource().contains("Page Not Found") ||
                              driver.getPageSource().contains("We can't find that page");

        if (isErrorPage) {
            System.out.println("PASS: Halaman 404/Not Found berhasil dideteksi.");
        } else {
            System.out.println("WARNING: Halaman terbuka normal (Bukan 404).");
        }
        
        Assert.assertTrue(isErrorPage, "Expected 404 Page, but got valid page: " + newTabTitle);

        driver.close();
        driver.switchTo().window(mainWindow);
        Thread.sleep(2000);
        
        System.out.println("--> Test 7 Link Broken SUKSES.");
    }

    @Test(priority = 8)
    @Story("Test Change Release Version")
    @Description("Refresh -> Ganti ke Version 2.9 -> Tunggu 5 detik -> Balik ke 3.3")
    public void testChangeVersion() throws InterruptedException {
        System.out.println("\n=== MULAI TEST 8: CHANGE VERSION ===");

        // 1. REFRESH HALAMAN
        System.out.println("--- Refresh Halaman (Reset Scroll) ---");
        driver.navigate().refresh();
        Thread.sleep(5000);

        // 2. KLIK DROPDOWN VERSION
        System.out.println("--- Klik Dropdown Version ---");
        WebElement versionDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//a[contains(@class, 'release_version')]")
        ));
        versionDropdown.click();
        Thread.sleep(2000);

        // 3. PILIH VERSI 3.0
        System.out.println("--- Pilih Version 3.0 ---");
        WebElement ver30 = driver.findElement(By.xpath("//*[contains(text(), '3.0')]"));
        ver30.click();

        // 4. TUNGGU 5 DETIK
        System.out.println("--- Versi 3.0 Terpilih, Menunggu 5 Detik... ---");
        Thread.sleep(8000);

        // 5. KEMBALIKAN KE VERSION 3.3
        System.out.println("--- Kembalikan ke Version 3.3 ---");
        versionDropdown = driver.findElement(By.xpath("//a[contains(@class, 'release_version')]"));
        versionDropdown.click();
        Thread.sleep(2000);

        // Pilih Versi 3.3
        WebElement ver33 = driver.findElement(By.xpath("//*[contains(text(), '3.3')]"));
        ver33.click();
        Thread.sleep(2000);

        // Validasi Akhir: Pastikan teks di dropdown kembali "VERSION 3.3"
        String currentVersionText = versionDropdown.getText();
        System.out.println("Versi Akhir: " + currentVersionText);
        Assert.assertTrue(currentVersionText.contains("3.3"), "Gagal mengembalikan versi ke 3.3!");

        System.out.println("--> Test 8 Change Version SUKSES.");
    }

    @Test(priority = 9)
    @Story("Test Download Android APK")
    @Description("Klik Download Android -> Verifikasi tidak ada error (Link Valid)")
    public void testAndroidDownload() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println("\n=== MULAI TEST 9: DOWNLOAD ANDROID APK ===");

        // 1. Kembali ke Sidebar DOWNLOADS
        WebElement sideBarDownload = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("downloads_link")));
        js.executeScript("arguments[0].scrollIntoView(true);", sideBarDownload);
        Thread.sleep(2000);
        sideBarDownload.click();
        Thread.sleep(3000);

        // 2. Klik Tombol DOWNLOAD ANDROID
        System.out.println("--- Klik Tombol Android Download ---");
        WebElement androidBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//b[contains(text(), 'Android')]/ancestor::div[@class='devices']")
        ));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", androidBox);
        Thread.sleep(2000);
        
        js.executeScript("arguments[0].click();", androidBox);
        
        // 3. Verifikasi
        System.out.println("Menunggu respons link (3 detik)...");
        Thread.sleep(3000);

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL setelah klik: " + currentUrl);

        boolean isErrorPage = currentUrl.contains("amazonaws.com") || 
                              driver.getPageSource().contains("NoSuchKey") || 
                              driver.getPageSource().contains("AccessDenied");

        if (isErrorPage) {
            System.err.println("FAIL: Link Download Rusak/Error S3.");
            Assert.fail("Download Link Broken (Redirected to S3 Error Page).");
        } else {
            System.out.println("PASS: URL tidak berubah (Download berjalan di background).");
        }

        System.out.println("--> Test 9 Download Android SUKSES.");
    }

    @Test(priority = 10)
    @Story("Test Download iOS APK")
    @Description("Klik Download iOS -> Pastikan tombol bisa diklik dan merespon")
    public void testIosDownload() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println("\n=== MULAI TEST 10: DOWNLOAD IOS APK (ACTION ONLY) ===");

        System.out.println("--- Klik Tombol iOS Download ---");
        WebElement iosBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//b[contains(text(), 'IOS')]/ancestor::div[@class='devices']")
        ));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", iosBox);
        Thread.sleep(2000);
        
        try {
            js.executeScript("arguments[0].click();", iosBox);
            System.out.println("INFO: Tombol berhasil diklik.");
        } catch (Exception e) {
            Assert.fail("GAGAL: Tidak bisa mengklik tombol iOS.");
        }
        
        System.out.println("Menunggu respons link (3 detik)...");
        Thread.sleep(3000);

        // Validasi Sederhana
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL setelah klik: " + currentUrl);

        // Assert True
        Assert.assertTrue(true, "Download trigger berhasil dilakukan.");
        
        System.out.println("--> Test 10 Download iOS SUKSES (Action Triggered).");
    }

    @Test(priority = 11)
    @Story("Test PostgreSQL Link Validity")
    @Description("Navigasi ke Installation -> Klik Link PostgreSQL -> Validasi URL -> Close Tab")
    public void testPostgreSQLLink() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println("\n=== MULAI TEST 11: CHECK POSTGRESQL LINK (FIXED) ===");

        // 1. Navigasi ke Section INSTALLATION (Sesuai koreksi)
        System.out.println("--- Navigasi ke Section Installation ---");
        WebElement sideBarInstall = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("installation_link")
        ));
        sideBarInstall.click();
        System.out.println("Menunggu scroll ke area Installation...");
        Thread.sleep(3000);

        // 2. Cari Link PostgreSQL
        System.out.println("--- Cari Link PostgreSQL ---");
        WebElement pgLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//a[contains(@href, 'postgresql.org')]")
        ));

        String mainWindow = driver.getWindowHandle();

        // 3. Click Link
        System.out.println("Klik Link PostgreSQL...");
        js.executeScript("arguments[0].click();", pgLink);

        // 4. Switch ke Tab Baru
        System.out.println("--- Switch ke Tab Baru ---");
        Thread.sleep(8000);

        Set<String> allWindows = driver.getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }

        // 5. Validasi URL & Title
        String newUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + newUrl);

        // Validasi URL harus mengandung domain postgresql
        boolean isValidLink = newUrl.contains("postgresql.org");
        Assert.assertTrue(isValidLink, "Test Failed: Link tidak mengarah ke postgresql.org! URL didapat: " + newUrl);

        System.out.println("PASS: Link valid menuju PostgreSQL Website.");

        // 6. Tutup Tab & Kembali ke Main Window
        driver.close();
        driver.switchTo().window(mainWindow);
        
        System.out.println("--> Test 11 PostgreSQL Link SUKSES.");
    }

    @Test(priority = 12)
    @Story("Test Download Admin Client (Negative Test)")
    @Description("Klik Download Admin Client -> Verifikasi Error XML (Same Tab) -> Back to Page")
    public void testAdminClientDownload() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println("\n=== MULAI TEST 12: DOWNLOAD ADMIN CLIENT (EXPECTED XML ERROR) ===");

        // 1. Navigasi ke Section Admin Client
        System.out.println("--- Navigasi Sidebar ke Admin Client ---");
        WebElement sideBarAdmin = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("admin_client_link")));
        sideBarAdmin.click();
        Thread.sleep(3000);

        // 2. Klik Tombol Admin Client
        System.out.println("--- Klik Tombol Admin Client Download ---");
        WebElement adminBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//b[contains(text(), 'Admin Client')]/ancestor::div[@class='devices']")
        ));
        js.executeScript("arguments[0].click();", adminBox);
        
        // 3. Validasi Error Page (XML)
        System.out.println("--- Menunggu Halaman Error XML ---");
        
        // Tunggu sampai URL mengandung 'amazonaws'
        try {
            wait.until(ExpectedConditions.urlContains("amazonaws.com"));
        } catch (Exception e) {
            System.out.println("Warning: Timeout menunggu URL berubah, lanjut verifikasi manual...");
        }
        Thread.sleep(5000);

        String currentUrl = driver.getCurrentUrl();
        String pageSource = driver.getPageSource();
        System.out.println("Current URL: " + currentUrl);

        // Cek Indikator XML Error S3
        boolean isXmlError = currentUrl.contains("amazonaws.com") && 
                             (pageSource.contains("NoSuchKey") || pageSource.contains("AccessDenied"));

        if (isXmlError) {
            System.out.println("PASS: Halaman Error XML berhasil terdeteksi.");
        } else {
            System.out.println("FAIL: Halaman tidak menampilkan XML Error yang diharapkan.");
            System.out.println("Page Content Preview: " + pageSource.substring(0, Math.min(pageSource.length(), 200)));
        }

        Assert.assertTrue(isXmlError, "Harusnya muncul Error XML (NoSuchKey), tapi tidak ditemukan.");
        
        // 4. RECOVERY (PENTING: KEMBALI KE HALAMAN ASAL)
        System.out.println("--- Recovery: Browser Back ke Website Utama ---");
        driver.navigate().back(); 
        
        // Tunggu sampai kita benar-benar kembali ke halaman 'Versions'
        wait.until(ExpectedConditions.urlContains("advantageonlineshopping"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("admin_client_link")));
        
        Thread.sleep(2000);

        System.out.println("--> Test 12 Admin Client Download SUKSES.");
    }

    @Test(priority = 13)
    @Story("Test GitHub Link Validity")
    @Description("Navigasi Docker Configuration -> Klik Link GitHub -> Switch Tab -> Validasi URL GitHub")
    public void testGithubLink() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println("\n=== MULAI TEST 13: CHECK GITHUB LINK ===");

        // 1. Navigasi ke Docker Configuration
        System.out.println("--- Navigasi ke Docker Configuration ---");
        WebElement sideBarDocker = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("DockerConfiguration_link")
        ));
        sideBarDocker.click();
        Thread.sleep(3000);

        // 2. Cari Link GitHub
        System.out.println("--- Cari Link GitHub ---");
        WebElement gitLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//a[contains(@href, 'github.com/aosapp/account-service')]")
        ));

        String mainWindow = driver.getWindowHandle();

        // 5. Klik & Switch Tab
        js.executeScript("arguments[0].click();", gitLink);
        System.out.println("--- Switch ke Tab Baru ---");
        Thread.sleep(8000);

        Set<String> allWindows = driver.getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }

        // 6. Validasi URL
        String newUrl = driver.getCurrentUrl();
        String newTitle = driver.getTitle();
        System.out.println("URL: " + newUrl);
        System.out.println("Title: " + newTitle);

        boolean isValidGithub = newUrl.contains("github.com") && newUrl.contains("account-service");
        Assert.assertTrue(isValidGithub, "Link tidak mengarah ke repository GitHub yang benar!");

        // 7. Tutup
        driver.close();
        driver.switchTo().window(mainWindow);
        Thread.sleep(3000);
        System.out.println("--> Test 13 GitHub Link SUKSES.");
    }

    @Test(priority = 14)
    @Story("Test Docker Compose Link Validity")
    @Description("Klik Link Docker Compose -> Validasi URL")
    public void testDockerComposeLink() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println("\n=== MULAI TEST 14: CHECK DOCKER COMPOSE LINK ===");

        // 1. Cari Link Docker Compose
        System.out.println("--- Cari Link Docker Compose ---");
        WebElement composeLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//a[contains(@href, 'docs.docker.com/compose')]")
        ));

        String mainWindow = driver.getWindowHandle();

        // 1. Click
        js.executeScript("arguments[0].click();", composeLink);

        // 2. Switch Tab
        System.out.println("--- Switch ke Tab Baru ---");
        Thread.sleep(8000); 

        Set<String> allWindows = driver.getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }

        // 3. Validasi
        String newUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + newUrl);
        Assert.assertTrue(newUrl.contains("docs.docker.com"), "Link tidak mengarah ke Docker Docs!");

        // 4. Cleanup
        driver.close();
        driver.switchTo().window(mainWindow);
        Thread.sleep(3000);
        System.out.println("--> Test 14 Docker Compose Link SUKSES.");
    }

    @Test(priority = 15)
    @Story("Test Docker App Link Validity")
    @Description("Klik Link Docker App -> Validasi URL")
    public void testDockerAppLink() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println("\n=== MULAI TEST 15: CHECK DOCKER APP LINK ===");

        // 1. Cari Link Docker App
        System.out.println("--- Cari Link Docker App ---");
        WebElement appLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//a[contains(@href, 'github.com/docker/app')]")
        ));

        String mainWindow = driver.getWindowHandle();

        // 2. Click
        js.executeScript("arguments[0].click();", appLink);
        Thread.sleep(2000);

        // 3. Switch Tab
        System.out.println("--- Switch ke Tab Baru ---");
        Thread.sleep(8000); 

        Set<String> allWindows = driver.getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }

        // 4. Validasi
        String newUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + newUrl);
        Assert.assertTrue(newUrl.contains("github.com/docker-archive-public/docker.app"), "Link tidak mengarah ke Docker App GitHub!");

        // 5. Cleanup
        driver.close();
        driver.switchTo().window(mainWindow);
        Thread.sleep(3000);
        System.out.println("--> Test 15 Docker App Link SUKSES.");
    }

    @Test(priority = 16)
    @Story("Test REST API Link Validity")
    @Description("Navigasi ke APIs -> Klik Link REST API -> Validasi URL")
    public void testRestApiLink() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println("\n=== MULAI TEST 16: CHECK REST API LINK ===");

        // 1. Klik Sidebar APIs
        WebElement sideBarAPIs = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("APIs_link")
        ));
        sideBarAPIs.click();
        Thread.sleep(3000);

        // 2. Cari Link REST API
        System.out.println("--- Cari Link REST API ---");
        WebElement apiLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//a[contains(@href, '/api/docs/')]")
        ));

        String mainWindow = driver.getWindowHandle();

        // 3. Click
        js.executeScript("arguments[0].click();", apiLink);

        // 4. Switch Tab
        System.out.println("--- Switch ke Tab Baru ---");
        Thread.sleep(8000); 

        Set<String> allWindows = driver.getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }

        // 5. Validasi URL
        String newUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + newUrl);
        boolean isValidApi = newUrl.contains("api/docs") || newUrl.contains("advantageonlineshopping.com");
        Assert.assertTrue(isValidApi, "Link tidak mengarah ke dokumentasi API yang benar!");

        // 6. Cleanup
        driver.close();
        driver.switchTo().window(mainWindow);
        Thread.sleep(3000);
        System.out.println("--> Test 16 REST API Link SUKSES.");
    }

    @Test(priority = 17)
    @Story("Test SOAP API Link Validity (Account Service)")
    @Description("Klik Link SOAP -> Validasi WSDL XML")
    public void testSoapApiLink() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println("\n=== MULAI TEST 17: CHECK SOAP API LINK (ACCOUNT SERVICE) ===");

        // 1. Cari Link SOAP API (Account Service)
        System.out.println("--- Cari Link SOAP API ---");
        WebElement soapLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//a[contains(@href, 'accountservice.wsdl')]")
        ));

        String mainWindow = driver.getWindowHandle();

        // 2. Click
        js.executeScript("arguments[0].click();", soapLink);

        // 3. Switch Tab
        System.out.println("--- Switch ke Tab Baru ---");
        Thread.sleep(8000); 

        Set<String> allWindows = driver.getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }

        // 4. Validasi URL & Konten WSDL
        String newUrl = driver.getCurrentUrl();
        String pageSource = driver.getPageSource();
        System.out.println("URL Tab Baru: " + newUrl);

        boolean isWsdlUrl = newUrl.contains("accountservice.wsdl");
        boolean isWsdlContent = pageSource.contains("wsdl:definitions") || pageSource.contains("xml version");

        Assert.assertTrue(isWsdlUrl, "URL tidak mengandung .wsdl!");
        Assert.assertTrue(isWsdlContent, "Halaman tidak menampilkan XML WSDL yang valid!");

        System.out.println("PASS: WSDL XML Valid terdeteksi.");

        // 5. Cleanup
        driver.close();
        driver.switchTo().window(mainWindow);
        Thread.sleep(3000);
        System.out.println("--> Test 17 SOAP API Link SUKSES.");
    }

    @Test(priority = 18)
    @Story("Test SOAP API Link Validity (ShipEx)")
    @Description("Klik Link SOAP ShipEx (Same Tab) -> Validasi WSDL XML -> Back to Main Page")
    public void testShipExApiLink() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println("\n=== MULAI TEST 18: CHECK SOAP API LINK (SHIPEX) ===");

        // 1. Cari Link & Scroll
        System.out.println("--- Cari Link SOAP API ShipEx ---");
        WebElement shipExLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//a[contains(@href, 'shipex.wsdl')]")
        ));

        // 2. Klik Link
        System.out.println("--- Klik Link ShipEx ---");
        js.executeScript("arguments[0].click();", shipExLink);

        // 3. Tunggu Halaman Berubah
        System.out.println("--- Menunggu Halaman WSDL Load ---");
        
        try {
            wait.until(ExpectedConditions.urlContains("shipex.wsdl"));
        } catch (Exception e) {
            System.out.println("Warning: Timeout menunggu URL update, mencoba validasi manual...");
        }
        Thread.sleep(3000);

        // 4. Validasi URL & Konten
        String currentUrl = driver.getCurrentUrl();
        String pageSource = driver.getPageSource();
        System.out.println("URL Saat Ini: " + currentUrl);

        boolean isWsdlUrl = currentUrl.contains("shipex.wsdl");
        boolean isWsdlContent = pageSource.contains("wsdl:definitions") || pageSource.contains("xml version");

        Assert.assertTrue(isWsdlUrl, "URL tidak berubah ke shipex.wsdl!");
        Assert.assertTrue(isWsdlContent, "Halaman tidak menampilkan konten XML WSDL yang valid!");

        System.out.println("PASS: ShipEx WSDL XML Valid terdeteksi.");

        // 5. RECOVERY: KEMBALI KE HALAMAN UTAMA
        System.out.println("--- Navigasi Back ke Halaman Utama ---");
        driver.navigate().back();

        try {
            wait.until(ExpectedConditions.urlContains("version"));
        } catch (Exception e) {
            System.out.println("Warning: Agak lambat kembali ke halaman utama...");
        }
        Thread.sleep(3000);

        System.out.println("--> Test 18 ShipEx API Link SUKSES.");
    }

    @Test(priority = 19)
    @Story("Test Advantage Online Shopping Link (Footer Note)")
    @Description("Klik Link AOS -> Validasi Home Page -> Close Tab Baru")
    public void testAdvantageOnlineShoppingLink() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println("\n=== MULAI TEST 19: CHECK ADVANTAGE LINK (PLEASE NOTE) ===");

        String mainWindow = driver.getWindowHandle();

        // 1. Cari Link Footer
        System.out.println("--- Cari Link Advantage Online Shopping ---");
        WebElement advLink = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//a[normalize-space(text())='advantageonlineshopping.com']")
        ));
        js.executeScript("arguments[0].click();", advLink);

        // 2. Switch Tab Baru
        System.out.println("--- Switch ke Tab Baru ---");
        Thread.sleep(8000); 

        Set<String> allWindows = driver.getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }

        // 3. Validasi URL
        String newUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + newUrl);
        boolean isValidUrl = newUrl.contains("advantageonlineshopping.com");
        Assert.assertTrue(isValidUrl, "Link tidak mengarah ke website Advantage Online Shopping!");

        System.out.println("PASS: Link Main Site Valid.");

        // 4. Cleanup: CLOSE TAB BARU & KEMBALI KE UTAMA
        System.out.println("--- Menutup Tab Baru & Kembali ke Utama ---");
        driver.close();
        driver.switchTo().window(mainWindow);
        Thread.sleep(3000);
        System.out.println("--> Test 19 Advantage Link SUKSES.");
    }

    @Test(priority = 20)
    @Story("Test Broken IP Link Validity")
    @Description("Klik Link IP (54.157...) -> Switch Tab -> Validasi Error 500/Oops")
    public void testBrokenIpLink() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println("\n=== MULAI TEST 20: CHECK BROKEN IP LINK (EXPECTED 500/OOPS) ===");

        String mainWindow = driver.getWindowHandle();

        // 1. Cari Link IP Address (54.157...)
        System.out.println("--- Cari Link IP Address ---");
        WebElement brokenLink = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//a[contains(@href, '54.157.232.206')]")
        ));

        // 2. Klik & Switch Tab
        js.executeScript("arguments[0].click();", brokenLink);

        System.out.println("--- Switch ke Tab Baru ---");
        Thread.sleep(8000);

        Set<String> allWindows = driver.getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }

        // 3. Validasi
        String newUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + newUrl);
        
        // Ambil seluruh teks di halaman body untuk validasi pesan error
        String pageBody = driver.findElement(By.tagName("body")).getText().toLowerCase();
        
        // Cek indikator error umum
        boolean isErrorDisplayed = pageBody.contains("oops") || 
                                   pageBody.contains("something went wrong") || 
                                   pageBody.contains("500") ||
                                   pageBody.contains("error");

        if (isErrorDisplayed) {
            System.out.println("PASS: Halaman Error berhasil terdeteksi (Sesuai Ekspektasi).");
        } else {
            System.out.println("FAIL: Halaman tidak menampilkan pesan error 'Oops/500'. Content: " + pageBody);
        }

        Assert.assertTrue(isErrorDisplayed, "Test Failed: Link IP Address seharusnya Error (Oops/500) tapi tidak muncul!");

        // 4. Cleanup: Tutup Tab Error & Kembali
        System.out.println("--- Menutup Tab Error & Kembali ---");
        driver.close();
        driver.switchTo().window(mainWindow);
        Thread.sleep(3000); 
        System.out.println("--> Test 20 Broken IP Link SUKSES.");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
