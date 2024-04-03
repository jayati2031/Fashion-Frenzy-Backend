package com.example.fashionfrenzy;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.regex.Pattern;

@Component
public class GetUserData {

    // Regular expression pattern for validating email addresses
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // Regular expression pattern for validating usernames (only alphabetic characters)
    private static final String USERNAME_REGEX = "^[a-zA-Z]+$";
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);

    public static String processUserData(String name, String email) {
        boolean isValidName = USERNAME_PATTERN.matcher(name).matches();
        boolean isValidEmail = EMAIL_PATTERN.matcher(email).matches();

        if (!isValidName || !isValidEmail) {
            return "Invalid name or email address.";
        }

        // Write the name and email to CSV file if email doesn't exist already
        boolean emailExists = checkIfEmailExists(email);
        if (!emailExists) {
            writeToCSV(name, email);
            return "New entry added to userData.csv";
        } else {
            return "Record with this email already exists in userData.csv";
        }
    }

    private static boolean checkIfEmailExists(String email) {
        try {
            File file = new File("userData.csv");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[1].equals(email)) {
                    br.close();
                    return true;
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("IOException caught: " + e.getMessage());
        }
        return false;
    }

    private static void writeToCSV(String name, String email) {
        try {
            File file = new File("userData.csv");
            FileWriter csvWriter = new FileWriter(file, true); // Append mode
            BufferedWriter bw = new BufferedWriter(csvWriter);
            PrintWriter pw = new PrintWriter(bw);

            pw.println(name + "," + email);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            System.out.println("IOException caught: " + e.getMessage());
        }
    }
}
