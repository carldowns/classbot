package com.pwllc.course;

import com.google.common.collect.Maps;
import com.pwllc.app.AppConfig;
import com.pwllc.app.AppPreferences;
import com.pwllc.notify.EmailClient;
import com.pwllc.selenium.UARKWebDriver;
import com.pwllc.selenium.UTWebDriver;
import com.pwllc.selenium.iWebDriver;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Map;
import java.util.concurrent.*;

/**
 * This class manages the Selenium automation jobs based on the CourseInfo definitions.
 * Care is taken to be sure they are not running to frequently.
 */
public class CourseAutomationMgr {

    private ScheduledThreadPoolExecutor threadPool;
    private Map<String, DateTime> notificationLog = Maps.newConcurrentMap();
    private AppPreferences pref;
    private CourseDAO dao;
    private AppConfig cfg;

    public CourseAutomationMgr(CourseDAO dao, AppPreferences pref, AppConfig cfg) {
        this.dao = dao;
        this.pref = pref;
        this.cfg = cfg;

        // start the pool so 'run now!' action works
        this.threadPool = new ScheduledThreadPoolExecutor(cfg.getAutomationThreadCount());

        // if background automation is enabled, queue JobRunner
        if (cfg.isAutomationPeriodicScansEnabled()) {
            threadPool.scheduleAtFixedRate(jobRunner,
                    0, cfg.getAutomationIntervalInSeconds(),
                    TimeUnit.SECONDS);
        }
    }

    /**
     * run the course automation now
     * @param courseNumber course number
     */
    public void runNow (String courseNumber) {
        try {
           CourseInfo ci = dao.getCourse(courseNumber);
            threadPool.execute(new Job (ci));
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * The Job Runner is set up to get all courses, and start them 15 seconds apart
     * each cycle gets a fresh set of CourseInfos so we are sure to get the latest configuration changes.
     */
    Runnable jobRunner = new Runnable () {
        public void run () {
            try {
                int count = 0;
                for (CourseInfo info : dao.getCourses()) {
                    if (info.getEnabled()) {
                        threadPool.schedule(new Job(info),
                                cfg.getAutomationStaggeredDelayInSeconds() * count++,
                                TimeUnit.SECONDS);
                    }
                }
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    };

    /**
     * Job Runnable to contain the Registrar and Course Info
     */
    class Job implements Runnable {
        CourseInfo info;
        iWebDriver flow;

        Job (CourseInfo info) {
            this.info = info;

            // select driver type
            switch (cfg.getAutomationDriverType()) {
                case "UT":
                    flow = new UTWebDriver(info, pref);
                    break;
                case "UARK":
                    flow = new UARKWebDriver(info, pref);
                    break;
                default:
                    throw new IllegalStateException("missing configuration: driverType: UT | UARK");
            }
        }

        public void run () {
            try {
                if (isNotificationWindowOpen(info)) {
                    flow.doFlow();
                    dao.updateCourse(info);
                    if (flow.isNotificationIndicated()) {
                        EmailClient.send(pref, info);
                    }
                }
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * returns true if the last cached notification for this course happened outside of the configured window.
     * @param info
     * @return
     */
    private boolean isNotificationWindowOpen (CourseInfo info) {

        String key = info.getCourseNumber() + "." + info.getStatus();
        DateTime now = new DateTime();
        DateTime lastTime = notificationLog.get(key);

        if (lastTime == null) {
            notificationLog.put(key, now);
            return true;
        }

        Duration timeElapsedSinceLastNotification = new Duration(lastTime, now);
        Duration window = Duration.standardMinutes(cfg.getAutomationNotificationWindowInMinutes());
        boolean isWindowOpen = timeElapsedSinceLastNotification.isLongerThan(window);

        if (isWindowOpen) {
            notificationLog.put(key, now);
            return true;
        }

        System.out.println (
                        info + " notification window is closed.  " +
                        "time elapsed since last notification is " + timeElapsedSinceLastNotification +
                        " which is less than configured window " + window);
        return false;
    }
}
