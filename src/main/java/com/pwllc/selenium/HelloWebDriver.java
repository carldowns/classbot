package com.pwllc.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * works
 */
public class HelloWebDriver {

    public static void main(String[] args) {
        try {
            new HelloWebDriver().hiGoogle(args);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void hiGoogle(String[] args) throws Exception {
        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface,
        // not the implementation.
        //System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        //WebDriver driver = new HtmlUnitDriver(true);
        //WebDriver driver = new FirefoxDriver();

        System.setProperty("webdriver.chrome.driver", "chromedriver/chromedriver");
        WebDriver driver = new ChromeDriver();

        // And now use this to visit Google
        driver.get("http://www.google.com");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        // Check the title of the page
        System.out.println("Initial Page title is: " + driver.getTitle());

        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("cheese!");
            }
        });

        // Should see: "cheese! - Google Search"
        System.out.println("Final Page title is: " + driver.getTitle());

        System.out.println("sleeping...");
        Thread.sleep(5000);
        System.out.println("done");

        //Close the browser
        driver.quit();
    }

}
