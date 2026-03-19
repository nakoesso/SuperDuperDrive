package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.entities.Users;
import com.udacity.jwdnd.course1.cloudstorage.mappers.UsersMapper;
import com.udacity.jwdnd.course1.cloudstorage.services.HashService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.SecureRandom;
import java.util.Base64;

@Controller
public class LoginController {

    private final UsersMapper usersMapper;
    private final HashService hashService;

    public LoginController(UsersMapper usersMapper, HashService hashService) {
        this.usersMapper = usersMapper;
        this.hashService = hashService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute Users user, Model model) {
        if (usersMapper.getUser(user.getUsername()) != null) {
            model.addAttribute("signupError", "Username already exists.");
            return "signup";
        }

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String hashedPassword = hashService.getHashedValue(user.getPassword(), encodedSalt);

        usersMapper.insert(new Users(null, user.getUsername(), encodedSalt, hashedPassword, user.getFirstName(), user.getLastName()));

        return "redirect:/login?signupSuccess";
    }
}
