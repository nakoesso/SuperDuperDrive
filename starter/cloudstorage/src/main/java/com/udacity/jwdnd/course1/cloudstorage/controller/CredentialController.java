package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.entities.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.mappers.CredentialsMapper;
import com.udacity.jwdnd.course1.cloudstorage.mappers.UsersMapper;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.util.Base64;

@Controller
@RequestMapping("/credentials")
public class CredentialController {

    private final CredentialsMapper credentialsMapper;
    private final UsersMapper usersMapper;
    private final EncryptionService encryptionService;

    public CredentialController(CredentialsMapper credentialsMapper, UsersMapper usersMapper, EncryptionService encryptionService) {
        this.credentialsMapper = credentialsMapper;
        this.usersMapper = usersMapper;
        this.encryptionService = encryptionService;
    }

    @PostMapping("/save")
    public String saveCredential(@RequestParam(required = false) Integer credentialId,
                                 @RequestParam String url,
                                 @RequestParam String username,
                                 @RequestParam String password,
                                 Authentication auth, RedirectAttributes redirectAttributes) {
        Integer userId = usersMapper.getUser(auth.getName()).getUserId();

        // Générer une clé et chiffrer le mot de passe
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[16];
        random.nextBytes(keyBytes);
        String encodedKey = Base64.getEncoder().encodeToString(keyBytes);
        String encryptedPassword = encryptionService.encryptValue(password, encodedKey);

        if (credentialId == null || credentialId == 0) {
            credentialsMapper.insert(new Credentials(null, url, username, encodedKey, encryptedPassword, userId));
        } else {
            credentialsMapper.update(new Credentials(credentialId, url, username, encodedKey, encryptedPassword, userId));
        }

        redirectAttributes.addFlashAttribute("credentialSuccess", true);
        return "redirect:/home";
    }

    @GetMapping("/delete/{credentialId}")
    public String deleteCredential(@PathVariable Integer credentialId, RedirectAttributes redirectAttributes) {
        credentialsMapper.delete(credentialId);
        redirectAttributes.addFlashAttribute("credentialSuccess", true);
        return "redirect:/home";
    }
}
