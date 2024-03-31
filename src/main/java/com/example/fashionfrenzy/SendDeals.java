package com.example.fashionfrenzy;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendDeals {

    public boolean sendEmail(String to, String from, String subject, String text) {
        boolean flag = false;
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.host", "smtp.gmail.com");

        String userName = "frenzyfashionacc";
        String password = "cafzzdyuhlhttzus";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setFrom(new InternetAddress(from));
            message.setSubject(subject);
            message.setText(text);
            Transport.send(message);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static void main(String[] args) {
        SendDeals send = new SendDeals();
        Scanner scanner = new Scanner(System.in);
        String to = "";
        boolean isValidEmail = false;

        // Regular expression pattern for validating email addresses
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);

        while (!isValidEmail) {
            System.out.print("Enter Your email address: ");
            to = scanner.nextLine();

            Matcher matcher = pattern.matcher(to);
            isValidEmail = matcher.matches();

            if (!isValidEmail) {
                System.out.println("Invalid email address. Please enter a valid email address.");
            }
        }

        String from = "frenzyfashionacc@gmail.com";
        String subject = "Fashion-Frenzy Top Deals";

        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("Hello.. Sale is going on the fashion frenzy....!\n\n");
        textBuilder.append("Here are the top deals:\n");
        textBuilder.append(FetchProductsFromExcelBasedOnCategory.readData(Arrays.asList("product_info.xlsx")));

        boolean b = send.sendEmail(to, from, subject, textBuilder.toString());

        if (b) {
            System.out.println("Email is sent successfully");
        } else {
            System.out.println("There is a problem in sending email");
        }

        scanner.close();
    }
}
