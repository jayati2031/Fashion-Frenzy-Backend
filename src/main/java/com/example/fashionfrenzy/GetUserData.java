package com.example.fashionfrenzy;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetUserData {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Regular expression pattern for validating email addresses
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern emailPattern = Pattern.compile(emailRegex);

        // Regular expression pattern for validating usernames (only alphabetic characters)
        String usernameRegex = "^[a-zA-Z]+$";
        Pattern usernamePattern = Pattern.compile(usernameRegex);

        String name = "";
        String email = "";

        boolean isValidName = false;
        boolean isValidEmail = false;

        // Loop until valid name and email are entered
        while (!isValidName || !isValidEmail) {
            // Validate and input name
            System.out.print("Enter Your name: ");
            name = scanner.nextLine();
            isValidName = true; // Assume name is always valid

            // Validate and input email
            System.out.print("Enter Your email address: ");
            email = scanner.nextLine();
            Matcher emailMatcher = emailPattern.matcher(email);
            isValidEmail = emailMatcher.matches();
            if (!isValidEmail) {
                System.out.println("Invalid email address. Please enter a valid email address.");
            }
        }

        // Write the name and email to CSV file if email doesn't exist already
        boolean emailExists = false;
        try {
            File file = new File("userData.csv");

            // Check if the email already exists in the CSV file
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[1].equals(email)) {
                    emailExists = true;
                    break;
                }
            }
            br.close();

            // If email doesn't exist, add it to the CSV file
            if (!emailExists) {
                FileWriter csvWriter = new FileWriter(file, true); // Append mode
                BufferedWriter bw = new BufferedWriter(csvWriter);
                PrintWriter pw = new PrintWriter(bw);

                pw.println(name + "," + email);
                pw.flush();
                System.out.println("New entry added to userData.csv");
                pw.close();
            } else {
                System.out.println("Record with this email already exists in userData.csv");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        // Prompt user to roll out hot deals
        String hotDealsChoice;
        do {
            System.out.print("Do you want to roll out hot deals? (Yes/No): ");
            hotDealsChoice = scanner.nextLine().toLowerCase();
            if (!hotDealsChoice.equals("yes") && !hotDealsChoice.equals("no")) {
                System.out.println("Invalid choice. Please enter 'Yes' or 'No'.");
            }
        } while (!hotDealsChoice.equals("yes") && !hotDealsChoice.equals("no"));

        // Handle user's choice
        if (hotDealsChoice.equals("yes")) {
            System.out.println("Yes");
        } else {
            System.out.println("No");
        }

        // Close the scanner
        scanner.close();
*/
    }
}

