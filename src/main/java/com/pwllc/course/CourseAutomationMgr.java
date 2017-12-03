package com.pwllc.course;

import com.pwllc.app.AppConfig;
import com.pwllc.app.AppPreferences;
import com.pwllc.selenium.UARKWebDriver;
import com.pwllc.selenium.UTWebDriver;
import com.pwllc.selenium.iWebDriver;

import java.util.concurrent.*;

/**
 * This class manages the Selenium automation jobs based on the CourseInfo definitions.
 * Care is taken to be sure they are not running to frequently.
 */
public class CourseAutomationMgr {

    private ScheduledThreadPoolExecutor threadPool;
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
        if (cfg.isEnableAutomation()) {
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
                flow.doFlow();
                dao.updateCourse(info);
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
