package com.pwllc.selenium;

import com.pwllc.app.AppPreferences;
import com.pwllc.course.CourseInfo;
import com.pwllc.notify.EmailClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Designed to determine the status of a given UT course
 * @see "https://registrar.utexas.edu/"
 */
public class UTWebDriver implements iWebDriver {

    WebDriver driver;
    AppPreferences pref;
    CourseInfo course;

    // testing entry point
    public static void main (String args[]) {

        CourseInfo open = new CourseInfo("Spring 2018", "47465");
        new UTWebDriver(open, new AppPreferences()).doFlow();
    }

    public UTWebDriver(CourseInfo course, AppPreferences pref) {
        this.course = course;
        this.pref = pref;
    }

    public void initChrome () {

        System.setProperty("webdriver.chrome.driver", "chromedriver/chromedriver");
        driver = new ChromeDriver();
    }

    public void doFlow () {

        try {
            initChrome();
            gotoHomePage();
            gotoSemesterPage();
            gotoSearchPage();
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
            driver.findElement(By.id("IDToken1"));
        }
        // not found, so we are logged in
        catch (Exception e) { // FIXME
            return;
        }

        // Yes, we are on the login page
        // pass the credentials
        WebElement name = driver.findElement(By.id("IDToken1"));
        WebElement word = driver.findElement(By.id("IDToken2"));
        WebElement button = driver.findElement(By.name("Login.Submit"));

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

    public void confirmTitleContains (String title) {

        // get the common page title element
        WebElement pageTitle = driver.findElement(By.className("page-title"));
        if (pageTitle == null) {
            throw new RuntimeException ("no title detected");
        }

        String text = pageTitle.getText();
        if (!text.contains(title)) {
            throw new RuntimeException ("title located but it does not match " + title);
        }
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
        driver.navigate().to("https://registrar.utexas.edu");
        confirmTitleContains ("Supporting students");
    }


    public void gotoSemesterPage() throws Exception {

        confirmLoggedIn();
        confirmTitleContains ("Supporting students");

        WebElement element = driver.findElement(By.linkText(course.getSemesterTitle()));
        element.click();
    }

    public void gotoSearchPage() {

        confirmLoggedIn();
        confirmTitleContains ("Course schedule");

        WebElement element = driver.findElement(By.linkText("Find courses now"));
        element.click();
    }

    public void searchByUniqueCourseNumber () {

        confirmLoggedIn();
        confirmPageContains (course.getSemesterTitle());

        // unique course number
        WebElement field = driver.findElement(By.name("unique_number"));
        field.sendKeys(course.getCourseNumber());

        WebElement button = driver.findElement(By.id("unique_search_button"));
        button.click();
    }

    public void evaluateCourseStatus() {

        confirmLoggedIn();
        confirmPageContains (course.getCourseNumber());

        WebElement details = driver.findElement(By.id("details_table"));
        String text = details.getText();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // remove the table headers: Unique Day Hour Room Instructor Status Flags
        if (text.contains(" Flags ")) {
            text = text.split(" Flags ")[1];
        }
        course.setDetail(text);
        course.setLastChecked(timeStamp);

        if (text.contains("open")) {
            course.setStatus("open");
            if (course.getNotifyOpen()) {
                sendNotification();
            }
            return;
        }

        if (text.contains("closed")) {
            course.setStatus("closed");
            if (course.getNotifyClosed()) {
                sendNotification();
            }
            return;
        }

        if (text.contains("waitlisted")) {
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

        String to = pref.getPhoneNumber();
        String from = pref.getEmailUser();
        String pass = pref.getEmailPass();

        String subject = "ClassBot: " + course.getCourseNumber() + " is " + course.getStatus();
        EmailClient.send(from, pass, to, subject, notice);
    }
}
