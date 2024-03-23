package com.blps.lab1.model.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.blps.lab1.model.beans.Post;

@Service
@RequiredArgsConstructor
public class PostValidationService {

    public boolean checkPost(Post post) {
        if (post.getArchived() == null ||
                post.getAddress() == null ||
                post.getPrice() == null ||
                post.getRoomNumber() == null ||
                post.getArea() == null ||
                post.getFloor() == null ||
                post.getTitle() == null ||
                post.getDescription() == null) {
            return false;
        }

        if (post.getArea() <= 0 ||
                post.getFloor() <= 0 ||
                post.getPrice() <= 0 ||
                post.getRoomNumber() <= 0 ||
                post.getTitle().length() == 0 ||
                post.getDescription().length() == 0) {
            return false;
        }

        return true;
    }
}