package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.entities.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.mappers.CredentialsMapper;
import com.udacity.jwdnd.course1.cloudstorage.mappers.FilesMapper;
import com.udacity.jwdnd.course1.cloudstorage.mappers.NotesMapper;
import com.udacity.jwdnd.course1.cloudstorage.mappers.UsersMapper;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final FilesMapper filesMapper;
    private final NotesMapper notesMapper;
    private final CredentialsMapper credentialsMapper;
    private final UsersMapper usersMapper;
    private final EncryptionService encryptionService;

    public HomeController(FilesMapper filesMapper, NotesMapper notesMapper, CredentialsMapper credentialsMapper,
                          UsersMapper usersMapper, EncryptionService encryptionService) {
        this.filesMapper = filesMapper;
        this.notesMapper = notesMapper;
        this.credentialsMapper = credentialsMapper;
        this.usersMapper = usersMapper;
        this.encryptionService = encryptionService;
    }

    @GetMapping("/home")
    public String homePage(Authentication auth, Model model) {
        Integer userId = usersMapper.getUser(auth.getName()).getUserId();
        model.addAttribute("files", filesMapper.getFilesByUser(userId));
        model.addAttribute("notes", notesMapper.getNotesByUser(userId));

        // credentials avec mot de passe chiffré pour le tableau
        List<Credentials> credentials = credentialsMapper.getCredentialsByUser(userId);
        model.addAttribute("credentials", credentials);

        // map id -> mot de passe déchiffré pour le modal
        Map<Integer, String> decryptedPasswords = credentials.stream()
                .collect(Collectors.toMap(
                        Credentials::getCredentialid,
                        c -> encryptionService.decryptValue(c.getPassword(), c.getEncryptionkey())
                ));
        model.addAttribute("decryptedPasswords", decryptedPasswords);
        return "home";
    }
}
