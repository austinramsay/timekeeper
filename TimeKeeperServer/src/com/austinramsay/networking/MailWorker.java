package com.austinramsay.networking;

import com.austinramsay.timekeeper.CorrectionRequest;
import com.austinramsay.controller.TimeKeeperServer;

import javax.mail.*;
import javax.mail.Authenticator;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailWorker {

    private static final String USERNAME = "timekeeper.notifications@gmail.com";
    private static final String PASSWORD = "Cffg5099!";

    /**
     * Sends statistical report on a time correction submitted.
     * Recipients determined by the defined list of administrators.
     * @param employeeName
     */
    public static void sendCorrectionSubmittedNotification(String employeeName, CorrectionRequest correction) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {

            Message notification = new MimeMessage(session);
            notification.setFrom(new InternetAddress(USERNAME));
            notification.setRecipient(Message.RecipientType.TO, new InternetAddress("austinramsay@gmail.com"));
            notification.setSubject(String.format("Time Correction Request: %s", employeeName));

            StringBuilder correctionStats = new StringBuilder();
            correctionStats.append("A new time correction request was submitted.");
            correctionStats.append("\n\n");
            correctionStats.append(String.format("Submitted by: %s", employeeName));
            correctionStats.append("\n\n");
            correctionStats.append(String.format("Submitted on: %s", correction.getDate()));
            correctionStats.append("\n\n");
            correctionStats.append(String.format("Description: %s", correction.getDescription()));
            correctionStats.append("\n\n");
            correctionStats.append("- Time Keeper");

            notification.setText(correctionStats.toString());
            Transport.send(notification);

        } catch (AddressException e) {
            TimeKeeperServer.broadcast("MailWorker: Address exception.");
        } catch (MessagingException e) {
            TimeKeeperServer.broadcast("MailWorker: Message exception.");
        }
    }
}
