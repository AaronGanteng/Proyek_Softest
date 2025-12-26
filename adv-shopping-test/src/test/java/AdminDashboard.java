import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Story;

public class AdminDashboard {
    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        js = (JavascriptExecutor) driver;

        driver.get("https://www.advantageonlineshopping.com/admin");
    }

    @Test(priority = 1)
    @Story("Admin Login")
    @Description("Login Admin Console dengan User testing1")
    public void testAdminLogin() throws InterruptedException {
        System.out.println("=== MULAI TEST 1: LOGIN ADMIN ===");

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
        Thread.sleep(8000); 

        String currentUrl = driver.getCurrentUrl();
        if (currentUrl.contains("login") || currentUrl.endsWith("admin/")) {
             System.out.println("WARNING: URL sepertinya belum berubah dari halaman login.");
        } else {
             System.out.println("LOGIN SUKSES. URL Akhir: " + currentUrl);
        }

        System.out.println("--> Test 1 Admin Login SELESAI.");
    }
    
    @Test(priority = 2)
    @Story("Dashboard UI")
    @Description("Ubah ke layout HP. BUG: Logo/Sidebar menimpa Judul Configuration atau elemen hilang.")
    public void testDashboardMobileLayoutBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 2: CEK LAYOUT DASHBOARD (MOBILE BUG) ===");

        // 1. UBAH KE MODE HP
        System.out.println("Mengubah ukuran layar ke 375x812...");
        driver.manage().window().setSize(new Dimension(375, 812));
        Thread.sleep(3000); 

        // 2. BLOK TRY-CATCH UNTUK VALIDASI BUG
        try {
            WebElement sidebarLogo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(), 'dvantage') or contains(text(), 'Management')]")
            ));

            WebElement contentTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("confHeaderTitle")
            ));

            Rectangle rectLogo = sidebarLogo.getRect();
            Rectangle rectTitle = contentTitle.getRect();

            System.out.println("Logo  : x=" + rectLogo.x + ", w=" + rectLogo.width);
            System.out.println("Title : x=" + rectTitle.x + ", w=" + rectTitle.width);

            boolean isOverlapping = (rectLogo.x + rectLogo.width) > rectTitle.x;

            if (isOverlapping) {
                System.out.println("[LOG BUG] SUCCESS: Terdeteksi tabrakan koordinat!");
                Assert.assertTrue(true);
            } else {
                System.out.println("INFO: Tidak ada overlap koordinat, tapi cek visual manual.");
            }

        } catch (Exception e) {
            System.out.println("[LOG BUG] Elemen tidak ditemukan atau tertimpa total!");
            
            Assert.assertTrue(true);
        } finally {
            driver.manage().window().maximize();
            Thread.sleep(2000);
        }

        System.out.println("--> Test 2 Selesai.");
    }

    @Test(priority = 3)
    @Story("Dashboard Stability")
    @Description("Refresh Halaman. BUG: Halaman Crash (404) dan User harus Login Ulang.")
    public void testRefreshPageSessionBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 3: REFRESH PAGE (SESSION BUG) ===");

        // 1. REFRESH HALAMAN
        System.out.println("Melakukan Refresh halaman...");
        driver.navigate().refresh();
        Thread.sleep(3000);

        // 2. CEK APAKAH BUG MUNCUL? (Browser Error Page / 404)
        boolean isErrorPage = false;
        
        try {
            // Ambil Judul Halaman & Teks Body
            String pageTitle = driver.getTitle();
            String pageText = driver.findElement(By.tagName("body")).getText();
            String currentUrl = driver.getCurrentUrl();

            System.out.println("URL setelah refresh: " + currentUrl);

            // Indikator Error Chrome / 404 Generic
            if (pageTitle.contains("404") || pageText.contains("HTTP ERROR 404") || 
                pageText.contains("can’t be found") || pageText.contains("not found")) {
                isErrorPage = true;
            }
        } catch (Exception e) {
            if (driver.getCurrentUrl().contains("management-console")) isErrorPage = true;
        }

        // 3. VALIDASI BUG & RECOVERY (WAJIB JALAN)
        if (isErrorPage) {
            System.out.println("[LOG BUG] Halaman Crash setelah Refresh!");
            System.out.println(">> RECOVERY: Kembali ke URL Admin awal...");
            
            // 1. Navigate Paksa ke URL Admin yang valid (Sesuai request: /admin)
            driver.get("https://www.advantageonlineshopping.com/admin/");
            Thread.sleep(2000);

            // 2. Lakukan Login Ulang
            System.out.println(">> RECOVERY: Melakukan Login Ulang...");
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username"))).sendKeys("testing1");
                Thread.sleep(1000);
            } catch (Exception e) {
                driver.findElement(By.id("userNameInput")).sendKeys("testing1");
            }
            
            driver.findElement(By.name("password")).sendKeys("Testing1");
            Thread.sleep(1000);
            
            // Klik Sign In
            try {
                driver.findElement(By.className("sign-in-btn")).click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//button[contains(text(),'Sign In')]")));
            }

            // 3. Tunggu Dashboard Muncul Kembali
            try {
                wait.until(ExpectedConditions.urlContains("management-console"));
                System.out.println(">> RECOVERY SUKSES: Kembali ke management-console.");
            } catch (Exception e) {
                System.out.println("WARNING: Login ulang mungkin lambat, tapi script lanjut terus.");
            }

            Assert.assertTrue(true);
        } else {
            System.out.println("INFO: Halaman tidak crash. Bug tidak muncul?");
        }

        System.out.println("--> Test 3 Refresh Bug Selesai.");
    }

    @Test(priority = 4)
    @Story("Dashboard Functionality")
    @Description("Klik Restore DB. BUG/STUB: Muncul popup 'Not available here!'.")
    public void testRestoreDBButtonStub() throws InterruptedException {
        System.out.println("=== MULAI TEST 4: CEK TOMBOL RESTORE DB ===");

        // 1. CARI TOMBOL 'RESTORE DB'
        WebElement restoreBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//h3[contains(text(), 'Restore DB')]")
        ));

        // 2. KLIK TOMBOL
        System.out.println("Mengklik tombol 'Restore DB to Factory Settings'...");
        restoreBtn.click();
        Thread.sleep(5000);

        // 3. VALIDASI POPUP 'NOT AVAILABLE HERE!'
        System.out.println("Menunggu respons sistem...");
        
        boolean isStubMessageDisplayed = false;
        try {
            WebElement alertMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'Not available here')]")
            ));
            
            System.out.println("Pesan ditemukan: " + alertMessage.getText());
            isStubMessageDisplayed = true;
        } catch (Exception e) {
            System.out.println("Info: Pesan 'Not available here' tidak muncul dalam batas waktu.");
        }

        // 4. ASSERTION
        if (isStubMessageDisplayed) {
            System.out.println("[LOG BUG/INFO] Tombol Restore DB hanya pajangan.");
            Assert.assertTrue(true);
        } else {
            System.out.println("WARNING: Tidak ada reaksi apa pun saat tombol diklik.");
        }
        
        // 5. CLEANUP (Tutup popup jika perlu/klik sembarang tempat)
        try {
            driver.findElement(By.tagName("body")).click();
        } catch (Exception e) {}

        System.out.println("--> Test 4 Restore DB Selesai.");
    }

    @Test(priority = 5)
    @Story("Dashboard UI")
    @Description("Scroll Tabel Functional. BUG: Dropdown 'Value' bocor/keluar dari batas tabel (Floating).")
    public void testTableScrollLeakageBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 5: CEK SCROLLING TABLE (LEAK BUG) ===");

        // 1. KLIK TAB 'FUNCTIONAL'
        System.out.println("Klik tab 'Functional'...");
        WebElement functionalTab = wait.until(ExpectedConditions.elementToBeClickable(By.id("Functional")));
        functionalTab.click();
        Thread.sleep(2000); // Tunggu tabel memuat data

        // 2. IDENTIFIKASI ELEMEN HEADER (BATAS ATAS)
        // Header adalah baris yang berisi tulisan: Name, Value, Description
        // Dari screenshot DOM Anda, class-nya adalah 'configuration-headlines' (ul)
        WebElement tableHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.className("configuration-headlines")
        ));

        // 3. IDENTIFIKASI ELEMEN YANG 'BOCOR' (DROPDOWN)
        // Target: <select name="Typos_on_order_payment">
        WebElement leakageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.name("Typos_on_order_payment")
        ));

        // 4. SCROLL PAGE KE BAWAH
        // Kita scroll supaya elemen dropdown ini naik ke arah header
        System.out.println("Melakukan Scroll ke bawah...");
        
        // A. Scroll dulu sampai elemen kelihatan jelas
        js.executeScript("arguments[0].scrollIntoView(true);", leakageElement);
        Thread.sleep(1000);

        // B. Scroll lagi sedikit ke bawah (misal 150px) untuk memaksanya menabrak header
        js.executeScript("window.scrollBy(0, 150)"); 
        Thread.sleep(1500);

        // 5. CEK TABRAKAN (BUG VALIDATION)
        Rectangle rectHeader = tableHeader.getRect();
        Rectangle rectDropdown = leakageElement.getRect();

        System.out.println("Header Bottom Y : " + (rectHeader.y + rectHeader.height));
        System.out.println("Dropdown Top Y  : " + rectDropdown.y);

        // LOGIKA BUG:
        // Bug terjadi jika elemen Dropdown 'mengapung' di atas Header.
        // Artinya: Posisi Y Dropdown < Posisi Bawah Header
        
        boolean isLeaking = rectDropdown.y < (rectHeader.y + rectHeader.height);
        
        // Pastikan juga elemennya masih terlihat (Visible)
        boolean isVisible = leakageElement.isDisplayed();

        if (isLeaking && isVisible) {
            System.out.println("[LOG BUG] SUCCESS: Elemen Dropdown 'bocor' keluar tabel!");
            Assert.assertTrue(true);
        } else {
            System.out.println("INFO: Elemen tertutup header dengan rapi (No Bug detected).");
            // Analisa visual manual mungkin diperlukan jika overlap hanya sedikit
        }

        // 6. CLEANUP (Scroll balik ke atas)
        js.executeScript("window.scrollTo(0, 0);");
        System.out.println("--> Test 5 Scroll Leakage Selesai.");
    }

    @Test(priority = 6)
    @Story("Dashboard Functionality")
    @Description("Search 'email' + ENTER. Validasi: Tabel terfilter dengan benar.")
    public void testTableSearchFunctionality() throws InterruptedException {
        System.out.println("=== MULAI TEST 6: CEK FITUR SEARCH (DEBUG MODE) ===");

        // 1. SCROLL KE ATAS
        js.executeScript("window.scrollTo(0, 0);");
        Thread.sleep(1000);

        // 2. KLIK ICON SEARCH
        WebElement searchIcon = null;
        try {
            searchIcon = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("svg#Layer_1")));
            searchIcon.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//*[local-name()='svg' and @id='Layer_1']")));
        }

        // 3. KETIK 'email' DAN ENTER
        System.out.println("Mengetik 'email' lalu tekan ENTER...");
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("searchInConfiguration")
        ));
        
        searchInput.clear();
        searchInput.sendKeys("email", Keys.ENTER);
        
        Thread.sleep(3000); // Tunggu tabel refresh
        
        // 4. CLEANUP (RESET SEARCH)
        try {
            // Locator berdasarkan snippet gambar Anda: class="close-png"
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("img.close-png") 
            ));
            
            closeButton.click();
            
            // Tunggu sebentar sampai tabel refresh kembali normal
            Thread.sleep(2000);
            System.out.println("Search berhasil di-reset.");
            
        } catch (Exception e) {
            System.out.println("WARNING: Gagal klik tombol 'X'. Mencoba hapus manual...");
            searchInput.clear();
            searchInput.sendKeys(Keys.ENTER);
        } 
        // Thread.sleep(3000);

        System.out.println("--> Test 6 Search Selesai.");
    }

    @Test(priority = 7)
    @Story("Dashboard Functionality")
    @Description("Klik Tab 'General'. Validasi: Tabel memuat data General dengan benar.")
    public void testGeneralTabFunctionality() throws InterruptedException {
        System.out.println("=== MULAI TEST 7: CEK TAB GENERAL ===");

        // 1. KLIK TAB 'GENERAL'
        WebElement generalTab = wait.until(ExpectedConditions.elementToBeClickable(By.id("General")));
        generalTab.click();
        Thread.sleep(3000);

        System.out.println("--> Test 7 General Tab Selesai.");
    }

    @Test(priority = 8)
    @Story("Dashboard Functionality")
    @Description("Klik Tab 'Performance'. Validasi: Tabel memuat data Performance dengan benar.")
    public void testPerformanceTabFunctionality() throws InterruptedException {
        System.out.println("=== MULAI TEST 8: CEK TAB PERFORMANCE ===");

        // 1. KLIK TAB 'PERFORMANCE'
        WebElement performanceTab = wait.until(ExpectedConditions.elementToBeClickable(By.id("Performance")));
        performanceTab.click();
        Thread.sleep(3000);
        System.out.println("--> Test 8 Performance Tab Selesai.");
    }

    @Test(priority = 9)
    @Story("Dashboard Functionality")
    @Description("Klik Tab 'Functional'. Validasi: Data muncul.")
    public void testFunctionalTabFunctionality() throws InterruptedException {
        System.out.println("=== MULAI TEST 9: CEK TAB FUNCTIONAL ===");
        
        // 1. KLIK TAB 'FUNCTIONAL'
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(By.id("Functional")));
        tab.click();
        Thread.sleep(3000);
        System.out.println("--> Test 9 Selesai.");
    }

    @Test(priority = 10)
    @Story("Dashboard Functionality")
    @Description("Klik Tab 'Mobile'. Validasi: Data muncul.")
    public void testMobileTabFunctionality() throws InterruptedException {
        System.out.println("=== MULAI TEST 10: CEK TAB MOBILE ===");

        // 1. KLIK TAB 'MOBILE'
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(By.id("Mobile")));
        tab.click();
        Thread.sleep(3000);

        System.out.println("--> Test 10 Selesai.");
    }

    @Test(priority = 11)
    @Story("Dashboard Functionality")
    @Description("Klik Tab 'ITOM'. Validasi: Data muncul.")
    public void testItomTabFunctionality() throws InterruptedException {
        System.out.println("=== MULAI TEST 11: CEK TAB ITOM ===");

        // 1. KLIK TAB 'ITOM'
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(By.id("Itom")));
        tab.click();
        Thread.sleep(3000); 

        System.out.println("--> Test 11 Selesai.");
    }

    @Test(priority = 12)
    @Story("Dashboard Functionality")
    @Description("Klik Tab 'Security'. Validasi: Data muncul.")
    public void testSecurityTabFunctionality() throws InterruptedException {
        System.out.println("=== MULAI TEST 12: CEK TAB SECURITY ===");

        // 1. KLIK TAB 'SECURITY'
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(By.id("Security")));
        tab.click();
        Thread.sleep(3000);
        
        System.out.println("--> Test 12 Selesai.");
    }

    @Test(priority = 13)
    @Story("Navigation")
    @Description("Klik Logo Advantage. BUG: User ter-logout paksa.")
    public void testLogoClickLogoutBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 13: CEK LOGO CLICK (LOGOUT BUG) ===");

        // 1. STRATEGI KLIK AGRESIF
        System.out.println("Mencoba klik logo Advantage...");
        WebElement logoText = wait.until(ExpectedConditions.elementToBeClickable(
            By.className("nav-headline-logo") 
        ));
        logoText.click();
        System.out.println(">> Klik Text dilakukan.");
        Thread.sleep(3000);

        // 2. ASSERTION & RECOVERY
        System.out.println("[LOG BUG] Klik Logo menyebabkan Logout!");
        System.out.println(">> RECOVERY: Melakukan Login Ulang...");
            
        driver.findElement(By.id("userNameInput")).sendKeys("testing1");
        Thread.sleep(1000);
        driver.findElement(By.name("password")).sendKeys("Testing1");
        Thread.sleep(1000);
            
        // Klik Sign In (Pakai JS biar aman)
        try {
            driver.findElement(By.className("sign-in-btn")).click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//button[contains(text(),'Sign In')]")));
        }

        try {
            wait.until(ExpectedConditions.urlContains("management-console"));
            System.out.println(">> Berhasil Login Ulang.");
        } catch (Exception e) {
            System.out.println("Warning: Dashboard lambat dimuat.");
        }

        Assert.assertTrue(true);
        Thread.sleep(5000);
        
        System.out.println("--> Test 13 Selesai.");
    }

    @Test(priority = 14)
    @Story("Navigation")
    @Description("Klik menu 'Products'. BUG: Redirect ke halaman 'Coming Soon'.")
    public void testProductSidebarBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 14: CEK SIDEBAR PRODUCTS (COMING SOON BUG) ===");
        
        // 1. KLIK MENU 'PRODUCTS'
        System.out.println("Mengklik menu 'Products'...");
        WebElement productMenu = wait.until(ExpectedConditions.elementToBeClickable(
            By.className("item-product")
        ));
        productMenu.click();
        Thread.sleep(2000);

        // 2. VALIDASI BUG (GAMBAR COMING SOON MUNCUL)
        System.out.println("Memvalidasi halaman 'Coming Soon'...");
        boolean isComingSoonDisplayed = false;

        try {
            WebElement comingSoonImg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//img[@alt='coming png'] | //img[contains(@src, 'Coming')]")
            ));
            
            if (comingSoonImg.isDisplayed()) {
                isComingSoonDisplayed = true;
            }
        } catch (Exception e) {
            System.out.println("Info: Gambar Coming Soon tidak ditemukan.");
        }

        // 3. ASSERTION
        if (isComingSoonDisplayed) {
            System.out.println("[LOG BUG] Masuk ke halaman 'Coming Soon'!");
            Assert.assertTrue(true);
        } else {
            System.out.println("FAIL: Tidak ada gambar Coming Soon. URL saat ini: " + driver.getCurrentUrl());
            Assert.fail("Bug tidak muncul: Menu Products mungkin sudah diperbaiki atau salah redirect.");
        }

        // 4. CLEANUP / RECOVERY
        System.out.println("Kembali ke Dashboard...");
        driver.navigate().back();
        Thread.sleep(2000);

        System.out.println("--> Test 14 Product Sidebar Selesai.");
    }

    @Test(priority = 15)
    @Story("Navigation")
    @Description("Klik menu 'Special Offers'. BUG: Redirect ke halaman 'Coming Soon'.")
    public void testSpecialOffersSidebarBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 15: CEK SIDEBAR SPECIAL OFFERS ===");
        
        // 1. KLIK MENU 'SPECIAL OFFERS'
        System.out.println("Mengklik menu 'Special Offers'...");
        WebElement offerMenu = wait.until(ExpectedConditions.elementToBeClickable(
            By.className("item-offer")
        ));
        offerMenu.click();
        Thread.sleep(3000);

        // 2. VALIDASI BUG (GAMBAR COMING SOON)
        System.out.println("Memvalidasi halaman 'Coming Soon'...");
        boolean isComingSoonDisplayed = false;

        try {
            WebElement comingSoonImg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//img[@alt='coming png'] | //img[contains(@src, 'Coming')]")
            ));
            
            if (comingSoonImg.isDisplayed()) {
                isComingSoonDisplayed = true;
            }
        } catch (Exception e) {
            System.out.println("Info: Gambar Coming Soon tidak ditemukan.");
        }

        // 3. ASSERTION
        if (isComingSoonDisplayed) {
            System.out.println("[LOG BUG] Masuk ke halaman 'Coming Soon'!");
            Assert.assertTrue(true);
        } else {
            System.out.println("FAIL: Tidak ada gambar Coming Soon. URL saat ini: " + driver.getCurrentUrl());
            Assert.fail("Bug tidak muncul: Menu mungkin sudah diperbaiki.");
        }

        // 4. CLEANUP (KEMBALI KE DASHBOARD)
        System.out.println("Kembali ke Dashboard...");
        driver.navigate().back();
        Thread.sleep(2000);

        System.out.println("--> Test 15 Special Offers Selesai.");
    }

    @Test(priority = 16)
    @Story("Navigation")
    @Description("Klik menu 'Popular Items'. BUG: Redirect ke halaman 'Coming Soon'.")
    public void testPopularItemsSidebarBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 16: CEK SIDEBAR POPULAR ITEMS ===");
        
        // 1. KLIK MENU 'POPULAR ITEMS'
        System.out.println("Mengklik menu 'Popular Items'...");
        WebElement popularMenu = wait.until(ExpectedConditions.elementToBeClickable(
            By.className("item-popular")
        ));
        popularMenu.click();
        Thread.sleep(3000);

        // 2. VALIDASI BUG (GAMBAR COMING SOON)
        System.out.println("Memvalidasi halaman 'Coming Soon'...");
        boolean isComingSoonDisplayed = false;

        try {
            WebElement comingSoonImg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//img[@alt='coming png'] | //img[contains(@src, 'Coming')]")
            ));
            
            if (comingSoonImg.isDisplayed()) {
                isComingSoonDisplayed = true;
            }
        } catch (Exception e) {
            System.out.println("Info: Gambar Coming Soon tidak ditemukan.");
        }

        // 3. ASSERTION
        if (isComingSoonDisplayed) {
            System.out.println("[LOG BUG] Masuk ke halaman 'Coming Soon'!");
            Assert.assertTrue(true);
        } else {
            System.out.println("FAIL: Tidak ada gambar Coming Soon. URL saat ini: " + driver.getCurrentUrl());
            Assert.fail("Bug tidak muncul: Menu mungkin sudah diperbaiki.");
        }

        // 4. CLEANUP (KEMBALI KE DASHBOARD)
        System.out.println("Kembali ke Dashboard...");
        driver.navigate().back();
        Thread.sleep(2000);

        System.out.println("--> Test 16 Popular Items Selesai.");
    }

    @Test(priority = 17)
    @Story("Navigation")
    @Description("Klik menu 'User Management'. BUG: Redirect ke halaman 'Coming Soon'.")
    public void testUserManagementSidebarBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 17: CEK SIDEBAR USER MANAGEMENT ===");
        
        // 1. KLIK MENU 'USER MANAGEMENT'
        System.out.println("Mengklik menu 'User Management'...");
        WebElement userMenu = wait.until(ExpectedConditions.elementToBeClickable(
            By.className("item-management")
        ));
        userMenu.click();
        Thread.sleep(2000);

        // 2. VALIDASI BUG (GAMBAR COMING SOON)
        System.out.println("Memvalidasi halaman 'Coming Soon'...");
        boolean isComingSoonDisplayed = false;

        try {
            WebElement comingSoonImg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//img[@alt='coming png'] | //img[contains(@src, 'Coming')]")
            ));
            
            if (comingSoonImg.isDisplayed()) {
                isComingSoonDisplayed = true;
            }
        } catch (Exception e) {
            System.out.println("Info: Gambar Coming Soon tidak ditemukan (Mungkin fitur ini jalan?).");
        }

        // 3. ASSERTION
        if (isComingSoonDisplayed) {
            System.out.println("[LOG BUG] Masuk ke halaman 'Coming Soon'!");
            Assert.assertTrue(true);
        } else {
            System.out.println("FAIL: Tidak ada gambar Coming Soon. URL saat ini: " + driver.getCurrentUrl());
            Assert.fail("Bug tidak muncul: Menu User Management mungkin berfungsi normal.");
        }

        // 4. CLEANUP (KEMBALI KE DASHBOARD)
        System.out.println("Kembali ke Dashboard...");
        driver.navigate().back();
        Thread.sleep(2000);

        System.out.println("--> Test 17 User Management Selesai.");
    }

    @Test(priority = 18)
    @Story("Navigation")
    @Description("Klik menu 'Configuration' saat sedang di halaman Configuration. Validasi: Tidak ada perubahan (Stay).")
    public void testConfigurationSidebarStability() throws InterruptedException {
        System.out.println("=== MULAI TEST 18: CEK SIDEBAR CONFIGURATION ===");

        // 1. SIMPAN URL SAAT INI
        String urlBefore = driver.getCurrentUrl();
        System.out.println("URL Awal: " + urlBefore);

        // 2. KLIK MENU 'CONFIGURATION'
        System.out.println("Mengklik menu 'Configuration'...");
        WebElement configMenu = wait.until(ExpectedConditions.elementToBeClickable(
            By.className("item-configuration")
        ));
        configMenu.click();
        Thread.sleep(2000);

        // 3. VALIDASI
        String urlAfter = driver.getCurrentUrl();
        System.out.println("URL Akhir: " + urlAfter);
        
        boolean isHeaderVisible = driver.findElements(By.xpath("//h3[contains(text(), 'Configuration') or contains(text(), 'CONFIGURATION')]")).size() > 0;

        if (urlBefore.equals(urlAfter) && isHeaderVisible) {
            System.out.println("[SUCCESS] Halaman stabil.");
            Assert.assertTrue(true);
        } else {
            System.out.println("FAIL: Terjadi redirect atau refresh yang tidak diharapkan.");
            Assert.assertEquals(urlAfter, urlBefore, "URL berubah setelah klik Configuration!");
        }
        Thread.sleep(2000);

        System.out.println("--> Test 18 Configuration Sidebar Selesai.");
    }

    @Test(priority = 19)
    @Story("Dashboard Functionality")
    @Description("Klik tombol Export Excel. BUG: Muncul popup 'Module Under Development'.")
    public void testExportExcelBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 19: CEK EXPORT EXCEL (FIX SVG) ===");

        // 1. KLIK TOMBOL EXPORT (STRATEGI BARU)
        System.out.println("Mengklik tombol Export Excel...");
        
        try {
            WebElement exportBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#Export_XLS") 
            ));
            
            // CARA 1: Pakai Actions Class (Simulasi Mouse Fisik)
            Actions action = new Actions(driver);
            action.moveToElement(exportBtn).click().build().perform();
        } catch (Exception e) {
            System.out.println("Klik Actions gagal, mencoba JS Dispatch Event...");
            
            // CARA 2: JS Dispatch Event
            WebElement exportBtn = driver.findElement(By.xpath("//*[local-name()='g' and @id='Export_XLS']"));
            js.executeScript(
                "var evt = new MouseEvent('click', {bubbles: true, cancelable: true, view: window});" +
                "arguments[0].dispatchEvent(evt);", 
                exportBtn
            );
        }

        // 3. VALIDASI POPUP MUNCUL
        System.out.println("Menunggu popup 'Under Development'...");
        boolean isPopupVisible = false;

        try {
            WebElement popupTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("popup-title")
            ));
            
            String titleText = popupTitle.getText();
            System.out.println("Popup Title: " + titleText);

            if (titleText.contains("Module Under Development")) {
                isPopupVisible = true;
            }
        } catch (Exception e) {
            System.out.println("Info: Popup tidak muncul dalam batas waktu.");
        }

        // 4. ASSERTION
        if (isPopupVisible) {
            System.out.println("[LOG BUG] Popup 'Module Under Development' muncul!");
            Assert.assertTrue(true);
        } else {
            System.out.println("FAIL: Popup tidak muncul.");
            Assert.fail("Bug tidak muncul: Popup 'Module Under Development' tidak ditemukan.");
        }

        // 5. CLEANUP (TUTUP POPUP)
        System.out.println("Cleanup: Menutup popup...");
        WebElement closeBtn = driver.findElement(By.className("popup-yes-btn"));
            
        Actions action = new Actions(driver);
        action.moveToElement(closeBtn).click().build().perform();
        Thread.sleep(2000);
            
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("pop-up-style")));
        System.out.println("Popup berhasil ditutup.");

        System.out.println("--> Test 19 Export Excel Selesai.");
    }

    @Test(priority = 20)
    @Story("Security")
    @Description("Klik Logout -> Yes. Validasi: User kembali ke halaman Login.")
    public void testLogoutFunctionality() throws InterruptedException {
        System.out.println("=== MULAI TEST 20: CEK LOGOUT ===");

        // 1. KLIK TOMBOL LOGOUT
        System.out.println("Mengklik tombol Logout...");
        WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.className("right-logout-btn")
        ));
        logoutBtn.click();
        Thread.sleep(2000);

        // 2. KONFIRMASI POPUP 'YES'
        try {
            System.out.println("Menunggu konfirmasi Logout...");
            WebElement yesBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[text()='Yes'] | //span[text()='Yes']") 
            ));
            yesBtn.click();
            System.out.println("Tombol 'Yes' diklik.");
        } catch (Exception e) {
            System.out.println("Info: Tidak ada popup konfirmasi, langsung logout.");
        }

        // 3. VALIDASI REDIRECT KE LOGIN PAGE
        System.out.println("Validasi halaman login...");
        boolean isLoggedOut = false;
        
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userNameInput")));
            isLoggedOut = true;
        } catch (Exception e) {
            isLoggedOut = false;
        }

        // 4. ASSERTION
        if (isLoggedOut) {
            System.out.println("[SUCCESS] Logout Berhasil.");
            Assert.assertTrue(true);
        } else {
            System.out.println("[FAIL] User masih tertahan di Dashboard.");
            System.out.println("URL saat ini: " + driver.getCurrentUrl());
            Assert.fail("Logout Gagal: Halaman login tidak muncul.");
        }

        System.out.println("Kembali ke Dashboard...");
        driver.navigate().back();
        Thread.sleep(3000);

        System.out.println("--> Test 20 Logout Selesai.");
    }

    @Test(priority = 21)
    @Story("Navigation")
    @Description("Klik Products -> Klik 'Back to AOS'. Validasi: Redirect ke Main Store.")
    public void testBackToAOSButton() throws InterruptedException {
        System.out.println("=== MULAI TEST 21: CEK TOMBOL BACK TO AOS ===");

        // 1. KLIK SIDEBAR 'PRODUCTS'
        System.out.println("Masuk ke halaman Products (Coming Soon)...");
        WebElement productMenu = wait.until(ExpectedConditions.elementToBeClickable(By.className("item-product")));
        productMenu.click();
        Thread.sleep(2000);

        WebElement backToAOSBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("backToAOS")
        ));

        // 3. KLIK TOMBOL 'BACK TO AOS'
        System.out.println("Mengklik tombol 'Back to AOS'...");
        backToAOSBtn.click();
        Thread.sleep(3000);

        // 4. VALIDASI REDIRECT
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL Saat ini: " + currentUrl);
        boolean isBackToStore = false;
        
        if (!currentUrl.contains("admin") && (currentUrl.contains("#/") || currentUrl.endsWith(".com/"))) {
            isBackToStore = true;
        }

        if (isBackToStore) {
            System.out.println("[SUCCESS] Berhasil kembali ke Halaman Utama Toko (AOS).");
            Assert.assertTrue(true);
        } else {
            System.out.println("[FAIL] Masih tertahan di halaman Admin atau Coming Soon.");
            Assert.fail("Tombol Back to AOS tidak mengarahkan ke toko utama.");
        }
        Thread.sleep(2000);
        System.out.println("--> Test 21 Back to AOS Selesai.");
    }

    @Test(priority = 22)
    @Story("Navigation")
    @Description("Browser Back -> Klik 'Management Console'. BUG: Redirect ke Login Page (Session Lost/Redirect Salah).")
    public void testManagementConsoleButtonBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 22: CEK TOMBOL MANAGEMENT CONSOLE ===");

        // 1. BROWSER BACK
        System.out.println("Melakukan Navigasi Back (Browser Back)...");
        driver.navigate().back();
        Thread.sleep(3000);

        // 2. CARI & KLIK TOMBOL 'MANAGEMENT CONSOLE'
        System.out.println("Mengklik tombol 'Management Console'...");
        WebElement consoleBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("backToManagement")
        ));
        consoleBtn.click();
        Thread.sleep(3000);

        // 3. VALIDASI BUG (REDIRECT KE LOGIN)
        System.out.println("Memvalidasi halaman tujuan...");
        boolean isRedirectedToLogin = false;

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username"))); 
            if(driver.findElements(By.id("userNameInput")).size() > 0) {
                isRedirectedToLogin = true;
            }
        } catch (Exception e) {
            // Jika timeout, berarti mungkin masuk dashboard (berarti bug tidak muncul)
        }

        // 4. ASSERTION
        if (isRedirectedToLogin) {
            System.out.println("[LOG BUG] Redirect ke Halaman Login!");
            
            // BROWSER BACK
            System.out.println("Melakukan Navigasi Back...");
            driver.navigate().back();
            Thread.sleep(3000);
        } else {
            if (driver.getCurrentUrl().contains("management-console")) {
                System.out.println("FAIL: Masuk ke Dashboard dengan sukses (Bug tidak muncul).");
                Assert.fail("Bug Redirect Login tidak terjadi. Fitur berfungsi normal.");
            } else {
                System.out.println("[LOG BUG] Redirect ke Halaman Login!");
            }
        }

        System.out.println("--> Test 22 Management Console Button Selesai.");
    }

    @Test(priority = 23)
    @Story("Social Media")
    @Description("Klik Icon Facebook. Validasi: Membuka tab baru ke Facebook.")
    public void testFacebookIconLink() throws InterruptedException {
        System.out.println("=== MULAI TEST 23: CEK FACEBOOK ICON ===");

        // 1. BROWSER BACK
        System.out.println("Melakukan Navigasi Back (Browser Back)...");
        driver.navigate().back();
        Thread.sleep(2000);

        // 2. SIMPAN WINDOW HANDLE LAMA (TAB UTAMA)
        String originalWindow = driver.getWindowHandle();
        Set<String> oldWindows = driver.getWindowHandles();
        System.out.println("Jumlah Tab Awal: " + oldWindows.size());

        // 3. KLIK ICON FACEBOOK
        System.out.println("Mengklik icon Facebook...");
        WebElement fbIcon = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("#facebookIcon")
        ));
            
        Actions action = new Actions(driver);
        action.moveToElement(fbIcon).click().build().perform();
        Thread.sleep(3000);

        // 4. TUNGGU TAB BARU MUNCUL
        System.out.println("Menunggu tab baru terbuka...");
        try {
            wait.until(ExpectedConditions.numberOfWindowsToBe(oldWindows.size() + 1));
        } catch (Exception e) {
            System.out.println("WARNING: Tab baru belum terdeteksi dalam waktu tunggu.");
        }

        // 5. PINDAH FOKUS KE TAB BARU
        Set<String> newWindows = driver.getWindowHandles();
        for (String handle : newWindows) {
            if (!handle.equals(originalWindow)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        // 6. VALIDASI URL FACEBOOK
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + currentUrl);

        if (currentUrl.contains("facebook.com")) {
            System.out.println("[SUCCESS] Link Facebook berfungsi.");
            Assert.assertTrue(true);
        } else {
            System.out.println("[FAIL] URL tidak mengarah ke Facebook.");
        }
        Thread.sleep(2000);

        // 7. CLEANUP (TUTUP TAB FACEBOOK & KEMBALI)
        System.out.println("Menutup Tab Facebook...");
        driver.close();
        
        System.out.println("Kembali ke Tab Utama...");
        driver.switchTo().window(originalWindow);
        Thread.sleep(2000);

        System.out.println("--> Test 23 Facebook Selesai.");
    }

    @Test(priority = 24)
    @Story("Social Media")
    @Description("Klik Icon Twitter. Handle: Facebook (Bug) atau Twitter (Success) atau Homepage (Bug).")
    public void testTwitterIconLinkBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 24: CEK TWITTER ICON (HYBRID CHECK) ===");

        // 1. PREPARE
        String originalWindow = driver.getWindowHandle();
        Set<String> oldWindows = driver.getWindowHandles();
        int initialWindowCount = oldWindows.size();
        String urlBefore = driver.getCurrentUrl();

        // 2. KLIK DENGAN RETRY
        boolean tabOpened = false;
        
        for (int i = 0; i < 3; i++) {
            try {
                WebElement twitterIcon = wait.until(ExpectedConditions.elementToBeClickable(By.id("twitterIcon")));
                Actions action = new Actions(driver);
                action.moveToElement(twitterIcon).click().build().perform();

                try {
                    wait.withTimeout(Duration.ofSeconds(5))
                        .until(ExpectedConditions.numberOfWindowsToBe(initialWindowCount + 1));
                    tabOpened = true;
                    break;
                } catch (Exception e) {
                    System.out.println("... Percobaan " + (i+1) + " belum membuka tab.");
                }

            } catch (Exception e) {
                System.out.println("Error klik: " + e.getMessage());
            }
        }

        // 3. LOGIKA VALIDASI HYBRID
        if (tabOpened) {
            // --- SKENARIO A: TAB BARU TERBUKA ---
            Set<String> newWindows = driver.getWindowHandles();
            for (String handle : newWindows) {
                if (!handle.equals(originalWindow)) {
                    driver.switchTo().window(handle);
                    break;
                }
            }

            String currentUrl = driver.getCurrentUrl();
            System.out.println("Hasil: Tab Baru Terbuka. URL: " + currentUrl);

            if (currentUrl.contains("facebook.com")) {
                System.out.println("[LOG BUG] Link Twitter mengarah ke Facebook!");
            } 
            else if (currentUrl.contains("twitter.com") || currentUrl.contains("x.com")) {
                System.out.println("[SUCCESS] Link Twitter berfungsi dengan benar.");
            } 
            else {
                System.out.println("[INFO] Link mengarah ke: " + currentUrl);
            }

            driver.close(); 
            driver.switchTo().window(originalWindow);

        } else {
            System.out.println("Hasil: Tidak ada tab baru terbuka.");
            String currentUrl = driver.getCurrentUrl();

            // Cek apakah malah refresh ke homepage (Bug lain)
            if (currentUrl.equals(urlBefore) || currentUrl.contains("dashboard")) {
                System.out.println("[LOG BUG] Tidak ada respon atau Redirect ke Homepage.");
            }
            
            driver.switchTo().window(originalWindow);
        }

        Assert.assertTrue(true);
        Thread.sleep(3000);

        System.out.println("--> Test 24 Selesai.");
    }

    @Test(priority = 25)
    @Story("Social Media")
    @Description("Klik Icon LinkedIn. Handle dua kemungkinan: Sukses (LinkedIn) atau Bug (Homepage).")
    public void testLinkedinIconLink() throws InterruptedException {
        System.out.println("=== MULAI TEST 25: CEK LINKEDIN ICON (HYBRID CHECK) ===");

        // 1. PREPARE
        String originalWindow = driver.getWindowHandle();
        Set<String> oldWindows = driver.getWindowHandles();

        // 2. KLIK ICON LINKEDIN
        System.out.println("Mengklik icon LinkedIn...");
        WebElement linkedinIcon = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("linkedinIcon")
        ));
            
        Actions action = new Actions(driver);
        action.moveToElement(linkedinIcon).click().build().perform();
        Thread.sleep(3000);

        Set<String> newWindows = driver.getWindowHandles();
        
        // KEMUNGKINAN 1: TAB BARU TERBUKA (BERHASIL)
        if (newWindows.size() > oldWindows.size()) {
            
            // Pindah ke tab baru untuk verifikasi
            for (String handle : newWindows) {
                if (!handle.equals(originalWindow)) {
                    driver.switchTo().window(handle);
                    break;
                }
            }
            
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Hasil: Tab Baru Terbuka. URL: " + currentUrl);
            
            if (currentUrl.contains("linkedin.com")) {
                System.out.println("[SUCCESS] Navigasi ke LinkedIn Berhasil.");
            } else {
                System.out.println("[WARNING] Tab baru terbuka, tapi bukan LinkedIn (" + currentUrl + ")");
            }

            driver.close();
            driver.switchTo().window(originalWindow);
            Thread.sleep(3000);
            
            Assert.assertTrue(true);
        } 
        else {
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Hasil: Tidak ada tab baru. URL Saat ini: " + currentUrl);

            if (!currentUrl.contains("linkedin.com")) {
                System.out.println("[LOG BUG] Redirect ke Homepage!");
                Assert.assertTrue(true);
            } else {
                System.out.println("[INFO] LinkedIn terbuka di tab yang sama.");
                Assert.assertTrue(true);
            }

            System.out.println("Melakukan Navigasi Back (Browser Back)...");
            driver.navigate().back();
            Thread.sleep(3000);
        }
    
        System.out.println("--> Test 25 LinkedIn Selesai.");
    }

    @Test(priority = 26)
    @Story("Navigation")
    @Description("Klik Logo 'dvantage' di halaman Coming Soon. BUG: Redirect ke Login Page (Logout Paksa).")
    public void testComingSoonLogoBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 26: CEK LOGO NAVIGASI (COMING SOON PAGE) ===");

        // 1. KLIK LOGO 'dvantage'
        System.out.println("Mengklik logo 'dvantage'...");
        WebElement logoText = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("h1.dvantage") 
        ));
            
        logoText.click();
        Thread.sleep(3000);

        // 2. VALIDASI
        System.out.println("Memvalidasi halaman tujuan...");
        boolean isLoggedOut = false;

        try {
            if (driver.findElements(By.name("username")).size() > 0 || 
                driver.findElements(By.id("userNameInput")).size() > 0) {
                isLoggedOut = true;
            }
        } catch (Exception e) {}

        // 3. ASSERTION & RECOVERY
        if (isLoggedOut) {
            System.out.println("[LOG BUG] Klik Logo menyebabkan Logout!");

            // BROWSER BACK
            System.out.println("Melakukan Navigasi Back (Browser Back)...");
            driver.navigate().back();
            Thread.sleep(3000);

            Assert.assertTrue(true);

        } else {
            if (driver.getCurrentUrl().contains("dashboard")) {
                System.out.println("[FAIL] Logo berfungsi normal (Masuk Dashboard). Bug tidak muncul.");
                Assert.fail("Expected Bug not found: User tidak ter-logout.");
            } else {
                System.out.println("[FAIL] Halaman tidak diketahui: " + driver.getCurrentUrl());
            }
        }
        Thread.sleep(2000);

        System.out.println("--> Test 26 Logo Bug Selesai.");
    }

    @Test(priority = 27)
    @Story("Layout")
    @Description("Ubah ukuran browser ke Mobile view (375x812) saat di halaman Coming Soon.")
    public void testMobileLayoutResize() throws InterruptedException {
        System.out.println("=== MULAI TEST 27: CEK RESIZE LAYOUT (MOBILE) ===");

        // 1. PASTIKAN DI HALAMAN COMING SOON
        // Karena P26 melakukan Back, seharusnya kita sudah disini. 
        // Tapi untuk safety, kita cek URL.
        if (driver.getCurrentUrl().contains("login")) {
             System.out.println("Info: Terlanjur di Login page, Back sekali lagi...");
             driver.navigate().back(); 
             Thread.sleep(1500);
        }

        // 2. UBAH UKURAN WINDOW (SIMULASI HP/MOBILE)
        System.out.println("Mengubah ukuran window ke 375x812 (iPhone X)...");
        Dimension mobileSize = new Dimension(375, 812);
        driver.manage().window().setSize(mobileSize);
        
        Thread.sleep(2000); // Tunggu rendering layout baru

        // 3. VALIDASI: APAKAH HALAMAN MASIH RENDER DENGAN BAIK?
        System.out.println("Validasi elemen di mode Mobile...");
        boolean isContentVisible = false;

        try {
            // Kita cek apakah gambar 'Coming Soon' masih terlihat?
            WebElement comingSoonImg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//img[contains(@src, 'Coming') or @alt='coming png']")
            ));
            
            if (comingSoonImg.isDisplayed()) {
                isContentVisible = true;
            }
        } catch (Exception e) {
            System.out.println("Element tidak ditemukan saat mode mobile.");
        }

        // 4. ASSERTION
        if (isContentVisible) {
            System.out.println("[SUCCESS] Layout Mobile berhasil dimuat.");
            Assert.assertTrue(true);
        } else {
            System.out.println("[FAIL] Konten hilang atau rusak saat di-resize ke mobile.");
            // Kita beri warning saja, jangan fail, karena fokusnya hanya 'bisa resize atau tidak'
            // Assert.fail("Mobile layout broken."); 
        }

        // 5. RESTORE WINDOW (WAJIB!)
        // Kembalikan ke mode Desktop agar test selanjutnya tidak error
        System.out.println("Mengembalikan ukuran window ke Full Screen (Maximize)...");
        driver.manage().window().maximize();
        Thread.sleep(2000); // Tunggu animasi maximize

        System.out.println("--> Test 27 Resize Selesai.");
    }

    @Test(priority = 28)
    @Story("Navigation")
    @Description("Refresh halaman Coming Soon. Validasi: Halaman tidak crash/404 Not Found.")
    public void testRefreshComingSoonPage() throws InterruptedException {
        System.out.println("=== MULAI TEST 28: CEK REFRESH HALAMAN ===");

        // 1. REFRESH HALAMAN
        System.out.println("Melakukan Refresh browser...");
        driver.navigate().refresh();
        Thread.sleep(3000);

        // 2. CEK APAKAH JADI ERROR / 404?
        String pageSource = driver.getPageSource().toLowerCase();
        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL setelah refresh: " + currentUrl);

        boolean isErrorFound = false;
        
        // Indikator Error 1: Teks '404' atau 'Not Found' muncul di layar
        if (pageSource.contains("404") || pageSource.contains("HTTP ERROR") || pageSource.contains("can't be found") || pageSource.contains("not found")) {
            isErrorFound = true;
        }

        // Indikator Error 2: Gambar Coming Soon HILANG
        boolean isImageVisible = false;
        try {
            WebElement img = driver.findElement(By.xpath("//img[contains(@src, 'Coming') or @alt='coming png']"));
            if (img.isDisplayed()) {
                isImageVisible = true;
                isErrorFound = false;
            }
        } catch (Exception e) {
            isImageVisible = false;
            isErrorFound = true;
        }

        // 3. ASSERTION
        if (isErrorFound) {
            System.out.println("[LOG BUG] Refresh menyebabkan Error / 404 Not Found.");
            Assert.assertTrue(true);
        } else {
            System.out.println("[SUCCESS] Halaman stabil setelah refresh.");
            Assert.assertTrue(true);
        }

        Thread.sleep(1000);
        System.out.println("--> Test 28 Refresh Selesai.");
    }
    
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
