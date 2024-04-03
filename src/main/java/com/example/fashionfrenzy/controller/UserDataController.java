package com.example.fashionfrenzy.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.fashionfrenzy.GetUserData;

/**
 * Controller class for handling user registration requests.
 */
@RestController
@RequestMapping("/api")

// here the class UserDataCOntroller Starts.
public class UserDataController {

    /**
     * Endpoint for registering a new user.
     *
     * @param name  the user's name obtained from the request parameter
     * @param email the user's email address obtained from the request parameter
     * @return a message indicating the outcome of the registration process
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam("name") String name, @RequestParam("email") String email) {
        // Delegate user registration process to GetUserData class
        return GetUserData.processUserData(name, email);
    }
}
