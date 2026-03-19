package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.entities.Notes;
import com.udacity.jwdnd.course1.cloudstorage.mappers.NotesMapper;
import com.udacity.jwdnd.course1.cloudstorage.mappers.UsersMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NotesMapper notesMapper;
    private final UsersMapper usersMapper;

    public NoteController(NotesMapper notesMapper, UsersMapper usersMapper) {
        this.notesMapper = notesMapper;
        this.usersMapper = usersMapper;
    }

    @PostMapping("/save")
    public String saveNote(@RequestParam(required = false) Integer noteId,
                           @RequestParam String noteTitle,
                           @RequestParam String noteDescription,
                           Authentication auth, RedirectAttributes redirectAttributes) {
        Integer userId = usersMapper.getUser(auth.getName()).getUserId();

        if (noteId == null || noteId == 0) {
            notesMapper.insert(new Notes(null, noteTitle, noteDescription, userId));
        } else {
            notesMapper.update(new Notes(noteId, noteTitle, noteDescription, userId));
        }

        redirectAttributes.addFlashAttribute("noteSuccess", true);
        return "redirect:/home";
    }

    @GetMapping("/delete/{noteId}")
    public String deleteNote(@PathVariable Integer noteId, RedirectAttributes redirectAttributes) {
        notesMapper.delete(noteId);
        redirectAttributes.addFlashAttribute("noteSuccess", true);
        return "redirect:/home";
    }
}
