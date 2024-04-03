package com.example.fashionfrenzy.controller;

import com.example.fashionfrenzy.GetUserData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserDataController {

    @PostMapping("/register")
    public String registerUser(@RequestParam("name") String name, @RequestParam("email") String email) {
        return GetUserData.processUserData(name, email);
    }
}

