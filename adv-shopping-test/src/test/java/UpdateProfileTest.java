import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.UUID;

public class UpdateProfileTest extends BaseTest {

    @Test
    public void updateNameTiming() {
        final String HOME = "https://www.advantageonlineshopping.com";
        final String USER = "testingone";
        final String PASS = "Testing1";

        long duration = -1;
        String conclusion = "UNKNOWN";

        try {
            // Login first (reuse pattern from LoginTest)
            driver.get(HOME);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.elementToBeClickable(By.id("menuUser"))).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            driver.findElement(By.name("username")).clear();
            driver.findElement(By.name("username")).sendKeys(USER);
            driver.findElement(By.name("password")).clear();
            driver.findElement(By.name("password")).sendKeys(PASS);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.loader")));
            wait.until(ExpectedConditions.elementToBeClickable(By.id("sign_in_btn")));
            driver.findElement(By.id("sign_in_btn")).click();
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("menuUserLink"), USER));

            // Navigate to My Account - try menu link first, else direct URL
            try {
                // open user menu and click My account link if present
                driver.findElement(By.id("menuUser")).click();
                WebElement acct = null;
                try {
                    acct = driver.findElement(By.xpath(
                            "//a[contains(., 'My account') or contains(., 'My Account') or contains(@href,'/user') or contains(@href,'account') ]"));
                } catch (Exception ignored) {
                }
                if (acct != null) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", acct);
                } else {
                    driver.get(HOME + "/#/accountDetails");
                }
            } catch (Exception ignored) {
                driver.get(HOME + "/#/accountDetails");
            }

            // Prepare a new first name
            String newFirst = "Updated" + UUID.randomUUID().toString().substring(0, 6);

            // Perform update and measure time until success message appears
            duration = measureExecutionTime(() -> {
                // attempt to set first name using several selectors
                try {
                    setIfPresent(By.name("first_nameRegisterPage"), newFirst);
                } catch (Exception ignored) {
                }
                try {
                    setIfPresent(By.name("firstName"), newFirst);
                } catch (Exception ignored) {
                }
                try {
                    setIfPresent(By.cssSelector("input[placeholder*='First']"), newFirst);
                } catch (Exception ignored) {
                }

                // If native fields not found, try JS to set by label text
                try {
                    String js = """
                              (function(nameVal){
                                var labels = Array.from(document.querySelectorAll('label'));
                                labels.forEach(function(l){
                                  if(l.innerText && l.innerText.toLowerCase().indexOf('first')!==-1){
                                    var id=l.getAttribute('for');
                                    var el = id ? document.getElementById(id) : (l.nextElementSibling && l.nextElementSibling.querySelector ? l.nextElementSibling.querySelector('input') : null);
                                    if(el){ el.focus(); el.value = nameVal; el.dispatchEvent(new Event('input',{bubbles:true})); el.dispatchEvent(new Event('change',{bubbles:true})); }
                                  }
                                });
                                return true;
                              })(arguments[0]);
                            """;
                    ((JavascriptExecutor) driver).executeScript(js, newFirst);
                } catch (Exception ignored) {
                }

                // click Save - try several locators
                // capture candidate buttons/links for debugging
                try {
                    String listJs = """
                                return JSON.stringify(Array.from(document.querySelectorAll('button, input[type=button], input[type=submit], a')).map(function(e){
                                    return { tag: e.tagName, text: (e.innerText||e.value||e.getAttribute('aria-label')||e.title||'').toString().trim(), disabled: !!e.disabled };
                                }));
                            """;
                    Object list = ((JavascriptExecutor) driver).executeScript(listJs);
                    if (list != null) {
                        try {
                            Files.write(Paths.get("target/account-buttons.json"), list.toString().getBytes());
                        } catch (Exception ignored) {
                        }
                    }
                } catch (Exception ignored) {
                }

                boolean clicked = false;
                try {
                    WebElement saveBtn = driver.findElement(By.xpath(
                            "//button[normalize-space(.)='SAVE' or normalize-space(.)='Save' or contains(.,'SAVE') or contains(.,'Save')]"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveBtn);
                    clicked = true;
                } catch (Exception ignored) {
                }
                if (!clicked) {
                    try {
                        WebElement in = driver
                                .findElement(By.cssSelector("input[type='submit'], input[type='button']"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", in);
                        clicked = true;
                    } catch (Exception ignored) {
                    }
                }
                if (!clicked) {
                    try {
                        driver.findElement(By.id("save_btn")).click();
                        clicked = true;
                    } catch (Exception ignored) {
                    }
                }
                if (!clicked) {
                    // try aggressive JS fallback: find buttons/inputs with text/value 'SAVE',
                    // enable and click
                    try {
                        String jsClick = """
                                    (function(){
                                        var els = Array.from(document.querySelectorAll('*'));
                                        for(var i=0;i<els.length;i++){
                                            var e = els[i];
                                            var txt = (e.innerText||e.value||e.getAttribute('aria-label')||e.title||'').toString().trim().toUpperCase();
                                            if(txt.indexOf('SAVE')!==-1){
                                                // try to find clickable ancestor
                                                var candidate = e.closest('button') || e.closest('a') || e.closest('input') || e;
                                                try{ candidate.removeAttribute && candidate.removeAttribute('disabled'); candidate.scrollIntoView({behavior:'auto',block:'center'}); candidate.click(); return true; }catch(err){}
                                                try{ e.dispatchEvent && e.dispatchEvent(new MouseEvent('click',{bubbles:true})); return true; }catch(err){}
                                            }
                                        }
                                        return false;
                                    })();
                                """;
                        Object res = ((JavascriptExecutor) driver).executeScript(jsClick);
                        if (Boolean.TRUE.equals(res) || (res != null && res.toString().equalsIgnoreCase("true"))) {
                            clicked = true;
                        }
                    } catch (Exception ignored) {
                    }
                    // if still not clicked, try submitting any form on the page as a last resort
                    if (!clicked) {
                        try {
                            String jsSubmit = """
                                      (function(){
                                        var f = document.querySelector('form');
                                        if(!f){ var fs = document.querySelectorAll('form'); f = fs.length>0?fs[0]:null; }
                                        if(f){ try{ f.dispatchEvent(new Event('submit',{bubbles:true})); }catch(e){} try{ f.submit(); }catch(e){} return true; }
                                        return false;
                                      })();
                                    """;
                            Object r2 = ((JavascriptExecutor) driver).executeScript(jsSubmit);
                            if (Boolean.TRUE.equals(r2) || (r2 != null && r2.toString().equalsIgnoreCase("true"))) {
                                clicked = true;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                if (!clicked) {
                    // capture page HTML and screenshot to target/ for debugging
                    try {
                        String dom = (String) ((JavascriptExecutor) driver)
                                .executeScript("return document.documentElement.outerHTML;");
                        Files.write(Paths.get("target/account-page.html"), dom.getBytes());
                    } catch (Exception ignored) {
                    }
                    try {
                        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                        Files.copy(src.toPath(), Paths.get("target/account-page.png"),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception ignored) {
                    }
                    throw new RuntimeException("Save button not found/clickable");
                }

                // poll for success indicator
                long start = System.currentTimeMillis();
                long timeout = 8000; // allow up to 8s to observe success indicator
                boolean ok = false;
                while (System.currentTimeMillis() - start < timeout) {
                    try {
                        // common success selectors/texts
                        WebElement s = null;
                        try {
                            s = driver.findElement(By.cssSelector(".success, .toast-success, .alert-success"));
                        } catch (Exception ignored) {
                        }
                        if (s != null && s.isDisplayed() && s.getText() != null
                                && (s.getText().toLowerCase().contains("success")
                                        || s.getText().toLowerCase().contains("updated")
                                        || s.getText().toLowerCase().contains("saved"))) {
                            ok = true;
                            break;
                        }

                        // general text search for 'updated' or 'saved' anywhere
                        try {
                            WebElement contain = driver.findElement(By.xpath(
                                    "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'updated') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'saved') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'success')]"));
                            if (contain != null && contain.isDisplayed()) {
                                ok = true;
                                break;
                            }
                        } catch (Exception ignored) {
                        }
                    } catch (Exception ignored) {
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {
                    }
                }

                if (!ok)
                    throw new RuntimeException("Profile update success not observed within timeout");
            });

            Reporter.log("Profile update time: " + duration + " ms");
            try {
                Assert.assertTrue(duration < 2000, "Profile update should be < 2000ms but was " + duration + "ms");
                conclusion = "PASS - update time " + duration + " ms";
                Reporter.log(conclusion);
                System.out.println(conclusion);
            } catch (AssertionError ae) {
                conclusion = "FAIL - update time " + duration + " ms";
                Reporter.log(conclusion);
                System.out.println(conclusion);
                throw ae;
            }

        } catch (Throwable t) {
            conclusion = "ERROR - " + t.getClass().getSimpleName() + ": " + t.getMessage() + " (measured " + duration
                    + " ms)";
            Reporter.log(conclusion);
            System.out.println(conclusion);
            throw new RuntimeException(t);
        } finally {
            Reporter.log("Conclusion: " + conclusion);
            System.out.println("Conclusion: " + conclusion);
        }
    }

    private WebElement waitClickable(By by, Duration timeout) {
        return new WebDriverWait(driver, timeout).until(ExpectedConditions.elementToBeClickable(by));
    }

    private void setIfPresent(By by, String value) {
        try {
            WebElement el = driver.findElement(by);
            el.clear();
            el.sendKeys(value);
        } catch (Exception ignored) {
        }
    }
}
