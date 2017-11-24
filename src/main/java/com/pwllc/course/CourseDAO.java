package com.pwllc.course;

import com.google.common.collect.Lists;
import com.pwllc.app.AppConfig;
import com.pwllc.h2.H2Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 *
 */
public class CourseDAO {

    AppConfig cfg;

    public CourseDAO(AppConfig cfg) {
        this.cfg = cfg;
        createTables();
    }

    public void createTables() {

        try {
            Connection conn = H2Database.getPersistentConnection();

            // determine if table exists
            ResultSet set = conn.getMetaData().getTables(null, null, "COURSE", null);
            if (set.next()) {
                return;
            }

            // create table if not yet created
            PreparedStatement stmt = conn.prepareStatement
                    ("create table COURSE " +
                            "(" +
                                "courseNumber varchar(100) primary key," +
                                "semesterTitle varchar(100)," +
                                "status varchar(100)," +
                                "detail varchar(5000)," +
                                "lastChecked varchar(100)," +
                                "enabled boolean," +
                                "notifyOpen boolean," +
                                "notifyClosed boolean," +
                                "notifyWaitListed boolean" +
                            ")");

            stmt.executeUpdate();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public List<CourseInfo> getCourses() {

        List<CourseInfo> results = Lists.newArrayList();
        try {
            Connection conn = H2Database.getPersistentConnection();
            PreparedStatement stmt = conn.prepareStatement
                    ("select * from course");
            stmt.executeQuery();

            ResultSet set = stmt.getResultSet();
            while (set.next()) {
                results.add(new CourseInfo(set));
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return results;
    }


    public CourseInfo getCourse(String courseNumber) {

        try {
            Connection conn = H2Database.getPersistentConnection();
            PreparedStatement stmt = conn.prepareStatement
                    ("select * from course where courseNumber=?");
            stmt.setString(1, courseNumber);
            stmt.executeQuery();

            ResultSet set = stmt.getResultSet();
            if (set.next()) {
                return new CourseInfo(set);
            }
            throw new RuntimeException("course not found " + courseNumber);

        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }

        throw new RuntimeException("unable to locate courseNumber: " + courseNumber);
    }

    public void addCourse (CourseInfo course) {
        try {
            Connection conn = H2Database.getPersistentConnection();
            PreparedStatement stmt = conn.prepareStatement
                    ("insert into course " +
                            "(courseNumber, " +
                            "semesterTitle, " +
                            "status, " +
                            "detail, " +
                            "lastChecked, " +
                            "enabled, " +
                            "notifyOpen, " +
                            "notifyClosed," +
                            "notifyWaitListed) " +
                            "values " +
                            "(?,?,?,?,?,?,?,?,?)");
            stmt.setString(1, course.getCourseNumber());
            stmt.setString(2, course.getSemesterTitle());
            stmt.setString(3, course.getStatus());
            stmt.setString(4, course.getDetail());
            stmt.setString(5, course.getLastChecked());
            stmt.setBoolean(6, course.getEnabled());
            stmt.setBoolean(7, course.getNotifyOpen());
            stmt.setBoolean(8, course.getNotifyClosed());
            stmt.setBoolean(9, course.getNotifyWaitListed());
            stmt.executeUpdate();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void updateCourse (CourseInfo course) {
        try {
            Connection conn = H2Database.getPersistentConnection();
            PreparedStatement stmt = conn.prepareStatement
                    ("update course set " +
                            "semesterTitle = ?, " +
                            "status = ?, " +
                            "detail = ?, " +
                            "lastChecked = ?, " +
                            "enabled = ?, " +
                            "notifyOpen = ?, " +
                            "notifyClosed = ?, " +
                            "notifyWaitListed = ? " +
                            "where courseNumber = ?");
            stmt.setString(1, course.getSemesterTitle());
            stmt.setString(2, course.getStatus());
            stmt.setString(3, course.getDetail());
            stmt.setString(4, course.getLastChecked());
            stmt.setBoolean(5, course.getEnabled());
            stmt.setBoolean(6, course.getNotifyOpen());
            stmt.setBoolean(7, course.getNotifyClosed());
            stmt.setBoolean(8, course.getNotifyWaitListed());
            stmt.setString(9, course.getCourseNumber());
            stmt.executeUpdate();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void deleteCourse (String courseNumber) {
        try {
            Connection conn = H2Database.getPersistentConnection();
            PreparedStatement stmt = conn.prepareStatement
                    ("delete from course where courseNumber = ?");
            stmt.setString(1, courseNumber);
            stmt.executeUpdate();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
