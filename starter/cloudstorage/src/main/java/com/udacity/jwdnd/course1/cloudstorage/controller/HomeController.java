package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.mappers.FilesMapper;
import com.udacity.jwdnd.course1.cloudstorage.mappers.UsersMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final FilesMapper filesMapper;
    private final UsersMapper usersMapper;

    public HomeController(FilesMapper filesMapper, UsersMapper usersMapper) {
        this.filesMapper = filesMapper;
        this.usersMapper = usersMapper;
    }

    @GetMapping("/home")
    public String homePage(Authentication auth, Model model) {
        Integer userId = usersMapper.getUser(auth.getName()).getUserId();
        model.addAttribute("files", filesMapper.getFilesByUser(userId));
        return "home";
    }
}
