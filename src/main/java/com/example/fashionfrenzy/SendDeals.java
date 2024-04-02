package com.example.fashionfrenzy;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class sends hot deals via email to recipients listed in a CSV file.
 */
@Component
public class SendDeals {

    // Email properties
    private static final String FROM_EMAIL = "frenzyfashionacc";
    private static final String EMAIL_SUBJECT = "Fashion-Frenzy Top Deals";
    private static final String EMAIL_TEMPLATE = "Hello.. Sale is going on the fashion frenzy....!\n\n" +
            "Here are the top deals:\n";
    private static final String EMAIL_REGEX = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";

    private final Properties emailProperties;

    /**
     * Constructor to initialize email properties.
     */
    public SendDeals() {
        emailProperties = new Properties();
        emailProperties.put("mail.smtp.auth", true); // Enable SMTP authentication
        emailProperties.put("mail.smtp.starttls.enable", true); // Enable TLS encryption
        emailProperties.put("mail.smtp.port", "587"); // SMTP port for TLS
        emailProperties.put("mail.smtp.host", "smtp.gmail.com"); // SMTP server host
    }

    /**
     * Sends hot deals via email.
     */
    public void sendDeals() {
        StringBuilder emailContent = new StringBuilder(EMAIL_TEMPLATE);
        try {
            // Read product information from Excel
            List<Map<String, String>> productInfo = FetchProductsFromExcelBasedOnCategory.readData(Collections.singletonList("product_info.xlsx"));
            // Include details of up to 5 products in the email content
            for (int i = 0; i < Math.min(productInfo.size(), 5); i++) {
                Map<String, String> productDetails = productInfo.get(i);
                emailContent.append("Title: ").append(productDetails.get("Title")).append("\n");
                emailContent.append("Brand: ").append(productDetails.get("Brand")).append("\n");
                emailContent.append("Price: ").append(productDetails.get("Price")).append("\n");
                emailContent.append("URL: ").append(productDetails.get("URL")).append("\n");
                emailContent.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            }
            // Send emails to recipients
            boolean allEmailsSentSuccessfully = sendEmails(readEmailsFromCSV(), emailContent.toString());
            if (allEmailsSentSuccessfully) {
                System.out.println("Emails sent successfully to all recipients.");
            } else {
                System.out.println("Failed to send emails to all recipients.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while sending hot deals: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads email addresses from a CSV file.
     *
     * @return An array of email addresses
     */
    private String[] readEmailsFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader("userData.csv"))) {
            return br.lines() // Read each line from the CSV file
                    .flatMap(line -> extractEmails(line).stream()) // Extract email addresses from each line
                    .toArray(String[]::new); // Convert extracted emails to an array
        } catch (Exception e) {
            System.out.println("An error occurred while reading email addresses from CSV: " + e.getMessage());
            e.printStackTrace();
            return new String[0];
        }
    }

    /**
     * Sends emails to the specified recipients.
     *
     * @param recipients   An array of email addresses
     * @param emailContent The content of the email
     * @return True if all emails are sent successfully, otherwise false
     */
    private boolean sendEmails(String[] recipients, String emailContent) {
        boolean allEmailsSentSuccessfully = true;
        Session session = Session.getInstance(emailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, "cafzzdyuhlhttzus"); // Authenticate with Gmail credentials
            }
        });

        for (String recipient : recipients) {
            try {
                Message message = new MimeMessage(session);
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient)); // Set recipient email address
                message.setFrom(new InternetAddress(FROM_EMAIL)); // Set sender email address
                message.setSubject(EMAIL_SUBJECT); // Set email subject
                message.setText(emailContent); // Set email content
                Transport.send(message); // Send the email
            } catch (Exception e) {
                allEmailsSentSuccessfully = false;
                System.out.println("Failed to send email to: " + recipient + ". Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return allEmailsSentSuccessfully;
    }

    /**
     * Extracts email addresses from the given text.
     *
     * @param text The text to extract email addresses from
     * @return A list of extracted email addresses
     */
    private static List<String> extractEmails(String text) {
        List<String> emails = new ArrayList<>();
        try {
            Pattern pattern = Pattern.compile(EMAIL_REGEX); // Compile the email regex pattern
            Matcher matcher = pattern.matcher(text); // Create matcher to find email patterns in text
            while (matcher.find()) {
                emails.add(matcher.group()); // Add found email to the list
            }
        } catch (Exception e) {
            System.out.println("An error occurred while extracting emails: " + e.getMessage());
            e.printStackTrace();
        }
        return emails;
    }

    /**
     * Main method to execute the program.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SendDeals sendDeals = new SendDeals();
        sendDeals.sendDeals();
    }
}