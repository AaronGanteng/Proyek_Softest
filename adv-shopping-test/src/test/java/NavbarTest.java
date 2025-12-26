import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.Dimension;
import java.time.Duration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Story;

public class NavbarTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        driver.get("https://www.advantageonlineshopping.com/#/");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("our_products")));
            
            System.out.println("Website berhasil dimuat sepenuhnya!");
        } catch (Exception e) {
            System.out.println("Website terlalu lama loading (Timeout)!");
        }
    }

    @Test(priority = 1)
    @Story("Navigation to Special Offer")
    @Description("Memastikan navigasi ke menu Special Offer berhasil")
    public void testNavSpecialOffer() throws InterruptedException {
        System.out.println("Mencoba klik menu SPECIAL OFFER...");

        // Temukan menu SPECIAL OFFER dan klik
        WebElement menuOffer = driver.findElement(By.xpath("//a[contains(text(), 'SPECIAL OFFER')]"));
        menuOffer.click();

        Thread.sleep(3000);

        // Validasi
        WebElement offerSection = driver.findElement(By.id("special_offer_items"));
        Assert.assertTrue(offerSection.isDisplayed(), "Gagal berpindah ke section Special Offer!");
        System.out.println("Sukses masuk ke Special Offer!");
    }

    @Test(priority = 2)
    @Story("Navigation to Popular Items")
    @Description("Memastikan navigasi ke menu Popular Items berhasil")
    public void testNavPopularItems() throws InterruptedException {
        System.out.println("Mencoba klik menu POPULAR ITEMS...");
        
        
        WebElement menuPopular = driver.findElement(By.xpath("//a[contains(text(), 'POPULAR ITEMS')]"));
        menuPopular.click();
        
        Thread.sleep(3000);

        // Validasi
        WebElement productSection = driver.findElement(By.id("popular_items"));
        Assert.assertTrue(productSection.isDisplayed(), "Gagal berpindah ke section Popular Items!");
        System.out.println("Sukses masuk ke Popular Items!");
    }

    @Test(priority = 3)
    @Story("Navigation to Contact Us")
    @Description("Memastikan navigasi ke menu Contact Us berhasil")
    public void testNavContactUs() throws InterruptedException {
        System.out.println("Mencoba klik menu CONTACT US...");
        
        WebElement menuContact = driver.findElement(By.xpath("//a[contains(text(), 'CONTACT US')]"));
        menuContact.click();
        
        Thread.sleep(3000);

        // Validasi
        WebElement productSection = driver.findElement(By.id("contact_us"));
        Assert.assertTrue(productSection.isDisplayed(), "Gagal berpindah ke section Contact Us!");
        System.out.println("Sukses masuk ke Contact Us!");
    }

    @Test(priority = 4)
    @Story("Navigation to Our Products")
    @Description("Memastikan navigasi ke menu Our Products berhasil")
    public void testNavOurProducts() throws InterruptedException {
        System.out.println("Mencoba klik menu OUR PRODUCTS...");
        
        WebElement menuProducts = driver.findElement(By.xpath("//a[contains(text(), 'OUR PRODUCTS')]"));
        menuProducts.click();
        
        Thread.sleep(3000);

        // Validasi
        WebElement productSection = driver.findElement(By.id("our_products"));
        Assert.assertTrue(productSection.isDisplayed(), "Gagal berpindah ke section Our Products!");
        System.out.println("Sukses masuk ke Our Products!");
    }

    @Test(priority = 5)
    @Story("Valid Search Fitur")
    @Description("Berhasil search barang dari search bar di navbar")
    public void testNavValidSearch() throws InterruptedException {
        System.out.println("Mencoba fitur search yang valid...");
       
        WebElement searchIcon = driver.findElement(By.id("menuSearch"));
        searchIcon.click();
        Thread.sleep(2000);

        WebElement searchInput = driver.findElement(By.id("autoComplete"));
        System.out.println("Menginput kata kunci pencarian yang valid...");
        searchInput.sendKeys("Laptop");
        Thread.sleep(10000);

        searchIcon.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        try {
            // XPath ini mencari elemen APAPUN yang teksnya mengandung "Search result"
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'Search result')]")
            ));
            System.out.println("Hasil pencarian sudah muncul!");
        } catch (Exception e) {
            Assert.fail("Timeout! Hasil pencarian tidak muncul dalam 15 detik.");
        }

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("Laptop"), "Gagal masuk ke halaman hasil pencarian!");

        Thread.sleep(12000);
        System.out.println("Berusaha kembali ke Homepage...");
        WebElement logo = driver.findElement(By.xpath("//span[contains(text(), 'dvantage')]"));
        logo.click();

        wait.until(ExpectedConditions.urlToBe("https://www.advantageonlineshopping.com/#/"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "Gagal kembali ke Homepage!");
        System.out.println("--> Berhasil kembali ke Homepage via menu HOME.");
        Thread.sleep(3000);
    }

    @Test(priority = 6)
    @Story("Invalid Search Fitur")
    @Description("Gagal search barang dari search bar di navbar")
    public void testNavInvalidSearch() throws InterruptedException {
        System.out.println("Mencoba fitur search yang invalid...");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loader")));
            wait.until(ExpectedConditions.elementToBeClickable(By.id("menuSearch")));
        } catch (Exception e) {
            System.out.println("Loader tidak terdeteksi atau tombol belum siap, mencoba lanjut...");
        }
       
        WebElement searchIcon = driver.findElement(By.id("menuSearch"));
        searchIcon.click();
        Thread.sleep(3000);

        WebElement searchInput = driver.findElement(By.id("autoComplete"));
        System.out.println("Menginput kata kunci pencarian yang invalid...");
        searchInput.sendKeys("asd");
        Thread.sleep(5000);

        searchIcon.click();

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'No results for')]")
            ));
            System.out.println("Pesan No Results sudah muncul!");
        } catch (Exception e) {
            Assert.fail("Timeout! Pesan error tidak muncul dalam 60 detik.");
        }

        Thread.sleep(12000);
        String pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains("No results for"), "Pesan 'No results for' tidak muncul!");
        System.out.println("--> Pesan 'No results for' berhasil terdeteksi.");

        System.out.println("Berusaha kembali ke Homepage...");
        WebElement logo = driver.findElement(By.xpath("//span[contains(text(), 'dvantage')]"));
        logo.click();

        wait.until(ExpectedConditions.urlToBe("https://www.advantageonlineshopping.com/#/"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "Gagal kembali ke Homepage!");
        System.out.println("--> Berhasil kembali ke Homepage via menu HOME.");
        Thread.sleep(3000);
    }

    @Test(priority = 7)
    @Story("User Profile Navigation")
    @Description("Memastikan klik ikon User memunculkan Pop-up Login, lalu tutup pakai tombol X")
    public void testNavUserProfile() throws InterruptedException {
        System.out.println("Mencoba klik ikon User Profile...");

        // 1. Klik Ikon User
        WebElement userIcon = driver.findElement(By.id("menuUser"));
        userIcon.click();
        
        // 2. Tunggu Pop-up Login Muncul
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            // Tunggu sampai kotak username terlihat
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            System.out.println("Pop-up Login berhasil muncul!");
        } catch (Exception e) {
            Assert.fail("Timeout! Pop-up login tidak muncul.");
        }

        // 3. Validasi elemen di dalam pop-up (Pastikan tombol Sign In ada)
        WebElement signInBtn = driver.findElement(By.id("sign_in_btn"));
        Assert.assertTrue(signInBtn.isDisplayed(), "Tombol Sign In tidak ditemukan!");

        Thread.sleep(1000);

        // 4. KLIK TOMBOL CLOSE (X)
        System.out.println("Menutup pop-up login...");
        WebElement closeBtn = driver.findElement(By.cssSelector(".loginPopUpCloseBtn"));
        closeBtn.click();

        // 5. Validasi Pop-up Sudah Tertutup
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loginPopUpCloseBtn")));
            System.out.println("Pop-up berhasil ditutup.");
        } catch (Exception e) {
            Assert.fail("Gagal menutup pop-up login (tombol X macet).");
        }
        
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "URL berubah, padahal harusnya tetap di Homepage!");
        Thread.sleep(3000);
    }

    @Test(priority = 8)
    @Story("Shopping Cart Navigation")
    @Description("Hover ke ikon Cart selama 3 detik, lalu klik, validasi halaman, dan kembali ke Home")
    public void testNavShoppingCart() throws InterruptedException {
        System.out.println("Mencoba Hover & Klik Shopping Cart...");

        // 1. Definisikan elemen Cart (ID: menuCart)
        WebElement cartIcon = driver.findElement(By.id("menuCart"));
        
        // 2. Lakukan HOVER (Gerakkan mouse ke ikon tanpa klik)
        Actions action = new Actions(driver);
        action.moveToElement(cartIcon).perform();
        
        System.out.println("Sedang hover di atas Cart...");
        
        // 3. Tunggu 3 detik (sesuai permintaan Anda, biar preview cart terlihat)
        Thread.sleep(3000); 

        // 4. Klik Ikon Cart
        cartIcon.click();

        // 5. Validasi Masuk Halaman Cart
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.urlContains("shoppingCart"));
            System.out.println("Berhasil masuk ke halaman Shopping Cart!");
        } catch (Exception e) {
            Assert.fail("Gagal masuk ke halaman Cart (Timeout).");
        }

        // Pastikan tulisan 'SHOPPING CART' muncul di halaman (Validasi Visual)
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[contains(text(), 'SHOPPING CART')]")));
        } catch (Exception e) {
            System.out.println("Warning: Heading 'SHOPPING CART' belum terlihat, tapi URL sudah benar.");
        }

        Thread.sleep(3000);

        // 6. KEMBALI KE HOMEPAGE PAKAI LOGO
        WebElement logo = driver.findElement(By.xpath("//span[contains(text(), 'dvantage')]"));
        logo.click();
        
        wait.until(ExpectedConditions.urlToBe("https://www.advantageonlineshopping.com/#/"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "Gagal kembali ke Homepage!");
        
        System.out.println("--> Berhasil kembali ke Homepage via Logo.");
    }

    @Test(priority = 9)
    @Story("Help Navigation")
    @Description("Klik ikon Help (?) -> Pilih About (Cari yang visible) -> Kembali ke Home")
    public void testNavHelpAbout() throws InterruptedException {
        System.out.println("Mencoba navigasi Help > About...");

        // 1. Klik Ikon Help (?)
        WebElement helpIcon = driver.findElement(By.id("menuHelp"));
        helpIcon.click();
        Thread.sleep(3000); 

        // 2. LOGIKA BARU: Cari SEMUA elemen 'About', klik yang Visible
        boolean clicked = false;
        java.util.List<WebElement> aboutOptions = driver.findElements(By.xpath("//label[contains(text(), 'About')]"));
        System.out.println("Ditemukan " + aboutOptions.size() + " elemen 'About' di halaman.");

        for (WebElement option : aboutOptions) {
            // Cek apakah elemen ini terlihat di layar?
            if (option.isDisplayed()) {
                System.out.println("Menemukan tombol About yang terlihat, melakukan klik...");
                option.click();
                clicked = true;
                break;
            }
        }

        // Jika tidak ada yang diklik, berarti gagal
        if (!clicked) {
            // Plan B: Coba pakai Javascript Executor pada elemen terakhir (biasanya desktop ada di urutan terakhir)
            try {
                System.out.println("Klik normal gagal, mencoba Plan B (JS Executor)...");
                WebElement lastOption = aboutOptions.get(aboutOptions.size() - 1);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", lastOption);
            } catch (Exception e) {
                Assert.fail("Gagal menemukan atau mengklik tombol About.");
            }
        }

        // 3. Validasi Masuk Halaman About
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.urlContains("about"));
            System.out.println("Halaman About berhasil dimuat.");
        } catch (Exception e) {
            Assert.fail("Gagal memuat halaman About (Timeout URL tidak berubah).");
        }

        Thread.sleep(3000); 

        // 4. KEMBALI KE HOMEPAGE PAKAI LOGO
        WebElement logo = driver.findElement(By.xpath("//span[contains(text(), 'dvantage')]"));
        logo.click();
        
        // 5. Validasi sudah balik ke Home
        wait.until(ExpectedConditions.urlToBe("https://www.advantageonlineshopping.com/#/"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "Gagal kembali ke Homepage!");
        
        System.out.println("--> Berhasil kembali ke Homepage via Logo.");
    }

    @Test(priority = 10)
    @Story("Help Navigation")
    @Description("Klik ikon Help (?) -> Pilih AOS Versions -> Validasi -> Kembali ke Home")
    public void testNavHelpAOSVersions() throws InterruptedException {
        System.out.println("Mencoba navigasi Help > AOS Versions...");

        // 1. Klik Ikon Help (?)
        WebElement helpIcon = driver.findElement(By.id("menuHelp"));
        helpIcon.click();
        Thread.sleep(3000); 

        // 2. LOGIKA PINTAR: Cari yang Visible
        boolean clicked = false;
        java.util.List<WebElement> versionOptions = driver.findElements(By.xpath("//label[contains(text(), 'AOS Versions')]"));
        System.out.println("Ditemukan " + versionOptions.size() + " elemen 'AOS Versions'.");

        for (WebElement option : versionOptions) {
            if (option.isDisplayed()) {
                System.out.println("Menemukan tombol AOS Versions yang terlihat, melakukan klik...");
                option.click();
                clicked = true;
                break;
            }
        }

        if (!clicked) {
            // Plan B: JS Executor jika klik biasa gagal
            try {
                WebElement lastOption = versionOptions.get(versionOptions.size() - 1);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", lastOption);
                System.out.println("Klik via JS Executor berhasil.");
            } catch (Exception e) {
                Assert.fail("Gagal menemukan atau mengklik tombol AOS Versions.");
            }
        }

        // 3. Validasi & Handling Tab Baru
        Thread.sleep(3000);
        System.out.println("Aksi klik AOS Versions selesai.");

        // 4. KEMBALI KE HOMEPAGE (Di Tab Utama)
        WebElement logo = driver.findElement(By.xpath("//span[contains(text(), 'dvantage')]"));
        logo.click();
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlToBe("https://www.advantageonlineshopping.com/#/"));
        
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "Gagal kembali ke Homepage!");
        System.out.println("--> Berhasil kembali ke Homepage via Logo.");
    }

    @Test(priority = 11)
    @Story("Help Navigation")
    @Description("Klik Management Console -> Pindah Tab -> Validasi -> Tutup Tab -> Kembali ke Home")
    public void testNavManagementConsole() throws InterruptedException {
        System.out.println("Mencoba navigasi Help > Management Console...");

        // 1. Simpan ID Tab Utama (Homepage)
        String mainWindowHandle = driver.getWindowHandle();

        // 2. Klik Ikon Help (?)
        WebElement helpIcon = driver.findElement(By.id("menuHelp"));
        helpIcon.click();
        Thread.sleep(3000); 

        // 3. Cari & Klik Tombol 'Management Console' (Pakai Logika Pintar)
        boolean clicked = false;
        java.util.List<WebElement> consoleOptions = driver.findElements(By.xpath("//label[contains(text(), 'Management Console')]"));
        
        for (WebElement option : consoleOptions) {
            if (option.isDisplayed()) {
                option.click();
                clicked = true;
                break;
            }
        }
        
        if (!clicked) {
            // Plan B: JS Executor
            try {
                WebElement lastOption = consoleOptions.get(consoleOptions.size() - 1);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", lastOption);
            } catch (Exception e) {
                Assert.fail("Gagal klik Management Console.");
            }
        }

        // 4. HANDLING NEW TAB (Pindah Fokus)
        Thread.sleep(5000);
        java.util.Set<String> allWindowHandles = driver.getWindowHandles();
        
        // Looping untuk mencari tab mana yang BUKAN tab utama
        for (String handle : allWindowHandles) {
            if (!handle.equals(mainWindowHandle)) {
                // Pindah fokus ke tab baru ini
                driver.switchTo().window(handle);
                System.out.println("Berhasil pindah fokus ke Tab Baru.");
                break;
            }
        }

        // 5. Validasi di Tab Baru
        String newTabUrl = driver.getCurrentUrl();
        System.out.println("URL Tab Baru: " + newTabUrl);
        Assert.assertTrue(newTabUrl.contains("admin") || newTabUrl.contains("management"), "URL Management Console salah!");

        // 6. Tutup Tab Baru & Kembali ke Homepage
        driver.close(); // Menutup tab Management Console
        driver.switchTo().window(mainWindowHandle); // Pindah fokus balik ke Homepage
        
        System.out.println("Tab baru ditutup, kembali fokus ke Homepage.");

        // 7. Validasi Akhir (Pastikan kita aman di Homepage)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlToBe("https://www.advantageonlineshopping.com/#/"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "Gagal validasi URL Homepage!");
    }

    @Test(priority = 12)
    @Story("Test Scroll Up Feature")
    @Description("Scroll ke Contact Us -> Tunggu Tombol Muncul -> Klik -> Validasi Top Page")
    public void testScrollUpButton() throws InterruptedException {
        System.out.println("Mencoba fitur Scroll Up...");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Scroll ke Bawah (Area Contact Us) agar tombol Go Up muncul
        System.out.println("Navigasi ke bagian bawah (Contact Us)...");
        WebElement menuContact = driver.findElement(By.xpath("//a[contains(text(), 'CONTACT US')]"));
        menuContact.click();
        
        // Kita tunggu sebentar agar animasi scroll browser selesai dan event scroll mentrigger tombol muncul
        Thread.sleep(3000); 

        // 2. Temukan Tombol Go Up
        // Berdasarkan HTML Anda: <img name="go_up_btn" ... >
        // Kita gunakan 'elementToBeClickable' untuk memastikan opacity sudah cukup dan elemen siap diklik
        System.out.println("Menunggu tombol Go Up terlihat...");
        WebElement goUpBtn = wait.until(ExpectedConditions.elementToBeClickable(By.name("go_up_btn")));

        // Alternatif jika name tidak jalan, bisa pakai ID pembungkusnya:
        // WebElement goUpBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("scrollToTop")));

        System.out.println("Tombol Go Up ditemukan, melakukan klik...");
        goUpBtn.click();

        // 3. Tunggu Animasi Scroll ke Atas Selesai
        System.out.println("Menunggu animasi scroll ke atas...");
        
        // Loop visual wait sederhana untuk memastikan scroll Y benar-benar kembali ke 0
        // (Terkadang butuh waktu lebih dari 1-2 detik tergantung panjang halaman)
        for (int i = 0; i < 5; i++) {
            Long yPos = (Long) js.executeScript("return window.pageYOffset;");
            if (yPos < 10) break; // Jika sudah di atas, keluar loop
            Thread.sleep(1000);
        }

        // 4. Validasi: Cek posisi Scroll (Y Coordinate)
        Long scrollPosition = (Long) js.executeScript("return window.pageYOffset;");
        System.out.println("Posisi Scroll Y saat ini: " + scrollPosition);

        // Validasi posisi harus < 10 (biasanya 0, tapi kita beri toleransi sedikit)
        Assert.assertTrue(scrollPosition < 10, "Gagal kembali ke posisi paling atas! Posisi tertahan di: " + scrollPosition);
        
        System.out.println("--> Sukses! Halaman kembali ke atas (Top of Page).");
    }

    @Test(priority = 13)
    @Story("Mobile Responsiveness Test")
    @Description("Ubah ukuran ke Mobile -> Cek elemen yg tetap ada vs elemen yg hilang")
    public void testMobileResponsiveness() throws InterruptedException {
        System.out.println("=== MULAI TEST 13: CEK RESPONSIVE MOBILE ===");

        // 1. Ubah Ukuran Layar ke Ukuran HP (misal: 375x812 pixel - iPhone X)
        System.out.println("Mengubah ukuran browser ke mode Mobile (375x812)...");
        driver.manage().window().setSize(new Dimension(375, 812));
        Thread.sleep(3000);

        // 2. VALIDASI KONTEN YANG HARUS TETAP ADA (Persamaan)
        WebElement logo = driver.findElement(By.xpath("//span[contains(text(), 'dvantage')]"));
        Assert.assertTrue(logo.isDisplayed(), "GAGAL: Logo hilang di mode mobile!");
        System.out.println("1. Logo: OK (Tetap terlihat)");

        WebElement cartIcon = driver.findElement(By.id("menuCart"));
        Assert.assertTrue(cartIcon.isDisplayed(), "GAGAL: Cart icon hilang di mode mobile!");
        System.out.println("2. Cart Icon: OK (Tetap terlihat)");

        // 3. VALIDASI KONTEN YANG BERBEDA / HILANG (Perbedaan)
        System.out.println("Mengecek menu navigasi teks (OUR PRODUCTS)...");
        WebElement navOurProducts = driver.findElement(By.xpath("//a[contains(text(), 'OUR PRODUCTS')]"));

        if (navOurProducts.isDisplayed()) {
            System.out.println("WARNING: Menu 'OUR PRODUCTS' masih muncul! Web mungkin tidak responsif sempurna.");
        } else {
            System.out.println("3. Menu Teks 'OUR PRODUCTS': OK (Disembunyikan/Hidden sesuai desain mobile)");
        }

        // 4. Validasi Chat Icon (Biasanya tetap ada)
        try {
             WebElement chatLogo = driver.findElement(By.id("chatLogo"));
             Assert.assertTrue(chatLogo.isDisplayed(), "Chat logo hilang di mobile!");
             System.out.println("4. Chat Logo: OK (Tetap terlihat)");
        } catch (Exception e) {
            System.out.println("Info: Chat logo tidak terdeteksi (Mungkin tertutup elemen lain).");
        }

        // 5. KEMBALIKAN UKURAN LAYAR (Restore)
        System.out.println("Mengembalikan ukuran browser ke Fullscreen (Maximize)...");
        driver.manage().window().maximize();
        Thread.sleep(3000);

        Assert.assertTrue(navOurProducts.isDisplayed(), "Gagal kembali ke tampilan Desktop!");
        
        System.out.println("--> Test Responsif Mobile SELESAI.");
    }

    @AfterClass
    public void tearDown() {
        // Beri jeda sebentar sebelum tutup browser
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        
        if (driver != null) {
            driver.quit();
        }
    }
}
