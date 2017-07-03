package br.edu.unifei.gpesc.evaluation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Isaac Caldas Ferreira
 */
public class MailSender {

    public static void sendEmailWithAttachments(String host, String port,
            final String userName, final String password, String toAddress,
            String subject, String message, String[] attachFiles)
            throws AddressException, MessagingException {

        // sets SMTP server properties
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.user", userName);
        props.put("mail.password", password);


//        props.put("mail.smtp.socketFactory.port", port); //SSL Port
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //SSL


        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(userName));
        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());

        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html");

        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // adds attachments
        if (attachFiles != null && attachFiles.length > 0) {
            for (String filePath : attachFiles) {
                MimeBodyPart attachPart = new MimeBodyPart();

                try {
                    attachPart.attachFile(filePath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                multipart.addBodyPart(attachPart);
            }
        }

        // sets the multi-part as e-mail's content
        msg.setContent(multipart);

        // sends the e-mail
        Transport.send(msg);

    }

    private static String t(int v) {
        return String.format("%02d", v);
    }

    private static String getTime(long startTime) {
        long milliseconds = System.currentTimeMillis() - startTime;

        int seconds = (int) ( milliseconds / 1000)      % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
        int days    = (int)  (milliseconds / (1000*60*60*24));

        return t(days) + "d" + t(hours) + "h" + t(minutes) + "m" + t(seconds) + "s";
    }

    public static synchronized void send(String tag, int h1Len, int h2Len, long startTime, float result, String... attachFiles) {
        String time = getTime(startTime);

        // SMTP info
        String host = "smtp.gmail.com";
        String port = "587";
        String mailFrom = "randomzener@gmail.com";
        String password = "orangotango";

        // message info
        String mailTo = "caldas.isaac@gmail.com";
        String subject = tag + h1Len + "_" + h2Len + " > Result: " + result + "%";
        String message = "Time: " + time;

        // attachments
//        String path = "/home/isaac/Unifei/Mestrado/SAS/Mail_Test/Testes_Organizados/05_mlp_weights/2000/";
//        String[] attachFiles = {
//            path + "train_1.dat",
//            path + "train_1.log"
//        };

        try {
            sendEmailWithAttachments(host, port, mailFrom, password, mailTo,
                subject, message, attachFiles);
            System.out.println("Email sent.");
        } catch (Exception ex) {
            System.out.println("Could not send email.");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        send("TAG", 1, 2, 3, 4);
    }

}