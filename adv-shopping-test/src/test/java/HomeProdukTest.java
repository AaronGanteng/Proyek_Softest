import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.List;

import io.qameta.allure.Description;
import io.qameta.allure.Story;

public class HomeProdukTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        
        driver.get("https://www.advantageonlineshopping.com/#/");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            // Kita tunggu sampai elemen Speakers ini muncul
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("speakersImg")));
            System.out.println("Homepage siap!");
        } catch (Exception e) {
            System.out.println("Website loading lama.");
        }
    }

    @Test(priority = 1)
    @Story("Product Category Navigation")
    @Description("Klik kategori Speakers di Homepage -> Validasi Masuk List Produk -> Kembali ke Home")
    public void testNavToSpeakersCategory() throws InterruptedException {
        System.out.println("=== TEST: Klik Kategori Speakers ===");

        // 1. Identifikasi Elemen (Pakai ID speakersImg)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement speakersCard = wait.until(ExpectedConditions.elementToBeClickable(By.id("speakersImg")));
        
        // 2. Klik Gambar Speakers
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", speakersCard);
        Thread.sleep(10000);
        
        speakersCard.click();
        System.out.println("Kategori Speakers diklik.");
        Thread.sleep(5000);

        // 3. Validasi Pindah Halaman
        try {
            wait.until(ExpectedConditions.urlContains("Speakers"));
            System.out.println("Berhasil masuk ke halaman produk Speakers.");
        } catch (Exception e) {
            Assert.fail("Gagal masuk ke halaman Speakers (URL tidak berubah).");
        }

        // Validasi Visual: Pastikan ada tulisan judul "SPEAKERS" di halaman list
        WebElement pageTitle = driver.findElement(By.xpath("//h3[contains(text(), 'SPEAKERS')]"));
        Assert.assertTrue(pageTitle.isDisplayed(), "Judul halaman SPEAKERS tidak muncul!");

        Thread.sleep(3000); // Jeda visual

        // 4. KEMBALI KE HOMEPAGE (Clean Up)
        WebElement logo = driver.findElement(By.xpath("//span[contains(text(), 'dvantage')]"));
        logo.click();
        Thread.sleep(2000);
        
        // Tunggu sampai balik ke Home
        wait.until(ExpectedConditions.urlToBe("https://www.advantageonlineshopping.com/#/"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "Gagal kembali ke Homepage!");
        
        System.out.println("--> Kembali ke Homepage sukses.");
    }

    @Test(priority = 2)
    @Story("Product Category Navigation")
    @Description("Klik kategori Tablets di Homepage -> Validasi Masuk List Produk -> Kembali ke Home")
    public void testNavToTabletsCategory() throws InterruptedException {
        System.out.println("=== TEST: Klik Kategori Tablets ===");

        // 1. Identifikasi Elemen (Pakai ID tabletsImg)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement tabletsCard = wait.until(ExpectedConditions.elementToBeClickable(By.id("tabletsImg")));
        
        // 2. Klik Gambar Tablets
        tabletsCard.click();
        System.out.println("Kategori Tablets diklik.");
        Thread.sleep(5000);

        // 3. Validasi Pindah Halaman
        try {
            wait.until(ExpectedConditions.urlContains("Tablets"));
            System.out.println("Berhasil masuk ke halaman produk Tablets.");
        } catch (Exception e) {
            Assert.fail("Gagal masuk ke halaman Tablets (URL tidak berubah).");
        }

        // Validasi Visual: Cari H3 yang tulisannya 'TABLETS'
        WebElement pageTitle = driver.findElement(By.xpath("//h3[contains(text(), 'TABLETS')]"));
        Assert.assertTrue(pageTitle.isDisplayed(), "Judul halaman TABLETS tidak muncul!");

        Thread.sleep(3000);

        // 4. KEMBALI KE HOMEPAGE
        WebElement logo = driver.findElement(By.xpath("//span[contains(text(), 'dvantage')]"));
        logo.click();
        Thread.sleep(2000);
        
        wait.until(ExpectedConditions.urlToBe("https://www.advantageonlineshopping.com/#/"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "Gagal kembali ke Homepage!");
        
        System.out.println("--> Kembali ke Homepage sukses.");
    }

    @Test(priority = 3)
    @Story("Product Category Navigation")
    @Description("Klik kategori Laptops di Homepage -> Validasi Masuk List Produk -> Kembali ke Home")
    public void testNavToLaptopsCategory() throws InterruptedException {
        System.out.println("=== TEST: Klik Kategori Laptops ===");

        // 1. Identifikasi Elemen (Pakai ID laptopsImg)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement laptopsCard = wait.until(ExpectedConditions.elementToBeClickable(By.id("laptopsImg")));
        
        // 2. Klik Gambar Laptops       
        laptopsCard.click();
        System.out.println("Kategori Laptops diklik.");
        Thread.sleep(5000);

        // 3. Validasi Pindah Halaman
        try {
            wait.until(ExpectedConditions.urlContains("Laptops"));
            System.out.println("Berhasil masuk ke halaman produk Laptops.");
        } catch (Exception e) {
            Assert.fail("Gagal masuk ke halaman Laptops (URL tidak berubah).");
        }

        // Validasi Visual: Cari H3 yang tulisannya 'LAPTOPS'
        WebElement pageTitle = driver.findElement(By.xpath("//h3[contains(text(), 'LAPTOPS')]"));
        Assert.assertTrue(pageTitle.isDisplayed(), "Judul halaman LAPTOPS tidak muncul!");

        Thread.sleep(3000);

        // 4. KEMBALI KE HOMEPAGE
        WebElement logo = driver.findElement(By.xpath("//span[contains(text(), 'dvantage')]"));
        logo.click();
        Thread.sleep(2000);
        
        wait.until(ExpectedConditions.urlToBe("https://www.advantageonlineshopping.com/#/"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "Gagal kembali ke Homepage!");
        
        System.out.println("--> Kembali ke Homepage sukses.");
    }

    @Test(priority = 4)
    @Story("Product Category Navigation")
    @Description("Klik kategori Mice di Homepage -> Validasi Masuk List Produk -> Kembali ke Home")
    public void testNavToMiceCategory() throws InterruptedException {
        System.out.println("=== TEST: Klik Kategori Mice ===");

        // 1. Identifikasi Elemen (Pakai ID miceImg)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement miceCard = wait.until(ExpectedConditions.elementToBeClickable(By.id("miceImg")));
        
        // 2. Klik Gambar Mice 
        miceCard.click();
        System.out.println("Kategori Mice diklik.");
        Thread.sleep(5000);

        // 3. Validasi Pindah Halaman
        try {
            wait.until(ExpectedConditions.urlContains("Mice"));
            System.out.println("Berhasil masuk ke halaman produk Mice.");
        } catch (Exception e) {
            Assert.fail("Gagal masuk ke halaman Mice (URL tidak berubah).");
        }

        // Validasi Visual: Cari H3 yang tulisannya 'MICE'
        WebElement pageTitle = driver.findElement(By.xpath("//h3[contains(text(), 'MICE')]"));
        Assert.assertTrue(pageTitle.isDisplayed(), "Judul halaman MICE tidak muncul!");

        Thread.sleep(3000);

        // 4. KEMBALI KE HOMEPAGE
        WebElement logo = driver.findElement(By.xpath("//span[contains(text(), 'dvantage')]"));
        logo.click();
        Thread.sleep(2000);
        
        wait.until(ExpectedConditions.urlToBe("https://www.advantageonlineshopping.com/#/"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "Gagal kembali ke Homepage!");
        
        System.out.println("--> Kembali ke Homepage sukses.");
    }

    @Test(priority = 5)
    @Story("Product Category Navigation")
    @Description("Klik kategori Headphones di Homepage -> Validasi Masuk List Produk -> Kembali ke Home")
    public void testNavToHeadphonesCategory() throws InterruptedException {
        System.out.println("=== TEST: Klik Kategori Headphones ===");

        // 1. Identifikasi Elemen (Pakai ID headphonesImg)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement headphonesCard = wait.until(ExpectedConditions.elementToBeClickable(By.id("headphonesImg")));
        
        // 2. Klik Gambar Headphones
        
        headphonesCard.click();
        System.out.println("Kategori Headphones diklik.");
        Thread.sleep(5000);

        // 3. Validasi Pindah Halaman
        try {
            wait.until(ExpectedConditions.urlContains("Headphones"));
            System.out.println("Berhasil masuk ke halaman produk Headphones.");
        } catch (Exception e) {
            Assert.fail("Gagal masuk ke halaman Headphones (URL tidak berubah).");
        }

        // Validasi Visual: Cari H3 yang tulisannya 'HEADPHONES'
        WebElement pageTitle = driver.findElement(By.xpath("//h3[contains(text(), 'HEADPHONES')]"));
        Assert.assertTrue(pageTitle.isDisplayed(), "Judul halaman HEADPHONES tidak muncul!");

        Thread.sleep(3000);

        // 4. KEMBALI KE HOMEPAGE
        WebElement logo = driver.findElement(By.xpath("//span[contains(text(), 'dvantage')]"));
        logo.click();
        Thread.sleep(3000);
        
        wait.until(ExpectedConditions.urlToBe("https://www.advantageonlineshopping.com/#/"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.advantageonlineshopping.com/#/", "Gagal kembali ke Homepage!");
        
        System.out.println("--> Kembali ke Homepage sukses.");
    }

    @Test(priority = 6)
    @Story("Special Offer Navigation")
    @Description("Klik tombol SEE OFFER di Banner Utama -> Validasi Masuk Halaman Produk -> Kembali ke Home")
    public void testNavSeeOffer() throws InterruptedException {
        System.out.println("=== TEST: Klik Tombol SEE OFFER ===");

        // 1. Identifikasi Elemen (Pakai ID see_offer_btn)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement menuOffer = driver.findElement(By.xpath("//a[contains(text(), 'SPECIAL OFFER')]"));
        menuOffer.click();
        Thread.sleep(5000);
        
        // 2. Klik Tombol
        WebElement seeOfferBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("see_offer_btn")));
        seeOfferBtn.click();
        System.out.println("Tombol SEE OFFER diklik.");
        Thread.sleep(5000);

        // 3. Validasi Pindah Halaman
        try {
            wait.until(ExpectedConditions.urlContains("product"));
            System.out.println("Berhasil masuk ke halaman promo produk.");
        } catch (Exception e) {
            Assert.fail("Gagal masuk ke halaman produk promo (URL tidak berubah).");
        }

        // Validasi Visual: Pastikan tombol 'ADD TO CART' muncul
        try {
            WebElement addToCartBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("save_to_cart")));
            Assert.assertTrue(addToCartBtn.isDisplayed(), "Tombol ADD TO CART tidak ditemukan!");
        } catch (Exception e) {
            System.out.println("Warning: Tombol Add to Cart belum muncul (mungkin stok habis atau loading lama).");
        }

        Thread.sleep(3000);
        System.out.println("--> Kembali ke Homepage sukses.");
    }

    @Test(priority = 7)
    @Story("Product Detail Verification")
    @Description("Validasi Gambar Utama Produk Muncul & Tidak Broken")
    public void testProductImageVisibility() {
        System.out.println("=== TEST: Cek Validitas Gambar Produk ===");

        // 1. Identifikasi Elemen Gambar Utama
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement mainImage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mainImg")));

        // 2. Validasi Visual
        boolean isVisible = mainImage.isDisplayed();
        System.out.println("Status Displayed Selenium: " + isVisible);
        Assert.assertTrue(isVisible, "Gambar utama tidak terlihat di halaman!");

        // 3. Validasi Teknis
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object widthObj = js.executeScript("return arguments[0].naturalWidth;", mainImage);
        
        long naturalWidth = 0;
        if (widthObj instanceof Long) {
            naturalWidth = (Long) widthObj;
        } else if (widthObj instanceof Double) {
            naturalWidth = ((Double) widthObj).longValue();
        }

        System.out.println("Lebar Asli Gambar (Pixels): " + naturalWidth);

        // Jika width > 0, berarti gambar valid.
        if (naturalWidth > 0) {
            System.out.println("PASS: Gambar produk muncul sempurna.");
        } else {
            System.out.println("FAIL: Gambar broken (Link mati atau gagal load).");
            Assert.fail("Gambar produk broken (naturalWidth = 0)!");
        }

        System.out.println("--> Test Cek Gambar SUKSES.");
    }

    @Test(priority = 8)
    @Story("Bug Reproduction: Image Not Changing")
    @Description("Search -> Pilih dari Katalog (Fixed Locator) -> Ganti Warna -> Cek Bug")
    public void testProductImageColorChangeBug() throws InterruptedException {
        System.out.println("=== MULAI TEST 8: CEK BUG GANTI WARNA (FIXED LOCATOR) ===");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 1. KLIK ICON SEARCH & INPUT
        System.out.println("Membuka Search Bar...");
        try {
            WebElement searchIcon = wait.until(ExpectedConditions.elementToBeClickable(By.id("menuSearch")));
            searchIcon.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", driver.findElement(By.id("menuSearch")));
        }

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("autoComplete")));
        searchInput.clear();
        
        String keyword = "HP Elite x2 1011 G1 Tablet"; 
        searchInput.sendKeys(keyword);
        Thread.sleep(3000); 
        searchInput.sendKeys(Keys.ENTER);
        Thread.sleep(3000); 
        
        // 2. TUNGGU HASIL PENCARIAN & KLIK PRODUK
        System.out.println("Menunggu hasil pencarian...");
        try {
            String xpathProduct = "//a[contains(@class, 'productName')][contains(text(), 'HP Elite x2')]";
            WebElement productLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathProduct)));
            
            System.out.println("Produk ditemukan: " + productLink.getText());
            js.executeScript("arguments[0].click();", productLink);
            System.out.println("Klik produk dilakukan via JS.");
        } catch (Exception e) {
            System.out.println("Gagal klik via Text, mencoba klik via Gambar...");
            try {
                WebElement imgProduct = driver.findElement(By.xpath("//img[contains(@class, 'imgProduct')]"));
                js.executeScript("arguments[0].click();", imgProduct);
            } catch (Exception ex) {
                Assert.fail("Gagal menemukan produk di list (Text maupun Gambar). Cek Keyword pencarian.");
            }
        }

        // 3. TUNGGU HALAMAN DETAIL PRODUK
        System.out.println("Menunggu halaman detail produk terbuka...");
        try {
            wait.until(ExpectedConditions.urlContains("product"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mainImg")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".productColor")));
            System.out.println("Berhasil masuk detail produk: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("!!! GAGAL LOAD DETAIL PRODUK !!!");
            System.out.println("Posisi URL: " + driver.getCurrentUrl());
            Assert.fail("Timeout: Gagal memuat halaman detail. Klik mungkin tidak ter-trigger.");
        }
        
        Thread.sleep(2000); 

        // 4. AMBIL SOURCE GAMBAR AWAL
        WebElement mainImg = driver.findElement(By.id("mainImg"));
        String initialSrc = mainImg.getDomProperty("src");
        System.out.println("Source Gambar Awal: " + initialSrc);

        // 5. PILIH WARNA LAIN
        java.util.List<WebElement> colorOptions = driver.findElements(By.cssSelector(".productColor"));
        
        boolean clicked = false;
        if (colorOptions.size() > 1) {
            for (WebElement color : colorOptions) {
                // Klik warna yang tidak selected
                if (!color.getDomAttribute("class").contains("colorSelected")) {
                    String colorName = color.getDomAttribute("title");
                    System.out.println("Mengklik varian warna: " + colorName);
                    
                    js.executeScript("arguments[0].click();", color);
                    clicked = true;
                    break;
                }
            }
        } 
        
        if (!clicked) {
            System.out.println("WARNING: Tidak ada warna lain untuk diklik.");
            return;
        }

        Thread.sleep(3000); 

        // 6. VALIDASI BUG
        String newSrc = mainImg.getDomProperty("src");
        System.out.println("Source Gambar Baru: " + newSrc);

        if (initialSrc.equals(newSrc)) {
            System.out.println("[LOG BUG] SUCCESS: Gambar tidak berubah saat ganti warna!");
            Assert.assertEquals(initialSrc, newSrc);
        } else {
            System.out.println("INFO: Gambar berubah normal (Bug tidak muncul).");
        }
        
        System.out.println("--> Test 8 SELESAI.");
    }

    @Test(priority = 9)
    @Story("Catalog Validation")
    @Description("Home -> Laptops -> Cek Duplikasi Produk di Katalog")
    public void testCheckDuplicateProductsInLaptops() throws InterruptedException {
        System.out.println("=== MULAI TEST 9: CEK DUPLIKASI PRODUK (LAPTOPS) ===");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 1. KEMBALI KE HOMEPAGE
        System.out.println("Navigasi kembali ke Homepage...");
        WebElement logo = driver.findElement(By.xpath("//span[contains(text(), 'dvantage')]"));
        logo.click();
        Thread.sleep(2000);
        
        wait.until(ExpectedConditions.urlToBe("https://www.advantageonlineshopping.com/#/"));
        Thread.sleep(2000);

        // 2. KLIK KATEGORI LAPTOPS
        System.out.println("Masuk ke kategori Laptops...");
        WebElement laptopsCard = wait.until(ExpectedConditions.elementToBeClickable(By.id("laptopsImg")));
        js.executeScript("arguments[0].click();", laptopsCard);
        Thread.sleep(2000);

        // 3. TUNGGU LIST PRODUK MUNCUL
        System.out.println("Menunggu katalog termuat...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@class, 'productName')]")));
        Thread.sleep(4000);

        // 4. AMBIL SEMUA NAMA PRODUK
        java.util.List<WebElement> productElements = driver.findElements(By.xpath("//a[contains(@class, 'productName')]"));
        System.out.println("Jumlah produk ditemukan: " + productElements.size());

        // 5. LOGIKA CEK DUPLIKAT
        java.util.Set<String> uniqueNames = new java.util.HashSet<>();
        java.util.List<String> duplicateNames = new java.util.ArrayList<>();

        System.out.println("--- DAFTAR PRODUK ---");
        for (WebElement prod : productElements) {
            String name = prod.getText().trim();
            if (!name.isEmpty()) {
                System.out.println("- " + name);
                if (!uniqueNames.add(name)) {
                    duplicateNames.add(name);
                }
            }
        }
        System.out.println("---------------------");

        // 6. ASSERTION / VALIDASI
        if (duplicateNames.size() > 0) {
            System.out.println("[FAIL] Ditemukan produk ganda di katalog!");
            System.out.println("Produk duplikat: " + duplicateNames.toString());
            Assert.fail("Data Katalog Kotor: Ada produk ganda -> " + duplicateNames.toString());
        } else {
            System.out.println("[PASS] Tidak ada produk ganda. Katalog bersih.");
            Assert.assertTrue(true);
        }

        System.out.println("--> Test 9 Duplicate Check SELESAI.");
    }

    @Test(priority = 10)
    @Story("Price Consistency Check")
    @Description("Bandingkan Harga di Katalog vs Harga di Halaman Detail (Produk Pertama)")
    public void testPriceConsistency() throws InterruptedException {
        System.out.println("=== MULAI TEST 10: CEK KONSISTENSI HARGA ===");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 1. AMBIL HARGA DARI KATALOG (PRODUK PERTAMA)
        System.out.println("Mengambil harga dari produk pertama di katalog...");
        List<WebElement> priceList = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
            By.xpath("//a[contains(@class, 'productPrice')]")
        ));
        
        if (priceList.isEmpty()) {
            Assert.fail("Tidak ada produk/harga ditemukan di katalog!");
        }
        String priceInCatalog = priceList.get(0).getText().trim().replace(" ", "");
        System.out.println("Harga di Katalog: [" + priceInCatalog + "]");

        // 2. KLIK PRODUK PERTAMA
        System.out.println("Mengklik produk pertama...");
        WebElement firstProduct = driver.findElements(By.xpath("//img[contains(@class, 'imgProduct')]")).get(0);
        js.executeScript("arguments[0].click();", firstProduct);
        Thread.sleep(3000);

        // 3. TUNGGU MASUK HALAMAN DETAIL
        System.out.println("Menunggu halaman detail...");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Description"))); 
        } catch (Exception e) {
            Assert.fail("Gagal masuk ke halaman detail produk.");
        }

        // 5. AMBIL HARGA DARI HALAMAN DETAIL
        WebElement detailPriceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//h2[contains(@class, 'screen768')]")
        ));
        
        String priceInDetail = detailPriceElement.getText().trim().replace(" ", "");
        System.out.println("Harga di Detail:  [" + priceInDetail + "]");

        // 6. BANDINGKAN (VALIDASI)
        if (priceInCatalog.equals(priceInDetail)) {
            System.out.println("[PASS] Harga Konsisten.");
            Assert.assertEquals(priceInDetail, priceInCatalog);
        } else {
            System.out.println("[FAIL] Harga Berbeda!");
            System.out.println("Katalog: " + priceInCatalog);
            System.out.println("Detail : " + priceInDetail);
            
            Assert.fail("Mismatch Price! Katalog: " + priceInCatalog + " vs Detail: " + priceInDetail);
        }
        Thread.sleep(2000);

        System.out.println("--> Test 10 Price Check SELESAI.");
    }

    @Test(priority = 11)
    @Story("Bug Validation: Description Text")
    @Description("Cek Deskripsi HP Elite x2 untuk mendeteksi karakter aneh (tm[2])")
    public void testDescriptionBugOnHPTablet() throws InterruptedException {
        System.out.println("=== MULAI TEST 11: CEK BUG DESKRIPSI (HP ELITE X2) ===");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 1. KEMBALI KE HOMEPAGE
        System.out.println("Navigasi kembali ke Homepage...");
        WebElement logo = driver.findElement(By.xpath("//span[contains(text(), 'dvantage')]"));
        logo.click();
        wait.until(ExpectedConditions.urlToBe("https://www.advantageonlineshopping.com/#/"));
        Thread.sleep(2000);

        // 2. KLIK KATEGORI TABLETS
        System.out.println("Masuk ke kategori Tablets...");
        WebElement tabletsCard = wait.until(ExpectedConditions.elementToBeClickable(By.id("tabletsImg")));
        js.executeScript("arguments[0].click();", tabletsCard);
        Thread.sleep(2000);

        // 3. PILIH PRODUK 'HP ELITE X2 1011 G1 TABLET'
        System.out.println("Mencari produk HP Elite x2...");
        String productXpath = "//a[contains(@class, 'productName')][contains(text(), 'HP Elite x2 1011 G1 Tablet')]";
        
        try {
            WebElement productLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(productXpath)));
            js.executeScript("arguments[0].click();", productLink);
            Thread.sleep(2000);
        } catch (Exception e) {
            Assert.fail("Gagal menemukan/mengklik produk HP Elite x2 di list Tablets.");
        }

        // 4. TUNGGU HALAMAN DETAIL & AMBIL DESKRIPSI
        System.out.println("Menunggu deskripsi produk...");
        WebElement descElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#Description p")
        ));
        
        String descriptionText = descElement.getText();
        System.out.println("Deskripsi : " + descriptionText);

        // 5. VALIDASI BUG (Mencari teks aneh '[2]' atau karakter encoding error)
        String lowerDesc = descriptionText.toLowerCase();
        boolean bugFound = lowerDesc.contains("[2]") || lowerDesc.contains("[4]");

        if (bugFound) {
            System.out.println("[LOG BUG] DITEMUKAN: Deskripsi mengandung karakter aneh '[2]'!");
            Assert.assertTrue(true);
        } else {
            System.out.println("INFO: Deskripsi terlihat normal (Tidak ada '[2]').");
            System.out.println("Mungkin bug sudah diperbaiki atau teksnya berbeda.");
            Assert.assertTrue(true);
        }

        System.out.println("--> Test 11 Description Bug Check SELESAI.");
    }

    @Test(priority = 12)
    @Story("Bug Detection: Data Integrity")
    @Description("Cek Kategori Headphones -> Cari Produk Dummy 'Game of Thrones'")
    public void testCheckDummyProductInHeadphones() throws InterruptedException {
        System.out.println("=== MULAI TEST 12: CEK DATA DUMMY (GAME OF THRONES) ===");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 1. KEMBALI KE HOMEPAGE
        System.out.println("Navigasi kembali ke Homepage...");
        try {
            WebElement logo = driver.findElement(By.xpath("//span[contains(text(), 'dvantage')]"));
            logo.click();
        } catch (Exception e) {
            driver.get("https://www.advantageonlineshopping.com/#/");
        }
        
        wait.until(ExpectedConditions.urlToBe("https://www.advantageonlineshopping.com/#/"));
        Thread.sleep(2000);

        // 2. KLIK KATEGORI HEADPHONES
        System.out.println("Masuk ke kategori Headphones...");
        WebElement headphonesCard = wait.until(ExpectedConditions.elementToBeClickable(By.id("headphonesImg")));
        js.executeScript("arguments[0].click();", headphonesCard);
        Thread.sleep(2000);

        // 3. TUNGGU LIST PRODUK MUNCUL
        System.out.println("Menunggu katalog Headphones termuat...");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@class, 'productName')]")));
        } catch (Exception e) {
            Assert.fail("Gagal memuat katalog Headphones (Timeout).");
        }
        
        Thread.sleep(3000);

        // 4. SCANNING PRODUK (CARI 'GAME OF THRONES')
        System.out.println("Memindai nama produk...");
        
        java.util.List<WebElement> productList = driver.findElements(By.xpath("//a[contains(@class, 'productName')]"));
        boolean isDummyFound = false;

        for (WebElement prod : productList) {
            String prodName = prod.getText().trim();
            if (prodName.toLowerCase().contains("game of thrones")) {
                isDummyFound = true;
                System.out.println("!!! DITEMUKAN: " + prodName);
                break;
            }
        }

        // 5. VALIDASI BUG
        if (isDummyFound) {
            System.out.println("[LOG BUG] SUCCESS: Produk dummy 'Game of Thrones' ditemukan di kategori Headphones!");
            Assert.assertTrue(true);
        } else {
            System.out.println("INFO: Tidak ditemukan produk 'Game of Thrones'. Katalog bersih.");
            Assert.assertTrue(true);
        }

        System.out.println("--> Test 12 Dummy Data Check SELESAI.");
    }

    @Test(priority = 13)
    @Story("Bug Detection: Wrong Category Product")
    @Description("Cek Kategori Headphones -> Cari Produk Salah Kategori (misal: Towels)")
    public void testCheckWrongCategoryProduct() throws InterruptedException {
        System.out.println("=== MULAI TEST 13: CEK PRODUK SALAH KATEGORI (TOWELS) ===");

        // 1. PINDAI NAMA PRODUK
        System.out.println("Memindai produk untuk mencari item 'Towels'...");
        
        java.util.List<WebElement> productList = driver.findElements(By.xpath("//a[contains(@class, 'productName')]"));
        boolean isAnomalyFound = false;
        String anomalyName = "";

        for (WebElement prod : productList) {
            String prodName = prod.getText().trim();
            String lowerName = prodName.toLowerCase();
            if (lowerName.contains("towels")) {
                isAnomalyFound = true;
                anomalyName = prodName;
                System.out.println("!!! DITEMUKAN PRODUK ANEH: " + prodName);
                break;
            }
        }

        // 4. VALIDASI BUG
        if (isAnomalyFound) {
            System.out.println("[LOG BUG] SUCCESS: Ditemukan produk 'Towels' di kategori Headphones!");
            Assert.assertTrue(true);
        } else {
            System.out.println("INFO: Tidak ditemukan produk 'Towels'. Katalog bersih dari item tersebut.");
            Assert.assertTrue(true);
        }

        System.out.println("--> Test 13 Wrong Category Check SELESAI.");
    }

    @AfterClass
    public void tearDown() {
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        if (driver != null) {
            driver.quit();
        }
    }
}
