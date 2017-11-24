package com.pwllc.course;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.ResultSet;

/**
 *
 */
@JsonInclude(value=JsonInclude.Include.NON_EMPTY)
public class CourseInfo {

    @JsonProperty("courseNumber")
    String courseNumber;

    @JsonProperty("semesterTitle")
    String semesterTitle;

    @JsonProperty("status")
    String status;

    @JsonProperty("detail")
    String detail;

    @JsonProperty("lastChecked")
    String lastChecked;

    @JsonProperty("enabled")
    Boolean enabled = true;

    @JsonProperty("notifyOpen")
    Boolean notifyOpen = true;

    @JsonProperty("notifyClosed")
    Boolean notifyClosed = false;

    @JsonProperty("notifyWaitListed")
    Boolean notifyWaitListed = false;

    public CourseInfo(String semesterTitle, String courseNumber) {
        this.semesterTitle = semesterTitle;
        this.courseNumber = courseNumber;
    }

    public CourseInfo(ResultSet set) throws Exception {
        this.courseNumber = set.getString("courseNumber");
        this.semesterTitle = set.getString("semesterTitle");
        this.status = set.getString("status");
        this.detail = set.getString("detail");
        this.lastChecked = set.getString("lastChecked");
        this.enabled = set.getBoolean("enabled");
        this.notifyOpen = set.getBoolean("notifyOpen");
        this.notifyClosed = set.getBoolean("notifyClosed");
        this.notifyWaitListed = set.getBoolean("notifyWaitListed");
    }

    @JsonIgnore
    public String getSemesterTitle() {
        return semesterTitle;
    }
    @JsonIgnore
    public void setSemesterTitle(String semesterTitle) {
        this.semesterTitle = semesterTitle;
    }
    @JsonIgnore
    public String getStatus() {
        return status;
    }
    @JsonIgnore
    public void setStatus(String status) {
        this.status = status;
    }
    @JsonIgnore
    public String getDetail() {
        return detail;
    }
    @JsonIgnore
    public void setDetail(String detail) {
        this.detail = detail;
    }
    @JsonIgnore
    public String getLastChecked() {
        return lastChecked;
    }
    @JsonIgnore
    public void setLastChecked(String lastChecked) {
        this.lastChecked = lastChecked;
    }
    @JsonIgnore
    public String getCourseNumber() {
        return courseNumber;
    }
    @JsonIgnore
    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }
    @JsonIgnore
    public Boolean getEnabled() {
        return enabled;
    }
    @JsonIgnore
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    @JsonIgnore
    public Boolean getNotifyOpen() {
        return notifyOpen;
    }
    @JsonIgnore
    public void setNotifyOpen(Boolean notifyOpen) {
        this.notifyOpen = notifyOpen;
    }
    @JsonIgnore
    public Boolean getNotifyClosed() {
        return notifyClosed;
    }
    @JsonIgnore
    public void setNotifyClosed(Boolean notifyClosed) {
        this.notifyClosed = notifyClosed;
    }
    @JsonIgnore
    public Boolean getNotifyWaitListed() {
        return notifyWaitListed;
    }
    @JsonIgnore
    public void setNotifyWaitListed(Boolean notifyWaitListed) {
        this.notifyWaitListed = notifyWaitListed;
    }
}
