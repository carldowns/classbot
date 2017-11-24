package com.pwllc.app;


import java.util.prefs.Preferences;

public class AppPreferences {

    private Preferences preferences =
            Preferences.userNodeForPackage(AppPreferences.class);

    public void setSiteCredentials(String siteUser, String sitePass) {
        preferences.put("siteUser", siteUser);
        preferences.put("sitePass", sitePass);
    }

    public void setEmailCredentials(String emailUser, String emailPass) {
        preferences.put("emailUser", emailUser);
        preferences.put("emailPass", emailPass);
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
}