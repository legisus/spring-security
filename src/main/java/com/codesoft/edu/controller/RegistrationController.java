package com.codesoft.edu.controller;

import com.codesoft.edu.model.User;
import com.codesoft.edu.repository.UserRepository;
import com.codesoft.edu.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    public RegistrationController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register-form";
    }

    @PostMapping("/register")
    public String registerUser(@Validated @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "register-form";
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            result.rejectValue("email", "error.user", "Email is already registered");
            return "register-form";
        }
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setRole(roleService.readById(2));
        userRepository.save(user);
        return "redirect:/login-form";
    }
}
