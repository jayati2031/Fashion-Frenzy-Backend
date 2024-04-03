package com.example.fashionfrenzy;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.regex.Pattern;

/**
 * Component class for processing user data and managing a CSV file.
 */
@Component
public class GetUserData {

    // Regular expression pattern for validating email addresses
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // Regular expression pattern for validating usernames (only alphabetic characters)
    private static final String USERNAME_REGEX = "^[a-zA-Z]+$";
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);

    /**
     * Process user data including name and email address.
     *
     * @param name  the user's name
     * @param email the user's email address
     * @return a message indicating the outcome of the process
     */
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

    /**
     * Check if an email address already exists in the userData.csv file.
     *
     * @param email the email address to check
     * @return true if the email exists, false otherwise
     */
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
            // Handle IOException
            System.out.println("IOException caught: " + e.getMessage());
        }
        return false;
    }

    /**
     * Write user data (name and email) to the userData.csv file.
     *
     * @param name  the user's name
     * @param email the user's email address
     */
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
            // Handle IOException
            System.out.println("IOException caught: " + e.getMessage());
        }
    }
}
