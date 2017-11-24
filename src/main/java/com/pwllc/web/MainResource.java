package com.pwllc.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pwllc.app.AppPreferences;
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

    public MainResource(CourseDAO dao, AppPreferences pref) {
        this.dao = dao;
        this.pref = pref;
    }

    @GET
    @Path("/")
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
    @Path("/email")
    public void putEmailCredentials (@QueryParam("emailUser") String user,
                                     @QueryParam("emailPass") String pass) {
        pref.setEmailCredentials(user, pass);
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
    public void delete(@PathParam("courseNumber") String courseNumber) {
        dao.deleteCourse(courseNumber);
    }

    // testing
    @GET
    @Path ("/test/init")
    public List initTestData() {
        CourseInfo open = new CourseInfo("Spring 2018", "47465");
        dao.addCourse(open);

        //CourseInfo closed = new CourseInfo("Spring 2018", "47470");
        //dao.addCourse(closed);
        
        return dao.getCourses();
    }


    @GET
    @Path("/news")
    public News[] getNews() {
        return new News[]{
                new News("Trump takes a shit at GOP conference","Someone clean it up"),
                new News("Putin wins US Presidency","We like Vodka"),
                new News("Clinton leaves for China","see you later"),
                new News("Mueller stikes a pose","Gonna bust you up man"),
                new News("GOP implodes","the weight was just too much"),
        };
    }

    static class News {
        @JsonProperty("title")
        String title;
        @JsonProperty("abstract")
        String detail;

        public News(String title, String detail) {
            this.title = title;
            this.detail = detail;
        }
    }
}