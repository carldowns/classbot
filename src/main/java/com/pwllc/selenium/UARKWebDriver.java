package com.pwllc.selenium;

import com.pwllc.app.AppPreferences;
import com.pwllc.course.CourseInfo;
import com.pwllc.notify.EmailClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Designed to determine the status of a given University of Arkansas course
 * @see "https://uaconnect.uark.edu/"
 */
public class UARKWebDriver implements iWebDriver {

    static final String startingURL = "https://uaconnect.uark.edu/";
    WebDriver driver;
    AppPreferences pref;
    CourseInfo course;

    // testing entry point
    public static void main (String args[]) {

        CourseInfo open = new CourseInfo("Spring 2018", "47465");
        new UTWebDriver(open, new AppPreferences()).doFlow();
    }

    public UARKWebDriver(CourseInfo course, AppPreferences pref) {
        this.course = course;
        this.pref = pref;
    }

    public void initChrome () {

        System.setProperty("webdriver.chrome.driver", "chromedriver/chromedriver");
        String agentString = "--user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25";
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(agentString);
        driver = new ChromeDriver(chromeOptions);
    }

    public void doFlow () {

        try {
            initChrome();
            gotoHomePage();
            gotoClassSearchPageDirect();
            //gotoMyConnectPage();
            //gotoManageClassesPage();
            //gotoClassSearchPage();
            searchByUniqueCourseNumber();
            evaluateCourseStatus();
        }
        catch (Exception e) {
            System.err.println (e.getMessage());
            course.setDetail(e.getMessage());
        }
        finally {
            driver.quit();
        }
    }

    public void confirmLoggedIn () {

        // Is this the login page?
        try {
            sleep();
            driver.findElement(By.id("username"));
        }
        // not found, so we are logged in
        catch (Exception e) { // FIXME
            return;
        }

        // Yes, we are on the login page
        // pass the credentials
        WebElement name = driver.findElement(By.id("username"));
        WebElement word = driver.findElement(By.id("password"));
        WebElement button = driver.findElement(By.tagName("button"));

        if (pref.getSiteUser() == null) {
            throw new RuntimeException("site user is not set");
        }
        name.sendKeys(pref.getSiteUser());

        if (pref.getSitePass() == null) {
            throw new RuntimeException("site pass is not set");
        }
        word.sendKeys(pref.getSitePass());
        button.submit();
    }

    public void confirmPageContains (String text) {

        // FIXME
        String pageSource = driver.getPageSource();
        if (!pageSource.toLowerCase().contains(text.toLowerCase())) {
            throw new RuntimeException ("text not located in pageSource: " + text);
        }
    }

    public void gotoHomePage() throws Exception {

        // registrar home page
        driver.navigate().to(startingURL);
        WebElement element = driver.findElement(By.linkText("University of Arkansas Student Information System"));
    }

//    public void gotoMyConnectPage() {
//
//        confirmLoggedIn();
//        WebElement element = driver.findElement(By.linkText("myConnect"));
//        element.click();
//    }
//
//    public void gotoManageClassesPage() {
//
//        confirmLoggedIn();
//        WebElement element = driver.findElement(By.id("PS_SCHEDULE_L_FL$3"));
//        element.click();
//    }
//
//    public void gotoClassSearchPage() {
//
//        confirmLoggedIn();
//        WebElement element = driver.findElement(By.id("PTGP_STEP_DVW_PTGP_STEP_LABEL$2"));
//        element.click();
//    }

    public void gotoClassSearchPageDirect() {
        confirmLoggedIn();
        driver.navigate().to("https://csprd.uark.edu/psc/csprd/EMPLOYEE/SA/c/SA_LEARNER_SERVICES.CLASS_SEARCH.GBL?&ICAGTarget=start#");
    }

    public void searchByUniqueCourseNumber () throws Exception {

        confirmLoggedIn();

        WebDriverWait wait = new WebDriverWait(driver, 15);

        // this page in particular is some terribly written JavaScript -- any changes to it
        // cause slow refreshes that the waits can even get around.
        // So I had to resort to frigging sleeps. :-/
        sleep();

        // semester selector
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("CLASS_SRCH_WRK2_STRM$35$")));
        Select select = new Select(driver.findElement(By.name("CLASS_SRCH_WRK2_STRM$35$")));
        select.selectByVisibleText(course.getSemesterTitle());
        System.out.println("CLASS_SRCH_WRK2_STRM$35$");

        sleep();

        // un-check open classes only checkbox
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("SSR_CLSRCH_WRK_SSR_OPEN_ONLY$7")));
        WebElement checkbox = driver.findElement(By.name("SSR_CLSRCH_WRK_SSR_OPEN_ONLY$7"));
        boolean checked = checkbox.isSelected();
        if (checked) {
            checkbox.click();
        }

        sleep();

        // additional search criteria - click to open the panel
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("DERIVED_CLSRCH_SSR_EXPAND_COLLAPS$149$$1")));
        WebElement criteria = driver.findElement(By.name("DERIVED_CLSRCH_SSR_EXPAND_COLLAPS$149$$1"));
        criteria.click();

        sleep();

        // unique course number
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("SSR_CLSRCH_WRK_CLASS_NBR$12")));
        WebElement field = driver.findElement(By.name("SSR_CLSRCH_WRK_CLASS_NBR$12"));
        field.sendKeys(course.getCourseNumber());

        sleep();

        WebElement button = driver.findElement(By.name("CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH"));
        button.click();
    }

    public void evaluateCourseStatus() {

        confirmLoggedIn();
        confirmPageContains (course.getCourseNumber());

        WebElement details = driver.findElement(By.id("SSR_CLSRCH_MTG1$scroll$0"));
        String text = details.getText();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // remove the table headers: Unique Day Hour Room Instructor Status Flags
        if (text.contains(" View Materials ")) {
            text = text.split(" View Materials ")[1];
        }
        course.setDetail(text);
        course.setLastChecked(timeStamp);

        // this page only shows Icons for status and there is a legend
        // count the number of icons -- should be 1 of each per the legend

        List<WebElement> openImages = driver.findElements(By.cssSelector("img[alt='Open']"));
        List<WebElement> closedImages = driver.findElements(By.cssSelector("img[alt='Closed']"));
        List<WebElement> WaitListImages = driver.findElements(By.cssSelector("img[alt='Wait List']"));

        // the one icon with two (2) instances is the current status for the class!
        if (openImages.size() > 1) {
            course.setStatus("open");
            if (course.getNotifyOpen()) {
                sendNotification();
            }
            return;
        }

        if (closedImages.size() > 1) {
            course.setStatus("closed");
            if (course.getNotifyClosed()) {
                sendNotification();
            }
            return;
        }

        if (WaitListImages.size() > 1) {
            course.setStatus("waitlisted");
            if (course.getNotifyWaitListed()) {
                sendNotification();
            }
            return;
        }

        throw new RuntimeException("unable to determine course status");
    }

    private void sendNotification() {

        // TODO replace with something to get the word to the user
        StringBuffer builder = new StringBuffer();
        builder.append(course.getCourseNumber()).append("  ");
        builder.append(course.getStatus()).append("  ");
        builder.append(course.getSemesterTitle()).append("  ");
        builder.append(course.getLastChecked()).append("  ");
        builder.append(course.getDetail());

        String notice = builder.toString();
        System.out.println(notice);

        if (pref.getEmailUser() == null) {
            throw new RuntimeException("email user is not set");
        }

        if (pref.getEmailPass() == null) {
            throw new RuntimeException("email pass is not set");
        }

        String to = pref.getEmailUser();
        String from = pref.getEmailUser();
        String pass = pref.getEmailPass();

        String subject = "ClassBot: " + course.getCourseNumber() + " is " + course.getStatus();
        EmailClient.send(from, pass, to, subject, notice);
    }

    private void sleep () {
        try {
            Thread.sleep(3000);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
