package com.pwllc.app;


import org.apache.commons.lang3.StringUtils;

import java.util.prefs.Preferences;

public class AppPreferences {

    private Preferences preferences =
            Preferences.userNodeForPackage(AppPreferences.class);

    public void setSiteCredentials(String siteUser, String sitePass) {
        preferences.put("siteUser", siteUser);
        preferences.put("sitePass", sitePass);
    }

    public void setNotificationCredentials(String emailUser, String emailPass, String phoneNumber) {

        if (!StringUtils.isEmpty(emailUser)) {
            preferences.put("emailUser", emailUser);
        }

        if (!StringUtils.isEmpty(emailPass)) {
            preferences.put("emailPass", emailPass);
        }

        if (!StringUtils.isEmpty(phoneNumber)) {
            preferences.put("phoneNumber", phoneNumber);
        }
    }

    public String getSiteUser() {
        return preferences.get("siteUser", null);
    }

    public String getSitePass() {
        return preferences.get("sitePass", null);
    }

    public String getEmailUser() {
        return preferences.get("emailUser", null);
    }

    public String getEmailPass() {
        return preferences.get("emailPass", null);
    }

    public String getPhoneNumber() {
        return preferences.get("phoneNumber", null);
    }

}