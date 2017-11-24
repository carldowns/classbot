package com.pwllc.notify;

import com.pwllc.app.AppPreferences;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailClient
{
    public static void main(String [] args) {
        try {
            AppPreferences pref = new AppPreferences();
            send(pref.getEmailUser(), pref.getEmailPass(), pref.getEmailUser(), "ClassBot", "test message");
        }
            catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void send(String from, String pass, String to, String subject, String body) {
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