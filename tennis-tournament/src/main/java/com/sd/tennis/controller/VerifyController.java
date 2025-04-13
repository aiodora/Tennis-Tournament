package com.sd.tennis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerifyController {

    @GetMapping("/verify")
    public String healthCheck() {
        return "Server is running";
    }
}

