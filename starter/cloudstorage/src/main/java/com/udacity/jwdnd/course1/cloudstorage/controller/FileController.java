package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.entities.Files;
import com.udacity.jwdnd.course1.cloudstorage.mappers.FilesMapper;
import com.udacity.jwdnd.course1.cloudstorage.mappers.UsersMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/files")
public class FileController {

    private final FilesMapper filesMapper;
    private final UsersMapper usersMapper;

    public FileController(FilesMapper filesMapper, UsersMapper usersMapper) {
        this.filesMapper = filesMapper;
        this.usersMapper = usersMapper;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile file,
                             Authentication auth, RedirectAttributes redirectAttributes) throws IOException {
        Integer userId = usersMapper.getUser(auth.getName()).getUserId();

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("fileError", "Please select a file to upload.");
            return "redirect:/home";
        }

        if (filesMapper.getFileByNameAndUser(file.getOriginalFilename(), userId) != null) {
            redirectAttributes.addFlashAttribute("fileError", "A file with that name already exists.");
            return "redirect:/home";
        }

        filesMapper.insert(new Files(null, file.getOriginalFilename(), file.getContentType(),
                String.valueOf(file.getSize()), userId, file.getBytes()));

        redirectAttributes.addFlashAttribute("fileSuccess", true);
        return "redirect:/home";
    }

    @GetMapping("/view/{fileId}")
    public ResponseEntity<byte[]> viewFile(@PathVariable Integer fileId) {
        Files file = filesMapper.getFile(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContenttype()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file.getFiledata());
    }

    @GetMapping("/delete/{fileId}")
    public String deleteFile(@PathVariable Integer fileId, RedirectAttributes redirectAttributes) {
        filesMapper.delete(fileId);
        redirectAttributes.addFlashAttribute("fileSuccess", true);
        return "redirect:/home";
    }
}
