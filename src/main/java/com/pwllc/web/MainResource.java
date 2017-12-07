package com.pwllc.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pwllc.app.AppConfig;
import com.pwllc.app.AppPreferences;
import com.pwllc.course.CourseAutomationMgr;
import com.pwllc.course.CourseInfo;
import com.pwllc.course.CourseDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/main")
@Produces(MediaType.APPLICATION_JSON)
public class MainResource {

	private CourseDAO dao;
    private AppPreferences pref;
    private CourseAutomationMgr autoMgr;
    private AppConfig cfg;

    public MainResource(CourseDAO dao, AppConfig cfg, AppPreferences pref, CourseAutomationMgr autoMgr) {
        this.dao = dao;
        this.cfg = cfg;
        this.pref = pref;
        this.autoMgr = autoMgr;
    }

    @GET
    @Path("/courses")
    public List<CourseInfo> getList() {
        return dao.getCourses();
    }

    @POST
    @Path("/site")
    public void putSiteCredentials (@QueryParam("siteUser") String user,
                                    @QueryParam("sitePass") String pass) {
        pref.setSiteCredentials(user, pass);
    }

    @POST
    @Path("/notification")
    public void putNotificationCredentials (@QueryParam("emailUser") String user,
                                            @QueryParam("emailPass") String pass,
                                            @QueryParam("phoneNumber") String phoneNumber) {
        pref.setNotificationCredentials(user, pass, phoneNumber);
    }

    @POST
    @Path("/course")
    public void post(@QueryParam("semesterTitle") String semesterTitle,
                     @QueryParam("courseNumber") String courseNumber) {
        CourseInfo ci = new CourseInfo (semesterTitle, courseNumber);
        dao.addCourse(ci);
    }

    @PUT
    @Path("/course/{courseNumber}/toggle/{property}")
    public void toggleProperty(@PathParam("courseNumber") String courseNumber,
                               @PathParam("property") String property) {
        CourseInfo ci = dao.getCourse(courseNumber);
        if (ci == null) {
            return;
        }

        switch (property) {
            case "enabled" : ci.setEnabled(!ci.getEnabled());
                break;

            case "notifyWhenOpen" : ci.setNotifyOpen(!ci.getNotifyOpen());
                break;

            case "notifyWhenClosed" : ci.setNotifyClosed(!ci.getNotifyClosed());
                break;

            case "notifyWhenWaitListed" : ci.setNotifyWaitListed(!ci.getNotifyWaitListed());
                break;

        }
        dao.updateCourse(ci);
    }

    @GET
    @Path("/course/{courseNumber}")
    public CourseInfo getCourse(@PathParam("courseNumber") String courseNumber) {
        return dao.getCourse(courseNumber);
    }

    @DELETE
    @Path("/course/{courseNumber}")
    public String delete(@PathParam("courseNumber") String courseNumber) {
        dao.deleteCourse(courseNumber);
        return "ok";
    }

    @GET
    @Path("/run/{courseNumber}")
    public String runAutomationNow(@PathParam("courseNumber") String courseNumber) {
        autoMgr.runNow(courseNumber);
        return "ok";
    }

    @GET
    @Path("/driverType")
    public String getDriverType() {
        return cfg.getAutomationDriverType();
    }
}