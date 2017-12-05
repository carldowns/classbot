package com.pwllc.notify;

import com.pwllc.app.AppPreferences;
import com.pwllc.course.CourseInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailClient
{
    // Sweet: AT&T has a way to send emails as SMS texts --
    // @see https://www.att.com/esupport/article.html#!/wireless/KM1061254
    // Target phone number must be an AT&T number of course.
    // Simply postfix this service string to the 10 digit phone number - no dashes or leading 1.

    private static String ATT_SMS_SERVICE_POSTFIX = "@txt.att.net";

    public static void main(String [] args) {
        try {
            AppPreferences pref = new AppPreferences();
            send(pref.getEmailUser(),
                    pref.getEmailPass(),
                    pref.getPhoneNumber() + ATT_SMS_SERVICE_POSTFIX,
                    "ClassBot",
                    "test message");
        }
            catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void send(AppPreferences pref, CourseInfo info) {

        // TODO replace with something to get the word to the user
        StringBuffer builder = new StringBuffer();
        builder.append(info.getCourseNumber()).append("  ");
        builder.append(info.getStatus()).append("  ");
        builder.append(info.getSemesterTitle()).append("  ");
        builder.append(info.getLastChecked()).append("  ");
        builder.append(info.getDetail());

        String notice = builder.toString();
        System.out.println(notice);

        if (pref.getEmailUser() == null) {
            throw new RuntimeException("email user is not set");
        }

        if (pref.getEmailPass() == null) {
            throw new RuntimeException("email pass is not set");
        }

        if (pref.getPhoneNumber() == null) {
            throw new RuntimeException("phone number is not set");
        }

        String to = pref.getPhoneNumber();
        String from = pref.getEmailUser();
        String pass = pref.getEmailPass();

        String subject = "ClassBot: " + info.getCourseNumber() + " is " + info.getStatus();
        send(from, pass, to, subject, notice);
    }

    /**
     * supports sending
     * @param from
     * @param pass
     * @param to can be an email address or an ATT phone number
     * @param subject
     * @param body
     */
    public static void send(String from, String pass, String to, String subject, String body) {

        // if numeric, then its a phone number, so append the postfix
        if (StringUtils.isNumeric (to)) {
            to = to + ATT_SMS_SERVICE_POSTFIX;
        }

        useYahooMail(from, pass, to, subject, body);
    }

    public static void useYahooMail(String from, String pass, String to, String subject, String body) {

        String host = "smtp.mail.yahoo.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", from);
        properties.put("mail.smtp.password", pass);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

//            // include the host address
//            message.addRecipient(Message.RecipientType.TO,
//                    new InternetAddress(from));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText(body);

            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            System.out.println("Email sent successfully");
        }
        catch (MessagingException mex) {
            System.err.println(mex.getMessage());
            throw new RuntimeException("unable to send notification: " + mex.getMessage());
        }
    }
}