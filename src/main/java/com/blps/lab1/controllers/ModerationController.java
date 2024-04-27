package com.blps.lab1.controllers;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blps.lab1.controllers.dto.ReceiveModerationApprovement;
import com.blps.lab1.controllers.dto.ResponsePost;
import com.blps.lab1.exceptions.AccessDeniedException;
import com.blps.lab1.exceptions.NotFoundException;
import com.blps.lab1.model.beans.Post;

import com.blps.lab1.model.services.ModerationService;
import com.blps.lab1.model.services.ModerationService.ModerationResult;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ModerationController {

    private final ModerationService moderationService;

    @GetMapping("/moderation")
    public ResponseEntity<?> getModerationPosts(
            @RequestHeader("authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (size <= 0)
            return ResponseEntity.badRequest().body("Invalid page size");
        String phone;
        try {
            phone = token.split(":")[0];
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");

        }
        ModerationResult moderationResult;
        try {
            moderationResult = moderationService.getModerationPosts(page, size, phone);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }

        if (page >= moderationResult.getTotalPages())
            return ResponseEntity.badRequest().body("Invalid page number");

        List<ResponsePost> responsePosts = new java.util.ArrayList<>();
        for (Post post : moderationResult.posts) {
            responsePosts.add(new ResponsePost(post));
        }
        var response = new HashMap<String, Object>();
        response.put("currentPage", page);
        response.put("totalPages", moderationResult.getTotalPages());
        response.put("posts", responsePosts);
        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/moderation")
    public ResponseEntity<?> setModeration(@RequestBody ReceiveModerationApprovement body,
            @RequestHeader("authorization") String token) {

        String phone;
        try {
            phone = token.split(":")[0];
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        try {
            moderationService.approve(body.getPostId(), phone, body.isApproved());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }

}
