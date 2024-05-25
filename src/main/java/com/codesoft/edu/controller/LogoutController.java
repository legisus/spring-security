package com.codesoft.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LogoutController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/login-form";
    }
}
