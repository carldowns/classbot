package com.pwllc.course;

import com.pwllc.app.AppConfig;
import com.pwllc.app.AppPreferences;
import com.pwllc.selenium.CourseStatusWebDriver;

import java.util.concurrent.*;

/**
 * This class manages the Selenium automation jobs based on the CourseInfo definitions.
 * Care is taken to be sure they are not running to frequently.
 */
public class CourseAutomationMgr {

    private ScheduledThreadPoolExecutor threadPool;
    private AppPreferences pref;
    private CourseDAO dao;

    public CourseAutomationMgr(CourseDAO dao, AppPreferences pref, AppConfig cfg) {
        this.dao = dao;
        this.pref = pref;

        if (cfg.isEnableAutomation()) {
            this.threadPool = new ScheduledThreadPoolExecutor(5);
            threadPool.scheduleAtFixedRate(jobRunner, 0, 1, TimeUnit.MINUTES); // run jobs every 1 minutes
        }
    }

    /**
     * The Job Runner is set up to get all courses, and start them 15 seconds apart
     * each cycle gets a fresh set of CourseInfos so we are sure to get the latest configuration changes.
     */
    Runnable jobRunner = new Runnable () {
        public void run () {
            try {
                int count = 0; // delay each job by an additional 15 seconds
                for (CourseInfo info : dao.getCourses()) {
                    if (info.getEnabled()) {
                        threadPool.schedule(new Job(info), 15 * count++, TimeUnit.SECONDS);
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
        CourseStatusWebDriver flow;

        Job (CourseInfo info) {
            this.info = info;
            flow = new CourseStatusWebDriver(info, pref);
        }

        public void run () {
            try {
                flow.doFlow();
                dao.updateCourse(info);
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
