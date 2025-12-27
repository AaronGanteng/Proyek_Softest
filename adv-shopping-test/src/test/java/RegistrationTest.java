import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.UUID;

public class RegistrationTest extends BaseTest {

    public RegistrationTest() {
        super();
    }

    @Test
    public void createAccountTiming() {
        final String HOME = "https://www.advantageonlineshopping.com";
        long duration = -1;
        String conclusion = "UNKNOWN";

        try {
            // navigate directly to the register page
            driver.get(HOME + "/#/register");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }

            // prepare test data
            String username = "testingbaru2";
            String password = "Testingbaru2";
            String email = "user+" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
            String firstName = "Test";
            String lastName = "User";

            // measure registration time
            duration = measureExecutionTime(() -> {
                // fill form fields (best-effort selectors)
                try {
                    setIfPresent(By.name("usernameRegisterPage"), username);
                } catch (Exception ignored) {
                }
                try {
                    setIfPresent(By.name("emailRegisterPage"), email);
                } catch (Exception ignored) {
                }
                try {
                    setIfPresent(By.name("passwordRegisterPage"), password);
                } catch (Exception ignored) {
                }
                try {
                    setIfPresent(By.name("confirm_passwordRegisterPage"), password);
                } catch (Exception ignored) {
                }
                try {
                    setIfPresent(By.name("first_nameRegisterPage"), firstName);
                } catch (Exception ignored) {
                }
                try {
                    setIfPresent(By.name("last_nameRegisterPage"), lastName);
                } catch (Exception ignored) {
                }

                // ensure 'I agree' checkbox is checked (if present)
                try {
                    String jsCheck = """
                            Array.from(document.querySelectorAll('label')).forEach(function(l){
                                if(l.innerText && l.innerText.indexOf('Conditions of Use')!==-1){
                                    var forAttr=l.getAttribute('for');
                                    if(forAttr){ var el=document.getElementById(forAttr); if(el) el.checked=true; }
                                    else { var cb=l.previousElementSibling; if(cb && cb.type==='checkbox') cb.checked=true; }
                                }
                            });
                            """;
                    ((JavascriptExecutor) driver).executeScript(jsCheck);
                } catch (Exception ignored) {
                }

                // click register - try multiple locators
                boolean clicked = false;
                try {
                    WebElement reg = driver.findElement(By.id("register_btnundefined"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", reg);
                    clicked = true;
                } catch (Exception ignored) {
                }
                if (!clicked) {
                    try {
                        WebElement btn = driver.findElement(By.xpath(
                                "//button[normalize-space(.)='REGISTER' or contains(., 'REGISTER') or contains(., 'Register')]"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                        clicked = true;
                    } catch (Exception ignored) {
                    }
                }
                if (!clicked) {
                    try {
                        WebElement inputBtn = driver
                                .findElement(By.cssSelector("input[type='submit'], input[type='button']"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", inputBtn);
                        clicked = true;
                    } catch (Exception ignored) {
                    }
                }
                if (!clicked) {
                    throw new RuntimeException("Register button not found or not clickable");
                }
                // As a fallback, try filling fields via JS (labels/placeholders) and re-attempt
                // click
                try {
                    String jsFill = String.format(
                            """
                                    (function(){
                                        var data={username:'%s', email:'%s', password:'%s', first:'%s', last:'%s'};
                                        function setVal(el,v){if(!el) return; el.focus(); el.value=v; el.dispatchEvent(new Event('input',{bubbles:true})); el.dispatchEvent(new Event('change',{bubbles:true}));}
                                        var tryByName = function(n){var e=document.getElementsByName(n); if(e && e.length>0){ setVal(e[0], data[n.replace('RegisterPage','')==='username'?'username':data[n.replace('RegisterPage','')==='email'?'email':n]); return true;} return false; };
                                        var mappings = [ ['usernameRegisterPage','username'], ['emailRegisterPage','email'], ['passwordRegisterPage','password'], ['confirm_passwordRegisterPage','password'], ['first_nameRegisterPage','first'], ['last_nameRegisterPage','last'] ];
                                        mappings.forEach(function(m){ var byName=document.getElementsByName(m[0]); if(byName && byName.length>0){ setVal(byName[0], data[m[1]]); } else { var byPlaceholder=document.querySelector("input[placeholder*='"+m[1]+"']"); if(byPlaceholder) setVal(byPlaceholder, data[m[1]]); else { Array.from(document.querySelectorAll('label')).forEach(function(l){ if(l.innerText && l.innerText.toLowerCase().indexOf(m[1].toLowerCase())!==-1){ var id=l.getAttribute('for'); if(id && document.getElementById(id)) setVal(document.getElementById(id), data[m[1]]); else { var input=l.nextElementSibling && l.nextElementSibling.querySelector ? l.nextElementSibling.querySelector('input') : null; if(input) setVal(input, data[m[1]]); } } }); } } });
                                        Array.from(document.querySelectorAll('label')).forEach(function(l){ if(l.innerText && l.innerText.indexOf('Conditions of Use')!==-1){ var forAttr=l.getAttribute('for'); if(forAttr){ var el=document.getElementById(forAttr); if(el) el.checked=true; } else { var cb=l.previousElementSibling; if(cb && cb.type==='checkbox') cb.checked=true; } } });
                                        return true; })();
                                    """,
                            username, email, password, firstName, lastName);
                    ((JavascriptExecutor) driver).executeScript(jsFill);
                    // small pause then re-click
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException ignored) {
                    }
                    try {
                        WebElement btn = driver.findElement(By.xpath(
                                "//button[normalize-space(.)='REGISTER' or contains(., 'REGISTER') or contains(., 'Register')]"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                    } catch (Exception ignored) {
                    }
                } catch (Exception ignored) {
                }

                // wait for account creation confirmation (user menu shows username or logout
                // appears)
                long start = System.currentTimeMillis();
                long timeout = 60000;
                boolean created = false;
                while (System.currentTimeMillis() - start < timeout) {
                    try {
                        // user menu should contain the username when logged in
                        WebElement menu = driver.findElement(By.id("menuUser"));
                        if (menu.getText() != null && menu.getText().toLowerCase().contains(username.toLowerCase())) {
                            created = true;
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) {
                    }
                }
                if (!created)
                    throw new RuntimeException("Account creation not observed within timeout");
            });

            Reporter.log("Registration time: " + duration + " ms");
            Assert.assertTrue(duration < 4000, "Registration should be < 4000ms but was " + duration + "ms");
            conclusion = "PASS - registration time " + duration + " ms";
        } catch (

        Throwable t) {
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
